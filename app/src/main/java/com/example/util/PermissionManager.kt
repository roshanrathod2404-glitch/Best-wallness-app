package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat

/**
 * Enumeration of App Feature Permission Groups with explicit user rationale and icon identifiers.
 */
enum class AppPermissionGroup(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val permissions: List<String>
) {
    CAMERA(
        title = "Camera Access",
        description = "To access the live camera, capture photos, and record videos directly in chat.",
        icon = Icons.Default.CameraAlt,
        permissions = listOf(Manifest.permission.CAMERA)
    ),
    RECORD_AUDIO(
        title = "Microphone Access",
        description = "To capture clear microphone audio for voice messages and video recording.",
        icon = Icons.Default.Mic,
        permissions = listOf(Manifest.permission.RECORD_AUDIO)
    ),
    GALLERY_AND_MEDIA(
        title = "Photos & Media Access",
        description = "To select and display photos, videos, and media files from device storage in chat.",
        icon = Icons.Default.PhotoLibrary,
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    ),
    CONTACTS(
        title = "Contacts Access",
        description = "To fetch and display authentic phone contacts inside your chat list.",
        icon = Icons.Default.Contacts,
        permissions = listOf(Manifest.permission.READ_CONTACTS)
    ),
    POST_NOTIFICATIONS(
        title = "Notifications Access",
        description = "To display real-time message popups and reminder alerts on screen.",
        icon = Icons.Default.NotificationsActive,
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
    ),
    EXACT_ALARM(
        title = "Exact Alarm & Reminders",
        description = "To trigger alarms and reminders at exact scheduled times across different timezones.",
        icon = Icons.Default.Alarm,
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.SCHEDULE_EXACT_ALARM)
        } else {
            emptyList()
        }
    )
}

object PermissionManager {

    /**
     * Helper to check if all permissions in a permission group are currently granted.
     */
    fun isGroupGranted(context: Context, group: AppPermissionGroup): Boolean {
        if (group.permissions.isEmpty()) return true
        return group.permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Redirect user directly to the application system details settings screen.
     */
    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Contextual Custom Pop-up Modal showing explicit reason for requiring a permission.
 */
@Composable
fun PermissionRationaleModal(
    group: AppPermissionGroup,
    onGrantRequested: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E262C),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00A884).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = group.icon,
                        contentDescription = group.title,
                        tint = Color(0xFF00A884),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = group.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = group.description,
                    color = Color(0xFFAEBAC1),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFAEBAC1)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374248))
                    ) {
                        Text("Not Now", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onGrantRequested()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A884),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Allow", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Permanently Denied Permission Dialog with redirect button to System App Settings.
 */
@Composable
fun PermanentlyDeniedDialog(
    group: AppPermissionGroup,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E262C),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings Required",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${group.title} Needed",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${group.description}\n\nPermission was previously denied. Please enable it in App System Settings to continue using this feature.",
                    color = Color(0xFFAEBAC1),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFAEBAC1)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374248))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            PermissionManager.openAppSettings(context)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00A884),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Open Settings", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Onboarding Overview Dialog explaining why key permissions are requested before system prompts.
 */
@Composable
fun OnboardingPermissionsDialog(
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var currentGroupIndex by remember { mutableIntStateOf(0) }
    val coreGroups = remember {
        listOf(
            AppPermissionGroup.CAMERA,
            AppPermissionGroup.RECORD_AUDIO,
            AppPermissionGroup.GALLERY_AND_MEDIA,
            AppPermissionGroup.CONTACTS,
            AppPermissionGroup.POST_NOTIFICATIONS
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (currentGroupIndex < coreGroups.size - 1) {
            currentGroupIndex++
        } else {
            onComplete()
        }
    }

    Dialog(onDismissRequest = onComplete) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E262C),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00A884).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Permissions",
                        tint = Color(0xFF00A884),
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "App Permissions Overview",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "To deliver live messaging, voice notes, media sharing, and exact alerts, SmartFit requests the following core permissions:",
                    color = Color(0xFFAEBAC1),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    coreGroups.forEach { group ->
                        val isGranted = PermissionManager.isGroupGranted(context, group)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A3942))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = group.icon,
                                contentDescription = null,
                                tint = if (isGranted) Color(0xFF00A884) else Color(0xFF8696A0),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = group.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = group.description,
                                    color = Color(0xFFAEBAC1),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            if (isGranted) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Granted",
                                    tint = Color(0xFF00A884),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val ungranted = coreGroups.firstOrNull { !PermissionManager.isGroupGranted(context, it) }
                        if (ungranted != null && ungranted.permissions.isNotEmpty()) {
                            launcher.launch(ungranted.permissions.toTypedArray())
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00A884),
                        contentColor = Color.White
                    )
                ) {
                    Text("Grant & Continue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}
