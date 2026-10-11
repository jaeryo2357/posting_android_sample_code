package com.example.myapplication.navigation.login

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LinkStateTest {
    private val initial = LinkState(listOf(ProductList, ProductDetail("7")))

    private fun open(state: LinkState, id: String, request: String, cold: Boolean = false) =
        receive(state, request, ProductDetail(id), listOf(ProductList, ProductDetail(id)), false, cold)

    @Test
    fun loginRetainsExistingHistory() {
        val result = complete(open(initial, "42", "a"), "a")
        assertEquals(listOf(ProductList, ProductDetail("7"), ProductDetail("42")), result.stack)
        assertNull(result.pending)
    }

    @Test
    fun oldCompletionCannotOpenReplacedDestination() {
        val waiting = open(open(initial, "42", "a"), "99", "b")
        assertEquals(waiting, complete(waiting, "a"))
        assertEquals(ProductDetail("99"), complete(waiting, "b").stack.last())
    }

    @Test
    fun duplicateCompletionDoesNothing() {
        val done = complete(open(initial, "42", "a"), "a")
        assertEquals(done, complete(done, "a"))
    }

    @Test
    fun cancellationRestoresPreviousHistory() {
        assertEquals(initial, cancel(open(initial, "42", "a"), "a"))
    }

    @Test
    fun coldStartUsesSyntheticPath() {
        val result = complete(open(initial, "42", "a", cold = true), "a")
        assertEquals(listOf(ProductList, ProductDetail("42")), result.stack)
    }

    @Test
    fun restoredPendingRequestCanBeCompleted() {
        val waiting = open(initial, "42", "a")
        val restored = Json.decodeFromString<LinkState>(Json.encodeToString(waiting))
        assertEquals(waiting, restored)
        assertEquals(complete(waiting, "a"), complete(restored, "a"))
    }

    @Test
    fun repeatedDestinationDoesNotDuplicateTopEntry() {
        val result = complete(open(initial, "7", "a"), "a")
        assertEquals(initial, result)
    }
}
