package com.example.nav3login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay

class MainActivity : ComponentActivity() {
    private val model: LinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restoration owns the existing request; do not replay the launch Intent.
        if (savedInstanceState == null) {
            intent.dataString?.let { model.open(it, replacePath = true) }
        }
        setContent {
            val state by model.state.collectAsStateWithLifecycle(LinkState())
            val loggedIn by model.loggedIn.collectAsStateWithLifecycle()
            val broken by model.broken.collectAsStateWithLifecycle()
            val previous by model.previousRequest.collectAsStateWithLifecycle()

            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.safeDrawingPadding().padding(16.dp)) {
                        Text("딥 링크 · 로그인 실습", style = MaterialTheme.typography.titleLarge)
                        Row {
                            Text("실패 구현", Modifier.weight(1f))
                            Switch(
                                checked = broken,
                                onCheckedChange = { model.reset(it) },
                                modifier = Modifier.testTag("broken-mode")
                            )
                        }
                        Text(if (loggedIn) "Mock 로그인 완료" else "Mock 로그아웃 상태")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("42", "99").forEach { id ->
                                Button(
                                    onClick = { model.open("https://shop.example.com/products/$id", false) },
                                    modifier = Modifier.testTag("link-$id")
                                ) { Text("상품 $id 링크") }
                            }
                        }
                        Row {
                            TextButton(onClick = { recreate() }) { Text("Activity 재생성") }
                            TextButton(onClick = { model.reset() }) { Text("초기화") }
                        }
                        TextButton(
                            onClick = { model.completeOldRequest() },
                            enabled = previous != null,
                            modifier = Modifier.testTag("old-completion")
                        ) { Text("이전 요청 완료 재현") }
                        Text("경로: " + state.stack.joinToString(" → ") { routeLabel(it) },
                            modifier = Modifier.testTag("stack"))
                        Text("대기: " + (state.pending?.targetStack?.lastOrNull()?.let(::routeLabel) ?: "없음"),
                            modifier = Modifier.testTag("pending"))
                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                        NavDisplay(
                            backStack = state.visibleStack,
                            modifier = Modifier.weight(1f),
                            onBack = { if (!model.back()) finish() },
                            entryProvider = { route ->
                                NavEntry(route) {
                                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                        when (route) {
                                            ProductList -> {
                                                Text("상품 목록", style = MaterialTheme.typography.headlineMedium)
                                                Text("링크 버튼이나 외부 Intent로 상품을 열어 보세요.")
                                            }
                                            is ProductDetail -> {
                                                Text("상품 ${route.id}", style = MaterialTheme.typography.headlineMedium,
                                                    modifier = Modifier.testTag("detail"))
                                                Button(onClick = { model.back() }) { Text("이전 화면") }
                                            }
                                            is Login -> {
                                                Text("로그인이 필요합니다", style = MaterialTheme.typography.headlineSmall)
                                                Button(onClick = { model.login(route.requestId) },
                                                    modifier = Modifier.testTag("login")) { Text("로그인 성공") }
                                                OutlinedButton(onClick = { model.back() },
                                                    modifier = Modifier.testTag("cancel")) { Text("취소") }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.dataString?.let { model.open(it, replacePath = false) }
    }
}

private fun routeLabel(route: Route): String = when (route) {
    ProductList -> "목록"
    is ProductDetail -> "상품 ${route.id}"
    is Login -> "로그인"
}
