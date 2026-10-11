package com.example.myapplication.navigation.login

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class LinkUiTest {
    @get:Rule
    val compose = createAndroidComposeRule<NavigationLoginActivity>()

    @Test
    fun loginAfterRecreationOpensLatestDestination() {
        compose.onNodeWithTag("link-42").performClick()
        compose.onNodeWithTag("pending").assertTextContains("상품 42")
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("pending").assertTextContains("상품 42")
        compose.onNodeWithTag("link-99").performClick()
        compose.onNodeWithTag("old-completion").performClick()
        compose.onNodeWithTag("pending").assertTextContains("상품 99")
        compose.onNodeWithTag("login").performClick()
        compose.onNodeWithTag("detail").assertTextEquals("상품 99")
        compose.onNodeWithTag("pending").assertTextEquals("대기: 없음")
    }

    @Test
    fun cancelDoesNotLoseList() {
        compose.onNodeWithTag("link-42").performClick()
        compose.onNodeWithTag("cancel").performClick()
        compose.onNodeWithTag("stack").assertTextEquals("경로: 목록")
        compose.onNodeWithTag("pending").assertTextEquals("대기: 없음")
    }
}
