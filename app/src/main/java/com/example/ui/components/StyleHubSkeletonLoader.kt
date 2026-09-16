package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.StyleHubDesignTokens

/**
 * Enterprise skeleton loader providing shimmer feedback during asynchronous data fetching.
 */
@Composable
fun StyleHubShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = StyleHubDesignTokens.RadiusSmall
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .clip(shape)
            .background(baseColor.copy(alpha = alpha))
    )
}

@Composable
fun StyleHubCardSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    StyleHubCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            StyleHubShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(18.dp)
            )
            StyleHubShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(14.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StyleHubShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(14.dp)
                )
                StyleHubShimmerBox(
                    modifier = Modifier
                        .width(60.dp)
                        .height(24.dp),
                    shape = StyleHubDesignTokens.RadiusSmall
                )
            }
        }
    }
}
