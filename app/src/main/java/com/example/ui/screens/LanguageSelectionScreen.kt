package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.BackHandler
import com.example.ui.theme.BgCharcoal
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSilver
import com.example.ui.theme.TextWhite

data class LanguageItem(
    val name: String,
    val nativeName: String,
    val code: String
)

@Composable
fun LanguageSelectionScreen(
    countryName: String,
    countryCode: String,
    currentLang: String = "English",
    onLanguageSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(enabled = true) { onBack() }
    val languages = remember {
        listOf(
            LanguageItem("English", "English", "en"),
            LanguageItem("Spanish", "Español", "es"),
            LanguageItem("French", "Français", "fr"),
            LanguageItem("German", "Deutsch", "de"),
            LanguageItem("Russian", "Русский", "ru"),
            LanguageItem("Arabic", "العربية", "ar"),
            LanguageItem("Chinese", "中文 (Mandarin)", "zh"),
            LanguageItem("Portuguese", "Português", "pt"),
            LanguageItem("Hindi", "हिन्दी", "hi"),
            LanguageItem("Japanese", "日本語", "ja"),
            LanguageItem("Korean", "한국어", "ko"),
            LanguageItem("Italian", "Italiano", "it"),
            LanguageItem("Turkish", "Türkçe", "tr"),
            LanguageItem("Dutch", "Nederlands", "nl")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf(languages[0]) }

    val filteredLanguages = languages.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.nativeName.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCharcoal)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", currentLang), tint = TextWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = Strings.get("select_lang_title", currentLang),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${Strings.get("selected_region", currentLang)}: $countryName ($countryCode)",
                        fontSize = 12.sp,
                        color = TextSilver
                    )
                }
            }

            // Search Bar for Languages
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(Strings.get("search_lang_placeholder", currentLang), color = TextSilver) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSilver) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextSilver.copy(alpha = 0.4f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            // Language Options List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredLanguages) { lang ->
                    val isSelected = selectedLang.name == lang.name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SurfaceDark else SurfaceDark.copy(alpha = 0.7f))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) TextWhite else Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedLang = lang }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = lang.name,
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = lang.nativeName,
                                color = TextSilver,
                                fontSize = 13.sp
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Unified Multilingual Onboarding Guidance Card & Proceed Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, TextWhite.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val langName = selectedLang.name
                    val step1 = when (langName) {
                        "Japanese" -> "ステップ 1: 国 ($countryName) とコード ($countryCode) が設定されました。"
                        "Hindi" -> "चरण 1: देश ($countryName) और कोड ($countryCode) सेट हो गए हैं।"
                        "Chinese" -> "第一步：国家 ($countryName) 与代码 ($countryCode) 设置成功。"
                        "Spanish" -> "Paso 1: País ($countryName) y código ($countryCode) configurados con éxito."
                        "French" -> "Étape 1 : Pays ($countryName) et code ($countryCode) configurés avec succès."
                        "German" -> "Schritt 1: Land ($countryName) und Vorwahl ($countryCode) erfolgreich eingestellt."
                        "Russian" -> "Шаг 1: Страна ($countryName) и код ($countryCode) успешно настроены."
                        "Arabic" -> "الخطوة 1: تم ضبط البلد ($countryName) الرمز ($countryCode) بنجاح."
                        "Portuguese" -> "Passo 1: País ($countryName) e código ($countryCode) configurados com sucesso."
                        "Korean" -> "1단계: 국가($countryName) 및 국가번호($countryCode)가 설정되었습니다."
                        "Italian" -> "Passaggio 1: Paese ($countryName) e codice ($countryCode) impostati con successo."
                        "Turkish" -> "Adım 1: Ülke ($countryName) ve kod ($countryCode) başarıyla ayarlandı."
                        "Dutch" -> "Stap 1: Land ($countryName) en code ($countryCode) succesvol ingesteld."
                        else -> "Step 1: Country Selected Successfully ($countryName - $countryCode)"
                    }
                    val step2 = when (langName) {
                        "Japanese" -> "ステップ 2: 有効なメールとパスワードでサインインしてください。"
                        "Hindi" -> "चरण 2: कृपया अपनी वैध ईमेल और पासवर्ड से साइन इन करें।"
                        "Chinese" -> "第二步：请使用有效电子邮件和密码登录。"
                        "Spanish" -> "Paso 2: Inicie sesión con su correo electrónico y contraseña válidos."
                        "French" -> "Étape 2 : Connectez-vous avec votre e-mail et votre mot de passe."
                        "German" -> "Schritt 2: Melden Sie sich mit E-Mail und Passwort an."
                        "Russian" -> "Шаг 2: Войдите, используя действительные email и пароль."
                        "Arabic" -> "الخطوة 2: يرجى تسجيل الدخول باستخدام البريد الإلكتروني وكلمة المرور."
                        "Portuguese" -> "Passo 2: Entre com seu e-mail e senha válidos."
                        "Korean" -> "2단계: 유효한 이메일과 비밀번호로 로그인하세요."
                        "Italian" -> "Passaggio 2: Accedi con e-mail e password valide."
                        "Turkish" -> "Adım 2: Geçerli e-posta ve şifrenizle giriş yapın."
                        "Dutch" -> "Stap 2: Log in met uw geldige e-mailadres en wachtwoord."
                        else -> "Step 2: Please sign in using your valid Email ID and Password."
                    }
                    val step3 = when (langName) {
                        "Japanese" -> "ステップ 3: 安全なOTPで電話番号を確認しアクセスします。"
                        "Hindi" -> "चरण 3: सुरक्षित OTP से फ़ोन नंबर सत्यापित करें।"
                        "Chinese" -> "第三步：通过安全验证码验证电话号码。"
                        "Spanish" -> "Paso 3: Verifique su teléfono con OTP seguro."
                        "French" -> "Étape 3 : Vérifiez votre numéro par OTP sécurisé."
                        "German" -> "Schritt 3: Verifizieren Sie Ihre Telefonnummer per sicherem OTP."
                        "Russian" -> "Шаг 3: Подтвердите номер телефона с помощью OTP."
                        "Arabic" -> "الخطوة 3: تحقق من رقم هاتفك برمز OTP آمن."
                        "Portuguese" -> "Passo 3: Verifique seu telefone com OTP seguro."
                        "Korean" -> "3단계: 안전한 OTP로 전화번호를 인증하세요."
                        "Italian" -> "Passaggio 3: Verifica il tuo numero con OTP sicuro."
                        "Turkish" -> "Adım 3: Güvenli OTP ile telefon numaranızı doğrulayın."
                        "Dutch" -> "Stap 3: Verifieer uw telefoonnummer met veilige OTP."
                        else -> "Step 3: Verify your phone number with a secure OTP to access the app."
                    }

                    Text(
                        text = "${Strings.get("onboarding_guidance", currentLang)} (${selectedLang.name})",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(text = "• $step1", color = TextSilver, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
                    Text(text = "• $step2", color = TextSilver, fontSize = 11.sp, modifier = Modifier.padding(bottom = 3.dp))
                    Text(text = "• $step3", color = TextSilver, fontSize = 11.sp, modifier = Modifier.padding(bottom = 10.dp))

                    Button(
                        onClick = {
                            onLanguageSelected(selectedLang.name)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TextWhite),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = Strings.get("proceed_signin", selectedLang.name),
                            color = BgCharcoal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
