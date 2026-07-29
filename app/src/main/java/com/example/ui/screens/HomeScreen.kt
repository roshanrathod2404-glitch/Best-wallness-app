package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactEntity
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

@Composable
fun HomeScreen(
    contacts: List<ContactEntity>,
    currentLang: String,
    onContactClick: (ContactEntity) -> Unit,
    onTogglePin: (Long, Boolean) -> Unit,
    onDeleteContact: (Long) -> Unit,
    onBlockContact: (Long) -> Unit,
    onStartChatWithNumber: (String) -> Unit,
    chatLists: Map<String, List<Long>> = emptyMap()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterList by remember { mutableStateOf("All") }
    var selectedContactForLongPress by remember { mutableStateOf<ContactEntity?>(null) }
    var previewContactForDp by remember { mutableStateOf<ContactEntity?>(null) }
    val context = LocalContext.current

    val isHomeScreenOverlayActive = previewContactForDp != null || selectedContactForLongPress != null || searchQuery.isNotEmpty()
    BackHandler(enabled = isHomeScreenOverlayActive) {
        when {
            previewContactForDp != null -> previewContactForDp = null
            selectedContactForLongPress != null -> selectedContactForLongPress = null
            searchQuery.isNotEmpty() -> searchQuery = ""
        }
    }

    val digitsOnly = searchQuery.filter { it.isDigit() }
    val isPhoneNumberQuery = searchQuery.matches(Regex("^[0-9+\\s-]{3,}$"))
    val isFullLengthPhone = digitsOnly.length >= 10

    val filteredContacts = contacts.filter {
        val matchesQuery = if (isPhoneNumberQuery && isFullLengthPhone) {
            it.phoneNumber.contains(searchQuery.trim())
        } else if (!isPhoneNumberQuery) {
            it.name.contains(searchQuery, ignoreCase = true) || it.phoneNumber.contains(searchQuery)
        } else {
            // For short numeric input (< 10 digits), only search matching existing contacts without triggering unsaved number rules
            it.name.contains(searchQuery, ignoreCase = true) || it.phoneNumber.contains(searchQuery)
        }

        val matchesList = if (selectedFilterList == "All") {
            true
        } else {
            val contactIds = chatLists[selectedFilterList] ?: emptyList()
            contactIds.contains(it.id)
        }

        matchesQuery && matchesList
    }

    // Database / Server registration check simulation (e.g. numbers ending in '9' are not registered)
    val isRegisteredUser = isFullLengthPhone && (
        contacts.any { it.phoneNumber.filter { c -> c.isDigit() } == digitsOnly } || !digitsOnly.endsWith("9")
    )
    val isUnregisteredUser = isFullLengthPhone && filteredContacts.isEmpty() && !isRegisteredUser

    val showUnsavedNumberAction = isFullLengthPhone && filteredContacts.isEmpty() && isRegisteredUser

    // Sorting Priority Rule:
    // 1st Priority: Pinned Contacts (always at the top)
    // 2nd Priority: Unread/New Messages (stacked right below pinned ones)
    // 3rd Priority: Regular read chats
    val sortedContacts = filteredContacts.sortedWith(
        Comparator { c1, c2 ->
            // 1. Pinned
            val pinCompare = c2.isPinned.compareTo(c1.isPinned)
            if (pinCompare != 0) return@Comparator pinCompare

            // 2. Unread
            val unread1 = if (c1.unreadCount > 0) 1 else 0
            val unread2 = if (c2.unreadCount > 0) 1 else 0
            val unreadCompare = unread2.compareTo(unread1)
            if (unreadCompare != 0) return@Comparator unreadCompare

            // 3. Fallback id descending
            c2.id.compareTo(c1.id)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(SurfaceDark, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSilver, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = Strings.get("search_contacts", currentLang),
                            color = TextSilver,
                            fontSize = 14.sp
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(color = TextWhite, fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // WhatsApp-style Filter Pills
        val lists = listOf("All") + chatLists.keys.toList()
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(lists) { listName ->
                val isSelected = selectedFilterList == listName
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color(0xFF00E676) else SurfaceDark)
                        .clickable { selectedFilterList = listName }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = listName,
                        color = if (isSelected) Color.Black else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Contacts List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (showUnsavedNumberAction) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onStartChatWithNumber(searchQuery.trim())
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Start chat with ${searchQuery.trim()}",
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap to message unsaved phone number",
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            if (isUnregisteredUser) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "This number is not on SmartFit Wellness",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "This person is not using this app",
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            items(sortedContacts) { contact ->
                ContactItem(
                    contact = contact,
                    currentLang = currentLang,
                    onClick = { onContactClick(contact) },
                    onLongPress = { selectedContactForLongPress = contact },
                    onAvatarClick = { previewContactForDp = it }
                )
            }
        }
    }

    // DP Preview Dialog
    if (previewContactForDp != null) {
        val contact = previewContactForDp!!
        ProfilePicturePreviewDialog(
            contactName = contact.name,
            avatarUrl = contact.localOverrideAvatar,
            avatarInitials = contact.avatarInitials,
            currentLang = currentLang,
            onDismiss = { previewContactForDp = null },
            onMessageClick = {
                val c = previewContactForDp
                previewContactForDp = null
                if (c != null) onContactClick(c)
            }
        )
    }

    // Long-Press Contextual Action Dialog
    if (selectedContactForLongPress != null) {
        val contact = selectedContactForLongPress!!
        AlertDialog(
            onDismissRequest = { selectedContactForLongPress = null },
            containerColor = SurfaceDark,
            title = { Text(Strings.get("contact_options", currentLang) + ": ${contact.name}", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val newPinState = !contact.isPinned
                            onTogglePin(contact.id, newPinState)
                            selectedContactForLongPress = null
                            Toast.makeText(context, if (newPinState) "Pinned ${contact.name}" else "Unpinned ${contact.name}", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (contact.isPinned) Strings.get("unpin_contact", currentLang) else Strings.get("pin_contact", currentLang), color = TextWhite)
                    }

                    Button(
                        onClick = {
                            onDeleteContact(contact.id)
                            selectedContactForLongPress = null
                            Toast.makeText(context, "Deleted ${contact.name}", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Strings.get("delete_contact", currentLang), color = Color(0xFFFF6B6B))
                    }

                    Button(
                        onClick = {
                            onBlockContact(contact.id)
                            selectedContactForLongPress = null
                            Toast.makeText(context, "Blocked ${contact.name}", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Strings.get("block_contact", currentLang), color = Color(0xFFFF6B6B))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedContactForLongPress = null }) {
                    Text(Strings.get("cancel", currentLang), color = TextSilver)
                }
            }
        )
    }
}

@Composable
fun ProfilePicturePreviewDialog(
    contactName: String,
    avatarUrl: String,
    avatarInitials: String,
    currentLang: String,
    onDismiss: () -> Unit,
    onMessageClick: (() -> Unit)? = null,
    onInfoClick: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        confirmButton = {},
        text = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.82f))
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier
                        .widthIn(max = 330.dp)
                        .padding(20.dp)
                        .clickable(enabled = false, onClick = {}) // prevent dismiss click propagating
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = contactName,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = 14.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BgCharcoal),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = avatarUrl,
                                    contentDescription = "Enlarged Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF075E54)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = avatarInitials.ifBlank { contactName.take(2).uppercase() },
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 80.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (onMessageClick != null) {
                                IconButton(onClick = onMessageClick) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Message",
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            if (onInfoClick != null) {
                                IconButton(onClick = onInfoClick) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Info",
                                        tint = Color(0xFF25D366),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextSilver,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

fun formatLastMessagePreview(lastMessage: String, fileType: String = "", fileName: String = ""): String {
    val trimmed = lastMessage.trim()
    if (trimmed.isEmpty()) return "No messages yet"

    if (trimmed == "New connection established") return "New connection established"
    if (trimmed == "Direct chat started") return "Direct chat started"

    val lower = trimmed.lowercase()

    return when {
        fileType.equals("Photo", true) || fileType.equals("Image", true) || lower.contains("photo") || lower.contains("image") || lower.contains("📷") -> {
            if (trimmed.startsWith("📷")) trimmed else "📷 Photo"
        }
        fileType.equals("Video", true) || lower.contains("video") || lower.contains("🎥") -> {
            if (trimmed.startsWith("🎥")) trimmed else "🎥 Video"
        }
        fileType.equals("Voice", true) || fileType.equals("Voice Note", true) || fileType.equals("Audio", true) || lower.contains("voice message") || lower.contains("voice note") || lower.contains("audio") || lower.contains("🎤") -> {
            if (trimmed.startsWith("🎤")) trimmed else "🎤 Voice message"
        }
        fileType.equals("PDF", true) || lower.contains("pdf") || lower.endsWith(".pdf") -> {
            val name = fileName.ifBlank { "PDF Document" }
            if (trimmed.startsWith("📄")) trimmed else "📄 $name"
        }
        fileType.equals("Document", true) || fileType.equals("Doc", true) || lower.contains("document") || lower.endsWith(".docx") || lower.endsWith(".doc") -> {
            val name = fileName.ifBlank { "Document" }
            if (trimmed.startsWith("📄")) trimmed else "📄 $name"
        }
        fileType.equals("Location", true) || lower.contains("location") || lower.contains("📌") -> {
            if (trimmed.startsWith("📌")) trimmed else "📌 Location"
        }
        fileType.equals("File", true) || lower.contains("file") || lower.contains("📁") -> {
            val name = fileName.ifBlank { "File" }
            if (trimmed.startsWith("📁")) trimmed else "📁 $name"
        }
        else -> {
            val firstLine = trimmed.lines().firstOrNull { it.isNotBlank() }?.trim() ?: trimmed
            firstLine
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(
    contact: ContactEntity,
    currentLang: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onAvatarClick: (ContactEntity) -> Unit
) {
    val hasUnread = contact.unreadCount > 0
    val nameColor = if (hasUnread) Color(0xFF00FF66) else TextWhite
    val fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Medium

    val formattedMsg = remember(contact.lastMessage) {
        formatLastMessagePreview(contact.lastMessage)
    }

    val displayLastMessage = when (contact.lastMessage) {
        "New connection established" -> Strings.get("new_connection_established", currentLang)
        "Direct chat started" -> Strings.get("direct_chat_started", currentLang)
        else -> formattedMsg
    }

    val displayLastMessageTime = when (contact.lastMessageTime) {
        "Yesterday" -> Strings.get("yesterday", currentLang)
        "Just now" -> Strings.get("just_now", currentLang)
        else -> contact.lastMessageTime
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark.copy(alpha = 0.6f))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Initial
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceDark)
                .clickable { onAvatarClick(contact) },
            contentAlignment = Alignment.Center
        ) {
            if (contact.localOverrideAvatar.isNotBlank()) {
                AsyncImage(
                    model = contact.localOverrideAvatar,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = contact.avatarInitials,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = contact.name,
                        color = nameColor,
                        fontWeight = fontWeight,
                        fontSize = 16.sp
                    )
                    if (contact.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = displayLastMessageTime,
                    color = if (hasUnread) Color(0xFF00FF66) else TextSilver,
                    fontSize = 11.sp,
                    fontWeight = if (hasUnread) FontWeight.Bold else FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayLastMessage,
                    color = if (hasUnread) TextWhite else TextSilver,
                    fontWeight = if (hasUnread) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (hasUnread) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00FF66)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.unreadCount.toString(),
                            color = BgCharcoal,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
