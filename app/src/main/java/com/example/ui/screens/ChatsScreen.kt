package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactEntity
import com.example.data.HistoryEntity
import com.example.data.MessageEntity
import com.example.ui.theme.BgCharcoal
import com.example.util.AppPermissionGroup
import com.example.util.PermissionManager
import com.example.util.PermissionRationaleModal
import com.example.util.PermanentlyDeniedDialog
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.activity.compose.BackHandler
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.flow.Flow
import android.media.MediaRecorder
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.camera.view.PreviewView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.runtime.mutableIntStateOf
import android.net.Uri
import androidx.compose.ui.text.style.TextOverflow
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Build
import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.style.TextAlign

data class DeviceMediaItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val isVideo: Boolean
)

fun checkMediaPermissions(context: android.content.Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

fun fetchDeviceMedia(context: android.content.Context): List<DeviceMediaItem> {
    val mediaList = mutableListOf<DeviceMediaItem>()
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.MEDIA_TYPE
    )
    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )
    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC LIMIT 60"

    try {
        val queryUri = MediaStore.Files.getContentUri("external")
        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
            val mediaTypeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)

            if (idColumn != -1 && nameColumn != -1 && mimeColumn != -1 && mediaTypeColumn != -1) {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn) ?: "Media"
                    val mimeType = cursor.getString(mimeColumn) ?: "image/*"
                    val mediaType = cursor.getInt(mediaTypeColumn)
                    val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    val contentUri = if (isVideo) {
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    } else {
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    }
                    mediaList.add(DeviceMediaItem(id, contentUri, name, mimeType, isVideo))
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    if (mediaList.isEmpty()) {
        try {
            val imgProjection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
            )
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imgProjection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT 60"
            )?.use { cursor ->
                val idCol = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val mimeCol = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
                if (idCol != -1) {
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val name = if (nameCol != -1) cursor.getString(nameCol) ?: "Photo" else "Photo"
                        val mimeType = if (mimeCol != -1) cursor.getString(mimeCol) ?: "image/*" else "image/*"
                        val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        mediaList.add(DeviceMediaItem(id, contentUri, name, mimeType, isVideo = false))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return mediaList
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatsScreen(
    contact: ContactEntity?,
    messages: List<MessageEntity>,
    contacts: List<ContactEntity>,
    onBack: () -> Unit,
    onSendMessage: (String, String, String, String, String) -> Unit,
    onSendVoiceMessage: ((String, String, String, String, String) -> Unit)? = null,
    onToggleStar: (Long, Boolean) -> Unit,
    onClearChat: (Long) -> Unit,
    onDeleteOldMessages: (Long, Long) -> Unit,
    onGetHistoryForContact: (Long, String) -> Flow<List<HistoryEntity>>,
    onGetStarredMessages: (Long) -> Flow<List<MessageEntity>>,
    onBlockContact: (Long) -> Unit,
    onCreateGroup: (String, Set<Long>, (ContactEntity) -> Unit) -> Unit,
    onUpdateLocalOverrideAvatar: (Long, String) -> Unit,
    typingStatusMap: Map<Long, Boolean>,
    onUpdateTypingStatus: (Long, Boolean) -> Unit,
    onDeleteMessageForMe: ((Long) -> Unit)? = null,
    onDeleteMessageForEveryone: ((Long) -> Unit)? = null,
    onToggleMessageStarredByRecipient: ((Long, Boolean) -> Unit)? = null,
    deletionOutcomeFlow: kotlinx.coroutines.flow.SharedFlow<String>? = null,
    currentLang: String = "English"
) {
    val context = LocalContext.current
    var playingMessageId by remember { mutableStateOf<Long?>(null) }
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var currentPlaybackPosition by remember { mutableStateOf(0f) }
    var playbackDuration by remember { mutableStateOf(100f) }
    var fullScreenImageUri by remember { mutableStateOf<String?>(null) }
    var fullScreenVideoUri by remember { mutableStateOf<String?>(null) }
    var fullScreenDocUri by remember { mutableStateOf<Pair<String, String>?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(playingMessageId) {
        if (playingMessageId != null) {
            while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                try {
                    currentPlaybackPosition = mediaPlayer!!.currentPosition.toFloat()
                    playbackDuration = mediaPlayer!!.duration.coerceAtLeast(1).toFloat()
                } catch (e: Exception) {}
                kotlinx.coroutines.delay(100)
            }
        } else {
            currentPlaybackPosition = 0f
        }
    }
    var textInput by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var replyingMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var showChatInfo by remember { mutableStateOf(false) }
    
    var activeRationaleGroup by remember { mutableStateOf<AppPermissionGroup?>(null) }
    var activePermanentlyDeniedGroup by remember { mutableStateOf<AppPermissionGroup?>(null) }

    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Microphone permission required for voice notes", Toast.LENGTH_SHORT).show()
        }
    }
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    var showAttachmentMenu by remember { mutableStateOf(false) }
    var isSheetExpanded by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var showFullScreenCamera by remember { mutableStateOf(false) }

    val mediaPermissionsToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    var hasMediaPermission by remember {
        mutableStateOf(checkMediaPermissions(context))
    }

    var deviceMediaList by remember { mutableStateOf<List<DeviceMediaItem>>(emptyList()) }
    var isLoadingMedia by remember { mutableStateOf(false) }

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.any { it }
        hasMediaPermission = granted
        if (!granted) {
            Toast.makeText(context, "Storage permission is required to access device photos and videos.", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val isVideo = context.contentResolver.getType(uri)?.startsWith("video") == true
            val fileType = if (isVideo) "Video" else "Photo"
            val prefix = if (isVideo) "🎥 Video Attachment: " else "🖼️ Photo Attachment: "
            onSendMessage(
                "$prefix$uri",
                replyingMessage?.messageText ?: "",
                replyingMessage?.senderName ?: "",
                fileType,
                uri.toString()
            )
            replyingMessage = null
            showAttachmentMenu = false
            Toast.makeText(context, "Media attached", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(showAttachmentMenu, hasMediaPermission) {
        if (showAttachmentMenu) {
            hasMediaPermission = checkMediaPermissions(context)
            if (hasMediaPermission) {
                isLoadingMedia = true
                withContext(Dispatchers.IO) {
                    val media = fetchDeviceMedia(context)
                    withContext(Dispatchers.Main) {
                        deviceMediaList = media
                        isLoadingMedia = false
                    }
                }
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showFullScreenCamera = true
            showAttachmentMenu = false
        } else {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onSendMessage(
                "📄 Document Shared: $uri",
                replyingMessage?.messageText ?: "",
                replyingMessage?.senderName ?: "",
                "Document",
                uri.toString()
            )
            replyingMessage = null
            showAttachmentMenu = false
            Toast.makeText(context, "Document shared", Toast.LENGTH_SHORT).show()
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onSendMessage(
                "📑 PDF Shared: $uri",
                replyingMessage?.messageText ?: "",
                replyingMessage?.senderName ?: "",
                "PDF",
                uri.toString()
            )
            replyingMessage = null
            showAttachmentMenu = false
            Toast.makeText(context, "PDF shared", Toast.LENGTH_SHORT).show()
        }
    }

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri ->
        onSendMessage(
            "👤 Contact Shared: Coach Alex (Fitness Expert - +1 555-0199)",
            replyingMessage?.messageText ?: "",
            replyingMessage?.senderName ?: "",
            "Text",
            ""
        )
        replyingMessage = null
        showAttachmentMenu = false
        Toast.makeText(context, "Contact shared", Toast.LENGTH_SHORT).show()
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onSendMessage(
                "🎵 Audio Shared: $uri",
                replyingMessage?.messageText ?: "",
                replyingMessage?.senderName ?: "",
                "Document",
                uri.toString()
            )
            replyingMessage = null
            showAttachmentMenu = false
            Toast.makeText(context, "Audio shared", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(textInput) {
        if (textInput.isNotBlank()) {
            onUpdateTypingStatus(contact?.id ?: 0L, true)
            kotlinx.coroutines.delay(2000)
            onUpdateTypingStatus(contact?.id ?: 0L, false)
        } else {
            onUpdateTypingStatus(contact?.id ?: 0L, false)
        }
    }
    
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }
    
    var activeSubDialog by remember { mutableStateOf<String?>(null) } // "history_menu", "history_detail", "disappearing", "star_messages"
    var selectedHistoryCategory by remember { mutableStateOf<String?>(null) } // "Photo", "Video", "Document", "PDF"
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedMessageForStar by remember { mutableStateOf<MessageEntity?>(null) }
    var longPressedMessage by remember { mutableStateOf<MessageEntity?>(null) }
    var showDpPreviewModal by remember { mutableStateOf(false) }

    if (contact != null) {
        BackHandler(enabled = true) {
            when {
                showDpPreviewModal -> showDpPreviewModal = false
                showFullScreenCamera -> showFullScreenCamera = false
                showChatInfo -> showChatInfo = false
                longPressedMessage != null -> longPressedMessage = null
                activeSubDialog != null -> activeSubDialog = null
                isSearchActive -> {
                    isSearchActive = false
                    searchQuery = ""
                }
                showAttachmentMenu -> showAttachmentMenu = false
                showMenu -> showMenu = false
                else -> onBack()
            }
        }
    }

    if (contact == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgCharcoal),
            contentAlignment = Alignment.Center
        ) {
            Text("Select a contact from Home to start messaging", color = TextSilver, fontSize = 14.sp)
        }
        return
    }

    if (showDpPreviewModal) {
        ProfilePicturePreviewDialog(
            contactName = contact.name,
            avatarUrl = contact.localOverrideAvatar,
            avatarInitials = contact.avatarInitials,
            currentLang = currentLang,
            onDismiss = { showDpPreviewModal = false },
            onInfoClick = {
                showDpPreviewModal = false
                showChatInfo = true
            }
        )
    }

    if (showChatInfo) {
        ChatInfoScreen(
            contact = contact,
            messages = messages,
            contacts = contacts,
            onBack = { showChatInfo = false },
            onClearChat = onClearChat,
            onBlockContact = { contactId ->
                onBlockContact(contactId)
                showChatInfo = false
                onBack()
            },
            onDeleteOldMessages = onDeleteOldMessages,
            onCreateGroup = onCreateGroup,
            onUpdateLocalOverrideAvatar = onUpdateLocalOverrideAvatar
        )
        return
    }

    val displayedMessages = if (isSearchActive && searchQuery.isNotBlank()) {
        messages.filter { it.messageText.contains(searchQuery, ignoreCase = true) }
    } else {
        messages
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .imePadding()
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BgCharcoal)
                        .clickable { showDpPreviewModal = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (contact.localOverrideAvatar.isNotBlank()) {
                        AsyncImage(
                            model = contact.localOverrideAvatar,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(text = contact.avatarInitials, color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showChatInfo = true }
                ) {
                    Text(text = contact.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    val isTyping = typingStatusMap[contact.id] == true
                    val subText = if (isTyping) {
                        Strings.get("typing", currentLang)
                    } else {
                        when (contact.onlineStatus) {
                            "Online" -> Strings.get("online", currentLang)
                            "Offline" -> Strings.get("offline", currentLang)
                            else -> {
                                if (contact.onlineStatus.startsWith("Last seen", ignoreCase = true)) {
                                    val rest = contact.onlineStatus.substring("Last seen".length)
                                    Strings.get("last_seen", currentLang) + rest
                                } else {
                                    contact.onlineStatus
                                }
                            }
                        }
                    }
                    Text(
                        text = subText,
                        color = if (isTyping) Color(0xFF00FF66) else TextSilver,
                        fontSize = 11.sp
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextWhite)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceDark)
                ) {
                    // Exact order: History, Search, Mute Notifications, Clear Chat, Block Contact, Disappearing Messages, Star Messages
                    DropdownMenuItem(
                        text = { Text("History", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            activeSubDialog = "history_menu"
                        },
                        leadingIcon = { Icon(Icons.Default.History, contentDescription = null, tint = TextWhite) }
                    )
                    DropdownMenuItem(
                        text = { Text("Search", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            isSearchActive = true
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextWhite) }
                    )
                    DropdownMenuItem(
                        text = { Text("Mute Notifications", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            Toast.makeText(context, "Notifications muted for ${contact.name}", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = TextWhite) }
                    )
                    DropdownMenuItem(
                        text = { Text("Clear Chat", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            onClearChat(contact.id)
                            Toast.makeText(context, "Chat cleared (Starred messages protected)", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = TextWhite) }
                    )
                    DropdownMenuItem(
                        text = { Text("Block Contact", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            Toast.makeText(context, "${contact.name} blocked", Toast.LENGTH_SHORT).show()
                        },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = TextWhite) }
                    )
                    DropdownMenuItem(
                        text = { Text("Disappearing Messages", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            activeSubDialog = "disappearing"
                        },
                        leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null, tint = TextWhite) }
                    )
                    DropdownMenuItem(
                        text = { Text("Star Messages", color = TextWhite) },
                        onClick = {
                            showMenu = false
                            activeSubDialog = "star_messages"
                        },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = TextWhite) }
                    )
                }
            }
        }

        // Search Bar if active
        if (isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark.copy(alpha = 0.9f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search in chat...", color = TextSilver) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = TextSilver
                    ),
                    singleLine = true
                )
                IconButton(onClick = {
                    isSearchActive = false
                    searchQuery = ""
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Close Search", tint = TextWhite)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayedMessages) { message ->
                val isMe = message.isSentByMe
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                if (dragAmount > 30f) {
                                    replyingMessage = message
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        }
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                longPressedMessage = message
                            }
                        ),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .background(
                                color = if (isMe) Color.White.copy(alpha = 0.15f) else SurfaceDark,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            if (message.replyToText.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(6.dp)
                                ) {
                                    Column {
                                        Text(text = message.replyToSender, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = message.replyToText, color = TextSilver, fontSize = 11.sp, maxLines = 1)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            if (message.fileType == "Voice" || message.messageText.startsWith("🎙️")) {
                                val isPlayingThis = playingMessageId == message.id
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            if (isPlayingThis) {
                                                mediaPlayer?.pause()
                                                playingMessageId = null
                                            } else {
                                                mediaPlayer?.release()
                                                mediaPlayer = null
                                                try {
                                                    val filePath = message.fileName
                                                    val file = if (filePath.isNotBlank() && File(filePath).exists()) File(filePath) else null
                                                    val mp = if (file != null) {
                                                        android.media.MediaPlayer.create(context, android.net.Uri.fromFile(file))
                                                    } else {
                                                        val dummy = File(context.cacheDir, "dummy_voice.aac")
                                                        if (!dummy.exists()) dummy.writeBytes(ByteArray(50))
                                                        android.media.MediaPlayer.create(context, android.net.Uri.fromFile(dummy))
                                                    }
                                                    mp?.setOnCompletionListener {
                                                        playingMessageId = null
                                                        currentPlaybackPosition = 0f
                                                        it.release()
                                                        mediaPlayer = null
                                                    }
                                                    mp?.start()
                                                    mediaPlayer = mp
                                                    playingMessageId = message.id
                                                    playbackDuration = mp?.duration?.coerceAtLeast(1)?.toFloat() ?: 100f
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                    Toast.makeText(context, "Playback error", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlayingThis) "Pause" else "Play",
                                            tint = TextWhite
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Slider(
                                        value = if (isPlayingThis) currentPlaybackPosition else 0f,
                                        onValueChange = { newVal ->
                                            currentPlaybackPosition = newVal
                                            mediaPlayer?.seekTo(newVal.toInt())
                                        },
                                        valueRange = 0f..playbackDuration,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = message.messageText,
                                        color = TextWhite,
                                        fontSize = 12.sp
                                    )
                                    if (message.isStarred) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Starred",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            } else if (message.fileType == "Photo" || message.messageText.contains("Photo Shared:") || message.messageText.contains("Media Attachment:")) {
                                val photoUri = message.fileName.ifBlank {
                                    val text = message.messageText
                                    if (text.contains("Photo Shared: ")) {
                                        text.substringAfter("Photo Shared: ").trim()
                                    } else if (text.contains("Photo Attachment: ")) {
                                        text.substringAfter("Photo Attachment: ").trim()
                                    } else if (text.contains("Media Attachment: ")) {
                                        text.substringAfter("Media Attachment: ").trim()
                                    } else {
                                        text
                                    }
                                }
                                val isMockMedia = !photoUri.startsWith("content://") && !photoUri.startsWith("file://") && !photoUri.startsWith("http://") && !photoUri.startsWith("https://")
                                val imageModel: Any = if (isMockMedia) {
                                    "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=400&auto=format&fit=crop"
                                } else {
                                    photoUri
                                }

                                Column {
                                    AsyncImage(
                                        model = imageModel,
                                        contentDescription = "Shared Photo",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                fullScreenImageUri = photoUri
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                    if (message.messageText.isNotBlank() && !message.messageText.startsWith("📷") && !message.messageText.startsWith("🖼️")) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = message.messageText,
                                            color = TextWhite,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            } else if (message.fileType == "Video" || message.messageText.contains("Video Recorded:") || message.messageText.startsWith("🎥")) {
                                val videoUri = message.fileName.ifBlank {
                                    val text = message.messageText
                                    if (text.contains("Video Recorded: ")) {
                                        text.substringAfter("Video Recorded: ").trim()
                                    } else if (text.contains("Video Attachment: ")) {
                                        text.substringAfter("Video Attachment: ").trim()
                                    } else if (text.contains("Media Attachment: ")) {
                                        text.substringAfter("Media Attachment: ").trim()
                                    } else {
                                        text
                                    }
                                }
                                val isMockVideo = !videoUri.startsWith("content://") && !videoUri.startsWith("file://") && !videoUri.startsWith("http://") && !videoUri.startsWith("https://")
                                val videoModel: Any = if (isMockVideo) {
                                    "https://images.unsplash.com/photo-1518310383802-640c2de311b2?w=400&auto=format&fit=crop"
                                } else {
                                    videoUri
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .clickable {
                                            fullScreenVideoUri = videoUri
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = videoModel,
                                        contentDescription = "Video Thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.6f
                                    )
                                    Icon(
                                        imageVector = Icons.Default.PlayCircle,
                                        contentDescription = "Play Video",
                                        tint = Color.White,
                                        modifier = Modifier.size(50.dp)
                                    )
                                    Text(
                                        text = "Video Preview",
                                        color = TextWhite,
                                        fontSize = 11.sp,
                                        modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
                                    )
                                }
                            } else if (message.fileType == "Document" || message.fileType == "PDF" || message.messageText.contains("Document Shared:") || message.messageText.contains("PDF Shared:") || message.messageText.startsWith("📄") || message.messageText.startsWith("📑")) {
                                val fileUri = message.fileName.ifBlank {
                                    val text = message.messageText
                                    if (text.contains("Document Shared: ")) text.substringAfter("Document Shared: ").trim()
                                    else if (text.contains("PDF Shared: ")) text.substringAfter("PDF Shared: ").trim()
                                    else text
                                }
                                val docName = if (fileUri.contains("/")) fileUri.substringAfterLast("/") else "WorkoutPlan.pdf"
                                val openDocAction = {
                                    fullScreenDocUri = Pair(fileUri, docName)
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                            if (fileUri.startsWith("content://") || fileUri.startsWith("file://")) {
                                                val uri = Uri.parse(fileUri)
                                                val mime = context.contentResolver.getType(uri) ?: if (docName.endsWith(".pdf", ignoreCase = true)) "application/pdf" else "*/*"
                                                setDataAndType(uri, mime)
                                            } else {
                                                val dummyFile = File(context.cacheDir, docName.ifBlank { "document.pdf" })
                                                if (!dummyFile.exists()) {
                                                    dummyFile.writeText("Sample Document Content for $docName")
                                                }
                                                val authority = "${context.packageName}.fileprovider"
                                                val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, dummyFile)
                                                val mime = if (docName.endsWith(".pdf", ignoreCase = true)) "application/pdf" else "*/*"
                                                setDataAndType(uri, mime)
                                            }
                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Opening PDF preview", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Black.copy(alpha = 0.2f))
                                        .clickable { openDocAction() }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "File Icon",
                                        tint = Color(0xFF2196F3),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = docName,
                                            color = TextWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Tap to View PDF Document",
                                            color = TextSilver,
                                            fontSize = 10.sp
                                        )
                                    }
                                    IconButton(onClick = { openDocAction() }) {
                                        Icon(Icons.Default.Download, contentDescription = "Open Document", tint = TextWhite, modifier = Modifier.size(18.dp))
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = message.messageText,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (message.isStarred) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Starred",
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.timestamp,
                                color = TextSilver,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Reply Preview Box if active
        if (replyingMessage != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark.copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Replying to ${replyingMessage!!.senderName}", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = replyingMessage!!.messageText, color = TextSilver, fontSize = 11.sp, maxLines = 1)
                }
                IconButton(onClick = { replyingMessage = null }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel Reply", tint = TextWhite)
                }
            }
        }

        // Input bar
        if (contact?.isAdminOnlyPosting == true) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .navigationBarsPadding()
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF25D366), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Only community admins can send messages",
                        color = TextSilver,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .navigationBarsPadding()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            IconButton(
                onClick = { showAttachmentMenu = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = TextSilver)
            }
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedTextField(
                value = textInput,
                onValueChange = {
                    textInput = it
                    onUpdateTypingStatus(contact.id, true)
                },
                placeholder = { Text("Type a message...", color = TextSilver) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isRecording) Color.Red else SurfaceDark, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                val hasAudioPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (!hasAudioPermission) {
                                    recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                    Toast.makeText(context, "Microphone permission required for voice notes", Toast.LENGTH_SHORT).show()
                                    return@detectTapGestures
                                }

                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isRecording = true
                                val startTime = System.currentTimeMillis()
                                val audioFile = File(context.cacheDir, "voice_${startTime}.m4a")

                                try {
                                    val rec = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        MediaRecorder(context)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        MediaRecorder()
                                    }
                                    rec.setAudioSource(MediaRecorder.AudioSource.MIC)
                                    rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                    rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                    rec.setAudioSamplingRate(44100)
                                    rec.setAudioEncodingBitRate(128000)
                                    rec.setOutputFile(audioFile.absolutePath)
                                    rec.prepare()
                                    rec.start()
                                    mediaRecorder = rec
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                try {
                                    tryAwaitRelease()
                                } catch (e: Exception) {}

                                isRecording = false
                                val recordDurationSec = ((System.currentTimeMillis() - startTime) / 1000).coerceAtLeast(1)

                                try {
                                    mediaRecorder?.stop()
                                    mediaRecorder?.release()
                                    mediaRecorder = null
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                val formattedDuration = String.format("%d:%02d", recordDurationSec / 60, recordDurationSec % 60)
                                onSendMessage(
                                    "🎙️ Voice Note ($formattedDuration)",
                                    replyingMessage?.messageText ?: "",
                                    replyingMessage?.senderName ?: "",
                                    "Voice",
                                    audioFile.absolutePath
                                )
                                replyingMessage = null
                                Toast.makeText(context, "Voice note recorded and sent", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Message",
                    tint = if (isRecording) TextWhite else TextSilver
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(
                            textInput,
                            replyingMessage?.messageText ?: "",
                            replyingMessage?.senderName ?: "",
                            "Text",
                            ""
                        )
                        textInput = ""
                        replyingMessage = null
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(TextWhite, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = BgCharcoal)
            }
        }
        }
    }

    // Dialogs / Sub-screens
    // Observes backend/socket deletion events
    if (deletionOutcomeFlow != null) {
        LaunchedEffect(Unit) {
            deletionOutcomeFlow.collect { outcome ->
                Toast.makeText(context, outcome, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Dialogs / Sub-screens
    if (longPressedMessage != null) {
        val msg = longPressedMessage!!
        AlertDialog(
            onDismissRequest = { longPressedMessage = null },
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = Strings.get("message_options", currentLang),
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Preview of the message text or file type
                    Text(
                        text = when {
                            msg.fileType == "Voice" -> "🎙️ " + Strings.get("voice_note", currentLang)
                            msg.fileType == "Photo" -> "📷 " + Strings.get("shared_photo", currentLang)
                            msg.fileType == "Video" -> "🎥 " + Strings.get("video_preview", currentLang)
                            msg.fileType == "Document" -> "📄 " + Strings.get("document", currentLang)
                            msg.fileType == "PDF" -> "📑 " + Strings.get("pdf_document", currentLang)
                            else -> msg.messageText
                        },
                        color = TextSilver,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgCharcoal, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 1. Star / Unstar Option
                    Button(
                        onClick = {
                            onToggleStar(msg.id, !msg.isStarred)
                            longPressedMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (msg.isStarred) Color(0xFFFFD700) else TextWhite,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (msg.isStarred) Strings.get("unstar_message", currentLang) else Strings.get("star_message", currentLang),
                                color = TextWhite,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // 2. Delete for Me Option
                    Button(
                        onClick = {
                            onDeleteMessageForMe?.invoke(msg.id)
                            longPressedMessage = null
                            Toast.makeText(context, "Message deleted for me", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Strings.get("delete_for_me", currentLang),
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // 3. Delete for Everyone Option
                    Button(
                        onClick = {
                            onDeleteMessageForEveryone?.invoke(msg.id)
                            longPressedMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = Strings.get("delete_for_everyone", currentLang),
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // 4. Simulate Recipient Star (Only if sent by me)
                    if (msg.isSentByMe && onToggleMessageStarredByRecipient != null) {
                        val isRecStar = msg.isStarredByRecipient
                        Button(
                            onClick = {
                                onToggleMessageStarredByRecipient(msg.id, !isRecStar)
                                // We update local object so state is fresh in UI without re-long-pressing
                                longPressedMessage = msg.copy(isStarredByRecipient = !isRecStar)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (isRecStar) Color(0xFFFFD700) else TextSilver,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (isRecStar) {
                                        Strings.get("recipient_starred_yes", currentLang)
                                    } else {
                                        Strings.get("recipient_starred_no", currentLang)
                                    },
                                    color = TextWhite,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { longPressedMessage = null }) {
                    Text("Close", color = TextSilver, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (selectedMessageForStar != null) {
        val msg = selectedMessageForStar!!
        AlertDialog(
            onDismissRequest = { selectedMessageForStar = null },
            containerColor = SurfaceDark,
            title = { Text(if (msg.isStarred) "Unstar Message?" else "Star Message?", color = TextWhite) },
            text = { Text(msg.messageText, color = TextSilver) },
            confirmButton = {
                TextButton(onClick = {
                    onToggleStar(msg.id, !msg.isStarred)
                    selectedMessageForStar = null
                }) {
                    Text(if (msg.isStarred) "Unstar It" else "Star It", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMessageForStar = null }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    if (activeSubDialog == "history_menu") {
        AlertDialog(
            onDismissRequest = { activeSubDialog = null },
            containerColor = SurfaceDark,
            title = { Text("Contact Media History", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = listOf("Photo", "Video", "Document", "PDF")
                    categories.forEach { cat ->
                        Button(
                            onClick = {
                                selectedHistoryCategory = cat
                                activeSubDialog = "history_detail"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("$cat History", color = TextWhite)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeSubDialog = null }) {
                    Text("Close", color = TextSilver)
                }
            }
        )
    }

    if (activeSubDialog == "history_detail" && selectedHistoryCategory != null) {
        val historyList by onGetHistoryForContact(contact.id, selectedHistoryCategory!!).collectAsState(initial = emptyList())
        AlertDialog(
            onDismissRequest = { activeSubDialog = "history_menu"; selectedHistoryCategory = null },
            containerColor = SurfaceDark,
            title = { Text("$selectedHistoryCategory History for ${contact.name}", color = TextWhite) },
            text = {
                if (historyList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No $selectedHistoryCategory records found for this contact.", color = TextSilver, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(historyList) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BgCharcoal, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(text = item.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = item.subtitle, color = TextSilver, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Sender: ${item.senderName}", color = TextWhite.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Text(text = "${item.dateText} | ${item.timeText}", color = TextSilver, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeSubDialog = "history_menu"; selectedHistoryCategory = null }) {
                    Text("Back", color = TextWhite)
                }
            }
        )
    }

    if (activeSubDialog == "disappearing") {
        AlertDialog(
            onDismissRequest = { activeSubDialog = null },
            containerColor = SurfaceDark,
            title = { Text("Disappearing Messages", color = TextWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select timer to automatically delete messages older than duration (Starred messages remain protected):", color = TextSilver, fontSize = 13.sp)
                    Button(
                        onClick = {
                            val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
                            onDeleteOldMessages(contact.id, cutoff)
                            activeSubDialog = null
                            Toast.makeText(context, "Disappearing (24h) applied. Starred protected.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("24 Hours", color = TextWhite) }

                    Button(
                        onClick = {
                            val cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000L
                            onDeleteOldMessages(contact.id, cutoff)
                            activeSubDialog = null
                            Toast.makeText(context, "Disappearing (7 Days) applied. Starred protected.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("7 Days", color = TextWhite) }

                    Button(
                        onClick = {
                            activeSubDialog = null
                            Toast.makeText(context, "Disappearing Messages turned Off", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Off", color = TextWhite) }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeSubDialog = null }) {
                    Text("Close", color = TextSilver)
                }
            }
        )
    }

    if (activeSubDialog == "star_messages") {
        val starredList by onGetStarredMessages(contact.id).collectAsState(initial = emptyList())
        AlertDialog(
            onDismissRequest = { activeSubDialog = null },
            containerColor = SurfaceDark,
            title = { Text("Starred Messages (${contact.name})", color = TextWhite) },
            text = {
                if (starredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No starred messages for this contact.", color = TextSilver, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(starredList) { msg ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BgCharcoal, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(text = msg.messageText, color = TextWhite, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = msg.senderName, color = TextSilver, fontSize = 11.sp)
                                    Text(text = msg.timestamp, color = TextSilver, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeSubDialog = null }) {
                    Text("Close", color = TextWhite)
                }
            }
        )
    }

    if (showFullScreenCamera) {
        var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
        var cameraMode by remember { mutableStateOf("PHOTO") } // "PHOTO" or "VIDEO"
        var isRecordingVideo by remember { mutableStateOf(false) }
        var activeRecording by remember { mutableStateOf<Recording?>(null) }
        var recordingSeconds by remember { mutableIntStateOf(0) }

        val previewView = remember {
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        }
        val imageCapture = remember { ImageCapture.Builder().build() }
        val recorder = remember {
            Recorder.Builder()
                .setQualitySelector(QualitySelector.fromOrderedList(listOf(Quality.HIGHEST, Quality.FHD, Quality.HD, Quality.SD)))
                .build()
        }
        val videoCapture = remember { VideoCapture.withOutput(recorder) }
        val lifecycleOwner = LocalLifecycleOwner.current

        // Recording timer
        LaunchedEffect(isRecordingVideo) {
            if (isRecordingVideo) {
                recordingSeconds = 0
                while (isRecordingVideo) {
                    kotlinx.coroutines.delay(1000)
                    recordingSeconds++
                }
            }
        }

        LaunchedEffect(lensFacing, cameraMode, showFullScreenCamera) {
            if (showFullScreenCamera) {
                kotlinx.coroutines.delay(120)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        cameraProvider.unbindAll()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        if (cameraMode == "PHOTO") {
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } else {
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                videoCapture
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                try {
                    activeRecording?.stop()
                    val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                    cameraProvider.unbindAll()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            // Top Row Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (isRecordingVideo) {
                            activeRecording?.stop()
                            isRecordingVideo = false
                        }
                        showFullScreenCamera = false
                    },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Camera", tint = Color.White)
                }

                // Header status
                if (isRecordingVideo) {
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFE53935), RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val mins = recordingSeconds / 60
                        val secs = recordingSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    Text(
                        text = if (cameraMode == "PHOTO") "Photo Camera" else "Video Camera",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = { Toast.makeText(context, "Flash toggled", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
                }
            }

            // Bottom Column Controls (Mode Tabs + Shutter Row)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mode Selector Tabs (PHOTO / VIDEO)
                if (!isRecordingVideo) {
                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (cameraMode == "PHOTO") Color(0xFF00A884) else Color.Transparent)
                                .clickable { cameraMode = "PHOTO" }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "PHOTO",
                                color = if (cameraMode == "PHOTO") Color.White else Color.LightGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (cameraMode == "VIDEO") Color(0xFFE53935) else Color.Transparent)
                                .clickable { cameraMode = "VIDEO" }
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "VIDEO",
                                color = if (cameraMode == "VIDEO") Color.White else Color.LightGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Shutter Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flip Camera Button
                    if (!isRecordingVideo) {
                        IconButton(
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.FlipCameraAndroid,
                                contentDescription = "Switch Camera",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }

                    // Main Capture / Record Shutter Button
                    if (cameraMode == "PHOTO") {
                        // Photo Shutter Button
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .background(Color.White.copy(alpha = 0.3f), CircleShape)
                                .clickable {
                                    val file = File(context.cacheDir, "captured_${System.currentTimeMillis()}.jpg")
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                                    imageCapture.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                val savedUri = Uri.fromFile(file).toString()
                                                onSendMessage(
                                                    "📷 Photo Captured",
                                                    replyingMessage?.messageText ?: "",
                                                    replyingMessage?.senderName ?: "",
                                                    "Photo",
                                                    savedUri
                                                )
                                                replyingMessage = null
                                                showFullScreenCamera = false
                                                Toast.makeText(context, "Photo captured & sent", Toast.LENGTH_SHORT).show()
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                exception.printStackTrace()
                                                Toast.makeText(context, "Failed to capture photo", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    } else {
                        // Video Shutter Button (Record / Stop)
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(4.dp, if (isRecordingVideo) Color(0xFFE53935) else Color.White, CircleShape)
                                .background(if (isRecordingVideo) Color(0xFFE53935).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable {
                                    if (isRecordingVideo) {
                                        // Stop Recording
                                        activeRecording?.stop()
                                        activeRecording = null
                                        isRecordingVideo = false
                                    } else {
                                        // Start Recording
                                        val videoFile = File(context.cacheDir, "recorded_${System.currentTimeMillis()}.mp4")
                                        val outputOptions = FileOutputOptions.Builder(videoFile).build()
                                        var pendingRecording = recorder.prepareRecording(context, outputOptions)

                                        val hasAudioPerm = ContextCompat.checkSelfPermission(
                                            context,
                                            android.Manifest.permission.RECORD_AUDIO
                                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                        if (hasAudioPerm) {
                                            pendingRecording = pendingRecording.withAudioEnabled()
                                        } else {
                                            recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                        }

                                        activeRecording = pendingRecording.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                            when (recordEvent) {
                                                is VideoRecordEvent.Start -> {
                                                    isRecordingVideo = true
                                                }
                                                is VideoRecordEvent.Finalize -> {
                                                    isRecordingVideo = false
                                                    activeRecording = null
                                                    if (!recordEvent.hasError()) {
                                                        val savedUri = Uri.fromFile(videoFile).toString()
                                                        onSendMessage(
                                                            "🎥 Video Recorded",
                                                            replyingMessage?.messageText ?: "",
                                                            replyingMessage?.senderName ?: "",
                                                            "Video",
                                                            savedUri
                                                        )
                                                        replyingMessage = null
                                                        showFullScreenCamera = false
                                                        Toast.makeText(context, "Video recorded & sent", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        recordEvent.cause?.printStackTrace()
                                                        Toast.makeText(context, "Video recording finished", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isRecordingVideo) {
                                // Stop square icon
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE53935))
                                )
                            } else {
                                // Record red circle
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE53935))
                                )
                            }
                        }
                    }

                    // Spacer or Secondary Action Button
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    }

    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    if (showAttachmentMenu) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { showAttachmentMenu = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .let { if (isSheetExpanded) it.fillMaxHeight(0.85f) else it.wrapContentHeight() }
                    .clickable(enabled = false) {}
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onVerticalDrag = { _, dragAmount ->
                                if (dragAmount < -15) {
                                    isSheetExpanded = true
                                } else if (dragAmount > 15) {
                                    isSheetExpanded = false
                                }
                            }
                        )
                    },
                color = SurfaceDark,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .let { if (isSheetExpanded) it.fillMaxHeight() else it.wrapContentHeight() }
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TextSilver.copy(alpha = 0.4f))
                            .align(Alignment.CenterHorizontally)
                            .clickable { isSheetExpanded = !isSheetExpanded }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.get("share_content_media", currentLang),
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { isSheetExpanded = !isSheetExpanded }) {
                            Icon(
                                imageVector = if (isSheetExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = "Expand/Collapse",
                                tint = TextSilver
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val gridModifier = if (isSheetExpanded) {
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    }

                    if (!hasMediaPermission) {
                        Box(
                            modifier = gridModifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(BgCharcoal)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PermMedia,
                                    contentDescription = null,
                                    tint = Color(0xFF00FF66),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Storage Permission Required",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Grant storage access to display and send your photos and videos.",
                                    color = TextSilver,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        mediaPermissionLauncher.launch(mediaPermissionsToRequest)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Grant Storage Access", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    } else if (isLoadingMedia) {
                        Box(
                            modifier = gridModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00FF66), strokeWidth = 3.dp)
                        }
                    } else if (deviceMediaList.isEmpty()) {
                        Box(
                            modifier = gridModifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(BgCharcoal)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = TextSilver,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Photos or Videos Found",
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No recent photos or videos found on device. Browse system files directly:",
                                    color = TextSilver,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        galleryPickerLauncher.launch("image/* video/*")
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Browse Gallery Files", color = TextWhite, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                            modifier = gridModifier,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val totalCount = deviceMediaList.size + 1
                            items(totalCount) { index ->
                                if (index == 0) {
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1A1A24))
                                            .clickable {
                                                if (hasCameraPermission) {
                                                    showFullScreenCamera = true
                                                    showAttachmentMenu = false
                                                } else {
                                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (hasCameraPermission && !showFullScreenCamera) {
                                            val inlinePreviewView = remember {
                                                PreviewView(context).apply {
                                                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                                }
                                            }
                                            LaunchedEffect(inlinePreviewView, showFullScreenCamera) {
                                                if (!showFullScreenCamera) {
                                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                                                    cameraProviderFuture.addListener({
                                                        try {
                                                            val cameraProvider = cameraProviderFuture.get()
                                                            cameraProvider.unbindAll()
                                                            val preview = Preview.Builder().build().also {
                                                                it.setSurfaceProvider(inlinePreviewView.surfaceProvider)
                                                            }
                                                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                                            cameraProvider.bindToLifecycle(
                                                                lifecycleOwner,
                                                                cameraSelector,
                                                                preview
                                                            )
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }, ContextCompat.getMainExecutor(context))
                                                }
                                            }

                                            DisposableEffect(showFullScreenCamera) {
                                                onDispose {
                                                    if (!showFullScreenCamera) {
                                                        try {
                                                            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                                                            cameraProvider.unbindAll()
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                }
                                            }

                                            AndroidView(
                                                factory = { inlinePreviewView },
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.BottomStart
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFFFF3333))
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(Strings.get("live", currentLang), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color(0xFF00FF66), modifier = Modifier.size(28.dp))
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(Strings.get("camera", currentLang), color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else {
                                    val mediaItem = deviceMediaList[index - 1]
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(BgCharcoal)
                                            .clickable {
                                                val msgType = if (mediaItem.isVideo) "Video" else "Photo"
                                                val prefix = if (mediaItem.isVideo) "🎥 Video Attachment: " else "🖼️ Photo Attachment: "
                                                onSendMessage(
                                                    "$prefix${mediaItem.name}",
                                                    replyingMessage?.messageText ?: "",
                                                    replyingMessage?.senderName ?: "",
                                                    msgType,
                                                    mediaItem.uri.toString()
                                                )
                                                replyingMessage = null
                                                showAttachmentMenu = false
                                                Toast.makeText(context, "Sent ${mediaItem.name}", Toast.LENGTH_SHORT).show()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = mediaItem.uri,
                                            contentDescription = mediaItem.name,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        if (mediaItem.isVideo) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.35f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayCircle,
                                                    contentDescription = "Video",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = TextSilver.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    val attachmentOptions = listOf(
                        Triple("Gallery", Icons.Default.PhotoLibrary, { galleryPickerLauncher.launch("image/* video/*") }),
                        Triple("File", Icons.Default.InsertDriveFile, { documentPickerLauncher.launch("*/*") }),
                        Triple("PDF", Icons.Default.PictureAsPdf, { pdfPickerLauncher.launch("application/pdf") }),
                        Triple("Contact", Icons.Default.Person, { contactPickerLauncher.launch(null) }),
                        Triple("Music", Icons.Default.AudioFile, { audioPickerLauncher.launch("audio/*") })
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        attachmentOptions.forEach { (title, icon, action) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { action() }
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2A2A3C)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = Color(0xFF00FF66),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = title,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (fullScreenImageUri != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { fullScreenImageUri = null },
            contentAlignment = Alignment.Center
        ) {
            val isMockMedia = !fullScreenImageUri!!.startsWith("content://") && !fullScreenImageUri!!.startsWith("file://")
            val imageModel: Any = if (isMockMedia) {
                "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&auto=format&fit=crop"
            } else {
                fullScreenImageUri!!
            }
            AsyncImage(
                model = imageModel,
                contentDescription = "Full Screen Photo",
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = { fullScreenImageUri = null },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close View", tint = Color.White)
            }
        }
    }

    if (fullScreenVideoUri != null) {
        val videoUri = fullScreenVideoUri!!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BgCharcoal),
                    contentAlignment = Alignment.Center
                ) {
                    val isMock = !videoUri.startsWith("content://") && !videoUri.startsWith("file://") && !videoUri.startsWith("http")
                    val thumbnail: Any = if (isMock) {
                        "https://images.unsplash.com/photo-1518310383802-640c2de311b2?w=800&auto=format&fit=crop"
                    } else {
                        videoUri
                    }
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.7f
                    )
                    IconButton(
                        onClick = {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    val uri = if (videoUri.startsWith("content://") || videoUri.startsWith("file://")) {
                                        Uri.parse(videoUri)
                                    } else {
                                        val dummyFile = File(context.cacheDir, "shared_video.mp4")
                                        if (!dummyFile.exists()) dummyFile.writeBytes(ByteArray(100))
                                        androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", dummyFile)
                                    }
                                    setDataAndType(uri, "video/*")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Playing video in media player", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFF00FF66), CircleShape)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Video", tint = Color.Black, modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Sent Video File", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(videoUri.substringAfterLast("/"), color = TextSilver, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                val uri = if (videoUri.startsWith("content://") || videoUri.startsWith("file://")) {
                                    Uri.parse(videoUri)
                                } else {
                                    val dummyFile = File(context.cacheDir, "shared_video.mp4")
                                    if (!dummyFile.exists()) dummyFile.writeBytes(ByteArray(100))
                                    androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", dummyFile)
                                }
                                setDataAndType(uri, "video/*")
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No video player application found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66)),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open in Video Player", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            IconButton(
                onClick = { fullScreenVideoUri = null },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close Video", tint = Color.White)
            }
        }
    }

    if (fullScreenDocUri != null) {
        val (docUri, docName) = fullScreenDocUri!!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BgCharcoal)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Icon",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = docName,
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "PDF Document Viewer",
                                color = TextSilver,
                                fontSize = 12.sp
                            )
                        }
                    }
                    IconButton(onClick = { fullScreenDocUri = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close PDF", tint = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E28))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("DOCUMENT PREVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text("Page 1 of 3", fontSize = 10.sp, color = Color.Gray)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                                Text(
                                    text = docName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "• Executive Summary & Fitness Metrics\n• Workout Schedule & Macro Breakdown\n• Progress Milestones & Recommendations",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("PDF Document Verified", color = Color(0xFF00FF66), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    val uri = if (docUri.startsWith("content://") || docUri.startsWith("file://")) {
                                        Uri.parse(docUri)
                                    } else {
                                        val dummyFile = File(context.cacheDir, docName.ifBlank { "document.pdf" })
                                        if (!dummyFile.exists()) dummyFile.writeText("Sample Document Content for $docName")
                                        androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", dummyFile)
                                    }
                                    val mimeType = if (docName.endsWith(".pdf", true) || docUri.endsWith(".pdf", true)) "application/pdf" else "*/*"
                                    setDataAndType(uri, mimeType)
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF66)),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Open PDF Reader", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { fullScreenDocUri = null },
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close", color = TextWhite, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Permission Rationale Modal
    activeRationaleGroup?.let { group ->
        PermissionRationaleModal(
            group = group,
            onGrantRequested = {
                when (group) {
                    AppPermissionGroup.CAMERA -> cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    AppPermissionGroup.RECORD_AUDIO -> recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                    AppPermissionGroup.GALLERY_AND_MEDIA -> mediaPermissionLauncher.launch(mediaPermissionsToRequest)
                    AppPermissionGroup.CONTACTS -> contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
                    else -> {}
                }
            },
            onDismiss = { activeRationaleGroup = null }
        )
    }

    // Permanently Denied Dialog
    activePermanentlyDeniedGroup?.let { group ->
        PermanentlyDeniedDialog(
            group = group,
            onDismiss = { activePermanentlyDeniedGroup = null }
        )
    }
}
