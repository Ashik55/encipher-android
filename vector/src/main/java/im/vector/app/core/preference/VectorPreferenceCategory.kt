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
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import im.vector.app.R
import im.vector.app.features.themes.ThemeUtils

/**
 * Customize PreferenceCategory class to redefine some attributes.
 */
class VectorPreferenceCategory : PreferenceCategory {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        // Set to false to remove the space when there is no icon
        isIconSpaceReserved = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val titleTextView = holder.findViewById(android.R.id.title) as? TextView
        val summaryTextView = holder.findViewById(android.R.id.summary) as? TextView

        // Customize title font, size, and color
        titleTextView?.setTypeface(null, Typeface.BOLD)
        titleTextView?.setTextColor(ThemeUtils.getColor(context, im.vector.lib.ui.styles.R.attr.vctr_content_primary))
        val customTypeface: Typeface? = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_75_bold)
        titleTextView?.typeface = customTypeface
        titleTextView?.textSize = 18f

//        // Add a divider view after the title
//        val parent = titleTextView?.parent as? ViewGroup
//        parent?.apply {
//            val dividerView = View(context).apply {
//                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2)
//                // Use a color or drawable for the divider background
//                setBackgroundResource(android.R.color.darker_gray) // or use R.drawable.your_divider_drawable
//            }
//            addView(dividerView)
//        }

        // Customize summary font, size, and color (similar to titleTextView)
        summaryTextView?.setTypeface(null, Typeface.NORMAL)
        val customTypefaceSum: Typeface? = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_55_roman)
        summaryTextView?.setTextColor(ThemeUtils.getColor(context, im.vector.lib.ui.styles.R.attr.vctr_content_secondary))
        summaryTextView?.typeface = customTypefaceSum
        summaryTextView?.textSize = 14f

        // "isIconSpaceReserved = false" does not work for preference category, so remove the padding
//        if (!isIconSpaceReserved) {
//            (titleTextView?.parent as? ViewGroup)?.setPadding(0, 0, 0, 0)
//        }
    }
}
