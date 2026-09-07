package com.rebottle.app.ui.screens.fundacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


private enum class FundacionTab {
    HOME,
    PROFILE
}


@Composable
fun FundacionFlowScreen() {

    var selectedTab by remember {
        mutableStateOf(FundacionTab.HOME)
    }


    when (selectedTab) {

        FundacionTab.HOME -> {

            HomeFundacionScreen(
                onProfileClick = {
                    selectedTab = FundacionTab.PROFILE
                }
            )
        }


        FundacionTab.PROFILE -> {

            ProfileFundacionScreen(
                onHomeClick = {
                    selectedTab = FundacionTab.HOME
                }
            )
        }
    }
}