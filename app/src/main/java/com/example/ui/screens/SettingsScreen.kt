package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.util.AppPermissionGroup
import com.example.util.PermissionRationaleModal
import com.example.util.PermanentlyDeniedDialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentGreen
import com.example.data.ContactEntity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border

@Composable
fun SettingsScreen(
    currentLang: String,
    onLanguageClick: () -> Unit,
    onProfileClick: () -> Unit,
    onAccountClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onBack: () -> Unit,
    onListsClick: () -> Unit = {},
    onChatsClick: () -> Unit = {},
    onAppearanceClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onStorageClick: () -> Unit = {},
    onInviteClick: () -> Unit = {},
    onRemindersClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .verticalScroll(scrollState)
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
                text = Strings.get("settings", currentLang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 1. Profile (Top Row)
        SettingsOptionRow(
            icon = Icons.Default.Person,
            title = Strings.get("profile_title", currentLang),
            subtitle = Strings.get("profile_subtitle", currentLang),
            onClick = onProfileClick
        )

        // 2. Account
        SettingsOptionRow(
            icon = Icons.Default.ManageAccounts,
            title = Strings.get("account_title", currentLang),
            subtitle = Strings.get("account_subtitle", currentLang),
            onClick = onAccountClick
        )

        // 2. Lists (WhatsApp-style categorizations)
        SettingsOptionRow(
            icon = Icons.Default.List,
            title = Strings.get("lists_title", currentLang),
            subtitle = Strings.get("lists_subtitle", currentLang),
            onClick = onListsClick
        )

        // 3. Chats
        SettingsOptionRow(
            icon = Icons.Default.Chat,
            title = Strings.get("chats_title", currentLang),
            subtitle = Strings.get("chats_subtitle", currentLang),
            onClick = onChatsClick
        )

        // 4. Appearance
        SettingsOptionRow(
            icon = Icons.Default.Palette,
            title = Strings.get("appearance_title", currentLang),
            subtitle = Strings.get("appearance_subtitle", currentLang),
            onClick = onAppearanceClick
        )

        // 5. Notifications
        SettingsOptionRow(
            icon = Icons.Default.Notifications,
            title = Strings.get("notifications_title", currentLang),
            subtitle = Strings.get("notifications_subtitle", currentLang),
            onClick = onNotificationsClick
        )

        // Reminder
        SettingsOptionRow(
            icon = Icons.Default.Schedule,
            title = Strings.get("my_reminders", currentLang),
            subtitle = "Schedule automated alerts & timezone reminders",
            onClick = onRemindersClick
        )

        // 6. Storage and Data
        SettingsOptionRow(
            icon = Icons.Default.Storage,
            title = Strings.get("storage_title", currentLang),
            subtitle = Strings.get("storage_subtitle", currentLang),
            onClick = onStorageClick
        )

        // 7. App Language
        SettingsOptionRow(
            icon = Icons.Default.Language,
            title = Strings.get("app_language", currentLang),
            subtitle = Strings.get("app_language_subtitle", currentLang),
            onClick = onLanguageClick
        )

        // 8. Help and Feedback
        SettingsOptionRow(
            icon = Icons.Default.Help,
            title = Strings.get("help_title", currentLang),
            subtitle = Strings.get("help_subtitle", currentLang),
            onClick = {
                Toast.makeText(context, "Support: support@smartfitwellness.app", Toast.LENGTH_SHORT).show()
            }
        )

        // 9. Invite a Friend
        SettingsOptionRow(
            icon = Icons.Default.Share,
            title = Strings.get("invite_title", currentLang),
            subtitle = Strings.get("invite_subtitle", currentLang),
            onClick = onInviteClick
        )

        // 10. App Updates
        SettingsOptionRow(
            icon = Icons.Default.SystemUpdate,
            title = Strings.get("updates_title", currentLang),
            subtitle = Strings.get("updates_subtitle", currentLang),
            onClick = {
                Toast.makeText(context, "SmartFit Wellness v3.4.1 is up to date!", Toast.LENGTH_SHORT).show()
            }
        )

        // 11. History
        SettingsOptionRow(
            icon = Icons.Default.History,
            title = Strings.get("history_title", currentLang),
            subtitle = Strings.get("history_subtitle", currentLang),
            onClick = onHistoryClick
        )
    }
}

