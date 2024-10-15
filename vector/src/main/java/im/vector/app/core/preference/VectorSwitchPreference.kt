/*
 * Copyright 2018 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.preference

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreference
import im.vector.app.features.themes.ThemeUtils

/**
 * Switch preference with title on multiline (only used in XML).
 */
class VectorSwitchPreference : SwitchPreference {

    // Note: @JvmOverload does not work here...
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    init {
        // Set to false to remove the space when there is no icon
        isIconSpaceReserved = false
    }

    var isHighlighted = false
        set(value) {
            field = value
            notifyChanged()
        }

    var currentHighlightAnimator: Animator? = null

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        // Set custom font and size for the title
        val title = holder.findViewById(android.R.id.title) as? TextView
        title?.let {
            it.textSize = 16f  // Set title text size (example: 18sp)

            // Set custom font for title
            val titleFont = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_75_bold)
            it.typeface = titleFont

            // Avoid ellipsis by setting multiline
            it.isSingleLine = false
        }

        // cancel existing animation (find a way to resume if happens during anim?)
        currentHighlightAnimator?.cancel()

        val itemView = holder.itemView
        if (isHighlighted) {
            val colorFrom = Color.TRANSPARENT
            val colorTo = ThemeUtils.getColor(itemView.context, com.google.android.material.R.attr.colorControlHighlight)
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

        super.onBindViewHolder(holder)
    }
}
