package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.data.ContactEntity
import com.example.data.HistoryEntity
import com.example.data.MessageEntity
import com.example.data.ReminderEntity
import kotlinx.coroutines.flow.Flow
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import com.example.viewmodel.SmartFitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHubScreen(
    contacts: List<ContactEntity>,
    reminders: List<ReminderEntity>,
    userName: String = "Sangeeta Rathod",
    userEmail: String,
    phoneNumber: String = "+91 9876543210",
    userCountry: String = "India",
    initialLanguage: String = "English",
    profileAvatar: String = "",
    currentLang: String,
    onLanguageChange: (String) -> Unit,
    onLogout: () -> Unit,
    onAddReminder: (String, Long, String, Int, Int, Boolean, String, String, String) -> Unit,
    onUpdateReminderStatus: (ReminderEntity, String) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit,
    onAddContact: (String, String, String) -> Unit,
    onUpdateUserName: (String) -> Unit,
    onUpdateAvatar: (String) -> Unit,
    onUpdateLocalOverrideAvatar: (Long, String) -> Unit,
    selectedContactForChat: ContactEntity?,
    onSelectContactForChat: (ContactEntity?) -> Unit,
    messagesForSelectedContact: List<MessageEntity>,
    onSendMessage: (String, String, String, String, String) -> Unit,
    onGetHistory: (String) -> Flow<List<HistoryEntity>>,
    onToggleStar: (Long, Boolean) -> Unit,
    onClearChat: (Long) -> Unit,
    onDeleteOldMessages: (Long, Long) -> Unit,
    onGetHistoryForContact: (Long, String) -> Flow<List<HistoryEntity>>,
    onGetStarredMessages: (Long) -> Flow<List<MessageEntity>>,
    onTogglePin: (Long, Boolean) -> Unit,
    onDeleteContact: (Long) -> Unit,
    onBlockContact: (Long) -> Unit,
    onStartChatWithNumber: (String) -> Unit,
    onCreateGroup: (String, Set<Long>, (ContactEntity) -> Unit) -> Unit,
    typingStatusMap: Map<Long, Boolean>,
    onUpdateTypingStatus: (Long, Boolean) -> Unit,
    viewModel: SmartFitViewModel? = null
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Home, 1: Chats, 2: SmartFit, 3: Settings
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showProfileScreen by remember { mutableStateOf(false) }
    var showAccountSettingsScreen by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showSwitchAccountDialog by remember { mutableStateOf(false) }
    var showDeleteAccountInfoDialog by remember { mutableStateOf(false) }
    var showHistoryScreen by remember { mutableStateOf(false) }
    var showNewCommunityFlow by remember { mutableStateOf(false) }

    var showListsSettingsScreen by remember { mutableStateOf(false) }
    var showChatsSettingsScreen by remember { mutableStateOf(false) }
    var showAppearanceSettingsScreen by remember { mutableStateOf(false) }
    var showNotificationsSettingsScreen by remember { mutableStateOf(false) }
    var showStorageSettingsScreen by remember { mutableStateOf(false) }
    var showInviteFriendScreen by remember { mutableStateOf(false) }
    var showRemindersModuleScreen by remember { mutableStateOf(false) }

    // Settings collected states from ViewModel
    val chatLists by (viewModel?.chatLists ?: remember { kotlinx.coroutines.flow.MutableStateFlow(emptyMap()) }).collectAsState()
    val fontSizePreference by (viewModel?.fontSizePreference ?: remember { kotlinx.coroutines.flow.MutableStateFlow("Medium") }).collectAsState()
    val enterIsSend by (viewModel?.enterIsSend ?: remember { kotlinx.coroutines.flow.MutableStateFlow(true) }).collectAsState()
    val mediaVisibility by (viewModel?.mediaVisibility ?: remember { kotlinx.coroutines.flow.MutableStateFlow(true) }).collectAsState()
    val themeMode by (viewModel?.themeMode ?: remember { kotlinx.coroutines.flow.MutableStateFlow("System Default") }).collectAsState()
    val messageTone by (viewModel?.messageTone ?: remember { kotlinx.coroutines.flow.MutableStateFlow("Default") }).collectAsState()
    val callRingTone by (viewModel?.callRingTone ?: remember { kotlinx.coroutines.flow.MutableStateFlow("Standard") }).collectAsState()
    val vibrationEnabled by (viewModel?.vibrationEnabled ?: remember { kotlinx.coroutines.flow.MutableStateFlow(true) }).collectAsState()
    val reactionAlertsEnabled by (viewModel?.reactionAlertsEnabled ?: remember { kotlinx.coroutines.flow.MutableStateFlow(true) }).collectAsState()
    val voiceNotesSize by (viewModel?.voiceNotesSize ?: remember { kotlinx.coroutines.flow.MutableStateFlow(4404019L) }).collectAsState()
    val photosSize by (viewModel?.photosSize ?: remember { kotlinx.coroutines.flow.MutableStateFlow(13421772L) }).collectAsState()
    val pdfsSize by (viewModel?.pdfsSize ?: remember { kotlinx.coroutines.flow.MutableStateFlow(16043212L) }).collectAsState()
    val docsSize by (viewModel?.docsSize ?: remember { kotlinx.coroutines.flow.MutableStateFlow(8912896L) }).collectAsState()
    val mobileDataDownload by (viewModel?.mobileDataDownload ?: remember { kotlinx.coroutines.flow.MutableStateFlow(emptySet()) }).collectAsState()
    val wifiDownload by (viewModel?.wifiDownload ?: remember { kotlinx.coroutines.flow.MutableStateFlow(emptySet()) }).collectAsState()
    val backupLogs by (viewModel?.backupLogs ?: remember { kotlinx.coroutines.flow.MutableStateFlow(emptyList()) }).collectAsState()

    var newAccountEmail by remember { mutableStateOf("") }
    var newAccountName by remember { mutableStateOf("") }
    var newAccountPhone by remember { mutableStateOf("") }

    val context = LocalContext.current
    val isChatActive = selectedTab == 1 && selectedContactForChat != null

    val isSettingsSubscreenOpen = selectedTab == 3 && (showHistoryScreen || showProfileScreen ||
        showAccountSettingsScreen || showListsSettingsScreen || showChatsSettingsScreen ||
        showAppearanceSettingsScreen || showNotificationsSettingsScreen || showStorageSettingsScreen ||
        showInviteFriendScreen || showRemindersModuleScreen)

    val isAnyHubOverlayOrNonHomeTab = showLanguageDialog || showProfileDialog || showAddAccountDialog ||
        showSwitchAccountDialog || showDeleteAccountInfoDialog || showOptionsMenu || showNewCommunityFlow ||
        isSettingsSubscreenOpen || (selectedContactForChat == null && selectedTab != 0)

    BackHandler(enabled = isAnyHubOverlayOrNonHomeTab) {
        when {
            showLanguageDialog -> showLanguageDialog = false
            showProfileDialog -> showProfileDialog = false
            showAddAccountDialog -> showAddAccountDialog = false
            showSwitchAccountDialog -> showSwitchAccountDialog = false
            showDeleteAccountInfoDialog -> showDeleteAccountInfoDialog = false
            showOptionsMenu -> showOptionsMenu = false
            showNewCommunityFlow -> showNewCommunityFlow = false
            selectedTab == 3 && showHistoryScreen -> showHistoryScreen = false
            selectedTab == 3 && showProfileScreen -> showProfileScreen = false
            selectedTab == 3 && showAccountSettingsScreen -> showAccountSettingsScreen = false
            selectedTab == 3 && showListsSettingsScreen -> showListsSettingsScreen = false
            selectedTab == 3 && showChatsSettingsScreen -> showChatsSettingsScreen = false
            selectedTab == 3 && showAppearanceSettingsScreen -> showAppearanceSettingsScreen = false
            selectedTab == 3 && showNotificationsSettingsScreen -> showNotificationsSettingsScreen = false
            selectedTab == 3 && showStorageSettingsScreen -> showStorageSettingsScreen = false
            selectedTab == 3 && showInviteFriendScreen -> showInviteFriendScreen = false
            selectedTab == 3 && showRemindersModuleScreen -> showRemindersModuleScreen = false
            selectedContactForChat == null && selectedTab != 0 -> selectedTab = 0
        }
    }

    Scaffold(
        topBar = {
            if (!isChatActive) {
                TopAppBar(
                    title = {
                        Text(
                            text = Strings.get("app_title", currentLang),
                            color = TextWhite,
                            fontSize = 18.sp
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showOptionsMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextWhite)
                            }
                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false },
                                modifier = Modifier.background(SurfaceDark)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Settings", color = TextWhite) },
                                    onClick = {
                                        showOptionsMenu = false
                                        showAccountSettingsScreen = false
                                        showHistoryScreen = false
                                        selectedTab = 3
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Read All", color = TextWhite) },
                                    onClick = {
                                        showOptionsMenu = false
                                        Toast.makeText(context, "Read All Clicked", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("New Community", color = TextWhite) },
                                    onClick = {
                                        showOptionsMenu = false
                                        showNewCommunityFlow = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Make a Group", color = TextWhite) },
                                    onClick = {
                                        showOptionsMenu = false
                                        Toast.makeText(context, "Make a Group Clicked", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCharcoal)
                )
            }
        },
        bottomBar = {
            if (!isChatActive) {
                NavigationBar(
                    containerColor = BgCharcoal,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(Strings.get("home", currentLang), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextWhite,
                            unselectedIconColor = TextSilver,
                            selectedTextColor = TextWhite,
                            unselectedTextColor = TextSilver,
                            indicatorColor = SurfaceDark
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                        label = { Text(Strings.get("chats", currentLang), fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextWhite,
                            unselectedIconColor = TextSilver,
                            selectedTextColor = TextWhite,
                            unselectedTextColor = TextSilver,
                            indicatorColor = SurfaceDark
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                        label = { Text("SmartFit", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextWhite,
                            unselectedIconColor = TextSilver,
                            selectedTextColor = TextWhite,
                            unselectedTextColor = TextSilver,
                            indicatorColor = SurfaceDark
                        )
                    )
                }
            }
        },
        containerColor = BgCharcoal
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isChatActive) PaddingValues(0.dp) else innerPadding)
        ) {
            if (showNewCommunityFlow) {
                NewCommunityFlowScreen(
                    contacts = contacts,
                    onBack = { showNewCommunityFlow = false },
                    onCreateCommunity = { name, desc, avatarUri, selectedGroupIds, newGroupNames, onCreated ->
                        viewModel?.createCommunity(
                            name = name,
                            description = desc,
                            avatarUri = avatarUri,
                            selectedExistingGroupIds = selectedGroupIds,
                            newGroupNames = newGroupNames,
                            onCommunityCreated = onCreated
                        )
                    },
                    onCommunityCreatedNavigate = { createdContact ->
                        showNewCommunityFlow = false
                        onSelectContactForChat(createdContact)
                        selectedTab = 1
                    }
                )
            } else {
                when (selectedTab) {
                0 -> HomeScreen(
                    contacts = contacts,
                    currentLang = currentLang,
                    onContactClick = { contact ->
                        onSelectContactForChat(contact)
                        selectedTab = 1
                    },
                    onTogglePin = onTogglePin,
                    onDeleteContact = onDeleteContact,
                    onBlockContact = onBlockContact,
                    onStartChatWithNumber = { number ->
                        onStartChatWithNumber(number)
                        selectedTab = 1
                    },
                    chatLists = chatLists
                )
                1 -> ChatsScreen(
                    contact = selectedContactForChat,
                    messages = messagesForSelectedContact,
                    contacts = contacts,
                    onBack = { selectedTab = 0 },
                    onSendMessage = onSendMessage,
                    onToggleStar = onToggleStar,
                    onClearChat = onClearChat,
                    onDeleteOldMessages = onDeleteOldMessages,
                    onGetHistoryForContact = onGetHistoryForContact,
                    onGetStarredMessages = onGetStarredMessages,
                    onBlockContact = onBlockContact,
                    onCreateGroup = onCreateGroup,
                    onUpdateLocalOverrideAvatar = onUpdateLocalOverrideAvatar,
                    typingStatusMap = typingStatusMap,
                    onUpdateTypingStatus = onUpdateTypingStatus,
                    onDeleteMessageForMe = { msgId -> viewModel?.deleteMessageForMe(msgId) },
                    onDeleteMessageForEveryone = { msgId -> viewModel?.deleteMessageForEveryone(msgId) },
                    onToggleMessageStarredByRecipient = { msgId, starred -> viewModel?.toggleMessageStarredByRecipient(msgId, starred) },
                    deletionOutcomeFlow = viewModel?.deletionOutcome,
                    currentLang = currentLang
                )
                2 -> SmartFitServiceScreen(
                    currentLang = currentLang
                )
                3 -> when {
                    showHistoryScreen -> HistoryScreen(
                        onGetHistory = onGetHistory,
                        onBack = { showHistoryScreen = false }
                    )
                    showProfileScreen -> ProfileScreen(
                        userName = userName,
                        phoneNumber = phoneNumber,
                        country = userCountry,
                        userEmail = userEmail,
                        profileAvatar = profileAvatar,
                        onUpdateUserName = onUpdateUserName,
                        onUpdateAvatar = onUpdateAvatar,
                        onBack = { showProfileScreen = false },
                        currentLang = currentLang
                    )
                    showAccountSettingsScreen -> AccountSettingsScreen(
                        userName = userName,
                        userEmail = userEmail,
                        phoneNumber = phoneNumber,
                        userCountry = userCountry,
                        initialLanguage = initialLanguage,
                        onProfileClick = { showProfileScreen = true },
                        onAddAccountClick = { showAddAccountDialog = true },
                        onSwitchAccountClick = { showSwitchAccountDialog = true },
                        onDeleteAccountInfoClick = { showDeleteAccountInfoDialog = true },
                        onLogoutClick = onLogout,
                        onBack = { showAccountSettingsScreen = false },
                        currentLang = currentLang
                    )
                    showListsSettingsScreen -> ListsSettingsScreen(
                        currentLang = currentLang,
                        contacts = contacts,
                        chatLists = chatLists,
                        onCreateList = { viewModel?.createList(it) },
                        onDeleteList = { viewModel?.deleteList(it) },
                        onAddContactToList = { name, id -> viewModel?.addContactToList(name, id) },
                        onRemoveContactFromList = { name, id -> viewModel?.removeContactFromList(name, id) },
                        onBack = { showListsSettingsScreen = false }
                    )
                    showChatsSettingsScreen -> ChatsSettingsScreen(
                        currentLang = currentLang,
                        fontSizePreference = fontSizePreference,
                        enterIsSend = enterIsSend,
                        mediaVisibility = mediaVisibility,
                        backupLogs = backupLogs,
                        onSetFontSize = { viewModel?.setFontSize(it) },
                        onSetEnterIsSend = { viewModel?.setEnterIsSend(it) },
                        onSetMediaVisibility = { viewModel?.setMediaVisibility(it) },
                        onAddBackupLog = { viewModel?.addBackupLog(it) },
                        onBack = { showChatsSettingsScreen = false }
                    )
                    showAppearanceSettingsScreen -> AppearanceSettingsScreen(
                        currentLang = currentLang,
                        themeMode = themeMode,
                        onSetThemeMode = { viewModel?.setThemeMode(it) },
                        onBack = { showAppearanceSettingsScreen = false }
                    )
                    showNotificationsSettingsScreen -> NotificationsSettingsScreen(
                        currentLang = currentLang,
                        messageTone = messageTone,
                        callRingTone = callRingTone,
                        vibrationEnabled = vibrationEnabled,
                        reactionAlertsEnabled = reactionAlertsEnabled,
                        onSetMessageTone = { viewModel?.setMessageTone(it) },
                        onSetCallRingTone = { viewModel?.setCallRingTone(it) },
                        onSetVibration = { viewModel?.setVibrationEnabled(it) },
                        onSetReactionAlerts = { viewModel?.setReactionAlertsEnabled(it) },
                        onBack = { showNotificationsSettingsScreen = false }
                    )
                    showStorageSettingsScreen -> StorageSettingsScreen(
                        currentLang = currentLang,
                        voiceNotesSize = voiceNotesSize,
                        photosSize = photosSize,
                        pdfsSize = pdfsSize,
                        docsSize = docsSize,
                        mobileDataDownload = mobileDataDownload,
                        wifiDownload = wifiDownload,
                        onClearCache = { viewModel?.clearCache() },
                        onUpdateMobileDataDownload = { viewModel?.updateMobileDataDownload(it) },
                        onUpdateWifiDownload = { viewModel?.updateWifiDownload(it) },
                        onBack = { showStorageSettingsScreen = false }
                    )
                    showInviteFriendScreen -> InviteFriendScreen(
                        currentLang = currentLang,
                        userName = userName,
                        onBack = { showInviteFriendScreen = false }
                    )
                    showRemindersModuleScreen -> RemindersScreen(
                        reminders = reminders,
                        contacts = contacts,
                        currentLang = currentLang,
                        onAddReminder = onAddReminder,
                        onUpdateStatus = onUpdateReminderStatus,
                        onDeleteReminder = onDeleteReminder,
                        onBack = { showRemindersModuleScreen = false }
                    )
                    else -> SettingsScreen(
                        currentLang = currentLang,
                        onLanguageClick = { showLanguageDialog = true },
                        onProfileClick = { showProfileScreen = true },
                        onAccountClick = { showAccountSettingsScreen = true },
                        onHistoryClick = { showHistoryScreen = true },
                        onBack = { selectedTab = 0 },
                        onListsClick = { showListsSettingsScreen = true },
                        onChatsClick = { showChatsSettingsScreen = true },
                        onAppearanceClick = { showAppearanceSettingsScreen = true },
                        onNotificationsClick = { showNotificationsSettingsScreen = true },
                        onStorageClick = { showStorageSettingsScreen = true },
                        onInviteClick = { showInviteFriendScreen = true },
                        onRemindersClick = { showRemindersModuleScreen = true }
                    )
                }
            }
        }
        }
    }

    // App Language Selection Dialog with Search Bar and Real-Time Filter
    if (showLanguageDialog) {
        AppLanguageSelectionDialog(
            currentLang = currentLang,
            onLanguageChange = { newLang ->
                onLanguageChange(newLang)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // "Your Sign-in Information" Profile Summary Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Your Sign-in Information", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProfileInfoRow(label = "Registered Name", value = userName)
                    ProfileInfoRow(label = "Email Address", value = userEmail)
                    ProfileInfoRow(label = "Phone Number", value = phoneNumber)
                    ProfileInfoRow(label = "Selected Country", value = userCountry)
                    ProfileInfoRow(label = "Initial Setup Language", value = initialLanguage)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Account Status: Verified & Active", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("OK", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Add Account Dialog
    if (showAddAccountDialog) {
        AlertDialog(
            onDismissRequest = { showAddAccountDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Add Account", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter manual credentials or continue with Google Firebase Auth:", color = TextSilver, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newAccountName,
                        onValueChange = { newAccountName = it },
                        label = { Text("Full Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = newAccountEmail,
                        onValueChange = { newAccountEmail = it },
                        label = { Text("Email Address") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    OutlinedTextField(
                        value = newAccountPhone,
                        onValueChange = { newAccountPhone = it },
                        label = { Text("Phone Number") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            showAddAccountDialog = false
                            Toast.makeText(context, "Google Auth sign-in successful for sandbox session!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue with Google", color = TextWhite)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newAccountEmail.isNotBlank()) {
                            showAddAccountDialog = false
                            Toast.makeText(context, "Account added: $newAccountEmail", Toast.LENGTH_LONG).show()
                            newAccountEmail = ""
                            newAccountName = ""
                            newAccountPhone = ""
                        } else {
                            Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Save Account", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAccountDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }

    // Switch Account Dialog
    if (showSwitchAccountDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchAccountDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Switch Active Account", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select an active session from local storage:", color = TextSilver, fontSize = 13.sp)
                    val activeSessions = listOf(
                        Triple("Sangeeta Rathod", userEmail, true),
                        Triple("Demo Wellness User", "demo.wellness@smartfit.app", false),
                        Triple("Enterprise Admin", "admin@smartfitwellness.app", false)
                    )
                    activeSessions.forEach { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (session.third) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    showSwitchAccountDialog = false
                                    Toast.makeText(context, "Switched active session to ${session.first} (${session.second})", Toast.LENGTH_LONG).show()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = session.first, color = TextWhite, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                Text(text = session.second, color = TextSilver, fontSize = 12.sp)
                            }
                            if (session.third) {
                                Text("Active", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSwitchAccountDialog = false }) {
                    Text("Close", color = TextSilver)
                }
            }
        )
    }

    // How to Delete My Account Dialog
    if (showDeleteAccountInfoDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountInfoDialog = false },
            containerColor = SurfaceDark,
            title = { Text("Account Deletion Guidelines", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Permanently deleting your SmartFit Wellness account will purge all local Room database records, chat logs, profile credentials, and reminders.", color = TextWhite, fontSize = 14.sp)
                    Text("1. All encryption keys will be revoked.\n2. Cloud backups will be disassociated.\n3. This action is irreversible.", color = TextSilver, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            showDeleteAccountInfoDialog = false
                            Toast.makeText(context, "Account data successfully purged from database.", Toast.LENGTH_LONG).show()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Purge & Delete Account Permanently", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDeleteAccountInfoDialog = false }) {
                    Text("Cancel", color = TextSilver)
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column {
        Text(text = label, color = TextSilver, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value.ifEmpty { "Not specified" }, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}
