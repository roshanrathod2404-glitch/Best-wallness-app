package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.ContactEntity
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CountrySelectionScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.MainHubScreen
import com.example.ui.screens.PermissionGatewayScreen
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SmartFitViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Lifecycle

class MainActivity : ComponentActivity() {
    private val viewModel: SmartFitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val useDarkTheme = when (themeMode) {
                "Light Mode" -> false
                "Dark Mode" -> true
                else -> isSystemDark
            }
            MyApplicationTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgCharcoal
                ) {
                    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
                    val userEmail by viewModel.currentUserEmail.collectAsState()
                    val currentLang by viewModel.currentLanguage.collectAsState()
                    val userName by viewModel.currentUserName.collectAsState()
                    val phoneNumber by viewModel.currentPhoneNumber.collectAsState()
                    val userCountry by viewModel.currentCountry.collectAsState()
                    val initialLanguage by viewModel.initialLanguage.collectAsState()
                    val profileAvatar by viewModel.userProfileAvatar.collectAsState()
                    val contacts by viewModel.contacts.collectAsState()
                    val reminders by viewModel.reminders.collectAsState()
                    val typingStatusMap by viewModel.typingStatusMap.collectAsState()

                    val lifecycleOwner = LocalLifecycleOwner.current
                    DisposableEffect(lifecycleOwner) {
                        val observer = LifecycleEventObserver { _, event ->
                            when (event) {
                                Lifecycle.Event.ON_START -> {
                                    // User active / online
                                }
                                Lifecycle.Event.ON_STOP -> {
                                    // User backgrounded
                                }
                                else -> {}
                            }
                        }
                        lifecycleOwner.lifecycle.addObserver(observer)
                        onDispose {
                            lifecycleOwner.lifecycle.removeObserver(observer)
                        }
                    }

                    var selectedCountryName by remember { mutableStateOf("India") }
                    var selectedCountryCode by remember { mutableStateOf("+91") }
                    var flowStep by remember { mutableStateOf(1) } // 1: Permissions Gateway, 2: Country, 3: Language, 4: Auth

                    var selectedContact by remember { mutableStateOf<ContactEntity?>(null) }
                    val messages by (selectedContact?.id?.let { viewModel.getMessagesForContact(it) }
                        ?: remember { kotlinx.coroutines.flow.flowOf(emptyList()) }).collectAsState(initial = emptyList())

                    if (!isAuthenticated) {
                        when (flowStep) {
                            1 -> {
                                PermissionGatewayScreen(
                                    currentLang = currentLang,
                                    onPermissionsCompleted = {
                                        flowStep = 2
                                    }
                                )
                            }
                            2 -> {
                                CountrySelectionScreen(
                                    currentLang = currentLang,
                                    onCountrySelected = { countryName, countryCode ->
                                        selectedCountryName = countryName
                                        selectedCountryCode = countryCode
                                        flowStep = 3
                                    },
                                    onBack = {
                                        flowStep = 1
                                    }
                                )
                            }
                            3 -> {
                                LanguageSelectionScreen(
                                    countryName = selectedCountryName,
                                    countryCode = selectedCountryCode,
                                    currentLang = currentLang,
                                    onLanguageSelected = { lang ->
                                        viewModel.setLanguage(lang)
                                        flowStep = 4
                                    },
                                    onBack = {
                                        flowStep = 2
                                    }
                                )
                            }
                            4 -> {
                                AuthScreen(
                                    initialCountryName = selectedCountryName,
                                    initialCountryCode = selectedCountryCode,
                                    currentLang = currentLang,
                                    onLanguageChange = { viewModel.setLanguage(it) },
                                    onLoginSuccess = { email, phone ->
                                        val dynamicName = email.substringBefore("@").replace(".", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                        viewModel.login(email, phone, dynamicName, selectedCountryName)
                                    },
                                    onBackToCountrySelection = {
                                        flowStep = 2
                                    }
                                )
                            }
                        }
                    } else {
                        MainHubScreen(
                            contacts = contacts,
                            reminders = reminders,
                            userName = userName,
                            userEmail = userEmail,
                            phoneNumber = phoneNumber,
                            userCountry = userCountry,
                            initialLanguage = initialLanguage,
                            profileAvatar = profileAvatar,
                            currentLang = currentLang,
                            viewModel = viewModel,
                            onLanguageChange = { viewModel.setLanguage(it) },
                            onLogout = {
                                viewModel.logout()
                                flowStep = 1
                            },
                            onAddReminder = { category, contactId, contactName, hour, minute, isAm, recurrence, message, targetTimezone ->
                                viewModel.addReminder(category, contactId, contactName, hour, minute, isAm, recurrence, message, targetTimezone)
                            },
                            onUpdateReminderStatus = { reminder, status ->
                                viewModel.updateReminderStatus(reminder, status)
                            },
                            onDeleteReminder = { reminder ->
                                viewModel.deleteReminder(reminder)
                            },
                            onAddContact = { name, phone, email ->
                                viewModel.addContact(name, phone, email)
                            },
                            onUpdateUserName = { viewModel.updateUserName(it) },
                            onUpdateAvatar = { viewModel.updateUserProfileAvatar(it) },
                            onUpdateLocalOverrideAvatar = { contactId, uri -> viewModel.updateLocalOverrideAvatar(contactId, uri) },
                            selectedContactForChat = selectedContact,
                            onSelectContactForChat = { contact ->
                                selectedContact = contact
                                contact?.let {
                                    if (it.unreadCount > 0) {
                                        viewModel.updateContactUnread(it.id, 0)
                                    }
                                }
                            },
                            messagesForSelectedContact = messages,
                            onSendMessage = { text, replyText, replySender, fileType, fileName ->
                                selectedContact?.let { contact ->
                                    viewModel.sendMessage(contact.id, "You", text, true, replyText, replySender, fileType, fileName)
                                }
                            },
                            onGetHistory = { type -> viewModel.getHistoryByType(type) },
                            onToggleStar = { messageId, isStarred -> viewModel.toggleMessageStarred(messageId, isStarred) },
                            onClearChat = { contactId -> viewModel.clearChat(contactId) },
                            onDeleteOldMessages = { contactId, cutoffTime -> viewModel.deleteMessagesOlderThan(contactId, cutoffTime) },
                            onGetHistoryForContact = { contactId, type -> viewModel.getHistoryForContactAndType(contactId, type) },
                            onGetStarredMessages = { contactId -> viewModel.getStarredMessagesForContact(contactId) },
                            onTogglePin = { contactId, isPinned -> viewModel.toggleContactPinned(contactId, isPinned) },
                            onDeleteContact = { contactId -> viewModel.deleteContact(contactId) },
                            onBlockContact = { contactId -> viewModel.blockContact(contactId) },
                            onStartChatWithNumber = { number ->
                                viewModel.startChatWithNumber(number) { contact ->
                                    selectedContact = contact
                                }
                            },
                            onCreateGroup = { name, ids, callback ->
                                viewModel.createGroup(name, ids, callback)
                            },
                            typingStatusMap = typingStatusMap,
                            onUpdateTypingStatus = { contactId, isTyping ->
                                viewModel.updateTypingStatus(contactId, isTyping)
                            }
                        )
                    }
                }
            }
        }
    }
}


