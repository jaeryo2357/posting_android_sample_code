package com.example.nav3login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import java.util.UUID
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private val productMatcher = UriDeepLinkMatcher(
    DeepLinkUri("https://shop.example.com/products/{id}"),
    serializer<ProductDetail>()
).withBackStack { listOf<Route>(ProductList, it.key) }

class LinkViewModel(private val saved: SavedStateHandle) : ViewModel() {
    private val initialJson = Json.encodeToString(LinkState())
    private val stateJson = saved.getStateFlow("link-state", initialJson)
    val state = stateJson.map { Json.decodeFromString<LinkState>(it) }
    val loggedIn = saved.getStateFlow("mock-session", false)
    val broken = saved.getStateFlow("broken", false)
    val previousRequest = saved.getStateFlow<String?>("previous-request", null)
    private val current: LinkState
        get() = Json.decodeFromString(stateJson.value)

    private fun update(next: LinkState) {
        saved["link-state"] = Json.encodeToString(next)
    }

    fun open(uri: String, replacePath: Boolean) {
        val match = productMatcher.match(DeepLinkRequest(uri = uri)) ?: return
        saved["previous-request"] = current.pending?.requestId
        update(receive(
            current, UUID.randomUUID().toString(), match.key,
            match.backStack, loggedIn.value, replacePath
        ))
    }

    fun login(requestId: String) {
        // Mock authentication; an obsolete callback must not navigate a new request.
        if (current.pending?.requestId != requestId) return
        saved["mock-session"] = true
        update(if (broken.value) LinkState() else complete(current, requestId))
    }

    fun completeOldRequest() {
        val id = previousRequest.value ?: return
        update(complete(current, id))
    }

    fun back(): Boolean {
        val value = current
        if (value.pending != null) {
            update(cancel(value, value.pending.requestId))
            return true
        }
        if (value.stack.size > 1) {
            update(value.copy(stack = value.stack.dropLast(1)))
            return true
        }
        return false
    }

    fun reset(useBroken: Boolean = broken.value) {
        saved["broken"] = useBroken
        saved["mock-session"] = false
        saved["previous-request"] = null
        update(LinkState())
    }
}
