package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import com.example.util.AppPermissionGroup
import com.example.util.PermissionManager

@Composable
fun PermissionGatewayScreen(
    currentLang: String = "English",
    onPermissionsCompleted: () -> Unit
) {
    val context = LocalContext.current
    var permissionStep by remember { mutableIntStateOf(0) } // 0: Idle Overview, 1: Camera, 2: Audio, 3: Media, 4: Contacts, 5: Notifications, 6: Battery

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> permissionStep = 2 }

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> permissionStep = 3 }

    val mediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> permissionStep = 4 }

    val contactsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> permissionStep = 5 }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> permissionStep = 6 }

    LaunchedEffect(permissionStep) {
        when (permissionStep) {
            1 -> cameraLauncher.launch(Manifest.permission.CAMERA)
            2 -> audioLauncher.launch(Manifest.permission.RECORD_AUDIO)
            3 -> {
                val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                mediaLauncher.launch(perms)
            }
            4 -> contactsLauncher.launch(Manifest.permission.READ_CONTACTS)
            5 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    permissionStep = 6
                }
            }
            6 -> {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
                onPermissionsCompleted()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF00A884),
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = Strings.get("permissions_title", currentLang),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "To enable full functionality like live camera, voice notes, media sharing, and exact reminder alerts, please grant the following permissions:",
                    fontSize = 12.sp,
                    color = TextSilver,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val groups = listOf(
                    AppPermissionGroup.CAMERA,
                    AppPermissionGroup.RECORD_AUDIO,
                    AppPermissionGroup.GALLERY_AND_MEDIA,
                    AppPermissionGroup.CONTACTS,
                    AppPermissionGroup.POST_NOTIFICATIONS,
                    AppPermissionGroup.EXACT_ALARM
                )

                groups.forEach { group ->
                    val isGranted = PermissionManager.isGroupGranted(context, group)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFF1E262C), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = group.icon,
                            contentDescription = null,
                            tint = if (isGranted) Color(0xFF00A884) else Color(0xFF8696A0),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = group.title,
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = group.description,
                                color = TextSilver,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        permissionStep = 1 // Start sequential runtime requests
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Grant & Continue",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
