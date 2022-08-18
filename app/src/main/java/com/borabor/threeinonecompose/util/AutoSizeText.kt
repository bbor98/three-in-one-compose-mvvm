package com.borabor.threeinonecompose.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit

@Composable
fun AutoSizeText(
    text: String,
    maxFontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    BoxWithConstraints(modifier = modifier) {
        var shrunkFontSize = maxFontSize

        val calculateIntrinsics = @Composable {
            ParagraphIntrinsics(
                text = text,
                style = TextStyle(
                    color = color,
                    fontSize = shrunkFontSize,
                ),
                density = LocalDensity.current,
                resourceLoader = LocalFontLoader.current
            )
        }

        var intrinsics = calculateIntrinsics()

        with(LocalDensity.current) {
            while (intrinsics.maxIntrinsicWidth > maxWidth.toPx()) {
                shrunkFontSize *= 0.9
                intrinsics = calculateIntrinsics()
            }
        }

        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = color,
            fontSize = shrunkFontSize,
            textAlign = TextAlign.End,
            maxLines = 1
        )
    }
}