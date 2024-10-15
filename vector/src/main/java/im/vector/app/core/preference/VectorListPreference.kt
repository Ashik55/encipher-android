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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import im.vector.app.R
import timber.log.Timber

/**
 * Customize ListPreference class to add a warning icon to the right side of the list.
 */
class VectorListPreference : ListPreference {

    //
    private var mWarningIconView: View? = null
    private var mIsWarningIconVisible = false
    private var mWarningIconClickListener: OnPreferenceWarningIconClickListener? = null

    /**
     * Interface definition for a callback to be invoked when the warning icon is clicked.
     */
    interface OnPreferenceWarningIconClickListener {
        /**
         * Called when a warning icon has been clicked.
         *
         * @param preference The Preference that was clicked.
         */
        fun onWarningIconClick(preference: Preference)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        widgetLayoutResource = R.layout.vector_settings_list_preference_with_warning
        // Set to false to remove the space when there is no icon
        isIconSpaceReserved = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val view = holder.itemView

        mWarningIconView = view.findViewById(R.id.list_preference_warning_icon)
        mWarningIconView!!.visibility = if (mIsWarningIconVisible) View.VISIBLE else View.GONE

        mWarningIconView!!.setOnClickListener {
            if (null != mWarningIconClickListener) {
                mWarningIconClickListener!!.onWarningIconClick(this@VectorListPreference)
            }
        }
        try {
            // Set custom font and size for the title
            val titleTextView = holder.findViewById(android.R.id.title) as? TextView
            val summaryTextView = holder.findViewById(android.R.id.summary) as? TextView

            titleTextView?.let {
                it.isSingleLine = false // Allow multiple lines for the title
                it.typeface = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_75_bold) // Custom font
                it.textSize = 16f // Custom size for the title
            }

            summaryTextView?.let {
                it.typeface = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_55_roman) // Custom font
                it.textSize = 14f // Custom size for the summary
            }
        } catch (e: Exception) {
            Timber.e(e, "Error setting custom font and size for title and summary")
        }
    }



    /**
     * Sets the callback to be invoked when this warning icon is clicked.
     *
     * @param onPreferenceWarningIconClickListener The callback to be invoked.
     */
    fun setOnPreferenceWarningIconClickListener(onPreferenceWarningIconClickListener: OnPreferenceWarningIconClickListener) {
        mWarningIconClickListener = onPreferenceWarningIconClickListener
    }

    /**
     * Set the warning icon visibility.
     *
     * @param isVisible to display the icon
     */
    fun setWarningIconVisible(isVisible: Boolean) {
        mIsWarningIconVisible = isVisible

        mWarningIconView?.visibility = if (mIsWarningIconVisible) View.VISIBLE else View.GONE
    }
}
