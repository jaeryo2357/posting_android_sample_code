package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink

@Composable
fun TextHyperLinkContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val annotatedText = buildAnnotatedString {
            append("블로그 ")

            withLink(
                LinkAnnotation.Url(
                    url = "https://jaeryo2357.tistory.com/",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = { annotation ->
                        if (annotation is LinkAnnotation.Url) {
                            Log.d("Clicked URL", annotation.url)
                        }
                    }
                )
            ) {
                append("방문하기")
            }
        }

        Text(
            text = annotatedText
        )
    }
}