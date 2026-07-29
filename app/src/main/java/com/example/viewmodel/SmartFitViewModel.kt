package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatRoomEntity
import com.example.data.CommunityEntity
import com.example.data.ContactEntity
import com.example.data.FirebaseRepository
import com.example.data.MessageEntity
import com.example.data.ReminderEntity
import com.example.data.SmartFitDatabase
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SmartFitViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SmartFitDatabase.getDatabase(application)
    private val contactDao = database.contactDao()
    private val messageDao = database.messageDao()
    private val reminderDao = database.reminderDao()
    private val historyDao = database.historyDao()
    private val chatRoomDao = database.chatRoomDao()
    private val communityDao = database.communityDao()


    val contacts: StateFlow<List<ContactEntity>> = contactDao.getAllContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reminders: StateFlow<List<ReminderEntity>> = reminderDao.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatRooms: StateFlow<List<ChatRoomEntity>> = chatRoomDao.getAllChatRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communities: StateFlow<List<CommunityEntity>> = communityDao.getAllCommunities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val firebaseRepo = FirebaseRepository()
    private var userDataListener: ListenerRegistration? = null
    private val liveChatListeners = mutableMapOf<Long, ListenerRegistration>()
    private val typingListeners = mutableMapOf<Long, ListenerRegistration>()

    private val _deletedContactIds = MutableStateFlow<Set<Long>>(emptySet())
    val deletedContactIds: StateFlow<Set<Long>> = _deletedContactIds.asStateFlow()

    private val prefs = application.getSharedPreferences("smartfit_prefs", android.content.Context.MODE_PRIVATE)

    // Authentication State
    private val _isAuthenticated = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUserEmail = MutableStateFlow(prefs.getString("user_email", "") ?: "")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentPhoneNumber = MutableStateFlow(prefs.getString("phone_number", "") ?: "")
    val currentPhoneNumber: StateFlow<String> = _currentPhoneNumber.asStateFlow()

    private val _currentUserName = MutableStateFlow(prefs.getString("user_name", "") ?: "")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _userProfileAvatar = MutableStateFlow(prefs.getString("user_profile_avatar", "") ?: "")
    val userProfileAvatar: StateFlow<String> = _userProfileAvatar.asStateFlow()

    private val _currentCountry = MutableStateFlow(prefs.getString("user_country", "India") ?: "India")
    val currentCountry: StateFlow<String> = _currentCountry.asStateFlow()

    private val _initialLanguage = MutableStateFlow(prefs.getString("initial_language", "English") ?: "English")
    val initialLanguage: StateFlow<String> = _initialLanguage.asStateFlow()

    private val _currentLanguage = MutableStateFlow(prefs.getString("app_language", "English") ?: "English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // 1. Lists / Categorization State
    private val _chatLists = MutableStateFlow<Map<String, List<Long>>>(emptyMap())
    val chatLists: StateFlow<Map<String, List<Long>>> = _chatLists.asStateFlow()

    // 2. Chat Settings
    private val _fontSizePreference = MutableStateFlow(prefs.getString("chat_font_size", "Medium") ?: "Medium")
    val fontSizePreference: StateFlow<String> = _fontSizePreference.asStateFlow()

    private val _enterIsSend = MutableStateFlow(prefs.getBoolean("chat_enter_is_send", false))
    val enterIsSend: StateFlow<Boolean> = _enterIsSend.asStateFlow()

    private val _mediaVisibility = MutableStateFlow(prefs.getBoolean("chat_media_visibility", true))
    val mediaVisibility: StateFlow<Boolean> = _mediaVisibility.asStateFlow()

    private val _backupLogs = MutableStateFlow<List<String>>(
        prefs.getStringSet("backup_history", setOf("Local: Oct 10, 2026, 10:00 AM (14.2 MB)", "Cloud: Oct 12, 2026, 03:15 PM (15.5 MB)"))?.toList()?.sorted() ?: emptyList()
    )
    val backupLogs: StateFlow<List<String>> = _backupLogs.asStateFlow()

    // 3. Theme Mode
    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "System Default") ?: "System Default")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // 4. Notifications Prefs
    private val _messageTone = MutableStateFlow(prefs.getString("notification_message_tone", "Default") ?: "Default")
    val messageTone: StateFlow<String> = _messageTone.asStateFlow()

    private val _callRingTone = MutableStateFlow(prefs.getString("notification_call_ringtone", "Standard") ?: "Standard")
    val callRingTone: StateFlow<String> = _callRingTone.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean("notification_vibration", true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _reactionAlertsEnabled = MutableStateFlow(prefs.getBoolean("notification_reaction_alerts", true))
    val reactionAlertsEnabled: StateFlow<Boolean> = _reactionAlertsEnabled.asStateFlow()

    // 5. Storage Prefs
    private val _voiceNotesSize = MutableStateFlow(prefs.getLong("storage_voice_notes_size", 4404019L)) // 4.2 MB
    val voiceNotesSize: StateFlow<Long> = _voiceNotesSize.asStateFlow()

    private val _photosSize = MutableStateFlow(prefs.getLong("storage_photos_size", 13421772L)) // 12.8 MB
    val photosSize: StateFlow<Long> = _photosSize.asStateFlow()

    private val _pdfsSize = MutableStateFlow(prefs.getLong("storage_pdfs_size", 16043212L)) // 15.3 MB
    val pdfsSize: StateFlow<Long> = _pdfsSize.asStateFlow()

    private val _docsSize = MutableStateFlow(prefs.getLong("storage_docs_size", 8912896L)) // 8.5 MB
    val docsSize: StateFlow<Long> = _docsSize.asStateFlow()

    private val _mobileDataDownload = MutableStateFlow(prefs.getStringSet("download_mobile_data", setOf("Photos")) ?: setOf("Photos"))
    val mobileDataDownload: StateFlow<Set<String>> = _mobileDataDownload.asStateFlow()

    private val _wifiDownload = MutableStateFlow(prefs.getStringSet("download_wifi", setOf("Photos", "Videos", "Documents")) ?: setOf("Photos", "Videos", "Documents"))
    val wifiDownload: StateFlow<Set<String>> = _wifiDownload.asStateFlow()

    init {
        loadListsFromPrefs()
        loadDeletedContactsFromPrefs()
        if (_isAuthenticated.value) {
            initFirebaseSession(_currentUserEmail.value)
        }
        viewModelScope.launch {
            contacts.collect { list ->
                if (list.isNotEmpty()) {
                    val current = _chatLists.value.toMutableMap()
                    var changed = false
                    if (current["Favorites"].isNullOrEmpty()) {
                        val favs = list.take(2).map { it.id }
                        current["Favorites"] = favs
                        prefs.edit().putString("list_contacts_Favorites", favs.joinToString(",")).apply()
                        changed = true
                    }
                    if (current["Work"].isNullOrEmpty() && list.size >= 3) {
                        val work = listOf(list[2].id)
                        current["Work"] = work
                        prefs.edit().putString("list_contacts_Work", work.joinToString(",")).apply()
                        changed = true
                    }
                    if (changed) {
                        _chatLists.value = current
                        saveListsToPrefs(current)
                    }
                    list.forEach { contact ->
                        subscribeToLiveChat(contact.id)
                    }
                }
            }
        }
    }

    private fun loadListsFromPrefs() {
        val listNamesStr = prefs.getString("custom_chat_lists", "Favorites,Work,Family") ?: "Favorites,Work,Family"
        val listNames = listNamesStr.split(",").filter { it.isNotBlank() }
        val map = mutableMapOf<String, List<Long>>()
        for (name in listNames) {
            val contactIdsStr = prefs.getString("list_contacts_$name", "") ?: ""
            val ids = contactIdsStr.split(",").mapNotNull { it.toLongOrNull() }
            map[name] = ids
        }
        _chatLists.value = map
    }

    fun createList(name: String) {
        if (name.isBlank()) return
        val current = _chatLists.value.toMutableMap()
        if (!current.containsKey(name)) {
            current[name] = emptyList()
            _chatLists.value = current
            saveListsToPrefs(current)
        }
    }

    fun deleteList(name: String) {
        val current = _chatLists.value.toMutableMap()
        if (current.containsKey(name)) {
            current.remove(name)
            _chatLists.value = current
            prefs.edit().remove("list_contacts_$name").apply()
            saveListsToPrefs(current)
        }
    }

    fun addContactToList(listName: String, contactId: Long) {
        val current = _chatLists.value.toMutableMap()
        val list = current[listName]?.toMutableList() ?: mutableListOf()
        if (!list.contains(contactId)) {
            list.add(contactId)
            current[listName] = list
            _chatLists.value = current
            prefs.edit().putString("list_contacts_$listName", list.joinToString(",")).apply()
        }
    }

    fun removeContactFromList(listName: String, contactId: Long) {
        val current = _chatLists.value.toMutableMap()
        val list = current[listName]?.toMutableList() ?: return
        if (list.contains(contactId)) {
            list.remove(contactId)
            current[listName] = list
            _chatLists.value = current
            prefs.edit().putString("list_contacts_$listName", list.joinToString(",")).apply()
        }
    }

    private fun saveListsToPrefs(map: Map<String, List<Long>>) {
        val listNames = map.keys.joinToString(",")
        prefs.edit().putString("custom_chat_lists", listNames).apply()
    }

    private fun loadDeletedContactsFromPrefs() {
        val deletedSet = prefs.getStringSet("deleted_contact_ids", emptySet()) ?: emptySet()
        _deletedContactIds.value = deletedSet.mapNotNull { it.toLongOrNull() }.toSet()
    }

    private fun saveDeletedContactsToPrefs(ids: Set<Long>) {
        prefs.edit().putStringSet("deleted_contact_ids", ids.map { it.toString() }.toSet()).apply()
    }

    fun syncUserSettingsAndState(actionName: String = "App State Updated") {
        val email = _currentUserEmail.value
        val uid = firebaseRepo.currentUid
        if (email.isBlank()) return

        val settingsMap = mapOf(
            "fontSizePreference" to _fontSizePreference.value,
            "enterIsSend" to _enterIsSend.value,
            "mediaVisibility" to _mediaVisibility.value,
            "themeMode" to _themeMode.value,
            "messageTone" to _messageTone.value,
            "callRingTone" to _callRingTone.value,
            "vibrationEnabled" to _vibrationEnabled.value,
            "reactionAlertsEnabled" to _reactionAlertsEnabled.value,
            "currentLanguage" to _currentLanguage.value,
            "userProfileAvatar" to _userProfileAvatar.value
        )

        firebaseRepo.syncUserDataAndStateToFirestore(
            userId = uid,
            email = email,
            name = _currentUserName.value,
            phone = _currentPhoneNumber.value,
            country = _currentCountry.value,
            avatar = _userProfileAvatar.value,
            settings = settingsMap,
            deletedContacts = _deletedContactIds.value.toList(),
            lastAction = actionName
        )
    }

    fun initFirebaseSession(email: String) {
        if (email.isBlank()) return
        firebaseRepo.ensureAuthenticatedSession(email) { uid ->
            val safeUid = uid ?: "user_${email.hashCode()}"
            firebaseRepo.updateUserOnlineStatus(safeUid, "Online")
            firebaseRepo.recordUserActivity(safeUid, email, "Logged In / Session Initialized")

            userDataListener?.remove()
            userDataListener = firebaseRepo.listenToUserData(safeUid) { userData ->
                (userData["deleted_contacts"] as? List<*>)?.mapNotNull { (it as? Number)?.toLong() }?.let { restoredDeleted ->
                    val newSet = restoredDeleted.toSet()
                    _deletedContactIds.value = newSet
                    saveDeletedContactsToPrefs(newSet)
                    viewModelScope.launch {
                        newSet.forEach { cId -> contactDao.deleteContact(cId) }
                    }
                }
                (userData["settings"] as? Map<*, *>)?.let { settings ->
                    (settings["fontSizePreference"] as? String)?.let { setFontSize(it, syncToFirestore = false) }
                    (settings["enterIsSend"] as? Boolean)?.let { setEnterIsSend(it, syncToFirestore = false) }
                    (settings["mediaVisibility"] as? Boolean)?.let { setMediaVisibility(it, syncToFirestore = false) }
                    (settings["themeMode"] as? String)?.let { setThemeMode(it, syncToFirestore = false) }
                    (settings["messageTone"] as? String)?.let { setMessageTone(it, syncToFirestore = false) }
                    (settings["callRingTone"] as? String)?.let { setCallRingTone(it, syncToFirestore = false) }
                    (settings["vibrationEnabled"] as? Boolean)?.let { setVibrationEnabled(it, syncToFirestore = false) }
                    (settings["reactionAlertsEnabled"] as? Boolean)?.let { setReactionAlertsEnabled(it, syncToFirestore = false) }
                    (settings["currentLanguage"] as? String)?.let { setLanguage(it, syncToFirestore = false) }
                }
            }

            syncUserSettingsAndState("App Session Initialized")

            contacts.value.forEach { contact ->
                subscribeToLiveChat(contact.id)
            }
        }
    }

    fun subscribeToLiveChat(contactId: Long) {
        if (liveChatListeners.containsKey(contactId)) return
        val listener = firebaseRepo.listenToLiveMessages(contactId) { liveMessages ->
            viewModelScope.launch {
                var latestMsg: MessageEntity? = null
                liveMessages.forEach { msg ->
                    messageDao.insertMessage(msg)
                    if (latestMsg == null || msg.epochTime > latestMsg!!.epochTime || msg.id > latestMsg!!.id) {
                        latestMsg = msg
                    }
                }
                latestMsg?.let { msg ->
                    val preview = com.example.ui.screens.formatLastMessagePreview(msg.messageText, msg.fileType, msg.fileName)
                    val timeStr = if (msg.timestamp.isNotBlank()) msg.timestamp else "Just now"
                    val isUnreadIncoming = !msg.isSentByMe
                    contactDao.updateLastMessage(
                        contactId = contactId,
                        lastMsg = preview,
                        lastMsgTime = timeStr,
                        incrementUnread = if (isUnreadIncoming) 1 else 0
                    )
                }
            }
        }
        if (listener != null) {
            liveChatListeners[contactId] = listener
        }

        val typingListener = firebaseRepo.listenToTypingStatus(contactId, firebaseRepo.currentUid) { isTyping ->
            val current = _typingStatusMap.value.toMutableMap()
            current[contactId] = isTyping
            _typingStatusMap.value = current
        }
        if (typingListener != null) {
            typingListeners[contactId] = typingListener
        }
    }

    fun setFontSize(size: String, syncToFirestore: Boolean = true) {
        _fontSizePreference.value = size
        prefs.edit().putString("chat_font_size", size).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Font Size: $size")
    }

    fun setEnterIsSend(enabled: Boolean, syncToFirestore: Boolean = true) {
        _enterIsSend.value = enabled
        prefs.edit().putBoolean("chat_enter_is_send", enabled).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Enter Is Send: $enabled")
    }

    fun setMediaVisibility(enabled: Boolean, syncToFirestore: Boolean = true) {
        _mediaVisibility.value = enabled
        prefs.edit().putBoolean("chat_media_visibility", enabled).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Media Visibility: $enabled")
    }

    fun addBackupLog(log: String) {
        val current = _backupLogs.value.toMutableList()
        current.add(log)
        _backupLogs.value = current
        prefs.edit().putStringSet("backup_history", current.toSet()).apply()
        syncUserSettingsAndState("Backup Log Added")
    }

    fun setThemeMode(theme: String, syncToFirestore: Boolean = true) {
        _themeMode.value = theme
        prefs.edit().putString("theme_mode", theme).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Theme Mode: $theme")
    }

    fun setMessageTone(tone: String, syncToFirestore: Boolean = true) {
        _messageTone.value = tone
        prefs.edit().putString("notification_message_tone", tone).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Message Tone: $tone")
    }

    fun setCallRingTone(tone: String, syncToFirestore: Boolean = true) {
        _callRingTone.value = tone
        prefs.edit().putString("notification_call_ringtone", tone).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Call Ring Tone: $tone")
    }

    fun setVibrationEnabled(enabled: Boolean, syncToFirestore: Boolean = true) {
        _vibrationEnabled.value = enabled
        prefs.edit().putBoolean("notification_vibration", enabled).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Vibration: $enabled")
    }

    fun setReactionAlertsEnabled(enabled: Boolean, syncToFirestore: Boolean = true) {
        _reactionAlertsEnabled.value = enabled
        prefs.edit().putBoolean("notification_reaction_alerts", enabled).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Reaction Alerts: $enabled")
    }

    fun clearCache() {
        _voiceNotesSize.value = 0L
        _photosSize.value = 0L
        _pdfsSize.value = 0L
        _docsSize.value = 0L
        prefs.edit()
            .putLong("storage_voice_notes_size", 0L)
            .putLong("storage_photos_size", 0L)
            .putLong("storage_pdfs_size", 0L)
            .putLong("storage_docs_size", 0L)
            .apply()
    }

    fun updateMobileDataDownload(types: Set<String>) {
        _mobileDataDownload.value = types
        prefs.edit().putStringSet("download_mobile_data", types).apply()
    }

    fun updateWifiDownload(types: Set<String>) {
        _wifiDownload.value = types
        prefs.edit().putStringSet("download_wifi", types).apply()
    }

    fun updateUserName(newName: String) {
        _currentUserName.value = newName
        prefs.edit().putString("user_name", newName).apply()
        syncUserSettingsAndState("Updated Name: $newName")
    }

    fun updateUserProfileAvatar(avatarUri: String) {
        _userProfileAvatar.value = avatarUri
        prefs.edit().putString("user_profile_avatar", avatarUri).apply()
        syncUserSettingsAndState("Updated Profile Avatar")
    }

    fun updateLocalOverrideAvatar(contactId: Long, avatarUri: String) {
        viewModelScope.launch {
            contactDao.updateLocalOverrideAvatar(contactId, avatarUri)
        }
    }

    fun setLanguage(language: String, syncToFirestore: Boolean = true) {
        _currentLanguage.value = language
        com.example.ui.screens.LocalizationHelper.setAppLocale(language)
        if (prefs.getString("initial_language", null) == null) {
            _initialLanguage.value = language
            prefs.edit().putString("initial_language", language).apply()
        }
        prefs.edit().putString("app_language", language).apply()
        if (syncToFirestore) syncUserSettingsAndState("Updated Language: $language")
    }

    fun login(email: String, phone: String, name: String = "User", country: String = "India") {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            // Clean Onboarding & User Data Isolation: clear previous local SQLite cache
            contactDao.deleteAllContacts()
            messageDao.deleteAllMessages()
            reminderDao.deleteAllReminders()
            historyDao.deleteAllHistory()
            chatRoomDao.deleteAllChatRooms()
            communityDao.deleteAllCommunities()
        }

        val resolvedName = if (name.isBlank() || name == "User") {
            email.substringBefore("@").replace(".", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            name
        }

        _currentUserEmail.value = email
        _currentPhoneNumber.value = phone
        _currentUserName.value = resolvedName
        _currentCountry.value = country
        _isAuthenticated.value = true

        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .putString("phone_number", phone)
            .putString("user_name", resolvedName)
            .putString("user_country", country)
            .apply()

        initFirebaseSession(email)
    }

    fun logout() {
        firebaseRepo.updateUserOnlineStatus(firebaseRepo.currentUid, "Offline")
        userDataListener?.remove()
        liveChatListeners.values.forEach { it.remove() }
        liveChatListeners.clear()
        typingListeners.values.forEach { it.remove() }
        typingListeners.clear()

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            // Clean Onboarding: wipe all local Room database state
            contactDao.deleteAllContacts()
            messageDao.deleteAllMessages()
            reminderDao.deleteAllReminders()
            historyDao.deleteAllHistory()
            chatRoomDao.deleteAllChatRooms()
            communityDao.deleteAllCommunities()
        }

        prefs.edit().clear().apply()

        _isAuthenticated.value = false
        _currentUserEmail.value = ""
        _currentPhoneNumber.value = ""
        _currentUserName.value = ""
    }

    fun addContact(name: String, phone: String, email: String) {
        viewModelScope.launch {
            val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
            contactDao.insertContact(
                ContactEntity(
                    name = name,
                    phoneNumber = phone,
                    email = email,
                    avatarInitials = if (initials.isNotEmpty()) initials else "SW",
                    lastMessage = "New connection established",
                    lastMessageTime = "Just now",
                    onlineStatus = "Online"
                )
            )
        }
    }

    fun checkUserRegistration(phoneNumber: String): Boolean {
        val cleanNum = phoneNumber.filter { c -> c.isDigit() }
        val existing = contacts.value.find { it.phoneNumber.filter { c -> c.isDigit() } == cleanNum }
        if (existing != null) return true
        if (cleanNum.length >= 10) {
            return !cleanNum.endsWith("9")
        }
        return false
    }

    fun startChatWithNumber(number: String, onContactReady: (ContactEntity) -> Unit) {
        viewModelScope.launch {
            val existing = contacts.value.find { it.phoneNumber == number || it.name == number }
            if (existing != null) {
                onContactReady(existing)
            } else {
                val initials = number.filter { it.isLetter() }.take(2).uppercase().ifEmpty { number.takeLast(2).ifEmpty { "SW" } }
                val id = contactDao.insertContact(
                    ContactEntity(
                        name = number,
                        phoneNumber = number,
                        email = "unsaved@smartfit.com",
                        avatarInitials = initials,
                        lastMessage = "Direct chat started",
                        lastMessageTime = "Just now",
                        onlineStatus = "Online"
                    )
                )
                val newContact = contactDao.getContactById(id) ?: ContactEntity(
                    id = id,
                    name = number,
                    phoneNumber = number,
                    email = "unsaved@smartfit.com",
                    avatarInitials = initials,
                    lastMessage = "Direct chat started",
                    lastMessageTime = "Just now",
                    onlineStatus = "Online"
                )
                onContactReady(newContact)
            }
        }
    }

    fun addReminder(
        category: String,
        contactId: Long,
        contactName: String,
        hour: Int,
        minute: Int,
        isAm: Boolean,
        recurrence: String,
        message: String,
        targetTimezone: String = "Asia/Kolkata"
    ) {
        viewModelScope.launch {
            val cleanZone = targetTimezone.substringAfter("(").substringBefore(")").ifBlank { targetTimezone }
            val utcMillis = try {
                val zoneId = java.time.ZoneId.of(cleanZone)
                val nowInZone = java.time.ZonedDateTime.now(zoneId)
                val hour24 = if (isAm) {
                    if (hour == 12) 0 else hour
                } else {
                    if (hour == 12) 12 else hour + 12
                }
                var targetDateTime = nowInZone.withHour(hour24).withMinute(minute).withSecond(0).withNano(0)
                if (targetDateTime.isBefore(nowInZone)) {
                    targetDateTime = targetDateTime.plusDays(1)
                }
                targetDateTime.toInstant().toEpochMilli()
            } catch (e: Exception) {
                System.currentTimeMillis() + 3600000L
            }

            reminderDao.insertReminder(
                ReminderEntity(
                    categoryName = category,
                    contactId = contactId,
                    contactName = contactName,
                    timeHour = hour,
                    timeMinute = minute,
                    isAm = isAm,
                    recurrenceRule = recurrence,
                    customMessage = message,
                    sentStatus = "Scheduled",
                    targetTimezone = targetTimezone,
                    utcTimestamp = utcMillis
                )
            )
        }
    }

    fun updateReminderStatus(reminder: ReminderEntity, newStatus: String) {
        viewModelScope.launch {
            reminderDao.updateReminder(reminder.copy(sentStatus = newStatus))
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.deleteReminder(reminder)
        }
    }

    fun sendMessage(contactId: Long, senderName: String, text: String, isMe: Boolean, replyToText: String = "", replyToSender: String = "", fileType: String = "Text", fileName: String = "") {
        viewModelScope.launch {
            val msg = MessageEntity(
                contactId = contactId,
                senderName = senderName,
                messageText = text,
                timestamp = "Just now",
                isSentByMe = isMe,
                fileType = fileType,
                fileName = fileName,
                replyToText = replyToText,
                replyToSender = replyToSender
            )
            messageDao.insertMessage(msg)
            val preview = com.example.ui.screens.formatLastMessagePreview(text, fileType, fileName)
            contactDao.updateLastMessage(contactId, preview, "Just now", 0)
            firebaseRepo.sendLiveMessage(contactId, msg)
            firebaseRepo.recordUserActivity(firebaseRepo.currentUid, _currentUserEmail.value, "Sent live message to contact $contactId")
        }
    }

    fun getMessagesForContact(contactId: Long) = messageDao.getMessagesForContact(contactId)

    fun getStarredMessagesForContact(contactId: Long) = messageDao.getStarredMessagesForContact(contactId)

    fun getAllStarredMessages() = messageDao.getAllStarredMessages()

    fun toggleMessageStarred(messageId: Long, isStarred: Boolean) {
        viewModelScope.launch {
            messageDao.updateMessageStarred(messageId, isStarred)
        }
    }

    fun clearChat(contactId: Long) {
        viewModelScope.launch {
            messageDao.clearChatExceptStarred(contactId)
        }
    }

    fun deleteMessagesOlderThan(contactId: Long, cutoffTime: Long) {
        viewModelScope.launch {
            messageDao.deleteMessagesOlderThanExceptStarred(contactId, cutoffTime)
        }
    }

    fun getHistoryByType(type: String) = historyDao.getHistoryByType(type)

    fun getHistoryForContactAndType(contactId: Long, type: String) = historyDao.getHistoryForContactAndType(contactId, type)

    fun toggleContactPinned(contactId: Long, isPinned: Boolean) {
        viewModelScope.launch {
            contactDao.updateContactPinned(contactId, isPinned)
        }
    }

    fun deleteContact(contactId: Long) {
        viewModelScope.launch {
            val currentDeleted = _deletedContactIds.value.toMutableSet()
            currentDeleted.add(contactId)
            _deletedContactIds.value = currentDeleted
            saveDeletedContactsToPrefs(currentDeleted)
            contactDao.deleteContact(contactId)
            syncUserSettingsAndState("Deleted contact ID $contactId")
        }
    }

    fun blockContact(contactId: Long) {
        viewModelScope.launch {
            contactDao.blockContact(contactId)
        }
    }

    fun updateContactUnread(contactId: Long, unreadCount: Int) {
        viewModelScope.launch {
            contactDao.updateContactUnread(contactId, unreadCount)
        }
    }

    fun createGroup(groupName: String, participantIds: Set<Long>, onGroupCreated: (ContactEntity) -> Unit) {
        viewModelScope.launch {
            val initials = groupName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifEmpty { "GRP" }
            chatRoomDao.insertChatRoom(
                ChatRoomEntity(
                    groupName = groupName,
                    participantCount = participantIds.size,
                    participantIds = participantIds.joinToString(","),
                    lastMessage = "Group created with $groupName",
                    lastMessageTime = "Just now",
                    avatarInitials = initials,
                    isPinned = true
                )
            )
            val contactId = contactDao.insertContact(
                ContactEntity(
                    name = groupName,
                    phoneNumber = "Group (${participantIds.size} members)",
                    email = "group@smartfit.com",
                    avatarInitials = initials,
                    lastMessage = "Group created successfully",
                    lastMessageTime = "Just now",
                    onlineStatus = "${participantIds.size} members",
                    isPinned = true,
                    unreadCount = 1
                )
            )
            val newGroupContact = contactDao.getContactById(contactId) ?: ContactEntity(
                id = contactId,
                name = groupName,
                phoneNumber = "Group (${participantIds.size} members)",
                email = "group@smartfit.com",
                avatarInitials = initials,
                lastMessage = "Group created successfully",
                lastMessageTime = "Just now",
                onlineStatus = "${participantIds.size} members",
                isPinned = true,
                unreadCount = 1
            )
            subscribeToLiveChat(contactId)
            onGroupCreated(newGroupContact)
        }
    }

    fun createCommunity(
        name: String,
        description: String,
        avatarUri: String,
        selectedExistingGroupIds: List<Long>,
        newGroupNames: List<String>,
        onCommunityCreated: (ContactEntity) -> Unit
    ) {
        viewModelScope.launch {
            val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifEmpty { "COMM" }
            val announcementContact = ContactEntity(
                name = "$name Announcements",
                phoneNumber = "Community Announcement",
                email = currentUserEmail.value,
                avatarInitials = "📢",
                lastMessage = "Welcome to $name! Official announcements will appear here.",
                lastMessageTime = "Just now",
                onlineStatus = "Community Channel",
                isPinned = true,
                unreadCount = 0,
                isCommunity = true,
                isGroup = true,
                isAnnouncementChannel = true,
                isAdminOnlyPosting = true,
                localOverrideAvatar = avatarUri
            )
            val announcementId = contactDao.insertContact(announcementContact)
            val createdAnnouncementContact = announcementContact.copy(id = announcementId)

            val createdNewGroupContacts = mutableListOf<ContactEntity>()
            newGroupNames.forEach { gName ->
                if (gName.isNotBlank()) {
                    val gInitials = gName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifEmpty { "GRP" }
                    val newGroupContact = ContactEntity(
                        name = gName,
                        phoneNumber = "Group Chat",
                        email = currentUserEmail.value,
                        avatarInitials = gInitials,
                        lastMessage = "Group created under $name",
                        lastMessageTime = "Just now",
                        onlineStatus = "Group",
                        isPinned = false,
                        isGroup = true,
                        communityId = announcementId
                    )
                    val gId = contactDao.insertContact(newGroupContact)
                    createdNewGroupContacts.add(newGroupContact.copy(id = gId))
                }
            }

            val linkedGroupContacts = mutableListOf<ContactEntity>()
            selectedExistingGroupIds.forEach { id ->
                val existing = contactDao.getContactById(id)
                if (existing != null) {
                    val updatedGroup = existing.copy(isGroup = true, communityId = announcementId)
                    contactDao.insertContact(updatedGroup)
                    linkedGroupContacts.add(updatedGroup)
                }
            }
            linkedGroupContacts.addAll(createdNewGroupContacts)

            val communityEntity = CommunityEntity(
                name = name,
                description = description,
                avatarUri = avatarUri,
                adminEmail = currentUserEmail.value,
                announcementContactId = announcementId,
                linkedGroupIds = linkedGroupContacts.map { it.id }.joinToString(","),
                createdAt = System.currentTimeMillis()
            )
            val commId = communityDao.insertCommunity(communityEntity)

            val welcomeMsg = MessageEntity(
                contactId = announcementId,
                senderName = name,
                messageText = "Welcome to $name!\n\n$description\n\n📢 Official community announcements and updates will be posted here by community admins.",
                timestamp = "Just now",
                isSentByMe = true,
                epochTime = System.currentTimeMillis()
            )
            messageDao.insertMessage(welcomeMsg)
            firebaseRepo.sendLiveMessage(announcementId, welcomeMsg)

            firebaseRepo.createCommunityInFirestore(
                community = communityEntity.copy(id = commId),
                linkedGroups = linkedGroupContacts,
                announcementContact = createdAnnouncementContact
            ) { success ->
                syncUserSettingsAndState("Created Community $name")
            }

            subscribeToLiveChat(announcementId)
            onCommunityCreated(createdAnnouncementContact)
        }
    }

    private val _typingStatusMap = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val typingStatusMap: StateFlow<Map<Long, Boolean>> = _typingStatusMap.asStateFlow()

    fun updateTypingStatus(contactId: Long, isTyping: Boolean) {
        val current = _typingStatusMap.value.toMutableMap()
        current[contactId] = isTyping
        _typingStatusMap.value = current
        firebaseRepo.setTypingStatus(contactId, firebaseRepo.currentUid, isTyping)
    }

    fun updateContactOnlineStatus(contactId: Long, status: String) {
        viewModelScope.launch {
            contactDao.updateContactOnlineStatus(contactId, status)
        }
    }

    private val _deletionOutcome = MutableSharedFlow<String>()
    val deletionOutcome: SharedFlow<String> = _deletionOutcome.asSharedFlow()

    fun deleteMessageForMe(messageId: Long) {
        viewModelScope.launch {
            val msg = messageDao.getMessageById(messageId)
            if (msg != null) {
                messageDao.deleteMessageById(messageId)
                firebaseRepo.deleteLiveMessage(msg.contactId, messageId)
                if (msg.fileName.isNotBlank()) {
                    try {
                        val file = java.io.File(msg.fileName)
                        if (file.exists()) {
                            file.delete()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun deleteMessageForEveryone(messageId: Long) {
        viewModelScope.launch {
            val msg = messageDao.getMessageById(messageId) ?: return@launch
            firebaseRepo.deleteLiveMessage(msg.contactId, messageId)
            
            _deletionOutcome.emit("Checking status and sending socket event to recipient's client...")
            delay(1200) // Realistic socket latency
            
            if (msg.isSentByMe) {
                // I sent it. The recipient is the contact.
                // On the recipient's device, we check if they starred it.
                if (msg.isStarredByRecipient) {
                    // Recipient has it starred -> Ignore the delete request on their side
                    _deletionOutcome.emit("Deletion event ignored: Message is starred on the recipient's device.")
                    
                    // But on our side, since we requested deletion, it is deleted for us (Delete for Everyone request)
                    messageDao.deleteMessageById(messageId)
                    if (msg.fileName.isNotBlank()) {
                        try {
                            val file = java.io.File(msg.fileName)
                            if (file.exists()) file.delete()
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                } else {
                    // Not starred by recipient -> permanently delete from both sides
                    _deletionOutcome.emit("Successfully deleted message and media file for everyone.")
                    messageDao.deleteMessageById(messageId)
                    if (msg.fileName.isNotBlank()) {
                        try {
                            val file = java.io.File(msg.fileName)
                            if (file.exists()) file.delete()
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            } else {
                // Recipient sent it. "Me" is the recipient.
                // Before removing the message on recipient's device (our device), check if isStarred == true.
                if (msg.isStarred) {
                    _deletionOutcome.emit("Deletion request from ${msg.senderName} ignored: Message is starred on your device.")
                } else {
                    _deletionOutcome.emit("${msg.senderName} deleted a message for everyone.")
                    messageDao.deleteMessageById(messageId)
                    if (msg.fileName.isNotBlank()) {
                        try {
                            val file = java.io.File(msg.fileName)
                            if (file.exists()) file.delete()
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
        }
    }

    fun toggleMessageStarredByRecipient(messageId: Long, isStarred: Boolean) {
        viewModelScope.launch {
            messageDao.updateMessageStarredByRecipient(messageId, isStarred)
        }
    }
}
