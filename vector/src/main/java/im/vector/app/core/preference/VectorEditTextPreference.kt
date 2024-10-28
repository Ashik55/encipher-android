/*
 * Copyright 2018-2024 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 * Please see LICENSE in the repository root for full details.
 */

package im.vector.app.core.preference

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceViewHolder
import im.vector.app.R
import timber.log.Timber

/**
 * Use this class to create an EditTextPreference form code and avoid a crash (see https://code.google.com/p/android/issues/detail?id=231576)
 */
class VectorEditTextPreference : EditTextPreference {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        dialogLayoutResource = R.layout.dialog_preference_edit_text
        // Set to false to remove the space when there is no icon
        isIconSpaceReserved = false
    }

    // No single line for title
    override fun onBindViewHolder(holder: PreferenceViewHolder) {

        // Set custom font and size for the title
        val title = holder.findViewById(android.R.id.title) as? TextView
        title?.let {
            it.textSize = 16f  // Set title text size (example: 18sp)

            // Set custom font for title
            val titleFont = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_75_bold)
            it.typeface = titleFont
        }

        // Set custom font and size for the summary
        val summary = holder.findViewById(android.R.id.summary) as? TextView
        summary?.let {
            it.textSize = 14f  // Set summary text size (example: 16sp)

            // Set custom font for summary
            val summaryFont = ResourcesCompat.getFont(context, im.vector.lib.ui.styles.R.font.helvetica_neue_lt_std_55_roman)
            it.typeface = summaryFont
        }


        // display the title in multi-line to avoid ellipsis.
        try {
            title?.isSingleLine = false
        } catch (e: Exception) {
            Timber.e(e, "onBindView")
        }

        super.onBindViewHolder(holder)
    }
}
