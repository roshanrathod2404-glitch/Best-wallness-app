package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE isBlocked = 0")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getContactCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<ContactEntity>)

    @Query("SELECT * FROM contacts WHERE id = :contactId")
    suspend fun getContactById(contactId: Long): ContactEntity?

    @Query("UPDATE contacts SET isPinned = :isPinned WHERE id = :contactId")
    suspend fun updateContactPinned(contactId: Long, isPinned: Boolean)

    @Query("UPDATE contacts SET unreadCount = :unreadCount WHERE id = :contactId")
    suspend fun updateContactUnread(contactId: Long, unreadCount: Int)

    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContact(contactId: Long)

    @Query("UPDATE contacts SET isBlocked = 1 WHERE id = :contactId")
    suspend fun blockContact(contactId: Long)

    @Query("UPDATE contacts SET isBlocked = :isBlocked WHERE id = :contactId")
    suspend fun updateBlockedStatus(contactId: Long, isBlocked: Boolean)

    @Query("UPDATE contacts SET isFavourite = :isFavourite WHERE id = :contactId")
    suspend fun updateFavourite(contactId: Long, isFavourite: Boolean)

    @Query("UPDATE contacts SET localOverrideAvatar = :avatarUri WHERE id = :contactId")
    suspend fun updateLocalOverrideAvatar(contactId: Long, avatarUri: String)

    @Query("UPDATE contacts SET onlineStatus = :status WHERE id = :contactId")
    suspend fun updateContactOnlineStatus(contactId: Long, status: String)

    @Query("UPDATE contacts SET lastMessage = :lastMsg, lastMessageTime = :lastMsgTime, unreadCount = CASE WHEN :incrementUnread = 1 THEN unreadCount + 1 ELSE unreadCount END WHERE id = :contactId")
    suspend fun updateLastMessage(contactId: Long, lastMsg: String, lastMsgTime: String, incrementUnread: Int = 0)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE contactId = :contactId ORDER BY id ASC")
    fun getMessagesForContact(contactId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE contactId = :contactId AND isStarred = 1 ORDER BY id ASC")
    fun getStarredMessagesForContact(contactId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE isStarred = 1 ORDER BY id DESC")
    fun getAllStarredMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET isStarred = :isStarred WHERE id = :messageId")
    suspend fun updateMessageStarred(messageId: Long, isStarred: Boolean)

    @Query("DELETE FROM messages WHERE contactId = :contactId AND isStarred = 0")
    suspend fun clearChatExceptStarred(contactId: Long)

    @Query("DELETE FROM messages WHERE contactId = :contactId AND isStarred = 0 AND epochTime < :cutoffTime")
    suspend fun deleteMessagesOlderThanExceptStarred(contactId: Long, cutoffTime: Long)

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): MessageEntity?

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("UPDATE messages SET isStarredByRecipient = :isStarred WHERE id = :messageId")
    suspend fun updateMessageStarredByRecipient(messageId: Long, isStarred: Boolean)

    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_items WHERE type = :itemType ORDER BY id DESC")
    fun getHistoryByType(itemType: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_items WHERE contactId = :contactId AND type = :itemType ORDER BY id DESC")
    fun getHistoryForContactAndType(contactId: Long, itemType: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_items ORDER BY id DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity)

    @Query("DELETE FROM history_items")
    suspend fun deleteAllHistory()
}

@Dao
interface ChatRoomDao {
    @Query("SELECT * FROM ChatRoomsTable ORDER BY id DESC")
    fun getAllChatRooms(): Flow<List<ChatRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoomEntity): Long

    @Query("DELETE FROM ChatRoomsTable")
    suspend fun deleteAllChatRooms()
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM communities ORDER BY id DESC")
    fun getAllCommunities(): Flow<List<CommunityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: CommunityEntity): Long

    @Query("SELECT * FROM communities WHERE id = :id")
    suspend fun getCommunityById(id: Long): CommunityEntity?

    @Query("DELETE FROM communities WHERE id = :id")
    suspend fun deleteCommunity(id: Long)

    @Query("DELETE FROM communities")
    suspend fun deleteAllCommunities()
}

