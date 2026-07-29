package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

data class SupportMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun SmartFitServiceScreen(
    currentLang: String
) {
    var messages by remember {
        mutableStateOf<List<SupportMessage>>(emptyList())
    }
    LaunchedEffect(currentLang) {
        if (messages.isEmpty()) {
            messages = listOf(
                SupportMessage(Strings.get("support_initial", currentLang), false)
            )
        }
    }
    var inputText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header / Brand Logo Placeholder
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(4.dp),
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
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = Strings.get("smartfit_service", currentLang),
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Strings.get("smartfit_service_desc", currentLang),
                        color = TextSilver,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chat / Q&A Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (msg.isUser) TextWhite else SurfaceDark,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = if (msg.isUser) BgCharcoal else TextWhite,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Question Suggestions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        messages = messages + listOf(
                            SupportMessage(Strings.get("user_q_plans", currentLang), true),
                            SupportMessage(Strings.get("system_a_plans", currentLang), false)
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(Strings.get("plans_btn", currentLang), fontSize = 11.sp, maxLines = 1)
                }
                OutlinedButton(
                    onClick = {
                        messages = messages + listOf(
                            SupportMessage(Strings.get("user_q_coaches", currentLang), true),
                            SupportMessage(Strings.get("system_a_coaches", currentLang), false)
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(Strings.get("coaches_btn", currentLang), fontSize = 11.sp, maxLines = 1)
                }
            }

            // Input Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(Strings.get("ask_question_placeholder", currentLang), color = TextSilver) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TextWhite,
                        unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userQ = inputText
                            inputText = ""
                            messages = messages + listOf(
                                SupportMessage(userQ, true),
                                SupportMessage(Strings.get("system_a_custom", currentLang), false)
                            )
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .background(TextWhite, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = BgCharcoal)
                }
            }
        }
    }
}
