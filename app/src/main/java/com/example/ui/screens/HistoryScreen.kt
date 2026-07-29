package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HistoryEntity
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.flow.Flow

data class HistoryOption(
    val title: String,
    val subtitle: String,
    val typeKey: String,
    val icon: ImageVector
)

@Composable
fun HistoryScreen(
    onGetHistory: (String) -> Flow<List<HistoryEntity>>,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) } // null = menu list, or "Contact", "Photo", "Video", "Document", "PDF"

    if (selectedCategory == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgCharcoal)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "History Vault",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            val historyOptions = listOf(
                HistoryOption("Contact History", "View past contacts and interactions", "Contact", Icons.Default.Contacts),
                HistoryOption("Video History", "View video playback and call logs", "Video", Icons.Default.Videocam),
                HistoryOption("Document History", "View shared text documents and notes", "Document", Icons.Default.Description),
                HistoryOption("Photo History", "View uploaded and received photos", "Photo", Icons.Default.Image),
                HistoryOption("PDF History", "View downloaded and shared PDF records", "PDF", Icons.Default.PictureAsPdf)
            )

            historyOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .clickable { selectedCategory = option.typeKey }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(option.icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = option.title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(text = option.subtitle, color = TextSilver, fontSize = 12.sp)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSilver)
                }
            }
        }
    } else {
        val historyList by onGetHistory(selectedCategory!!).collectAsState(initial = emptyList())
        val categoryTitle = when(selectedCategory) {
            "Contact" -> "Contact History"
            "Photo" -> "Photo History"
            "Video" -> "Video History"
            "Document" -> "Document History"
            "PDF" -> "PDF History"
            else -> "History"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgCharcoal)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedCategory = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = categoryTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No records found in this history category.", color = TextSilver, fontSize = 14.sp)
                }
            } else if (selectedCategory == "Photo" || selectedCategory == "Video") {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceDark)
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (selectedCategory == "Photo") Icons.Default.Image else Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = TextWhite,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = item.senderName,
                                        color = TextSilver,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = item.title,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${item.dateText} • ${item.timeText}",
                                        color = TextSilver,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historyList) { item ->
                        val icon = when(selectedCategory) {
                            "PDF" -> Icons.Default.PictureAsPdf
                            "Document" -> Icons.Default.Description
                            else -> Icons.Default.Contacts
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceDark)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.subtitle,
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "From: ${item.senderName}",
                                        color = TextWhite.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${item.dateText} | ${item.timeText}",
                                        color = TextSilver,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
