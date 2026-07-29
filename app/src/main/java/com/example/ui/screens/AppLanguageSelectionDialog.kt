package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

data class AppLanguageItem(
    val englishName: String,
    val nativeName: String,
    val flag: String
)

val SupportedLanguages = listOf(
    AppLanguageItem("English", "English", "🇬🇧"),
    AppLanguageItem("Hindi", "हिन्दी", "🇮🇳"),
    AppLanguageItem("Spanish", "Español", "🇪🇸"),
    AppLanguageItem("French", "Français", "🇫🇷"),
    AppLanguageItem("German", "Deutsch", "🇩🇪"),
    AppLanguageItem("Japanese", "日本語", "🇯🇵"),
    AppLanguageItem("Chinese", "中文 (简体)", "🇨🇳"),
    AppLanguageItem("Arabic", "العربية", "🇸🇦"),
    AppLanguageItem("Russian", "Русский", "🇷🇺"),
    AppLanguageItem("Portuguese", "Português", "🇵🇹"),
    AppLanguageItem("Italian", "Italiano", "🇮🇹"),
    AppLanguageItem("Korean", "한국어", "🇰🇷"),
    AppLanguageItem("Turkish", "Türkçe", "🇹🇷"),
    AppLanguageItem("Dutch", "Nederlands", "🇳🇱"),
    AppLanguageItem("Vietnamese", "Tiếng Việt", "🇻🇳"),
    AppLanguageItem("Polish", "Polski", "🇵🇱")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLanguageSelectionDialog(
    currentLang: String,
    onLanguageChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SupportedLanguages
        } else {
            SupportedLanguages.filter { item ->
                item.englishName.contains(searchQuery, ignoreCase = true) ||
                item.nativeName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = Strings.get("app_language", currentLang),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSilver)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = Strings.get("search_your_language", currentLang),
                            color = TextSilver,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = WhatsAppGreen)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSilver)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = WhatsAppGreen,
                        unfocusedBorderColor = TextSilver,
                        cursorColor = WhatsAppGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            Box(modifier = Modifier.heightIn(max = 360.dp, min = 180.dp)) {
                if (filteredLanguages.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No languages found matching '$searchQuery'",
                            color = TextSilver,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredLanguages) { langItem ->
                            val isSelected = currentLang.equals(langItem.englishName, ignoreCase = true)
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) WhatsAppDarkGreen.copy(alpha = 0.5f) else SurfaceDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onLanguageChange(langItem.englishName)
                                        LocalizationHelper.setAppLocale(langItem.englishName)
                                        onDismiss()
                                        Toast.makeText(
                                            context,
                                            "Language set to ${langItem.nativeName}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = langItem.flag, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = langItem.nativeName,
                                                color = if (isSelected) WhatsAppGreen else TextWhite,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 15.sp
                                            )
                                            if (!langItem.nativeName.equals(langItem.englishName, ignoreCase = true)) {
                                                Text(
                                                    text = langItem.englishName,
                                                    color = TextSilver,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = WhatsAppGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
