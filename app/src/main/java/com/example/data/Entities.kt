package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val avatarInitials: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val onlineStatus: String,
    val isPinned: Boolean = false,
    val unreadCount: Int = 0,
    val isBlocked: Boolean = false,
    val isFavourite: Boolean = false,
    val localOverrideAvatar: String = "",
    val isCommunity: Boolean = false,
    val isGroup: Boolean = false,
    val communityId: Long = 0L,
    val isAnnouncementChannel: Boolean = false,
    val isAdminOnlyPosting: Boolean = false
)

@Entity(tableName = "communities")
data class CommunityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String,
    val avatarUri: String = "",
    val adminEmail: String = "",
    val announcementContactId: Long = 0L,
    val linkedGroupIds: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val contactId: Long,
    val senderName: String,
    val messageText: String,
    val timestamp: String,
    val isSentByMe: Boolean,
    val isStarred: Boolean = false,
    val fileType: String = "Text", // "Text", "Photo", "Video", "Document", "PDF"
    val fileName: String = "",
    val dateText: String = "",
    val timeText: String = "",
    val epochTime: Long = System.currentTimeMillis(),
    val replyToText: String = "",
    val replyToSender: String = "",
    val isStarredByRecipient: Boolean = false,
    val status: String = "sent" // "sending", "sent", "delivered", "read"
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val categoryName: String,
    val contactId: Long,
    val contactName: String,
    val timeHour: Int,
    val timeMinute: Int,
    val isAm: Boolean,
    val recurrenceRule: String, // "Only Today", "Everyday", "For 1 Week", "For 1 Month"
    val customMessage: String, // Up to 5000 words
    val sentStatus: String, // "Scheduled", "Sent", "Failed"
    val targetTimezone: String = "Asia/Kolkata",
    val utcTimestamp: Long = 0L
)

@Entity(tableName = "history_items")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val contactId: Long = 0L,
    val type: String, // "Contact", "Video", "Document", "Photo", "PDF"
    val title: String, // File name or contact name
    val subtitle: String, // Description or details
    val senderName: String,
    val dateText: String, // e.g. "14 June, 2026"
    val timeText: String, // e.g. "04:30 PM"
    val mediaUrl: String = "" // Image/Video preview
)

@Entity(tableName = "ChatRoomsTable")
data class ChatRoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val groupName: String,
    val participantCount: Int,
    val participantIds: String,
    val lastMessage: String = "Group created",
    val lastMessageTime: String = "Just now",
    val avatarInitials: String = "GRP",
    val isPinned: Boolean = true,
    val unreadCount: Int = 0
)

