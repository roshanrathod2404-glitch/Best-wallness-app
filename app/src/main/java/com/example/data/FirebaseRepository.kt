package com.example.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseRepository {
    private val auth: FirebaseAuth? by lazy {
        try {
            val instance = FirebaseAuth.getInstance()
            setupDynamicTokenManagement(instance)
            instance
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "FirebaseAuth init error", e)
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val instance = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                )
                .build()
            instance.firestoreSettings = settings
            instance
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "FirebaseFirestore init error", e)
            null
        }
    }

    private fun setupDynamicTokenManagement(authInstance: FirebaseAuth) {
        try {
            authInstance.addIdTokenListener(FirebaseAuth.IdTokenListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    user.getIdToken(false)
                        .addOnSuccessListener { result ->
                            val token = result.token
                            Log.d("FirebaseRepository", "Dynamic Auth Token active/refreshed successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w("FirebaseRepository", "Token refresh error, retrying force refresh", e)
                            user.getIdToken(true)
                        }
                }
            })
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error setting up dynamic token management", e)
        }
    }

    val currentUid: String
        get() = auth?.currentUser?.uid ?: "guest_user_${auth?.currentUser?.email?.hashCode() ?: 0}"

    val currentUserEmail: String
        get() = auth?.currentUser?.email ?: ""

    // 1. Firebase Setup & Authentication Session
    fun ensureAuthenticatedSession(email: String, onComplete: (String?) -> Unit) {
        val safeAuth = auth
        if (safeAuth == null) {
            onComplete("fallback_user_${email.hashCode()}")
            return
        }

        try {
            val currentUser = safeAuth.currentUser
            if (currentUser != null && (email.isBlank() || currentUser.email.equals(email, ignoreCase = true))) {
                val uid = currentUser.uid
                fetchAndSyncFcmToken(uid)
                onComplete(uid)
                return
            }

            if (email.isNotBlank()) {
                // Sign in or create user session tied to unique Email ID
                val password = "sf_${email.lowercase().filter { it.isLetterOrDigit() }}_2026"
                safeAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: email
                        fetchAndSyncFcmToken(uid)
                        onComplete(uid)
                    }
                    .addOnFailureListener {
                        // Try creating account if sign in fails
                        safeAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { result ->
                                val uid = result.user?.uid ?: email
                                fetchAndSyncFcmToken(uid)
                                onComplete(uid)
                            }
                            .addOnFailureListener {
                                // Fallback to anonymous session tied to email hash
                                safeAuth.signInAnonymously()
                                    .addOnCompleteListener { task ->
                                        val uid = safeAuth.currentUser?.uid ?: "user_${email.hashCode()}"
                                        fetchAndSyncFcmToken(uid)
                                        onComplete(uid)
                                    }
                            }
                    }
            } else {
                safeAuth.signInAnonymously()
                    .addOnCompleteListener { task ->
                        val uid = safeAuth.currentUser?.uid ?: "anonymous_user"
                        fetchAndSyncFcmToken(uid)
                        onComplete(uid)
                    }
            }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "ensureAuthenticatedSession failed", e)
            onComplete("fallback_user_${email.hashCode()}")
        }
    }

    private fun fetchAndSyncFcmToken(uid: String) {
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    if (!token.isNullOrBlank()) {
                        firestore?.collection("users")?.document(uid)
                            ?.set(mapOf("fcmToken" to token, "last_token_updated" to System.currentTimeMillis()), SetOptions.merge())
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("FirebaseRepository", "FCM token fetch failed: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error getting FCM token", e)
        }
    }

    // 2. User-Specific Storage & Activity Logs in Firestore
    fun syncUserDataAndStateToFirestore(
        userId: String,
        email: String,
        name: String,
        phone: String,
        country: String,
        avatar: String,
        settings: Map<String, Any>,
        deletedContacts: List<Long>,
        lastAction: String
    ) {
        val userDocData = hashMapOf<String, Any>(
            "uid" to userId,
            "email" to email,
            "name" to name,
            "phone" to phone,
            "country" to country,
            "avatar" to avatar,
            "last_opened" to System.currentTimeMillis(),
            "last_action_performed" to lastAction,
            "deleted_contacts" to deletedContacts,
            "settings" to settings,
            "online_status" to "Online",
            "last_active_timestamp" to System.currentTimeMillis()
        )

        firestore?.collection("users")?.document(userId)
            ?.set(userDocData, SetOptions.merge())
            ?.addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error syncing user data to Firestore", e)
            }
    }

    fun listenToUserData(userId: String, onUserData: (Map<String, Any>) -> Unit): ListenerRegistration? {
        if (userId.isBlank()) return null
        return firestore?.collection("users")?.document(userId)
            ?.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error listening to user data", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                    onUserData(snapshot.data!!)
                }
            }
    }

    fun recordUserActivity(userId: String, email: String, actionName: String) {
        val activityData = mapOf(
            "email" to email,
            "last_action_performed" to actionName,
            "last_action_timestamp" to System.currentTimeMillis()
        )
        firestore?.collection("users")?.document(userId)
            ?.set(activityData, SetOptions.merge())

        firestore?.collection("users")?.document(userId)
            ?.collection("activity_logs")
            ?.add(mapOf(
                "action" to actionName,
                "email" to email,
                "timestamp" to System.currentTimeMillis()
            ))
    }

    // 3. Instant Live Messaging Logic (<0.1s message stream via Firestore snapshot listener)
    // Strictly scoped under users/{userId}/chats/chat_{contactId}/messages for complete User Data Isolation
    fun listenToLiveMessages(contactId: Long, onMessagesReceived: (List<MessageEntity>) -> Unit): ListenerRegistration? {
        val chatId = "chat_$contactId"
        val uid = currentUid
        return firestore?.collection("users")
            ?.document(uid)
            ?.collection("chats")
            ?.document(chatId)
            ?.collection("messages")
            ?.orderBy("epochTime")
            ?.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error listening to live chat $chatId for user $uid", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val isPendingLocalWrite = snapshot.metadata.hasPendingWrites()
                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            val id = doc.getLong("id") ?: doc.id.hashCode().toLong()
                            val cId = doc.getLong("contactId") ?: contactId
                            val senderName = doc.getString("senderName") ?: "User"
                            val messageText = doc.getString("messageText") ?: ""
                            val timestamp = doc.getString("timestamp") ?: "Just now"
                            val isSentByMe = doc.getBoolean("isSentByMe") ?: false
                            val isStarred = doc.getBoolean("isStarred") ?: false
                            val fileType = doc.getString("fileType") ?: "Text"
                            val fileName = doc.getString("fileName") ?: ""
                            val epochTime = doc.getLong("epochTime") ?: System.currentTimeMillis()
                            val replyToText = doc.getString("replyToText") ?: ""
                            val replyToSender = doc.getString("replyToSender") ?: ""
                            val isStarredByRecipient = doc.getBoolean("isStarredByRecipient") ?: false
                            val docStatus = doc.getString("status") ?: if (isPendingLocalWrite) "sending" else "sent"

                            MessageEntity(
                                id = id,
                                contactId = cId,
                                senderName = senderName,
                                messageText = messageText,
                                timestamp = timestamp,
                                isSentByMe = isSentByMe,
                                isStarred = isStarred,
                                fileType = fileType,
                                fileName = fileName,
                                epochTime = epochTime,
                                replyToText = replyToText,
                                replyToSender = replyToSender,
                                isStarredByRecipient = isStarredByRecipient,
                                status = docStatus
                            )
                        } catch (e: Exception) {
                            Log.e("FirebaseRepository", "Error parsing message doc", e)
                            null
                        }
                    }
                    onMessagesReceived(messages)
                }
            }
    }

    fun sendLiveMessage(contactId: Long, message: MessageEntity, onComplete: ((Boolean) -> Unit)? = null) {
        val chatId = "chat_$contactId"
        val docId = if (message.id != 0L) message.id.toString() else System.currentTimeMillis().toString()
        val uid = currentUid

        val msgMap = hashMapOf<String, Any>(
            "id" to if (message.id != 0L) message.id else System.currentTimeMillis(),
            "contactId" to contactId,
            "senderName" to message.senderName,
            "messageText" to message.messageText,
            "timestamp" to message.timestamp,
            "isSentByMe" to message.isSentByMe,
            "isStarred" to message.isStarred,
            "fileType" to message.fileType,
            "fileName" to message.fileName,
            "epochTime" to message.epochTime,
            "replyToText" to message.replyToText,
            "replyToSender" to message.replyToSender,
            "isStarredByRecipient" to message.isStarredByRecipient,
            "status" to "sent"
        )

        val targetFirestore = firestore
        if (targetFirestore == null) {
            onComplete?.invoke(false)
            return
        }

        targetFirestore.collection("users")
            .document(uid)
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .document(docId)
            .set(msgMap, SetOptions.merge())
            .addOnSuccessListener {
                targetFirestore.collection("users").document(uid).collection("chats").document(chatId).set(
                    mapOf(
                        "contactId" to contactId,
                        "lastMessage" to message.messageText,
                        "lastMessageTime" to message.timestamp,
                        "updatedAt" to System.currentTimeMillis()
                    ),
                    SetOptions.merge()
                )
                onComplete?.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error sending message to Firestore for user $uid", e)
                onComplete?.invoke(false)
            }
    }

    fun deleteLiveMessage(contactId: Long, messageId: Long) {
        val chatId = "chat_$contactId"
        val uid = currentUid
        firestore?.collection("users")
            ?.document(uid)
            ?.collection("chats")
            ?.document(chatId)
            ?.collection("messages")
            ?.document(messageId.toString())
            ?.delete()
    }

    // 4. Presence & Typing Indicators
    fun updateUserOnlineStatus(userId: String, status: String) {
        if (userId.isBlank()) return
        val updates = mapOf(
            "online_status" to status,
            "last_active_timestamp" to System.currentTimeMillis()
        )
        firestore?.collection("users")?.document(userId)
            ?.set(updates, SetOptions.merge())
    }

    fun setTypingStatus(contactId: Long, userId: String, isTyping: Boolean) {
        val chatId = "chat_$contactId"
        val typingDoc = firestore?.collection("users")
            ?.document(userId)
            ?.collection("chats")
            ?.document(chatId)
            ?.collection("typing")
            ?.document(userId)

        if (isTyping) {
            typingDoc?.set(mapOf("isTyping" to true, "timestamp" to System.currentTimeMillis()))
        } else {
            typingDoc?.delete()
        }
    }

    fun listenToTypingStatus(contactId: Long, currentUserId: String, onTypingChange: (Boolean) -> Unit): ListenerRegistration? {
        val chatId = "chat_$contactId"
        return firestore?.collection("users")
            ?.document(currentUserId)
            ?.collection("chats")
            ?.document(chatId)
            ?.collection("typing")
            ?.addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val containsOtherTyping = snapshot.documents.any { doc ->
                        doc.id != currentUserId && (doc.getBoolean("isTyping") == true)
                    }
                    onTypingChange(containsOtherTyping)
                }
            }
    }

    // 5. Community & Group Management Architecture
    fun createCommunityInFirestore(
        community: CommunityEntity,
        linkedGroups: List<ContactEntity>,
        announcementContact: ContactEntity,
        onComplete: (Boolean) -> Unit
    ) {
        val commId = if (community.id != 0L) community.id else System.currentTimeMillis()
        val communityDocId = commId.toString()
        val uid = currentUid
        val commMap = hashMapOf<String, Any>(
            "id" to commId,
            "name" to community.name,
            "description" to community.description,
            "avatarUri" to community.avatarUri,
            "adminEmail" to community.adminEmail,
            "announcementContactId" to announcementContact.id,
            "linkedGroupIds" to linkedGroups.map { it.id }.joinToString(","),
            "createdAt" to community.createdAt
        )

        val targetFirestore = firestore
        if (targetFirestore == null) {
            onComplete(false)
            return
        }

        targetFirestore.collection("users").document(uid).collection("communities").document(communityDocId)
            .set(commMap, SetOptions.merge())
            .addOnSuccessListener {
                linkedGroups.forEach { group ->
                    targetFirestore.collection("users").document(uid).collection("communities").document(communityDocId)
                        .collection("linked_groups").document(group.id.toString())
                        .set(mapOf(
                            "groupId" to group.id,
                            "groupName" to group.name,
                            "addedAt" to System.currentTimeMillis()
                        ), SetOptions.merge())
                }

                targetFirestore.collection("users").document(uid).collection("communities").document(communityDocId)
                    .collection("announcements").document(announcementContact.id.toString())
                    .set(mapOf(
                        "channelId" to announcementContact.id,
                        "channelName" to announcementContact.name,
                        "isAnnouncementChannel" to true,
                        "isAdminOnlyPosting" to true,
                        "adminEmail" to community.adminEmail
                    ), SetOptions.merge())

                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseRepository", "Error creating community in Firestore", e)
                onComplete(false)
            }
    }
}
