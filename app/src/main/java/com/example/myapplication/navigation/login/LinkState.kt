package com.example.myapplication.navigation.login

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey

@Serializable
data object ProductList : Route

@Serializable
data class ProductDetail(val id: String) : Route

@Serializable
data class Login(val requestId: String) : Route

@Serializable
data class PendingLink(val requestId: String, val targetStack: List<Route>)

@Serializable
data class LinkState(
    val stack: List<Route> = listOf(ProductList),
    val pending: PendingLink? = null,
) {
    val visibleStack: List<Route>
        get() = pending?.let { stack + Login(it.requestId) } ?: stack
}

fun receive(
    state: LinkState,
    requestId: String,
    destination: ProductDetail,
    syntheticPath: List<Route>,
    loggedIn: Boolean,
    replacePath: Boolean,
): LinkState {
    val target = when {
        replacePath -> syntheticPath
        state.stack.lastOrNull() == destination -> state.stack
        else -> state.stack + destination
    }
    val next = state.copy(pending = PendingLink(requestId, target))
    return if (loggedIn) complete(next, requestId) else next
}

fun complete(state: LinkState, requestId: String): LinkState {
    val pending = state.pending ?: return state
    if (pending.requestId != requestId) return state
    return state.copy(stack = pending.targetStack, pending = null)
}

fun cancel(state: LinkState, requestId: String): LinkState =
    if (state.pending?.requestId == requestId) state.copy(pending = null) else state
