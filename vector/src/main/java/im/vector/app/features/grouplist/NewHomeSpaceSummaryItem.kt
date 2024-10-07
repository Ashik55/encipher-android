/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package im.vector.app.features.grouplist

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import im.vector.app.R
import im.vector.app.core.epoxy.ClickListener
import im.vector.app.core.epoxy.VectorEpoxyHolder
import im.vector.app.core.epoxy.VectorEpoxyModel
import im.vector.app.core.epoxy.onClick
import im.vector.app.core.platform.CheckableConstraintLayout
import im.vector.app.features.home.room.list.UnreadCounterBadgeView
import im.vector.app.features.themes.ThemeUtils
import im.vector.lib.strings.CommonStrings

@EpoxyModelClass
abstract class NewHomeSpaceSummaryItem : VectorEpoxyModel<NewHomeSpaceSummaryItem.Holder>(R.layout.item_new_space) {

    @EpoxyAttribute var text: String = ""
    @EpoxyAttribute var selected: Boolean = false
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) var listener: ClickListener? = null
    @EpoxyAttribute var countState: UnreadCounterBadgeView.State = UnreadCounterBadgeView.State.Count(0, false)
    @EpoxyAttribute var showSeparator: Boolean = false

    override fun getViewType() = R.id.space_item_home

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.root.onClick(listener)

        // Set the text for the item
        holder.name.text = holder.view.context.getString(CommonStrings.all_chats)

        // Update the checked state
        holder.root.isChecked = selected

        // Default state: white text color and default icon
        if (!selected) {
            holder.name.setTextColor(ContextCompat.getColor(holder.view.context, im.vector.lib.ui.styles.R.color.white))
            holder.avatar.setImageResource(R.drawable.ic_space_home)  // Default icon
//            holder.avatar.imageTintList = ColorStateList.valueOf(
//                    ThemeUtils.getColor(holder.view.context, im.vector.lib.ui.styles.R.attr.vctr_content_primary)
//            )
        }
        // Selected state: change text color and icon tint
        else {
            holder.name.setTextColor(ContextCompat.getColor(holder.view.context, im.vector.lib.ui.styles.R.color.secondaryColor))
            holder.avatar.setImageResource(R.drawable.ic_space_home_selected)  // Selected icon
//            holder.avatar.imageTintList = ColorStateList.valueOf(
//                    ThemeUtils.getColor(holder.view.context, im.vector.lib.ui.styles.R.color.secondaryColor)
//            )
        }

        // Set the background for the avatar (this part seems constant)
        holder.avatar.background = ContextCompat.getDrawable(holder.view.context, R.drawable.new_space_home_background)
        holder.avatar.backgroundTintList = ColorStateList.valueOf(
                ColorUtils.setAlphaComponent(
                        ThemeUtils.getColor(holder.view.context, im.vector.lib.ui.styles.R.attr.vctr_content_tertiary), (255 * 0.3).toInt()
                )
        )
        holder.avatar.scaleType = ImageView.ScaleType.CENTER_INSIDE

        // Handle the unread counter state
        holder.unreadCounter.render(countState)
    }


    class Holder : VectorEpoxyHolder() {
        val root by bind<CheckableConstraintLayout>(R.id.root)
        val avatar by bind<ImageView>(R.id.avatar)
        val name by bind<TextView>(R.id.name)
        val unreadCounter by bind<UnreadCounterBadgeView>(R.id.unread_counter)
    }
}
