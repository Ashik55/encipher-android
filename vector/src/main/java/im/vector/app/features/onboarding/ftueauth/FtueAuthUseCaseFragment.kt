/*
 * Copyright (c) 2021 New Vector Ltd
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

package im.vector.app.features.onboarding.ftueauth

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.R
import im.vector.app.core.extensions.setLeftDrawable
import im.vector.app.databinding.FragmentFtueAuthUseCaseBinding
import im.vector.app.features.VectorFeatures
import im.vector.app.features.login.ServerType
import im.vector.app.features.onboarding.FtueUseCase
import im.vector.app.features.onboarding.OnboardingAction
import im.vector.app.features.themes.ThemeProvider
import im.vector.lib.strings.CommonStrings
import javax.inject.Inject

private const val DARK_MODE_ICON_BACKGROUND_ALPHA = 0.30f
private const val LIGHT_MODE_ICON_BACKGROUND_ALPHA = 0.15f

@AndroidEntryPoint
class FtueAuthUseCaseFragment :
        AbstractFtueAuthFragment<FragmentFtueAuthUseCaseBinding>() {

    @Inject lateinit var themeProvider: ThemeProvider
    @Inject lateinit var vectorFeatures: VectorFeatures

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFtueAuthUseCaseBinding {
        return FragmentFtueAuthUseCaseBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        views.useCaseConnectToServerGroup.isGone = vectorFeatures.isOnboardingCombinedRegisterEnabled()

        views.useCaseOptionOne.renderUseCase(
                useCase = FtueUseCase.FRIENDS_FAMILY,
                label = CommonStrings.ftue_auth_use_case_option_one,
                icon = R.drawable.ic_use_case_friends
        )
        views.useCaseOptionTwo.renderUseCase(
                useCase = FtueUseCase.TEAMS,
                label = CommonStrings.ftue_auth_use_case_option_two,
                icon = R.drawable.ic_use_case_teams
        )
        views.useCaseOptionThree.renderUseCase(
                useCase = FtueUseCase.COMMUNITIES,
                label = CommonStrings.ftue_auth_use_case_option_three,
                icon = R.drawable.ic_use_case_communities
        )

        views.useCaseSkipText.text = getString(CommonStrings.ftue_auth_use_case_skip)

        views.useCaseSkip.setOnClickListener {
            viewModel.handle(OnboardingAction.UpdateUseCase(FtueUseCase.SKIP))
        }

        views.useCaseConnectToServer.setOnClickListener {
            viewModel.handle(OnboardingAction.UpdateServerType(ServerType.Other))
        }
    }

    override fun resetViewModel() {
        viewModel.handle(OnboardingAction.ResetUseCase)
    }

    private fun TextView.renderUseCase(useCase: FtueUseCase, @StringRes label: Int, @DrawableRes icon: Int) {
        setLeftDrawable(createIcon(icon))
        setText(label)
        debouncedClicks {
            viewModel.handle(OnboardingAction.UpdateUseCase(useCase))
        }
    }

    private fun createIcon(icon: Int): Drawable {
        val context = requireContext()
        return ContextCompat.getDrawable(context, icon) ?: throw IllegalArgumentException("Icon not found")
    }

}
