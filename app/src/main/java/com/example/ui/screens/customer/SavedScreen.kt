package com.example.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entities.BusinessEntity
import com.example.ui.components.BusinessCard
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.GoldPrimary

@Composable
fun SavedScreen(
    savedBusinesses: List<BusinessEntity>,
    onViewBusiness: (Long) -> Unit,
    onBookBusiness: (BusinessEntity) -> Unit,
    onToggleSave: (Long) -> Unit,
    onExplore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("saved_screen")
    ) {
        if (savedBusinesses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "No saved businesses yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Tap the heart icon on any barber, braider or hair salon to save them here for quick booking.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onExplore,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = CharcoalDark)
                    ) {
                        Text("Explore Grooming Studios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Saved Favourites (${savedBusinesses.size})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                items(savedBusinesses) { biz ->
                    BusinessCard(
                        business = biz,
                        onClick = { onViewBusiness(biz.id) },
                        onBookClick = { onBookBusiness(biz) },
                        isSaved = true,
                        onToggleSave = { onToggleSave(biz.id) }
                    )
                }
            }
        }
    }
}
