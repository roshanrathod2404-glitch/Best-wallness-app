package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ContactEntity
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

val WhatsAppGreen = Color(0xFF25D366)
val WhatsAppDarkGreen = Color(0xFF075E54)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCommunityFlowScreen(
    contacts: List<ContactEntity>,
    onBack: () -> Unit,
    onCreateCommunity: (
        name: String,
        description: String,
        avatarUri: String,
        selectedGroupIds: List<Long>,
        newGroupNames: List<String>,
        onCreated: (ContactEntity) -> Unit
    ) -> Unit,
    onCommunityCreatedNavigate: (ContactEntity) -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) } // 1: Intro, 2: Setup, 3: Group Management

    var communityName by remember { mutableStateOf("") }
    var communityDescription by remember { mutableStateOf("") }
    var communityAvatarUri by remember { mutableStateOf<String?>("https://picsum.photos/seed/community_${System.currentTimeMillis()}/300/300") }

    val selectedGroupIds = remember { mutableStateListOf<Long>() }
    val newGroupNames = remember { mutableStateListOf<String>() }

    var showCreateNewGroupDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        when {
            showCreateNewGroupDialog -> showCreateNewGroupDialog = false
            currentStep > 1 -> currentStep -= 1
            else -> onBack()
        }
    }
    var showAddExistingGroupsDialog by remember { mutableStateOf(false) }
    var newGroupNameInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            communityAvatarUri = uri.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentStep) {
                            1 -> "New Community"
                            2 -> "Community Details"
                            3 -> "Add Groups"
                            else -> "New Community"
                        },
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1) {
                                currentStep--
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    if (currentStep == 2) {
                        TextButton(
                            onClick = {
                                if (communityName.isBlank()) {
                                    Toast.makeText(context, "Please enter a community name", Toast.LENGTH_SHORT).show()
                                } else {
                                    currentStep = 3
                                }
                            }
                        ) {
                            Text("Next", color = WhatsAppGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    } else if (currentStep == 3) {
                        IconButton(
                            enabled = !isSubmitting,
                            onClick = {
                                if (communityName.isBlank()) {
                                    Toast.makeText(context, "Community name cannot be empty", Toast.LENGTH_SHORT).show()
                                    return@IconButton
                                }
                                isSubmitting = true
                                onCreateCommunity(
                                    communityName.trim(),
                                    communityDescription.trim(),
                                    communityAvatarUri ?: "",
                                    selectedGroupIds.toList(),
                                    newGroupNames.toList()
                                ) { createdContact ->
                                    isSubmitting = false
                                    Toast.makeText(context, "Community '$communityName' created successfully!", Toast.LENGTH_LONG).show()
                                    onCommunityCreatedNavigate(createdContact)
                                }
                            }
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = WhatsAppGreen, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Done", tint = WhatsAppGreen)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCharcoal)
            )
        },
        containerColor = BgCharcoal
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentStep) {
                1 -> CommunityIntroScreen(
                    onGetStarted = { currentStep = 2 }
                )
                2 -> CommunitySetupScreen(
                    communityName = communityName,
                    onNameChange = { communityName = it },
                    communityDescription = communityDescription,
                    onDescriptionChange = { communityDescription = it },
                    avatarUri = communityAvatarUri,
                    onPickImage = { imagePickerLauncher.launch("image/*") },
                    onNext = {
                        if (communityName.isBlank()) {
                            Toast.makeText(context, "Please enter a community name", Toast.LENGTH_SHORT).show()
                        } else {
                            currentStep = 3
                        }
                    }
                )
                3 -> CommunityGroupManagementScreen(
                    communityName = communityName,
                    selectedGroupIds = selectedGroupIds,
                    newGroupNames = newGroupNames,
                    contacts = contacts,
                    onOpenCreateNewGroupDialog = { showCreateNewGroupDialog = true },
                    onOpenAddExistingGroupsDialog = { showAddExistingGroupsDialog = true },
                    onRemoveNewGroup = { newGroupNames.remove(it) },
                    onRemoveSelectedGroup = { selectedGroupIds.remove(it) },
                    isSubmitting = isSubmitting,
                    onCreateCommunityClick = {
                        if (communityName.isBlank()) {
                            Toast.makeText(context, "Community name cannot be empty", Toast.LENGTH_SHORT).show()
                            return@CommunityGroupManagementScreen
                        }
                        isSubmitting = true
                        onCreateCommunity(
                            communityName.trim(),
                            communityDescription.trim(),
                            communityAvatarUri ?: "",
                            selectedGroupIds.toList(),
                            newGroupNames.toList()
                        ) { createdContact ->
                            isSubmitting = false
                            Toast.makeText(context, "Community '$communityName' created!", Toast.LENGTH_LONG).show()
                            onCommunityCreatedNavigate(createdContact)
                        }
                    }
                )
            }
        }
    }

    // Dialog 1: Create New Sub-Group
    if (showCreateNewGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateNewGroupDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Create New Group", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter a name for the new group to add under $communityName:", color = TextSilver, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newGroupNameInput,
                        onValueChange = { newGroupNameInput = it },
                        placeholder = { Text("Group Name (e.g., Announcements, General)", color = TextSilver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = WhatsAppGreen,
                            unfocusedBorderColor = TextSilver
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newGroupNameInput.isNotBlank()) {
                            newGroupNames.add(newGroupNameInput.trim())
                            newGroupNameInput = ""
                            showCreateNewGroupDialog = false
                        } else {
                            Toast.makeText(context, "Group name cannot be blank", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Add Group", color = WhatsAppGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateNewGroupDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Dialog 2: Add Existing Groups Checklist
    if (showAddExistingGroupsDialog) {
        val existingGroups = contacts.filter { !it.isBlocked && !it.isCommunity }
        AlertDialog(
            onDismissRequest = { showAddExistingGroupsDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Add Existing Groups / Chats", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.heightIn(max = 350.dp)) {
                    Text("Select existing chats or groups you own/administer to merge under $communityName:", color = TextSilver, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (existingGroups.isEmpty()) {
                        Text("No existing contacts or groups found.", color = TextSilver, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        LazyColumn {
                            items(existingGroups) { contactItem ->
                                val isSelected = selectedGroupIds.contains(contactItem.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) {
                                                selectedGroupIds.remove(contactItem.id)
                                            } else {
                                                selectedGroupIds.add(contactItem.id)
                                            }
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedGroupIds.add(contactItem.id) else selectedGroupIds.remove(contactItem.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = WhatsAppGreen, checkmarkColor = BgCharcoal)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(WhatsAppDarkGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = contactItem.avatarInitials.ifBlank { contactItem.name.take(2).uppercase() },
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(contactItem.name, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                        Text(contactItem.phoneNumber, color = TextSilver, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddExistingGroupsDialog = false }) {
                    Text("Done", color = WhatsAppGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --------------------------------------------------
// STEP 1: Intro Onboarding
// --------------------------------------------------
@Composable
fun CommunityIntroScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Community Hero Graphic / Banner
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(WhatsAppDarkGreen.copy(alpha = 0.4f))
                    .border(3.dp, WhatsAppGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Diversity3,
                    contentDescription = "Communities Graphic",
                    tint = WhatsAppGreen,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Create a new community",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bring together related groups and send announcements. Now, your communities like neighborhoods, schools, or fitness clubs can have their own space.",
                color = TextSilver,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Key Highlights List
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureHighlightRow(
                    icon = Icons.Default.Groups,
                    title = "Organize related groups",
                    description = "Combine multiple groups under one single community hub."
                )
                FeatureHighlightRow(
                    icon = Icons.Default.VolumeUp,
                    title = "Automated Announcements",
                    description = "Broadcast important updates to all members via an official admin channel."
                )
                FeatureHighlightRow(
                    icon = Icons.Default.Lock,
                    title = "Admin Controls & Privacy",
                    description = "Control posting rights, manage members, and keep groups safe."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onGetStarted,
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Get Started",
                color = BgCharcoal,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun FeatureHighlightRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, color = TextSilver, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

// --------------------------------------------------
// STEP 2: Details Setup
// --------------------------------------------------
@Composable
fun CommunitySetupScreen(
    communityName: String,
    onNameChange: (String) -> Unit,
    communityDescription: String,
    onDescriptionChange: (String) -> Unit,
    avatarUri: String?,
    onPickImage: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Community Profile Picture Picker Frame
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .border(2.dp, WhatsAppGreen.copy(alpha = 0.6f), CircleShape)
                .clickable { onPickImage() },
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUri.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUri,
                    contentDescription = "Community Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = TextSilver,
                    modifier = Modifier.size(50.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(WhatsAppGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Image",
                    tint = BgCharcoal,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Tap camera icon to set Community Photo", color = TextSilver, fontSize = 11.sp)

        Spacer(modifier = Modifier.height(28.dp))

        // Community Name Field
        OutlinedTextField(
            value = communityName,
            onValueChange = { if (it.length <= 100) onNameChange(it) },
            label = { Text("Community Name (Required)", color = WhatsAppGreen) },
            placeholder = { Text("e.g. Fitness Club, Neighborhood Watch", color = TextSilver) },
            singleLine = true,
            trailingIcon = {
                Text(
                    text = "${100 - communityName.length}",
                    color = TextSilver,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = WhatsAppGreen,
                unfocusedBorderColor = TextSilver,
                focusedLabelColor = WhatsAppGreen,
                unfocusedLabelColor = TextSilver
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Community Description Field
        OutlinedTextField(
            value = communityDescription,
            onValueChange = { onDescriptionChange(it) },
            label = { Text("Community Description (Optional)", color = TextSilver) },
            placeholder = { Text("Describe the purpose, rules, or guidelines of this community...", color = TextSilver) },
            minLines = 3,
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedBorderColor = WhatsAppGreen,
                unfocusedBorderColor = TextSilver,
                focusedLabelColor = WhatsAppGreen,
                unfocusedLabelColor = TextSilver
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Next: Add Groups", color = BgCharcoal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// --------------------------------------------------
// STEP 3: Group Management & Announcements
// --------------------------------------------------
@Composable
fun CommunityGroupManagementScreen(
    communityName: String,
    selectedGroupIds: List<Long>,
    newGroupNames: List<String>,
    contacts: List<ContactEntity>,
    onOpenCreateNewGroupDialog: () -> Unit,
    onOpenAddExistingGroupsDialog: () -> Unit,
    onRemoveNewGroup: (String) -> Unit,
    onRemoveSelectedGroup: (Long) -> Unit,
    isSubmitting: Boolean,
    onCreateCommunityClick: () -> Unit
) {
    val selectedContacts = contacts.filter { selectedGroupIds.contains(it.id) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Automated Announcement Channel Banner Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, WhatsAppGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WhatsAppDarkGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📢", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$communityName Announcements",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Auto-created • Only admins can post announcements to all members.",
                            color = WhatsAppGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. Add Groups Section Header & Action Buttons
        item {
            Text("Group Management", color = TextSilver, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Create New Group
                OutlinedButton(
                    onClick = onOpenCreateNewGroupDialog,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WhatsAppGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create New Group", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Button 2: Add Existing Groups
                OutlinedButton(
                    onClick = onOpenAddExistingGroupsDialog,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextSilver),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Existing", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Linked Groups (${newGroupNames.size + selectedContacts.size})", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 3. New Groups list
        items(newGroupNames) { gName ->
            GroupRowItem(
                title = gName,
                subtitle = "New Sub-Group",
                avatarText = gName.take(2).uppercase(),
                isNew = true,
                onRemove = { onRemoveNewGroup(gName) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 4. Selected Existing Groups list
        items(selectedContacts) { contact ->
            GroupRowItem(
                title = contact.name,
                subtitle = contact.phoneNumber,
                avatarText = contact.avatarInitials.ifBlank { contact.name.take(2).uppercase() },
                isNew = false,
                onRemove = { onRemoveSelectedGroup(contact.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (newGroupNames.isEmpty() && selectedContacts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No additional groups added yet.\nYou can create new groups or add existing ones above.",
                        color = TextSilver,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 5. Final Create Button
        item {
            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onCreateCommunityClick,
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BgCharcoal)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = BgCharcoal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finish & Create Community", color = BgCharcoal, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GroupRowItem(
    title: String,
    subtitle: String,
    avatarText: String,
    isNew: Boolean,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isNew) WhatsAppGreen else WhatsAppDarkGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarText,
                    color = if (isNew) BgCharcoal else TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextSilver, fontSize = 11.sp)
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextSilver, modifier = Modifier.size(20.dp))
            }
        }
    }
}
