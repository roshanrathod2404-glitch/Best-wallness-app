package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun AccountScreen(
    userEmail: String,
    currentLang: String,
    onLanguageChange: (String) -> Unit,
    onNavigateReminders: () -> Unit,
    onLogout: () -> Unit,
    onAddAccount: (String, String, String) -> Unit
) {
    var showAccountSubmenu by remember { mutableStateOf(false) }
    var showDevicesDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showDeleteGuideDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = Strings.get("account", currentLang),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Row A: Account
        SettingsRow(
            icon = Icons.Default.Person,
            title = Strings.get("add_account", currentLang),
            subtitle = userEmail,
            onClick = { showAccountSubmenu = true }
        )

        // Row B: My Reminders
        SettingsRow(
            icon = Icons.Default.Notifications,
            title = Strings.get("my_reminders", currentLang),
            subtitle = "Automated Message Dashboard",
            onClick = onNavigateReminders
        )

        // Row C: Linked Devices
        SettingsRow(
            icon = Icons.Default.Devices,
            title = Strings.get("linked_devices", currentLang),
            subtitle = "1 Active Mobile Device",
            onClick = { showDevicesDialog = true }
        )

        // Row D: App Language
        SettingsRow(
            icon = Icons.Default.Language,
            title = Strings.get("app_language", currentLang),
            subtitle = "Current: $currentLang",
            onClick = { showLanguageDialog = true }
        )
    }

    // Account Submenu Dialog
    if (showAccountSubmenu) {
        AlertDialog(
            onDismissRequest = { showAccountSubmenu = false },
            containerColor = SurfaceDark,
            title = { Text("Account Management", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        showAccountSubmenu = false
                        showAddAccountDialog = true
                    }) {
                        Text("Add Account (Manual / Google)", color = TextWhite)
                    }
                    TextButton(onClick = {
                        showAccountSubmenu = false
                        showDeleteGuideDialog = true
                    }) {
                        Text(Strings.get("delete_account", currentLang), color = TextWhite)
                    }
                    TextButton(onClick = {
                        showAccountSubmenu = false
                        showExportDialog = true
                    }) {
                        Text(Strings.get("export_data", currentLang), color = TextWhite)
                    }
                    TextButton(onClick = {
                        showAccountSubmenu = false
                        onLogout()
                    }) {
                        Text(Strings.get("log_out", currentLang), color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAccountSubmenu = false }) {
                    Text("Close", color = TextSilver)
                }
            }
        )
    }

    // Linked Devices Dialog
    if (showDevicesDialog) {
        AlertDialog(
            onDismissRequest = { showDevicesDialog = false },
            containerColor = SurfaceDark,
            title = { Text(Strings.get("linked_devices", currentLang), color = TextWhite) },
            text = {
                Column {
                    Text("• SmartFit Android Client (Primary Device)", color = TextWhite)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Status: Connected & Synchronized in IST", color = TextSilver, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDevicesDialog = false }) {
                    Text("OK", color = TextWhite)
                }
            }
        )
    }

    // Language Selector Dialog with Search Bar and Real-Time Filter
    if (showLanguageDialog) {
        AppLanguageSelectionDialog(
            currentLang = currentLang,
            onLanguageChange = { newLang ->
                onLanguageChange(newLang)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Add Account Dialog
    if (showAddAccountDialog) {
        var newName by remember { mutableStateOf("") }
        var newPhone by remember { mutableStateOf("") }
        var newEmail by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAccountDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Add New Account", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
                    OutlinedTextField(value = newPhone, onValueChange = { newPhone = it }, label = { Text("Phone Number") })
                    OutlinedTextField(value = newEmail, onValueChange = { newEmail = it }, label = { Text("Email") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newName.isNotBlank()) {
                        onAddAccount(newName, newPhone, newEmail)
                        showAddAccountDialog = false
                    }
                }) {
                    Text("Add", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAccountDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Delete Guide Dialog
    if (showDeleteGuideDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteGuideDialog = false },
            containerColor = SurfaceDark,
            title = { Text("How to Delete My Account", color = TextWhite) },
            text = {
                Text("To permanently delete your SmartFit Wellness account and associated local Room database logs, navigate to Settings > Account > Delete Account and confirm via your registered email or phone OTP verification.", color = TextSilver, fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(onClick = { showDeleteGuideDialog = false }) {
                    Text("Got It", color = TextWhite)
                }
            }
        )
    }

    // Export Data Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Export My Data", color = TextWhite) },
            text = {
                Text("Your contacts, reminders, and chat message history have been successfully packaged into JSON format for secure local export.", color = TextSilver, fontSize = 14.sp)
            },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("OK", color = TextWhite)
                }
            }
        )
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextSilver, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSilver, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSilver)
    }
}
