package im.vector.app.core.preference

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import im.vector.app.features.themes.ThemeUtils
import org.matrix.android.sdk.api.extensions.orFalse
import timber.log.Timber

/**
 * Create a Preference with dedicated click/long click methods.
 * It also allows the title to be displayed on several lines.
 */
open class VectorPreference : Preference {

    var mTypeface: Typeface? = null
    var mTypefaceSmall: Typeface? = null

    var titleTextSize: Float = 16f
    var summaryTextSize: Float = 14f

    /**
     * Callback to be invoked when this Preference is long clicked.
     */
    var onPreferenceLongClickListener: OnPreferenceLongClickListener? = null

    /**
     * Interface definition for a callback to be invoked when a preference is
     * long clicked.
     */
    interface OnPreferenceLongClickListener {
        /**
         * Called when a Preference has been clicked.
         *
         * @param preference The Preference that was clicked.
         * @return True if the click was handled.
         */
        fun onPreferenceLongClick(preference: Preference): Boolean
    }

    constructor(context: Context) : super(context) {
        initTypeface(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initTypeface(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initTypeface(context)
    }

    private fun initTypeface(context: Context) {
        // Load the custom font from res/font
        mTypeface = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_75_bold)
        mTypefaceSmall = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_55_roman)
    }

    init {
        // Set to false to remove the space when there is no icon
        isIconSpaceReserved = false
    }

    var isHighlighted = false
        set(value) {
            field = value
            notifyChanged()
        }

    var tintIcon = false
        set(value) {
            field = value
            notifyChanged()
        }

    var currentHighlightAnimator: Animator? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        val itemView = holder.itemView
        addClickListeners(itemView)

        // Display the title in multi-line to avoid ellipsis.
        try {
            val title = holder.findViewById(android.R.id.title) as? TextView
            val summary = holder.findViewById(android.R.id.summary) as? TextView

            // Set title properties
            title?.let {
                it.isSingleLine = false
                it.typeface = mTypeface
                it.textSize = titleTextSize

                if (key == "SETTINGS_SIGN_OUT_KEY") {
                    it.setTextColor(Color.parseColor("#EB3E18"))
                }
            }

            // Set summary properties
            summary?.let {
                it.typeface = mTypefaceSmall
                it.textSize = summaryTextSize
            }

            if (tintIcon) {
                // Tint icons (See #1786)
                val icon = holder.findViewById(android.R.id.icon) as? ImageView

                icon?.let {
                    val color = ThemeUtils.getColor(context, im.vector.lib.ui.styles.R.attr.vctr_content_secondary)
                    ImageViewCompat.setImageTintList(it, ColorStateList.valueOf(color))
                }
            }

            // Cancel existing animation (find a way to resume if happens during anim?)
            currentHighlightAnimator?.cancel()
            if (isHighlighted) {
                val colorFrom = Color.TRANSPARENT
                val colorTo = ThemeUtils.getColor(itemView.context, com.google.android.material.R.attr.colorPrimary)
                currentHighlightAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo).apply {
                    duration = 250 // milliseconds
                    addUpdateListener { animator ->
                        itemView.setBackgroundColor(animator.animatedValue as Int)
                    }
                    doOnEnd {
                        currentHighlightAnimator = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom).apply {
                            duration = 250 // milliseconds
                            addUpdateListener { animator ->
                                itemView.setBackgroundColor(animator.animatedValue as Int)
                            }
                            doOnEnd {
                                isHighlighted = false
                            }
                            start()
                        }
                    }
                    startDelay = 200
                    start()
                }
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        } catch (e: Exception) {
            Timber.e(e, "onBindView")
        }

        super.onBindViewHolder(holder)
    }

    /**
     * @param view
     */
    private fun addClickListeners(view: View) {
        view.setOnLongClickListener {
            onPreferenceLongClickListener?.onPreferenceLongClick(this@VectorPreference).orFalse()
        }

        view.setOnClickListener {
            // Call only the click listener
            onPreferenceClickListener?.onPreferenceClick(this@VectorPreference)
        }
    }
}
