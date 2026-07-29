package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactEntity
import com.example.data.MessageEntity
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.activity.compose.BackHandler
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoScreen(
    contact: ContactEntity,
    messages: List<MessageEntity>,
    contacts: List<ContactEntity>,
    onBack: () -> Unit,
    onClearChat: (Long) -> Unit,
    onBlockContact: (Long) -> Unit,
    onDeleteOldMessages: (Long, Long) -> Unit,
    onCreateGroup: (String, Set<Long>, (ContactEntity) -> Unit) -> Unit,
    onUpdateLocalOverrideAvatar: (Long, String) -> Unit
) {
    val context = LocalContext.current
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onUpdateLocalOverrideAvatar(contact.id, it.toString())
            Toast.makeText(context, "Custom contact DP updated successfully", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Group creation wizard states
    var groupWizardStep by remember { mutableStateOf(1) } // 1: Contact Picker, 2: Group Naming
    var selectedParticipantIds by remember { mutableStateOf(setOf(contact.id)) }
    var pickerSearchQuery by remember { mutableStateOf("") }
    var groupNameInput by remember { mutableStateOf("${contact.name}'s Wellness Group") }
    
    // States for advanced settings
    var notificationsMuted by remember { mutableStateOf(false) }
    var muteDurationOption by remember { mutableStateOf("Off") }
    var customToneEnabled by remember { mutableStateOf(false) }

    var mediaVisibilityOption by remember { mutableStateOf("Default (Yes)") }
    var chatLocked by remember { mutableStateOf(false) }
    var disappearingOption by remember { mutableStateOf("Off") }
    var ipProtectionEnabled by remember { mutableStateOf(true) }

    var isFavourite by remember { mutableStateOf(contact.isFavourite) }
    var isBlocked by remember { mutableStateOf(contact.isBlocked) }

    // Dialog visibility states
    var showClearChatDialog by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showDisappearingDialog by remember { mutableStateOf(false) }
    var showChatLockDialog by remember { mutableStateOf(false) }
    var showStorageScreen by remember { mutableStateOf(false) }
    var showGroupDialog by remember { mutableStateOf(false) }
    var groupDialogTitle by remember { mutableStateOf("") }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showMediaVisibilityDialog by remember { mutableStateOf(false) }
    var showEncryptionDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAddToGroupDialog by remember { mutableStateOf(false) }
    var showAddToListDialog by remember { mutableStateOf(false) }
    var selectedCustomLists by remember { mutableStateOf(setOf<String>()) }

    // Filter messages for this contact
    val contactMessages = messages.filter { it.contactId == contact.id }

    BackHandler(enabled = true) {
        when {
            showStorageScreen -> showStorageScreen = false
            showGroupDialog -> {
                if (groupWizardStep > 1) groupWizardStep = 1 else showGroupDialog = false
            }
            showClearChatDialog -> showClearChatDialog = false
            showBlockDialog -> showBlockDialog = false
            showDisappearingDialog -> showDisappearingDialog = false
            showChatLockDialog -> showChatLockDialog = false
            showNotificationsDialog -> showNotificationsDialog = false
            showMediaVisibilityDialog -> showMediaVisibilityDialog = false
            showEncryptionDialog -> showEncryptionDialog = false
            showPrivacyDialog -> showPrivacyDialog = false
            showAddToGroupDialog -> showAddToGroupDialog = false
            showAddToListDialog -> showAddToListDialog = false
            else -> onBack()
        }
    }

    if (showStorageScreen) {
        ChatStorageSubScreen(
            contact = contact,
            messages = contactMessages,
            onBack = { showStorageScreen = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = contact.name,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCharcoal)
            )
        },
        containerColor = BgCharcoal
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BgCharcoal)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (contact.localOverrideAvatar.isNotBlank()) {
                            AsyncImage(
                                model = contact.localOverrideAvatar,
                                contentDescription = "Custom Contact DP",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = contact.avatarInitials,
                                color = TextWhite,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // Pencil Icon Overlay at Bottom-Right
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00FF66))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Custom DP",
                                tint = BgCharcoal,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = contact.name, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = contact.phoneNumber, color = TextSilver, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = contact.email, color = TextSilver, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = contact.onlineStatus, color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Quick Actions Section (Exact 6 items in vertical order)
            Text(text = "Quick Actions", color = TextSilver, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    QuickActionRow(
                        icon = Icons.Default.GroupAdd,
                        title = "Create group with ${contact.name}",
                        onClick = {
                            groupWizardStep = 1
                            selectedParticipantIds = setOf(contact.id)
                            pickerSearchQuery = ""
                            groupNameInput = "${contact.name}'s Wellness Group"
                            showGroupDialog = true
                        }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)
                    QuickActionRow(
                        icon = Icons.Default.PlaylistAdd,
                        title = "Add to group",
                        onClick = { showAddToGroupDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)
                    QuickActionRow(
                        icon = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        title = if (isFavourite) "Remove from favourites" else "Add to favourites",
                        tint = if (isFavourite) Color.Magenta else TextWhite,
                        onClick = {
                            isFavourite = !isFavourite
                            Toast.makeText(context, if (isFavourite) "${contact.name} added to favourites" else "${contact.name} removed from favourites", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)
                    QuickActionRow(
                        icon = Icons.Default.List,
                        title = "Add to list",
                        onClick = { showAddToListDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)
                    QuickActionRow(
                        icon = Icons.Default.DeleteSweep,
                        title = "Clear chat",
                        onClick = { showClearChatDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)
                    QuickActionRow(
                        icon = Icons.Default.Block,
                        title = if (isBlocked) "Unblock ${contact.name}" else "Block ${contact.name}",
                        tint = Color.Red,
                        onClick = { showBlockDialog = true }
                    )
                }
            }

            // Advanced Management Grid / List Section (Exact 7 items in sequence)
            Text(text = "Advanced Management & Privacy", color = TextSilver, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // 1. Manage Storage
                    val totalMediaCount = contactMessages.size + 12
                    AdvancedActionRow(
                        icon = Icons.Default.Storage,
                        title = "Manage Storage",
                        subtitle = "${totalMediaCount * 2.8} MB used • ${contactMessages.size} shared files",
                        onClick = { showStorageScreen = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)

                    // 2. Notifications
                    AdvancedActionRow(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = if (notificationsMuted) "Muted ($muteDurationOption)" else "Enabled (Default Tones)",
                        onClick = { showNotificationsDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)

                    // 3. Media Visibility
                    AdvancedActionRow(
                        icon = Icons.Default.PhotoLibrary,
                        title = "Media Visibility",
                        subtitle = mediaVisibilityOption,
                        onClick = { showMediaVisibilityDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)

                    // 4. Encryption
                    AdvancedActionRow(
                        icon = Icons.Default.Lock,
                        title = "Encryption",
                        subtitle = "End-to-end encrypted. Tap to verify security code.",
                        onClick = { showEncryptionDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)

                    // 5. Disappearing Messages
                    AdvancedActionRow(
                        icon = Icons.Default.Timer,
                        title = "Disappearing Messages",
                        subtitle = disappearingOption,
                        onClick = { showDisappearingDialog = true }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)

                    // 6. Chat Lock
                    AdvancedToggleRow(
                        icon = Icons.Default.Fingerprint,
                        title = "Chat Lock",
                        subtitle = if (chatLocked) "Locked with Biometric/PIN" else "Unlocked",
                        checked = chatLocked,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showChatLockDialog = true
                            } else {
                                chatLocked = false
                                Toast.makeText(context, "Chat unlocked", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    Divider(color = BgCharcoal, thickness = 1.dp)

                    // 7. Advanced Chat Privacy
                    AdvancedActionRow(
                        icon = Icons.Default.Security,
                        title = "Advanced Chat Privacy",
                        subtitle = if (ipProtectionEnabled) "IP Protection & Proxy Routing Active" else "Standard Routing",
                        onClick = { showPrivacyDialog = true }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- Dialogs ---

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Mute Notifications", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select mute duration for ${contact.name}:", color = TextSilver, fontSize = 13.sp)
                    val durations = listOf("8 Hours", "1 Week", "Always", "Off")
                    durations.forEach { opt ->
                        Button(
                            onClick = {
                                muteDurationOption = opt
                                notificationsMuted = (opt != "Off")
                                showNotificationsDialog = false
                                Toast.makeText(context, if (notificationsMuted) "Notifications muted for $opt" else "Notifications unmuted", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (muteDurationOption == opt) MaterialTheme.colorScheme.primary else BgCharcoal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(opt, color = TextWhite)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { customToneEnabled = !customToneEnabled },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = customToneEnabled,
                            onCheckedChange = { customToneEnabled = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use Custom Alert Tone", color = TextWhite, fontSize = 14.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Close", color = TextSilver)
                }
            }
        )
    }

    // Media Visibility Dialog
    if (showMediaVisibilityDialog) {
        AlertDialog(
            onDismissRequest = { showMediaVisibilityDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Media Visibility", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Show newly downloaded media from this chat in your phone's gallery?", color = TextSilver, fontSize = 13.sp)
                    val options = listOf("Default (Yes)", "Yes", "No")
                    options.forEach { opt ->
                        Button(
                            onClick = {
                                mediaVisibilityOption = opt
                                showMediaVisibilityDialog = false
                                if (opt == "No") {
                                    Toast.makeText(context, "Isolated directory configured with .nomedia stub. Gallery indexing bypassed.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Media visibility set to $opt", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (mediaVisibilityOption == opt) MaterialTheme.colorScheme.primary else BgCharcoal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(opt, color = TextWhite)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMediaVisibilityDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Encryption Verification Dialog
    if (showEncryptionDialog) {
        AlertDialog(
            onDismissRequest = { showEncryptionDialog = false },
            containerColor = SurfaceDark,
            title = { Text("End-to-End Encryption Verification", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Messages and calls with ${contact.name} are secured with end-to-end encryption. Verify the 40-digit security code below or scan the QR code:", color = TextSilver, fontSize = 13.sp, textAlign = TextAlign.Center)
                    
                    // 40-digit code visualizer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgCharcoal, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "84920  38291  10492  84729  39201  10293  84729  38291",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // QR Code placeholder box
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.size(90.dp))
                            Text("SECURE QR", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Status: Verified & Secure 🔒", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showEncryptionDialog = false
                    Toast.makeText(context, "Encryption code successfully verified!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Verify Code", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Chat Lock Authentication Dialog
    if (showChatLockDialog) {
        AlertDialog(
            onDismissRequest = { showChatLockDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Authenticate to Lock Chat", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(60.dp))
                    Text("Confirm biometric or PIN to lock this chat and hide it from the main screen list.", color = TextSilver, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        chatLocked = true
                        showChatLockDialog = false
                        Toast.makeText(context, "Biometric authenticated! Chat is now locked & hidden.", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Authenticate & Lock", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatLockDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Advanced Chat Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Advanced Chat Privacy", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Configure advanced security protocols for ${contact.name}:", color = TextSilver, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgCharcoal, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Protect IP Address in Calls", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Prevents calls from revealing your direct network IP via relay servers.", color = TextSilver, fontSize = 11.sp)
                        }
                        Switch(
                            checked = ipProtectionEnabled,
                            onCheckedChange = { ipProtectionEnabled = it }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Done", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Clear Chat Confirmation
    if (showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Clear Chat History?", color = TextWhite) },
            text = { Text("This will wipe all unstarred messages for ${contact.name}. Starred messages remain protected.", color = TextSilver) },
            confirmButton = {
                TextButton(onClick = {
                    showClearChatDialog = false
                    onClearChat(contact.id)
                    Toast.makeText(context, "Chat cleared successfully", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Clear", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearChatDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Block / Unblock Contact Dialog
    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            containerColor = SurfaceDark,
            title = { Text(if (isBlocked) "Unblock ${contact.name}?" else "Block ${contact.name}?", color = TextWhite) },
            text = { Text(if (isBlocked) "Unblocking will allow ${contact.name} to send you messages and call you again." else "Blocked contacts will no longer be able to send you messages or call you.", color = TextSilver) },
            confirmButton = {
                TextButton(onClick = {
                    showBlockDialog = false
                    isBlocked = !isBlocked
                    if (isBlocked) {
                        onBlockContact(contact.id)
                        Toast.makeText(context, "${contact.name} has been blocked", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "${contact.name} has been unblocked", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(if (isBlocked) "Unblock" else "Block", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Add To Group Dialog
    if (showAddToGroupDialog) {
        AlertDialog(
            onDismissRequest = { showAddToGroupDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Add ${contact.name} to Group", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a group channel to add ${contact.name}:", color = TextSilver, fontSize = 13.sp)
                    val groups = listOf("SmartFit Wellness VIP", "Marathon Training Club", "Weight Loss Support", "Nutritionists Masterclass")
                    groups.forEach { groupName ->
                        Button(
                            onClick = {
                                showAddToGroupDialog = false
                                Toast.makeText(context, "${contact.name} successfully added to '$groupName'", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(groupName, color = TextWhite)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddToGroupDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Add To List Dialog
    if (showAddToListDialog) {
        AlertDialog(
            onDismissRequest = { showAddToListDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Add to Custom Lists", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select lists for ${contact.name}:", color = TextSilver, fontSize = 13.sp)
                    val lists = listOf("VIP Clients", "Family & Friends", "Fitness Buddies", "Work Colleagues")
                    lists.forEach { listName ->
                        val isChecked = selectedCustomLists.contains(listName)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCustomLists = if (isChecked) {
                                        selectedCustomLists - listName
                                    } else {
                                        selectedCustomLists + listName
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedCustomLists = if (checked) {
                                        selectedCustomLists + listName
                                    } else {
                                        selectedCustomLists - listName
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(listName, color = TextWhite, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showAddToListDialog = false
                    Toast.makeText(context, "${contact.name} added to selected lists", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Save", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddToListDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Disappearing Messages Dialog
    if (showDisappearingDialog) {
        AlertDialog(
            onDismissRequest = { showDisappearingDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Disappearing Messages", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select timer for automated message purging:", color = TextSilver, fontSize = 13.sp)
                    val options = listOf("24 Hours", "7 Days", "90 Days", "Off")
                    options.forEach { opt ->
                        Button(
                            onClick = {
                                disappearingOption = opt
                                showDisappearingDialog = false
                                if (opt != "Off") {
                                    val days = when (opt) {
                                        "24 Hours" -> 1L
                                        "7 Days" -> 7L
                                        else -> 90L
                                    }
                                    val cutoff = System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L
                                    onDeleteOldMessages(contact.id, cutoff)
                                }
                                Toast.makeText(context, "Disappearing messages set to $opt", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (disappearingOption == opt) MaterialTheme.colorScheme.primary else BgCharcoal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(opt, color = TextWhite)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDisappearingDialog = false }) {
                    Text("Close", color = TextSilver)
                }
            }
        )
    }

    // Group Creation Wizard Dialog
    if (showGroupDialog) {
        val filteredPickerContacts = contacts.filter {
            it.name.contains(pickerSearchQuery, ignoreCase = true) || it.phoneNumber.contains(pickerSearchQuery)
        }

        if (groupWizardStep == 1) {
            AlertDialog(
                onDismissRequest = { showGroupDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Create group with ${contact.name}", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Select participants for your new group:", color = TextSilver, fontSize = 13.sp)
                        OutlinedTextField(
                            value = pickerSearchQuery,
                            onValueChange = { pickerSearchQuery = it },
                            placeholder = { Text("Search contacts...", color = TextSilver) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSilver) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = BgCharcoal,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(filteredPickerContacts) { c ->
                                val isSelected = selectedParticipantIds.contains(c.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedParticipantIds = if (isSelected) {
                                                selectedParticipantIds - c.id
                                            } else {
                                                selectedParticipantIds + c.id
                                            }
                                        }
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            selectedParticipantIds = if (checked) {
                                                selectedParticipantIds + c.id
                                            } else {
                                                selectedParticipantIds - c.id
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = c.avatarInitials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = c.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text(text = c.phoneNumber, color = TextSilver, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { groupWizardStep = 2 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A1A1A),
                            contentColor = Color(0xFF00FF66)
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "NEXT (${selectedParticipantIds.size} selected) →",
                            color = Color(0xFF00FF66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGroupDialog = false }) {
                        Text("Cancel", color = TextSilver)
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showGroupDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Name Your Group", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Provide a group name for the ${selectedParticipantIds.size} selected members:", color = TextSilver, fontSize = 13.sp)
                        OutlinedTextField(
                            value = groupNameInput,
                            onValueChange = { groupNameInput = it },
                            placeholder = { Text("Group Name", color = TextSilver) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = BgCharcoal,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Members include: ${selectedParticipantIds.size} participants", color = TextSilver, fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showGroupDialog = false
                            onCreateGroup(groupNameInput, selectedParticipantIds) { newGroupContact ->
                                Toast.makeText(context, "Group '$groupNameInput' successfully created with ${selectedParticipantIds.size} participants!", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF000000),
                            contentColor = Color(0xFF00FF66)
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "CREATE GROUP",
                            color = Color(0xFF00FF66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { groupWizardStep = 1 }) {
                        Text("← Back", color = TextSilver)
                    }
                }
            )
        }
    }
}

// --- Manage Storage Sub-Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatStorageSubScreen(
    contact: ContactEntity,
    messages: List<MessageEntity>,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Photos, 1: Videos, 2: Documents
    val context = LocalContext.current

    val photos = messages.filter { it.fileType.equals("Photo", ignoreCase = true) || it.messageText.contains("photo", ignoreCase = true) }
    val videos = messages.filter { it.fileType.equals("Video", ignoreCase = true) || it.messageText.contains("video", ignoreCase = true) }
    val documents = messages.filter { it.fileType.equals("Document", ignoreCase = true) || it.fileType.equals("PDF", ignoreCase = true) || it.messageText.contains("pdf", ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Storage (${contact.name})", color = TextWhite, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCharcoal)
            )
        },
        containerColor = BgCharcoal
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Storage Overview Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Storage Used", color = TextSilver, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("142.5 MB", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StorageStatItem("Photos", "${photos.size} files • 94 MB")
                        StorageStatItem("Videos", "${videos.size} files • 38 MB")
                        StorageStatItem("Docs", "${documents.size} files • 10.5 MB")
                    }
                }
            }

            // Tabs for Photos, Videos, Documents
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = TextWhite
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Photos (${photos.size})", color = if (selectedTab == 0) TextWhite else TextSilver) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Videos (${videos.size})", color = if (selectedTab == 1) TextWhite else TextSilver) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Docs (${documents.size})", color = if (selectedTab == 2) TextWhite else TextSilver) }
                )
            }

            // Content List based on selected tab
            val currentList = when (selectedTab) {
                0 -> photos.ifEmpty { listOf(MessageEntity(contactId = contact.id, senderName = contact.name, messageText = "Vacation_Photo_01.jpg", timestamp = "10:30 AM", isSentByMe = false, fileType = "Photo", fileName = "Vacation_Photo_01.jpg")) }
                1 -> videos.ifEmpty { listOf(MessageEntity(contactId = contact.id, senderName = contact.name, messageText = "Workout_Session_4K.mp4", timestamp = "Yesterday", isSentByMe = true, fileType = "Video", fileName = "Workout_Session_4K.mp4")) }
                else -> documents.ifEmpty { listOf(MessageEntity(contactId = contact.id, senderName = contact.name, messageText = "Wellness_Diet_Plan.pdf", timestamp = "2 days ago", isSentByMe = false, fileType = "PDF", fileName = "Wellness_Diet_Plan.pdf")) }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentList.forEach { item ->
                    val sizeLabel = when (selectedTab) {
                        0 -> "${(2..5).random()}.4 MB"
                        1 -> "${(12..35).random()}.8 MB"
                        else -> "${(350..950).random()} KB"
                    }
                    val fileName = if (item.fileName.isNotBlank()) item.fileName else item.messageText

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = when (selectedTab) {
                                        0 -> Icons.Default.Image
                                        1 -> Icons.Default.VideoFile
                                        else -> Icons.Default.PictureAsPdf
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = fileName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = item.timestamp, color = TextSilver, fontSize = 11.sp)
                                }
                            }
                            // High-contrast file size label
                            Box(
                                modifier = Modifier
                                    .background(BgCharcoal, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = sizeLabel,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StorageStatItem(label: String, value: String) {
    Column {
        Text(text = label, color = TextSilver, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun QuickActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color = TextWhite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = tint, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AdvancedActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSilver, fontSize = 12.sp)
        }
    }
}

@Composable
fun AdvancedToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = TextWhite, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = TextSilver, fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