@Composable
fun AccountSettingsScreen(
    userName: String,
    userEmail: String,
    phoneNumber: String,
    userCountry: String,
    initialLanguage: String,
    onProfileClick: () -> Unit,
    onAddAccountClick: () -> Unit,
    onSwitchAccountClick: () -> Unit,
    onDeleteAccountInfoClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onBack: () -> Unit,
    currentLang: String
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", currentLang), tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.get("account_management", currentLang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Row 1: Your Sign-in Information
        SettingsOptionRow(
            icon = Icons.Default.Person,
            title = Strings.get("your_signin_info", currentLang),
            subtitle = "$userName • $userEmail",
            onClick = onProfileClick
        )

        // Row 2: Add Account
        SettingsOptionRow(
            icon = Icons.Default.PersonAdd,
            title = Strings.get("add_account", currentLang),
            subtitle = Strings.get("add_account_desc", currentLang),
            onClick = onAddAccountClick
        )

        // Row 3: Switch Account
        SettingsOptionRow(
            icon = Icons.Default.SwapHoriz,
            title = Strings.get("switch_account", currentLang),
            subtitle = Strings.get("switch_account_desc", currentLang),
            onClick = onSwitchAccountClick
        )

        // Row 4: How to Delete My Account
        SettingsOptionRow(
            icon = Icons.Default.DeleteForever,
            title = Strings.get("delete_account", currentLang),
            subtitle = Strings.get("delete_account_desc", currentLang),
            onClick = onDeleteAccountInfoClick
        )

        // Row 5: Log Out
        SettingsOptionRow(
            icon = Icons.Default.Logout,
            title = Strings.get("log_out", currentLang),
            subtitle = Strings.get("logout_desc", currentLang),
            onClick = onLogoutClick
        )
    }
}

@Composable
fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = TextSilver, fontSize = 12.sp, maxLines = 1)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSilver)
    }
}

