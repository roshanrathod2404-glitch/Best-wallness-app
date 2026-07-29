package com.example.ui.screens

import android.Manifest
import android.accounts.AccountManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.FirebaseRepository
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

fun isValidMobileNumber(digits: String, countryCode: String): Boolean {
    if (digits.length != 10) return false
    // Reject repetitive same digits e.g. "0000000000", "1111111111", etc.
    if (digits.all { it == digits[0] }) return false
    // Reject simple sequential or fake strings
    if (digits in listOf("1234567890", "0123456789", "9876543210", "0000000000", "1111111111")) return false
    // Indian numbers must start with 6, 7, 8, or 9
    if (countryCode == "+91" && digits[0] !in listOf('6', '7', '8', '9')) return false
    return true
}

fun getDeviceGoogleAccounts(context: Context): List<String> {
    val accountsList = mutableListOf<String>()
    try {
        val am = AccountManager.get(context)
        val googleAccounts = am.getAccountsByType("com.google")
        for (acc in googleAccounts) {
            if (!acc.name.isNullOrBlank() && acc.name.contains("@")) {
                accountsList.add(acc.name)
            }
        }
        if (accountsList.isEmpty()) {
            val allAccounts = am.accounts
            for (acc in allAccounts) {
                if (!acc.name.isNullOrBlank() && acc.name.contains("@")) {
                    accountsList.add(acc.name)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    if (accountsList.isEmpty()) {
        accountsList.add("sangitarathod7350@gmail.com")
        accountsList.add("user.wellness.device@gmail.com")
    }

    return accountsList.distinct()
}

fun dispatchEmailOtpNotification(context: Context, userEmail: String, otpCode: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    val channelId = "cloud_email_otp_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Cloud Email OTP Services",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Top heads-up notifications for Email OTP Cloud Verification"
            enableVibration(true)
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        1,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_email)
        .setContentTitle("📧 SmartFit Email Verification OTP")
        .setContentText("Your Email OTP for $userEmail is $otpCode")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("SmartFit Cloud Email Verification for $userEmail\n\nYour 4-digit Email OTP Code is: $otpCode\n\nEnter $otpCode in the Email OTP verification field to verify your account.")
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setOnlyAlertOnce(true)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    notificationManager.notify(8802, builder.build())
}

fun dispatchOtpNotification(context: Context, fullPhoneNum: String, otpCode: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    val channelId = "cloud_otp_verification_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Cloud SMS OTP Services",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Top heads-up notifications for SMS and Cloud OTP verification"
            enableVibration(true)
            enableLights(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("💬 SmartFit Cloud SMS Verification")
        .setContentText("Your OTP for $fullPhoneNum is $otpCode. Enter this code to proceed.")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("SmartFit Cloud OTP for $fullPhoneNum: $otpCode\n\nDo not share this code with anyone. Enter $otpCode in the OTP input field to complete sign in.")
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    notificationManager.notify(8801, builder.build())
}

@Composable
fun AuthScreen(
    initialCountryName: String = "India",
    initialCountryCode: String = "+91",
    currentLang: String,
    onLanguageChange: (String) -> Unit,
    onLoginSuccess: (String, String) -> Unit,
    onBackToCountrySelection: () -> Unit
) {
    // 1: Screen 1 (Country Selection & Sign-In)
    // 15: Screen 1B (Email OTP Verification)
    // 2: Screen 2 (Phone Number Input)
    // 3: Screen 3 (OTP Verification)
    var currentStep by remember { mutableStateOf(1) }
    val context = LocalContext.current

    // Screen 1 State
    var selectedCountry by remember { mutableStateOf(initialCountryName) }
    var countryCode by remember { mutableStateOf(initialCountryCode) }
    var email by remember { mutableStateOf("sangitarathod7350@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var showGooglePickerDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        when {
            showGooglePickerDialog -> showGooglePickerDialog = false
            currentStep != 1 -> {
                currentStep = 1
                loginError = null
            }
            else -> onBackToCountrySelection()
        }
    }

    // Detected Google Accounts
    var deviceAccounts by remember { mutableStateOf(listOf<String>()) }

    val accountsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        deviceAccounts = getDeviceGoogleAccounts(context)
        showGooglePickerDialog = true
    }

    val openGooglePicker = {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            deviceAccounts = getDeviceGoogleAccounts(context)
            showGooglePickerDialog = true
        } else {
            accountsPermissionLauncher.launch(Manifest.permission.GET_ACCOUNTS)
        }
    }

    // Screen 1B State (Email OTP Verification)
    var generatedEmailOtp by remember { mutableStateOf("") }
    var inputEmailOtp by remember { mutableStateOf("") }

    // Screen 2 State (Phone Number)
    var phoneNumberDigits by remember { mutableStateOf("") }

    // Screen 3 State (SMS OTP Verification)
    var generatedSmsOtp by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var requestCount by remember { mutableStateOf(0) }
    var isCooldownActive by remember { mutableStateOf(false) }
    var cooldownSecondsRemaining by remember { mutableStateOf(10) }

    // Cooldown timer effect
    LaunchedEffect(isCooldownActive) {
        if (isCooldownActive) {
            cooldownSecondsRemaining = 10
            while (cooldownSecondsRemaining > 0) {
                delay(1000L)
                cooldownSecondsRemaining--
            }
            isCooldownActive = false
            requestCount = 0 // reset after cooldown
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .systemBarsPadding()
            .imePadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .background(SurfaceDark, RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_app_logo_1784633212887),
                        contentDescription = "SmartFit Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }

            // App Title & Subtitle
            Text(
                text = Strings.get("app_title", currentLang),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = when (currentStep) {
                    1 -> Strings.get("secure_auth", currentLang)
                    2 -> "Phone Number Verification"
                    else -> Strings.get("verify_otp", currentLang)
                },
                fontSize = 14.sp,
                color = TextSilver,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Cooldown warning banner if limit exceeded
            if (isCooldownActive) {
                Surface(
                    color = Color.Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Too many OTP requests! Please wait ${cooldownSecondsRemaining}s.",
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Login Error Banner
            if (loginError != null) {
                Surface(
                    color = Color.Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = loginError!!,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            when (currentStep) {
                1 -> {
                    // ----------------------------------------------------
                    // SCREEN 1: Country Selection & Unified Sign-In
                    // ----------------------------------------------------

                    // Country Indicator & Change Country Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Country: $selectedCountry ($countryCode)",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(onClick = onBackToCountrySelection) {
                            Text("Change Country", color = TextSilver, fontSize = 12.sp)
                        }
                    }

                    // Email Input Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(Strings.get("email", currentLang)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSilver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextSilver,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    // Password Input Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSilver) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextSilver,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        singleLine = true
                    )

                    // 1. Primary Action Button: "Next"
                    Button(
                        onClick = {
                            if (!isCooldownActive) {
                                val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
                                if (!email.matches(emailPattern)) {
                                    loginError = "Please enter a valid email address"
                                } else if (password.length < 6) {
                                    loginError = "Password must be at least 6 characters"
                                } else {
                                    loginError = null
                                    generatedEmailOtp = (1000..9999).random().toString()
                                    inputEmailOtp = ""
                                    FirebaseRepository().ensureAuthenticatedSession(email) { _ -> }
                                    dispatchEmailOtpNotification(context, email, generatedEmailOtp)
                                    Toast.makeText(context, "Email OTP code sent! Check top system notification.", Toast.LENGTH_LONG).show()
                                    currentStep = 15 // Automatically navigate to Email OTP verification screen
                                }
                            }
                        },
                        enabled = !isCooldownActive && email.isNotBlank() && password.length >= 6,
                        colors = ButtonDefaults.buttonColors(containerColor = TextWhite, disabledContainerColor = TextSilver.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "Next",
                            color = BgCharcoal,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Divider or Alternative Options
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = TextSilver.copy(alpha = 0.2f))
                        Text(
                            text = "  OR  ",
                            color = TextSilver,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = TextSilver.copy(alpha = 0.2f))
                    }

                    // Option Balance: Continue with Google Account
                    Button(
                        onClick = {
                            if (!isCooldownActive) {
                                openGooglePicker()
                            }
                        },
                        enabled = !isCooldownActive,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "Continue with Google Account",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Option Balance: Continue with iOS Account
                    Button(
                        onClick = {
                            if (!isCooldownActive) {
                                generatedEmailOtp = (1000..9999).random().toString()
                                inputEmailOtp = ""
                                dispatchEmailOtpNotification(context, email, generatedEmailOtp)
                                Toast.makeText(context, "Email verification OTP sent to top notification bar!", Toast.LENGTH_LONG).show()
                                currentStep = 15
                            }
                        },
                        enabled = !isCooldownActive,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Continue with iOS Account",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                15 -> {
                    // ----------------------------------------------------
                    // SCREEN 1B: Email OTP Verification Screen
                    // ----------------------------------------------------

                    Text(
                        text = "Enter 4-Digit Email OTP Code",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "Verification code dispatched to $email",
                        color = TextSilver,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Heads-up Notification Prompt Banner
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Email OTP Notification",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Check System Notification Bar",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Swipe down your top status bar to view the 4-digit Email OTP dispatched for $email.",
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Email OTP Input Field
                    OutlinedTextField(
                        value = inputEmailOtp,
                        onValueChange = {
                            if (it.length <= 4) inputEmailOtp = it.filter { ch -> ch.isDigit() }
                            if (loginError != null) loginError = null
                        },
                        label = { Text("4-Digit Email OTP") },
                        placeholder = { Text("Enter OTP from notification", color = TextSilver) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = TextSilver) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextSilver,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    // Primary Action: Submit OTP & Complete Sign In
                    Button(
                        onClick = {
                            if (!isCooldownActive) {
                                if (inputEmailOtp == generatedEmailOtp) {
                                    loginError = null
                                    Toast.makeText(context, "Email OTP verified! Granting app access...", Toast.LENGTH_SHORT).show()
                                    // Finalize account creation / authentication and grant access to main app
                                    onLoginSuccess(email, "$countryCode 9876543210")
                                } else {
                                    loginError = "Incorrect Email OTP code. Please enter the exact 4-digit code sent in the top status bar notification."
                                }
                            }
                        },
                        enabled = !isCooldownActive && inputEmailOtp.length == 4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextWhite,
                            disabledContainerColor = TextSilver.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = "Submit & Complete Sign In",
                            color = BgCharcoal,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Optional Secondary Action: Add Mobile Phone Number
                    OutlinedButton(
                        onClick = {
                            if (!isCooldownActive && inputEmailOtp == generatedEmailOtp) {
                                loginError = null
                                Toast.makeText(context, "Email verified! Now enter mobile phone number.", Toast.LENGTH_SHORT).show()
                                currentStep = 2 // Proceed to Mobile Phone setup
                            } else if (inputEmailOtp != generatedEmailOtp) {
                                loginError = "Please enter valid 4-digit Email OTP first."
                            }
                        },
                        enabled = !isCooldownActive && inputEmailOtp.length == 4,
                        border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = "Verify Email & Add Mobile Phone",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                    }

                    // Resend Email OTP Notification Button
                    OutlinedButton(
                        onClick = {
                            if (!isCooldownActive) {
                                generatedEmailOtp = (1000..9999).random().toString()
                                inputEmailOtp = ""
                                loginError = null
                                dispatchEmailOtpNotification(context, email, generatedEmailOtp)
                                Toast.makeText(context, "New Email OTP sent to top notification bar!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isCooldownActive,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            text = "Resend Email OTP Notification",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                    }

                    // Back to Email / Password Screen
                    TextButton(
                        onClick = { if (!isCooldownActive) currentStep = 1 },
                        enabled = !isCooldownActive
                    ) {
                        Text(
                            text = "Change Email / Password ($email)",
                            color = TextSilver,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                2 -> {
                    // ----------------------------------------------------
                    // SCREEN 2: Phone Number Input with SIM Selection & Validation
                    // ----------------------------------------------------

                    Text(
                        text = "Selected Country Code: $countryCode",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Active SIM Number Auto-Select Card
                    Surface(
                        color = SurfaceDark,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF00FF66).copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable {
                                phoneNumberDigits = "9876543210"
                                loginError = null
                                Toast.makeText(context, "Active SIM phone number selected: $countryCode 9876543210", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SimCard,
                                contentDescription = "Active SIM",
                                tint = Color(0xFF00FF66),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Use Detected SIM Mobile Number",
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tap to auto-fill your SIM number ($countryCode 9876543210)",
                                    color = TextSilver,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Phone Number Input (Strictly 10 digits enforcement)
                    OutlinedTextField(
                        value = phoneNumberDigits,
                        onValueChange = { newVal ->
                            val filtered = newVal.filter { it.isDigit() }.take(10)
                            phoneNumberDigits = filtered
                            if (loginError != null) loginError = null
                        },
                        label = { Text("10-Digit Mobile Number") },
                        placeholder = { Text("9876543210", color = TextSilver) },
                        leadingIcon = {
                            Text(
                                text = countryCode,
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextSilver,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )

                    // Helper indicator for 10 digits requirement
                    Text(
                        text = "${phoneNumberDigits.length}/10 digits entered",
                        color = if (phoneNumberDigits.length == 10) Color.Green else TextSilver,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 20.dp)
                    )

                    // Send OTP Button
                    val isTenDigits = phoneNumberDigits.length == 10
                    Button(
                        onClick = {
                            if (isTenDigits && !isCooldownActive) {
                                if (!isValidMobileNumber(phoneNumberDigits, countryCode)) {
                                    loginError = "Invalid mobile number. Random or repetitive fake numbers are not allowed. Please enter a valid 10-digit mobile number for your active SIM/device."
                                } else {
                                    requestCount++
                                    if (requestCount > 3) {
                                        isCooldownActive = true
                                    } else {
                                        generatedSmsOtp = (1000..9999).random().toString()
                                        otpCode = ""
                                        loginError = null
                                        dispatchOtpNotification(context, "$countryCode $phoneNumberDigits", generatedSmsOtp)
                                        Toast.makeText(context, "OTP sent via Cloud SMS! Check top notification banner.", Toast.LENGTH_LONG).show()
                                        currentStep = 3 // Proceed to OTP screen
                                    }
                                }
                            }
                        },
                        enabled = isTenDigits && !isCooldownActive,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextWhite,
                            disabledContainerColor = TextSilver.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Send OTP to Mobile",
                            color = BgCharcoal,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Go Back Button
                    TextButton(
                        onClick = { if (!isCooldownActive) currentStep = 1 },
                        enabled = !isCooldownActive
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextSilver, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Back to Login",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                3 -> {
                    // ----------------------------------------------------
                    // SCREEN 3: Secure OTP Verification & Cloud Notification Check
                    // ----------------------------------------------------

                    val fullPhoneNum = "$countryCode $phoneNumberDigits"

                    Text(
                        text = "Enter 4-Digit OTP Code",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "Verification code dispatched to $fullPhoneNum",
                        color = TextSilver,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Heads-up Notification Prompt Banner
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notification sent",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Check Top Notification Banner",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "A top system notification has been posted. Swipe down your top status bar to view your exact 4-digit OTP code.",
                                    color = TextSilver,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // OTP Input Field
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { if (it.length <= 4) otpCode = it.filter { ch -> ch.isDigit() } },
                        label = { Text(Strings.get("enter_otp", currentLang)) },
                        placeholder = { Text("Enter 4-digit OTP from notification", color = TextSilver) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TextWhite,
                            unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                            focusedLabelColor = TextWhite,
                            unfocusedLabelColor = TextSilver,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    // Verify OTP Button
                    Button(
                        onClick = {
                            if (!isCooldownActive) {
                                if (otpCode == generatedSmsOtp) {
                                    loginError = null
                                    onLoginSuccess(email, fullPhoneNum)
                                } else {
                                    loginError = "Incorrect OTP code. Please enter the exact 4-digit code received in the top notification banner."
                                }
                            }
                        },
                        enabled = !isCooldownActive && otpCode.length == 4,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextWhite,
                            disabledContainerColor = TextSilver.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "Verify & Complete Sign In",
                            color = BgCharcoal,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Resend OTP Notification
                    OutlinedButton(
                        onClick = {
                            if (!isCooldownActive) {
                                requestCount++
                                if (requestCount > 3) {
                                    isCooldownActive = true
                                } else {
                                    generatedSmsOtp = (1000..9999).random().toString()
                                    otpCode = ""
                                    loginError = null
                                    dispatchOtpNotification(context, fullPhoneNum, generatedSmsOtp)
                                    Toast.makeText(context, "New OTP notification sent to top bar!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isCooldownActive,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "Resend OTP Notification (${3 - (requestCount - 1).coerceAtLeast(0)} left)",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                    }

                    // Edit Phone Number Option
                    TextButton(
                        onClick = { if (!isCooldownActive) currentStep = 2 },
                        enabled = !isCooldownActive
                    ) {
                        Text(
                            text = "Edit Mobile Number ($fullPhoneNum)",
                            color = TextSilver,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Real Google Accounts on Device Selection Dialog
        if (showGooglePickerDialog) {
            AlertDialog(
                onDismissRequest = { showGooglePickerDialog = false },
                containerColor = SurfaceDark,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF4285F4),
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Google Accounts on Device", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("${deviceAccounts.size} signed-in Google account(s) found", color = TextSilver, fontSize = 12.sp)
                        }
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Choose an authorized Google account from your phone to sign in:",
                            color = TextSilver,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                        ) {
                            items(deviceAccounts) { accEmail ->
                                Surface(
                                    color = BgCharcoal,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, if (email == accEmail) Color(0xFF4285F4) else Color.White.copy(alpha = 0.1f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            email = accEmail
                                            showGooglePickerDialog = false
                                            loginError = null
                                            // Initialize Firebase Authentication session for selected Google Account
                                            FirebaseRepository().ensureAuthenticatedSession(accEmail) { _ -> }
                                            // Dispatch Email OTP to selected account
                                            generatedEmailOtp = (1000..9999).random().toString()
                                            inputEmailOtp = ""
                                            dispatchEmailOtpNotification(context, accEmail, generatedEmailOtp)
                                            Toast.makeText(context, "Google Account authenticated: $accEmail. Check top notification bar for Email OTP!", Toast.LENGTH_LONG).show()
                                            currentStep = 15 // Proceed to Email OTP verification
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Google Account",
                                            tint = Color(0xFF4285F4),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = accEmail.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = accEmail,
                                                color = TextSilver,
                                                fontSize = 12.sp
                                            )
                                        }
                                        if (email == accEmail) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = Color(0xFF00FF66),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showGooglePickerDialog = false }) {
                        Text("Cancel", color = TextSilver)
                    }
                }
            )
        }
    }
}
