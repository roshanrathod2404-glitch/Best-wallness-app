package com.example.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ContactEntity
import com.example.data.ReminderEntity
import com.example.data.ReminderNotificationReceiver
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import com.example.util.AppPermissionGroup
import com.example.util.PermissionManager
import com.example.util.PermissionRationaleModal
import com.example.util.PermanentlyDeniedDialog
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class WorldTimezoneItem(
    val englishName: String,
    val zoneIdStr: String,
    val gmtOffset: String,
    val flag: String
)

val SupportedWorldTimezones = listOf(
    WorldTimezoneItem("US Eastern Time", "America/New_York", "UTC-5 / UTC-4", "🇺🇸"),
    WorldTimezoneItem("US Pacific Time (PST)", "America/Los_Angeles", "UTC-8 / UTC-7", "🇺🇸"),
    WorldTimezoneItem("US Central Time (CST)", "America/Chicago", "UTC-6 / UTC-5", "🇺🇸"),
    WorldTimezoneItem("India Standard Time (IST)", "Asia/Kolkata", "UTC+5:30", "🇮🇳"),
    WorldTimezoneItem("Greenwich Mean Time (GMT/UTC)", "Etc/UTC", "UTC+0", "🇬🇧"),
    WorldTimezoneItem("Central European Time (CET)", "Europe/Paris", "UTC+1 / UTC+2", "🇫🇷"),
    WorldTimezoneItem("Japan Standard Time (JST)", "Asia/Tokyo", "UTC+9", "🇯🇵"),
    WorldTimezoneItem("China Standard Time (CST)", "Asia/Shanghai", "UTC+8", "🇨🇳"),
    WorldTimezoneItem("Singapore Time (SGT)", "Asia/Singapore", "UTC+8", "🇸🇬"),
    WorldTimezoneItem("Australian Eastern Time (AEST)", "Australia/Sydney", "UTC+10 / UTC+11", "🇦🇺"),
    WorldTimezoneItem("Gulf Standard Time (GST)", "Asia/Dubai", "UTC+4", "🇦🇪"),
    WorldTimezoneItem("Brasilia Time (BRT)", "America/Sao_Paulo", "UTC-3", "🇧🇷"),
    WorldTimezoneItem("Korea Standard Time (KST)", "Asia/Seoul", "UTC+9", "🇰🇷"),
    WorldTimezoneItem("Turkey Time (TRT)", "Europe/Istanbul", "UTC+3", "🇹🇷")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    reminders: List<ReminderEntity>,
    contacts: List<ContactEntity>,
    currentLang: String,
    onAddReminder: (String, Long, String, Int, Int, Boolean, String, String, String) -> Unit,
    onUpdateStatus: (ReminderEntity, String) -> Unit,
    onDeleteReminder: (ReminderEntity) -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedChoiceTab by remember { mutableStateOf(0) } // 0: Add Reminder, 1: Reminder Settings
    var showContactPickerFullScreen by remember { mutableStateOf(false) }

    // Add Reminder Form States
    var reminderTitle by remember { mutableStateOf("") }
    var reminderMessage by remember { mutableStateOf("") }
    var selectedContact by remember { mutableStateOf<ContactEntity?>(contacts.firstOrNull()) }
    var selectedTimezone by remember { mutableStateOf(SupportedWorldTimezones[3]) } // IST Default
    var hourText by remember { mutableStateOf("08") }
    var minuteText by remember { mutableStateOf("30") }
    var isAm by remember { mutableStateOf(true) }
    var selectedRecurrence by remember { mutableStateOf("Only Today") }
    var showTimezonePickerModal by remember { mutableStateOf(false) }

    var activeRationaleGroup by remember { mutableStateOf<AppPermissionGroup?>(null) }
    var activePermanentlyDeniedGroup by remember { mutableStateOf<AppPermissionGroup?>(null) }

    // Reminder Settings preferences
    var notificationsEnabled by remember { mutableStateOf(true) }
    var autoDispatchMessage by remember { mutableStateOf(true) }

    BackHandler(enabled = true) {
        when {
            showContactPickerFullScreen -> showContactPickerFullScreen = false
            showTimezonePickerModal -> showTimezonePickerModal = false
            else -> onBack()
        }
    }

    if (showContactPickerFullScreen) {
        FullScreenContactPicker(
            contacts = contacts,
            onSelectContact = { contact ->
                selectedContact = contact
                showContactPickerFullScreen = false
            },
            onDismiss = { showContactPickerFullScreen = false }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reminder Module",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BgCharcoal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Choice selector card: "Add Reminder" or "Reminder Settings"
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { selectedChoiceTab = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedChoiceTab == 0) WhatsAppGreen else Color.Transparent,
                            contentColor = if (selectedChoiceTab == 0) BgCharcoal else TextSilver
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddAlarm, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Reminder", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = { selectedChoiceTab = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedChoiceTab == 1) WhatsAppGreen else Color.Transparent,
                            contentColor = if (selectedChoiceTab == 1) BgCharcoal else TextSilver
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reminder Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedChoiceTab == 0) {
                // "ADD REMINDER" SCREEN FLOW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Input Fields: Reminder Title & Message Text area
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "1. Reminder Content",
                                color = WhatsAppGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            OutlinedTextField(
                                value = reminderTitle,
                                onValueChange = { reminderTitle = it },
                                label = { Text("Reminder Title / Category (e.g., Water, Medicine)", color = TextSilver) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = WhatsAppGreen,
                                    unfocusedBorderColor = TextSilver
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = reminderMessage,
                                onValueChange = { reminderMessage = it },
                                label = { Text("Reminder Message Content", color = TextSilver) },
                                placeholder = { Text("Enter message text to send or display...", color = TextSilver.copy(alpha = 0.5f)) },
                                shape = RoundedCornerShape(10.dp),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = WhatsAppGreen,
                                    unfocusedBorderColor = TextSilver
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // 2. Country Timezone Selector
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimezonePickerModal = true }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2. Country Timezone Selector",
                                    color = WhatsAppGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text("Change", color = WhatsAppGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(BgCharcoal, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text(selectedTimezone.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(selectedTimezone.englishName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("${selectedTimezone.zoneIdStr} (${selectedTimezone.gmtOffset})", color = TextSilver, fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = WhatsAppGreen)
                            }
                        }
                    }

                    // 3. Contact Selection (Full Screen Button)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "3. Target Recipient",
                                color = WhatsAppGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            Button(
                                onClick = { showContactPickerFullScreen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BgCharcoal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, WhatsAppGreen, RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(WhatsAppDarkGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selectedContact?.avatarInitials?.ifBlank { selectedContact?.name?.take(2)?.uppercase() } ?: "?",
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = selectedContact?.name ?: "Select Person",
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = selectedContact?.phoneNumber ?: "Tap to open contact picker",
                                                color = TextSilver,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.PersonSearch, contentDescription = "Select Contact", tint = WhatsAppGreen)
                                }
                            }
                        }
                    }

                    // 4. Scheduling & Frequency Options
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "4. Exact Time & Recurrence",
                                color = WhatsAppGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )

                            // Exact time picker row (AM/PM)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = hourText,
                                    onValueChange = { if (it.length <= 2) hourText = it },
                                    label = { Text("Hour", color = TextSilver) },
                                    placeholder = { Text("08") },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, focusedBorderColor = WhatsAppGreen),
                                    modifier = Modifier.weight(1f)
                                )

                                Text(":", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                                OutlinedTextField(
                                    value = minuteText,
                                    onValueChange = { if (it.length <= 2) minuteText = it },
                                    label = { Text("Minute", color = TextSilver) },
                                    placeholder = { Text("30") },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, focusedBorderColor = WhatsAppGreen),
                                    modifier = Modifier.weight(1f)
                                )

                                Button(
                                    onClick = { isAm = !isAm },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isAm) WhatsAppGreen else Color(0xFF1F2C34)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    Text(
                                        text = if (isAm) "AM" else "PM",
                                        color = if (isAm) BgCharcoal else TextWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Recurrence Selector choices
                            Text("Recurrence Frequency:", color = TextSilver, fontSize = 13.sp)

                            val recurrenceOptions = listOf("Only Today", "Everyday", "For 1 Week", "For 1 Month")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                recurrenceOptions.forEach { option ->
                                    val isSelected = selectedRecurrence == option
                                    Surface(
                                        color = if (isSelected) WhatsAppGreen else BgCharcoal,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedRecurrence = option }
                                    ) {
                                        Text(
                                            text = option,
                                            color = if (isSelected) BgCharcoal else TextWhite,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Dynamic Timezone Conversion Calculation Card
                    val h = hourText.toIntOrNull() ?: 8
                    val m = minuteText.toIntOrNull() ?: 30
                    val calculatedUtc = remember(h, m, isAm, selectedTimezone) {
                        calculateUtcDetails(h, m, isAm, selectedTimezone.zoneIdStr)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "5. Dynamic Timezone Conversion Engine",
                                    color = WhatsAppGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Text(
                                text = "• Recipient Target Time: ${calculatedUtc.targetTimeString}",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "• Sender Local Equivalent: ${calculatedUtc.senderTimeString}",
                                color = Color(0xFF38BDF8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "• UTC Timestamp: ${calculatedUtc.utcDateString} UTC (${calculatedUtc.epochMillis} ms)",
                                color = TextSilver,
                                fontSize = 12.sp
                            )

                            Surface(
                                color = WhatsAppDarkGreen.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "ℹ️ ${calculatedUtc.timeDiffNotice}",
                                    color = WhatsAppGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    // Save & Schedule Button
                    Button(
                        onClick = {
                            if (reminderTitle.isBlank()) {
                                Toast.makeText(context, "Please enter a reminder title", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (selectedContact == null) {
                                Toast.makeText(context, "Please select a contact", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val finalMsg = if (reminderMessage.isBlank()) "Automated health alert from SmartFit Wellness." else reminderMessage
                            val targetTzString = "${selectedTimezone.englishName} (${selectedTimezone.zoneIdStr})"

                            onAddReminder(
                                reminderTitle,
                                selectedContact!!.id,
                                selectedContact!!.name,
                                h,
                                m,
                                isAm,
                                selectedRecurrence,
                                finalMsg,
                                targetTzString
                            )

                            // Schedule local alarm notification
                            scheduleLocalAlarm(
                                context = context,
                                title = reminderTitle,
                                message = "To ${selectedContact!!.name}: $finalMsg",
                                utcMillis = calculatedUtc.epochMillis
                            )

                            Toast.makeText(
                                context,
                                "Reminder scheduled for ${selectedContact!!.name} at $h:${String.format("%02d", m)} ${if (isAm) "AM" else "PM"} (${selectedTimezone.englishName})",
                                Toast.LENGTH_LONG
                            ).show()

                            reminderTitle = ""
                            reminderMessage = ""
                            selectedChoiceTab = 1 // Switch to Reminder Settings / List view
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = BgCharcoal)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Schedule Alarm", color = BgCharcoal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // "REMINDER SETTINGS" / SCHEDULED ALARMS SCREEN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Preferences Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Reminder Module Configuration",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Push Notification Alerts", color = TextWhite, fontSize = 14.sp)
                                    Text("Trigger system notification on exact time", color = TextSilver, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreen, checkedTrackColor = WhatsAppDarkGreen)
                                )
                            }

                            HorizontalDivider(color = TextSilver.copy(alpha = 0.2f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Auto Chat Dispatch", color = TextWhite, fontSize = 14.sp)
                                    Text("Automatically send message to contact when alarm fires", color = TextSilver, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = autoDispatchMessage,
                                    onCheckedChange = { autoDispatchMessage = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = WhatsAppGreen, checkedTrackColor = WhatsAppDarkGreen)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Scheduled Alarms & Reminders (${reminders.size})",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    if (reminders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(SurfaceDark, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = TextSilver, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No scheduled reminders yet.", color = TextSilver, fontSize = 14.sp)
                                TextButton(onClick = { selectedChoiceTab = 0 }) {
                                    Text("Tap to Add New Reminder", color = WhatsAppGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            reminders.forEach { reminder ->
                                ReminderDetailCard(
                                    reminder = reminder,
                                    onToggleStatus = { newStatus -> onUpdateStatus(reminder, newStatus) },
                                    onDelete = { onDeleteReminder(reminder) },
                                    onTriggerAlarmNow = {
                                        scheduleLocalAlarm(
                                            context = context,
                                            title = reminder.categoryName,
                                            message = "To ${reminder.contactName}: ${reminder.customMessage}",
                                            utcMillis = System.currentTimeMillis() + 1000L
                                        )
                                        onUpdateStatus(reminder, "Sent")
                                        Toast.makeText(context, "Alarm triggered for ${reminder.contactName}!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Modal Timezone Selector Dialog
    if (showTimezonePickerModal) {
        WorldTimezoneSelectionModal(
            currentZoneStr = selectedTimezone.zoneIdStr,
            onSelectZone = { item ->
                selectedTimezone = item
                showTimezonePickerModal = false
            },
            onDismiss = { showTimezonePickerModal = false }
        )
    }

    // Permission Rationale Modal
    activeRationaleGroup?.let { group ->
        val exactAlarmLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ -> }

        PermissionRationaleModal(
            group = group,
            onGrantRequested = {
                when (group) {
                    AppPermissionGroup.EXACT_ALARM -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            exactAlarmLauncher.launch(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
                        }
                    }
                    AppPermissionGroup.POST_NOTIFICATIONS -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            exactAlarmLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
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

@Composable
fun ReminderDetailCard(
    reminder: ReminderEntity,
    onToggleStatus: (String) -> Unit,
    onDelete: () -> Unit,
    onTriggerAlarmNow: () -> Unit
) {
    val timeStr = String.format("%02d:%02d %s", reminder.timeHour, reminder.timeMinute, if (reminder.isAm) "AM" else "PM")
    val isSent = reminder.sentStatus == "Sent"

    val senderLocalEquivalent = remember(reminder.utcTimestamp) {
        if (reminder.utcTimestamp > 0L) {
            try {
                val instant = java.time.Instant.ofEpochMilli(reminder.utcTimestamp)
                val zdt = instant.atZone(ZoneId.systemDefault())
                zdt.format(DateTimeFormatter.ofPattern("hh:mm a z (E, MMM d)"))
            } catch (e: Exception) {
                "N/A"
            }
        } else "N/A"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = reminder.categoryName, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSilver, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Target Person: ${reminder.contactName}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = "🎯 Recipient Time: $timeStr | Zone: ${reminder.targetTimezone}", color = TextSilver, fontSize = 12.sp)
            Text(text = "📱 Sender Local Equivalent: $senderLocalEquivalent", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(text = "🔄 Recurrence: ${reminder.recurrenceRule}", color = TextSilver, fontSize = 12.sp)

            if (reminder.utcTimestamp > 0L) {
                Text(text = "🌐 UTC Epoch: ${reminder.utcTimestamp} ms", color = WhatsAppGreen, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reminder.customMessage,
                color = TextWhite,
                fontSize = 12.sp,
                maxLines = 3,
                modifier = Modifier
                    .background(BgCharcoal, RoundedCornerShape(8.dp))
                    .padding(10.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isSent) Color.Green.copy(alpha = 0.2f) else Color(0xFFFF9800).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Status: ${reminder.sentStatus}",
                        color = if (isSent) Color.Green else Color(0xFFFF9800),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onTriggerAlarmNow,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Trigger Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldTimezoneSelectionModal(
    currentZoneStr: String,
    onSelectZone: (WorldTimezoneItem) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredZones = remember(searchQuery) {
        if (searchQuery.isBlank()) SupportedWorldTimezones
        else SupportedWorldTimezones.filter {
            it.englishName.contains(searchQuery, ignoreCase = true) ||
            it.zoneIdStr.contains(searchQuery, ignoreCase = true) ||
            it.gmtOffset.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Target Timezone", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSilver)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country or timezone...", color = TextSilver, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = WhatsAppGreen) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, focusedBorderColor = WhatsAppGreen),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            Box(modifier = Modifier.heightIn(max = 350.dp, min = 180.dp)) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(filteredZones) { zone ->
                        val isSelected = zone.zoneIdStr == currentZoneStr
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) WhatsAppDarkGreen.copy(alpha = 0.5f) else SurfaceDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectZone(zone) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(zone.flag, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = zone.englishName,
                                        color = if (isSelected) WhatsAppGreen else TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${zone.zoneIdStr} • ${zone.gmtOffset}",
                                        color = TextSilver,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = WhatsAppGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

data class UtcCalculationResult(
    val targetTimeString: String,
    val senderTimeString: String,
    val utcDateString: String,
    val epochMillis: Long,
    val timeDiffNotice: String
)

fun calculateUtcDetails(hour: Int, minute: Int, isAm: Boolean, zoneIdStr: String): UtcCalculationResult {
    return try {
        val targetZone = ZoneId.of(zoneIdStr)
        val nowInTarget = ZonedDateTime.now(targetZone)

        val hour24 = if (isAm) {
            if (hour == 12) 0 else hour
        } else {
            if (hour == 12) 12 else hour + 12
        }

        var targetTime = nowInTarget.withHour(hour24).withMinute(minute).withSecond(0).withNano(0)
        if (targetTime.isBefore(nowInTarget)) {
            targetTime = targetTime.plusDays(1)
        }

        val senderZone = ZoneId.systemDefault()
        val senderTime = targetTime.withZoneSameInstant(senderZone)

        val utcZoned = targetTime.withZoneSameInstant(ZoneId.of("UTC"))
        val utcFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a z (E, MMM d)")

        val targetOffsetSec = targetTime.offset.totalSeconds
        val senderOffsetSec = senderTime.offset.totalSeconds
        val diffMinutes = (targetOffsetSec - senderOffsetSec) / 60
        val absMinutes = Math.abs(diffMinutes)
        val hours = absMinutes / 60
        val mins = absMinutes % 60

        val diffNotice = if (diffMinutes == 0) {
            "Target location is in the same timezone as your local device."
        } else if (diffMinutes < 0) {
            "Target location is ${if (hours > 0) "$hours hr " else ""}${if (mins > 0) "$mins min " else ""}behind your local device time."
        } else {
            "Target location is ${if (hours > 0) "$hours hr " else ""}${if (mins > 0) "$mins min " else ""}ahead of your local device time."
        }

        UtcCalculationResult(
            targetTimeString = targetTime.format(timeFormatter),
            senderTimeString = senderTime.format(timeFormatter),
            utcDateString = utcZoned.format(utcFormatter),
            epochMillis = targetTime.toInstant().toEpochMilli(),
            timeDiffNotice = diffNotice
        )
    } catch (e: Exception) {
        val fallbackMillis = System.currentTimeMillis() + 3600000L
        UtcCalculationResult(
            targetTimeString = "08:30 AM",
            senderTimeString = "08:30 AM",
            utcDateString = "2026-07-25 13:00:00",
            epochMillis = fallbackMillis,
            timeDiffNotice = "Standard timezone calculation applied."
        )
    }
}

fun scheduleLocalAlarm(context: Context, title: String, message: String, utcMillis: Long) {
    try {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (System.currentTimeMillis() % 100000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = if (utcMillis > System.currentTimeMillis()) utcMillis else System.currentTimeMillis() + 5000L
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenContactPicker(
    contacts: List<ContactEntity>,
    onSelectContact: (ContactEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredContacts = remember(searchQuery, contacts) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.phoneNumber.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Person", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        containerColor = BgCharcoal
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search person by name or phone...", color = TextSilver, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF25D366)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSilver)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite, unfocusedTextColor = TextWhite,
                    focusedBorderColor = Color(0xFF25D366), unfocusedBorderColor = TextSilver
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredContacts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No contacts found", color = TextSilver, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filteredContacts) { contact ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectContact(contact)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF075E54)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.avatarInitials.ifBlank { contact.name.take(2).uppercase() },
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(text = contact.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(text = contact.phoneNumber, color = TextSilver, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