@Composable
fun ProfileScreen(
    userName: String,
    phoneNumber: String,
    country: String,
    userEmail: String,
    profileAvatar: String,
    onUpdateUserName: (String) -> Unit,
    onUpdateAvatar: (String) -> Unit,
    onBack: () -> Unit,
    currentLang: String
) {
    val context = LocalContext.current
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(userName) }
    var showPhotoOptionDialog by remember { mutableStateOf(false) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                if (inputStream != null) {
                    context.filesDir.listFiles()?.forEach { f ->
                        if (f.name.startsWith("profile_avatar_") && f.name.endsWith(".jpg")) {
                            f.delete()
                        }
                    }
                    val newFile = java.io.File(context.filesDir, "profile_avatar_${System.currentTimeMillis()}.jpg")
                    val outputStream = java.io.FileOutputStream(newFile)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    onUpdateAvatar(Uri.fromFile(newFile).toString())
                    Toast.makeText(context, Strings.get("user_profile_updated", currentLang), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error reading image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error saving profile picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(tempPhotoUri!!)
                if (inputStream != null) {
                    context.filesDir.listFiles()?.forEach { f ->
                        if (f.name.startsWith("profile_avatar_") && f.name.endsWith(".jpg")) {
                            f.delete()
                        }
                    }
                    val newFile = java.io.File(context.filesDir, "profile_avatar_${System.currentTimeMillis()}.jpg")
                    val outputStream = java.io.FileOutputStream(newFile)
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    onUpdateAvatar(Uri.fromFile(newFile).toString())
                    Toast.makeText(context, Strings.get("user_profile_updated", currentLang), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error saving photo from camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val takePhotoPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            try {
                context.filesDir.listFiles()?.forEach { f ->
                    if (f.name.startsWith("profile_avatar_") && f.name.endsWith(".jpg")) {
                        f.delete()
                    }
                }
                val newFile = java.io.File(context.filesDir, "profile_avatar_${System.currentTimeMillis()}.jpg")
                val outputStream = java.io.FileOutputStream(newFile)
                it.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
                outputStream.flush()
                outputStream.close()
                onUpdateAvatar(Uri.fromFile(newFile).toString())
                Toast.makeText(context, Strings.get("user_profile_updated", currentLang), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error saving profile photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val launchCamera = {
        try {
            val tempFile = java.io.File(context.cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
            tempPhotoUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            takePhotoPreviewLauncher.launch(null)
        }
    }

    var activeRationaleGroup by remember { mutableStateOf<AppPermissionGroup?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(context, "Camera permission is required to capture profile photo", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", currentLang), tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Strings.get("personal_profile", currentLang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large Centered Circular DP with Edit Camera Icon Overlay
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .clickable { showPhotoOptionDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (profileAvatar.isNotBlank()) {
                AsyncImage(
                    model = profileAvatar,
                    contentDescription = Strings.get("profile_title", currentLang),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = userName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifEmpty { "SR" },
                    color = TextWhite,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            // Camera Badge Overlay at Bottom-Right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00FF66))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take or Change Profile Picture",
                    tint = BgCharcoal,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (showPhotoOptionDialog) {
            AlertDialog(
                onDismissRequest = { showPhotoOptionDialog = false },
                title = {
                    Text(
                        text = "Profile Photo Options",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Select an option to update your wellness account photo:",
                            color = TextSilver,
                            fontSize = 13.sp
                        )

                        // Camera Option
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showPhotoOptionDialog = false
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        launchCamera()
                                    } else {
                                        activeRationaleGroup = AppPermissionGroup.CAMERA
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Take Photo",
                                    tint = Color(0xFF00FF66),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Take Photo with Camera",
                                        color = TextWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Capture a new photo using your camera",
                                        color = TextSilver,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Gallery Option
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showPhotoOptionDialog = false
                                    imagePickerLauncher.launch("image/*")
                                },
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Choose from Gallery",
                                    tint = Color(0xFF0088FF),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Choose from Gallery",
                                        color = TextWhite,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Select an existing image from your device",
                                        color = TextSilver,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Remove Photo Option
                        if (profileAvatar.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showPhotoOptionDialog = false
                                        context.filesDir.listFiles()?.forEach { f ->
                                            if (f.name.startsWith("profile_avatar_") && f.name.endsWith(".jpg")) {
                                                f.delete()
                                            }
                                        }
                                        onUpdateAvatar("")
                                        Toast.makeText(context, "Profile photo removed", Toast.LENGTH_SHORT).show()
                                    },
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Photo",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = "Remove Current Photo",
                                        color = Color(0xFFFF5252),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showPhotoOptionDialog = false }) {
                        Text(text = Strings.get("cancel", currentLang), color = TextSilver)
                    }
                },
                containerColor = BgCharcoal
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Name Modifier Row with Pencil Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = Strings.get("full_name", currentLang), color = TextSilver, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                if (isEditingName) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Color(0xFF00FF66),
                            unfocusedBorderColor = TextSilver
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(text = userName, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                if (isEditingName) {
                    if (nameInput.isNotBlank()) {
                        onUpdateUserName(nameInput)
                        Toast.makeText(context, Strings.get("user_profile_updated", currentLang), Toast.LENGTH_SHORT).show()
                    }
                }
                isEditingName = !isEditingName
            }) {
                Icon(
                    imageVector = if (isEditingName) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = "Edit Name",
                    tint = Color(0xFF00FF66)
                )
            }
        }

        // Read-only Phone Number Field
        ProfileInfoCard(label = Strings.get("phone", currentLang), value = phoneNumber, icon = Icons.Default.Phone)

        // Read-only Country Field
        ProfileInfoCard(label = Strings.get("country", currentLang), value = country, icon = Icons.Default.Public)

        // Read-only Email Field
        ProfileInfoCard(label = Strings.get("email", currentLang), value = userEmail, icon = Icons.Default.Email)

        activeRationaleGroup?.let { group ->
            PermissionRationaleModal(
                group = group,
                onGrantRequested = {
                    if (group == AppPermissionGroup.CAMERA) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onDismiss = { activeRationaleGroup = null }
            )
        }
    }
}

@Composable
fun ProfileInfoCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextSilver, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = TextSilver, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ListsSettingsScreen(
    currentLang: String,
    contacts: List<ContactEntity>,
    chatLists: Map<String, List<Long>>,
    onCreateList: (String) -> Unit,
    onDeleteList: (String) -> Unit,
    onAddContactToList: (String, Long) -> Unit,
    onRemoveContactFromList: (String, Long) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListInput by remember { mutableStateOf("") }
    var selectedListForManage by remember { mutableStateOf<String?>(null) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Custom Lists",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Text(
            text = "Create and manage custom categories (e.g., Favorites, Work, Family) to filter your chats on the home screen.",
            color = TextSilver,
            fontSize = 13.sp
        )

        Button(
            onClick = { showCreateDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create New List", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // List of Lists
        val listState = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(listState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatLists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No custom lists created yet. Tap above to create one!", color = TextSilver, fontSize = 14.sp)
                }
            } else {
                chatLists.forEach { (listName, contactIds) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedListForManage = listName }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(listName, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${contactIds.size} Chats associated", color = TextSilver, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onDeleteList(listName) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = "Manage", tint = TextSilver)
                            }
                        }
                    }
                }
            }
        }
    }

    // Create List Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Create New List", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newListInput,
                    onValueChange = { newListInput = it },
                    placeholder = { Text("e.g. Family, Gym, Work", color = TextSilver) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Color(0xFF00E676),
                        unfocusedBorderColor = TextSilver
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newListInput.isNotBlank()) {
                            onCreateList(newListInput.trim())
                            showCreateDialog = false
                            newListInput = ""
                            Toast.makeText(context, "List created successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Create", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Manage List Contacts Detail Dialog
    if (selectedListForManage != null) {
        val listName = selectedListForManage!!
        val associatedIds = chatLists[listName] ?: emptyList()
        val associatedContacts = contacts.filter { associatedIds.contains(it.id) }
        val nonAssociatedContacts = contacts.filter { !associatedIds.contains(it.id) }

        AlertDialog(
            onDismissRequest = { selectedListForManage = null },
            containerColor = SurfaceDark,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Manage: $listName", color = TextWhite, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { showAddContactDialog = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Contact", tint = Color(0xFF00E676))
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    Text("Chats in this list:", color = TextSilver, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    
                    if (associatedContacts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No chats in this list yet.", color = TextSilver, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(associatedContacts) { contact ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BgCharcoal)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(contact.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    IconButton(
                                        onClick = { onRemoveContactFromList(listName, contact.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.RemoveCircle, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedListForManage = null }) {
                    Text("Done", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            }
        )

        // Sub-dialog to add a contact to the list
        if (showAddContactDialog) {
            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                containerColor = BgCharcoal,
                title = { Text("Add Chat to $listName", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        if (nonAssociatedContacts.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("All chats are already added!", color = TextSilver, fontSize = 13.sp)
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(nonAssociatedContacts) { contact ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceDark)
                                            .clickable {
                                                onAddContactToList(listName, contact.id)
                                                showAddContactDialog = false
                                                Toast.makeText(context, "${contact.name} added to $listName", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(contact.name, color = TextWhite, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAddContactDialog = false }) {
                        Text("Cancel", color = TextSilver)
                    }
                }
            )
        }
    }
}

@Composable
fun ChatsSettingsScreen(
    currentLang: String,
    fontSizePreference: String,
    enterIsSend: Boolean,
    mediaVisibility: Boolean,
    backupLogs: List<String>,
    onSetFontSize: (String) -> Unit,
    onSetEnterIsSend: (Boolean) -> Unit,
    onSetMediaVisibility: (Boolean) -> Unit,
    onAddBackupLog: (String) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var isBackingUp by remember { mutableStateOf(false) }
    var backupProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(isBackingUp) {
        if (isBackingUp) {
            backupProgress = 0f
            while (backupProgress < 1f) {
                kotlinx.coroutines.delay(100)
                backupProgress += 0.05f
            }
            val formatter = java.text.SimpleDateFormat("MMM dd, yyyy, hh:mm a", java.util.Locale.getDefault())
            val timeStr = formatter.format(java.util.Date())
            val size = (10 + (Math.random() * 20)).toInt()
            val type = if (Math.random() > 0.5) "Cloud" else "Local"
            onAddBackupLog("$type: $timeStr ($size.8 MB)")
            isBackingUp = false
            Toast.makeText(context, "Backup completed successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Chats Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        // Section 1: Visuals
        Text("Visual Preferences", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Font Size Selector
                Column {
                    Text("Chat Font Size", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Scale size of message bubble text", color = TextSilver, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Small", "Medium", "Large").forEach { size ->
                            val isSelected = fontSizePreference == size
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF00E676).copy(alpha = 0.15f) else BgCharcoal)
                                    .border(2.dp, if (isSelected) Color(0xFF00E676) else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { onSetFontSize(size) }
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(size, color = if (isSelected) Color(0xFF00E676) else TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Keyboard & Media
        Text("Keyboard & Storage", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Enter-is-send toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enter is Send", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Enter key will send the typed message", color = TextSilver, fontSize = 12.sp)
                    }
                    Switch(
                        checked = enterIsSend,
                        onCheckedChange = onSetEnterIsSend,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.5f))
                    )
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Media visibility toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Media Visibility", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Show newly downloaded media in phone's gallery", color = TextSilver, fontSize = 12.sp)
                    }
                    Switch(
                        checked = mediaVisibility,
                        onCheckedChange = onSetMediaVisibility,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.5f))
                    )
                }
            }
        }

        // Section 3: Chat Backup
        Text("Backup Controls", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Chat History Backups", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Back up your messages and media to cloud storage", color = TextSilver, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF00E676))
                }

                if (isBackingUp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        LinearProgressIndicator(
                            progress = backupProgress,
                            color = Color(0xFF00E676),
                            trackColor = BgCharcoal,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Creating secure chat backup... ${(backupProgress * 100).toInt()}%", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { isBackingUp = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Backup Now", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Backup History Logs:", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                if (backupLogs.isEmpty()) {
                    Text("No local or cloud backups found.", color = TextSilver, fontSize = 12.sp)
                } else {
                    backupLogs.forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BgCharcoal)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (log.startsWith("Cloud")) Icons.Default.CloudUpload else Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = if (log.startsWith("Cloud")) AccentBlue else AccentGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(log, color = TextSilver, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppearanceSettingsScreen(
    currentLang: String,
    themeMode: String,
    onSetThemeMode: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Appearance Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Text("Choose application theme preference:", color = TextSilver, fontSize = 14.sp)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Light Mode", "Dark Mode", "System Default").forEach { theme ->
                    val isSelected = themeMode == theme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF00E676).copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { onSetThemeMode(theme) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (theme) {
                                    "Light Mode" -> Icons.Default.LightMode
                                    "Dark Mode" -> Icons.Default.DarkMode
                                    else -> Icons.Default.SettingsSuggest
                                },
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF00E676) else TextSilver,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(theme, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSetThemeMode(theme) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00E676), unselectedColor = TextSilver)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsSettingsScreen(
    currentLang: String,
    messageTone: String,
    callRingTone: String,
    vibrationEnabled: Boolean,
    reactionAlertsEnabled: Boolean,
    onSetMessageTone: (String) -> Unit,
    onSetCallRingTone: (String) -> Unit,
    onSetVibration: (Boolean) -> Unit,
    onSetReactionAlerts: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showMessageToneDialog by remember { mutableStateOf(false) }
    var showCallRingtoneDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Notifications Preferences",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Text("Configure alert sounds, ringtones, and feedback patterns:", color = TextSilver, fontSize = 14.sp)

        // Message Notification Preference Cards
        Text("Message Alerts", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Message Tone Picker Clickable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMessageToneDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Notification Message Tone", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(messageTone, color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = TextSilver)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Reaction alerts toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reaction Alerts", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Show notifications for reactions to messages you send", color = TextSilver, fontSize = 12.sp)
                    }
                    Switch(
                        checked = reactionAlertsEnabled,
                        onCheckedChange = onSetReactionAlerts,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.5f))
                    )
                }
            }
        }

        // Call Notifications Preference Cards
        Text("Call Alerts", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Call Ringtone Picker Clickable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCallRingtoneDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Incoming Call Ringtone", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(callRingTone, color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.RingVolume, contentDescription = null, tint = TextSilver)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Vibration toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vibrate on Calls & Messages", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Toggle vibration feedback for incoming alerts", color = TextSilver, fontSize = 12.sp)
                    }
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = onSetVibration,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.5f))
                    )
                }
            }
        }
    }

    // Message Tone dialog
    if (showMessageToneDialog) {
        val tones = listOf("Default", "Simple", "Joyful", "Melodic", "None")
        AlertDialog(
            onDismissRequest = { showMessageToneDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Select Message Tone", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tones.forEach { tone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (messageTone == tone) Color(0xFF00E676).copy(alpha = 0.1f) else Color.Transparent)
                                .clickable {
                                    onSetMessageTone(tone)
                                    showMessageToneDialog = false
                                    Toast.makeText(context, "Message Tone: $tone", Toast.LENGTH_SHORT).show()
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tone, color = TextWhite, fontSize = 15.sp)
                            if (messageTone == tone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676))
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Call Ringtone dialog
    if (showCallRingtoneDialog) {
        val ringtones = listOf("Standard", "Energize", "Calm", "Classic", "None")
        AlertDialog(
            onDismissRequest = { showCallRingtoneDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Select Call Ringtone", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ringtones.forEach { tone ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (callRingTone == tone) Color(0xFF00E676).copy(alpha = 0.1f) else Color.Transparent)
                                .clickable {
                                    onSetCallRingTone(tone)
                                    showCallRingtoneDialog = false
                                    Toast.makeText(context, "Call Ringtone: $tone", Toast.LENGTH_SHORT).show()
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tone, color = TextWhite, fontSize = 15.sp)
                            if (callRingTone == tone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF00E676))
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun StorageSettingsScreen(
    currentLang: String,
    voiceNotesSize: Long,
    photosSize: Long,
    pdfsSize: Long,
    docsSize: Long,
    mobileDataDownload: Set<String>,
    wifiDownload: Set<String>,
    onClearCache: () -> Unit,
    onUpdateMobileDataDownload: (Set<String>) -> Unit,
    onUpdateWifiDownload: (Set<String>) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showMobileDialog by remember { mutableStateOf(false) }
    var showWifiDialog by remember { mutableStateOf(false) }

    fun formatSize(bytes: Long): String {
        if (bytes == 0L) return "0 KB"
        val mbs = bytes.toDouble() / (1024 * 1024)
        return String.format("%.1f MB", mbs)
    }

    val totalSize = voiceNotesSize + photosSize + pdfsSize + docsSize

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Storage & Auto-Download",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        // Breakdown Card
        Text("Local Storage Breakdown", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Cached Media Size", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(formatSize(totalSize), color = Color(0xFF00E676), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Voice Notes
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Voice Notes", color = TextSilver, fontSize = 14.sp)
                    Text(formatSize(voiceNotesSize), color = TextWhite, fontSize = 14.sp)
                }

                // Photos
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Photos & Visuals", color = TextSilver, fontSize = 14.sp)
                    Text(formatSize(photosSize), color = TextWhite, fontSize = 14.sp)
                }

                // PDFs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PDF Workout Plans & Documents", color = TextSilver, fontSize = 14.sp)
                    Text(formatSize(pdfsSize), color = TextWhite, fontSize = 14.sp)
                }

                // Other Docs
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Other App Files", color = TextSilver, fontSize = 14.sp)
                    Text(formatSize(docsSize), color = TextWhite, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onClearCache()
                        Toast.makeText(context, "Storage cache cleared!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Storage Cache", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section: Media Auto-Download
        Text("Media Auto-Download Preferences", color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Mobile Data Download Parameters Clickable Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMobileDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("When Using Mobile Data", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (mobileDataDownload.isEmpty()) "No media" else mobileDataDownload.joinToString(", "),
                            color = Color(0xFF00E676),
                            fontSize = 13.sp
                        )
                    }
                    Icon(Icons.Default.SignalCellularAlt, contentDescription = null, tint = TextSilver)
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))

                // Wi-Fi Download Parameters Clickable Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWifiDialog = true }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("When Connected on Wi-Fi", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (wifiDownload.isEmpty()) "No media" else wifiDownload.joinToString(", "),
                            color = Color(0xFF00E676),
                            fontSize = 13.sp
                        )
                    }
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = TextSilver)
                }
            }
        }
    }

    // Mobile data dialog
    if (showMobileDialog) {
        val options = listOf("Photos", "Videos", "Documents", "Audio")
        AlertDialog(
            onDismissRequest = { showMobileDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Mobile Data Auto-Download", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { opt ->
                        val isSelected = mobileDataDownload.contains(opt)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val current = mobileDataDownload.toMutableSet()
                                    if (isSelected) current.remove(opt) else current.add(opt)
                                    onUpdateMobileDataDownload(current)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(opt, color = TextWhite, fontSize = 15.sp)
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    val current = mobileDataDownload.toMutableSet()
                                    if (isSelected) current.remove(opt) else current.add(opt)
                                    onUpdateMobileDataDownload(current)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E676))
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMobileDialog = false }) {
                    Text("OK", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Wi-Fi dialog
    if (showWifiDialog) {
        val options = listOf("Photos", "Videos", "Documents", "Audio")
        AlertDialog(
            onDismissRequest = { showWifiDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Wi-Fi Auto-Download", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    options.forEach { opt ->
                        val isSelected = wifiDownload.contains(opt)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val current = wifiDownload.toMutableSet()
                                    if (isSelected) current.remove(opt) else current.add(opt)
                                    onUpdateWifiDownload(current)
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(opt, color = TextWhite, fontSize = 15.sp)
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    val current = wifiDownload.toMutableSet()
                                    if (isSelected) current.remove(opt) else current.add(opt)
                                    onUpdateWifiDownload(current)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF00E676))
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWifiDialog = false }) {
                    Text("OK", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun InviteFriendScreen(
    currentLang: String,
    userName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val inviteLink = "https://smartfitwellness.app/invite/${userName.replace(" ", "").lowercase()}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Invite a Friend",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Share your exclusive wellness referral link and invite your contacts to join SmartFit!",
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Canvas stylized QR Code Box (Artistic Grid)
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(4.dp, Color(0xFF00E676), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(120.dp)) {
                val size = 120.dp.toPx()
                val cellSize = size / 8
                
                // Draw 8x8 stylized QR Code Grid (Custom stylized pattern!)
                for (row in 0 until 8) {
                    for (col in 0 until 8) {
                        val isMarker = (row < 3 && col < 3) || (row > 4 && col < 3) || (row < 3 && col > 4)
                        val isMockDot = (row + col) % 2 == 0 && (row * col % 3 != 1)
                        if (isMarker || isMockDot) {
                            drawRect(
                                color = if (isMarker) Color(0xFF00E676) else TextWhite,
                                topLeft = androidx.compose.ui.geometry.Offset(col * cellSize, row * cellSize),
                                size = androidx.compose.ui.geometry.Size(cellSize - 2, cellSize - 2)
                            )
                        }
                    }
                }
            }
        }

        Text("Your Unique QR Code", color = TextSilver, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Invite Link Card Clickable to Copy
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    try {
                        val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("SmartFit Invite Link", inviteLink)
                        clipboardManager.setPrimaryClip(clip)
                        Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Your Referral Invite Link", color = TextSilver, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(inviteLink, color = Color(0xFF00E676), fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = Color(0xFF00E676))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big System Share Intent Button
        Button(
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Hey! Join me on SmartFit Wellness to track our health, workout sessions, and chat together! Use my exclusive link: $inviteLink")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Invite via")
                try {
                    context.startActivity(shareIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Invite Friends Now", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

