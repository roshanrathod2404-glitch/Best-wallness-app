package com.example.ui.screens

import java.util.Locale

object LocalizationHelper {
    fun setAppLocale(language: String) {
        val localeTag = when (language) {
            "Hindi" -> "hi"
            "Japanese" -> "ja"
            "Chinese" -> "zh"
            "Spanish" -> "es"
            "French" -> "fr"
            "German" -> "de"
            "Arabic" -> "ar"
            "Russian" -> "ru"
            "Portuguese" -> "pt"
            "Italian" -> "it"
            "Korean" -> "ko"
            "Turkish" -> "tr"
            "Dutch" -> "nl"
            "Vietnamese" -> "vi"
            "Polish" -> "pl"
            else -> "en"
        }
        try {
            val locale = Locale(localeTag)
            Locale.setDefault(locale)
        } catch (e: Exception) {
            // Fallback
        }
    }
}

object Strings {
    private val extraTranslations = mapOf(
        "search_your_language" to mapOf(
            "English" to "Search your language", "Spanish" to "Buscar tu idioma", "French" to "Rechercher votre langue", "German" to "Sprache suchen", "Russian" to "Поиск языка",
            "Arabic" to "ابحث عن لغتك", "Chinese" to "搜索您的语言", "Portuguese" to "Pesquisar seu idioma", "Hindi" to "अपनी भाषा खोजें", "Japanese" to "言語を検索",
            "Korean" to "언어 검색", "Italian" to "Cerca la tua lingua", "Turkish" to "Dilinizi arayın", "Dutch" to "Zoek uw taal", "Vietnamese" to "Tìm kiếm ngôn ngữ của bạn", "Polish" to "Szukaj swojego języka"
        ),
        "app_language" to mapOf(
            "English" to "App Language", "Spanish" to "Idioma de la aplicación", "French" to "Langue de l'application", "German" to "App-Sprache", "Russian" to "Язык приложения",
            "Arabic" to "لغة التطبيق", "Chinese" to "应用语言", "Portuguese" to "Idioma do aplicativo", "Hindi" to "ऐप की भाषा", "Japanese" to "アプリの言語",
            "Korean" to "앱 언어", "Italian" to "Lingua dell'app", "Turkish" to "Uygulama Dili", "Dutch" to "App-taal", "Vietnamese" to "Ngôn ngữ ứng dụng", "Polish" to "Język aplikacji"
        ),
        "online" to mapOf(
            "English" to "Online", "Spanish" to "En línea", "French" to "En ligne", "German" to "Online", "Russian" to "В сети",
            "Arabic" to "متصل", "Chinese" to "在线", "Portuguese" to "Online", "Hindi" to "ऑनलाइन", "Japanese" to "オンライン",
            "Korean" to "온라인", "Italian" to "In linea", "Turkish" to "Çevrimiçi", "Dutch" to "Online", "Vietnamese" to "Trực tuyến", "Polish" to "W sieci"
        ),
        "offline" to mapOf(
            "English" to "Offline", "Spanish" to "Desconectado", "French" to "Hors ligne", "German" to "Offline", "Russian" to "Не в сети",
            "Arabic" to "غير متصل", "Chinese" to "离线", "Portuguese" to "Desconectado", "Hindi" to "ऑफ़लाइन", "Japanese" to "オフライン",
            "Korean" to "오프라인", "Italian" to "Non in linea", "Turkish" to "Çevrimdışı", "Dutch" to "Off-line", "Vietnamese" to "Ngoại tuyến", "Polish" to "Niezalogowany"
        ),
        "typing" to mapOf(
            "English" to "Typing...", "Spanish" to "Escribiendo...", "French" to "Écrit...", "German" to "Tippt...", "Russian" to "Печатает...",
            "Arabic" to "يكتب...", "Chinese" to "正在输入...", "Portuguese" to "Digitando...", "Hindi" to "टाइपिंग...", "Japanese" to "入力中...",
            "Korean" to "입력 중...", "Italian" to "Sta digitando...", "Turkish" to "Yazıyor...", "Dutch" to "Typen...", "Vietnamese" to "Đang nhập...", "Polish" to "Pisze..."
        ),
        "last_seen" to mapOf(
            "English" to "Last seen ", "Spanish" to "Última vez ", "French" to "Vu à ", "German" to "Zuletzt gesehen ", "Russian" to "Был(а) в сети ",
            "Arabic" to "آخر ظهور ", "Chinese" to "上次在线 ", "Portuguese" to "Visto por último ", "Hindi" to "अंतिम बार देखा गया ", "Japanese" to "最終既読 ",
            "Korean" to "마지막 접속 ", "Italian" to "Ultimo accesso ", "Turkish" to "Son görülme ", "Dutch" to "Liefst gezien ", "Vietnamese" to "Lần cuối xem ", "Polish" to "Ostatnio widziany "
        ),
        "new_community" to mapOf(
            "English" to "New Community", "Spanish" to "Nueva Comunidad", "French" to "Nouvelle communauté", "German" to "Neue Community", "Russian" to "Новое сообщество",
            "Arabic" to "مجتمع جديد", "Chinese" to "新社区", "Portuguese" to "Nova Comunidade", "Hindi" to "नया समुदाय", "Japanese" to "新しいコミュニティ",
            "Korean" to "새 커뮤니티", "Italian" to "Nuova community", "Turkish" to "Yeni Topluluk", "Dutch" to "Nieuwe community", "Vietnamese" to "Cộng đồng mới", "Polish" to "Nowa społeczność"
        ),
        "announcements" to mapOf(
            "English" to "Announcements", "Spanish" to "Anuncios", "French" to "Annonces", "German" to "Ankündigungen", "Russian" to "Объявления",
            "Arabic" to "الإعلانات", "Chinese" to "公告", "Portuguese" to "Avisos", "Hindi" to "घोषणाएं", "Japanese" to "お知らせ",
            "Korean" to "공지사항", "Italian" to "Annunci", "Turkish" to "Duyurular", "Dutch" to "Aankondigingen", "Vietnamese" to "Thông báo", "Polish" to "Ogłoszenia"
        ),
        "only_admins_can_send" to mapOf(
            "English" to "Only community admins can send messages", "Spanish" to "Solo los administradores de la comunidad pueden enviar mensajes", "French" to "Seuls les administrateurs de la communauté peuvent envoyer des messages", "German" to "Nur Community-Admins können Nachrichten senden", "Russian" to "Только администраторы сообщества могут отправлять сообщения",
            "Arabic" to "يمكن لمديري المجتمع فقط إرسال الرسائل", "Chinese" to "仅社区管理员可以发送消息", "Portuguese" to "Apenas administradores da comunidade podem enviar mensagens", "Hindi" to "केवल समुदाय व्यवस्थापक संदेश भेज सकते हैं", "Japanese" to "コミュニティ管理者のみがメッセージを送信できます",
            "Korean" to "커뮤니티 관리자만 메시지를 보낼 수 있습니다", "Italian" to "Solo gli amministratori della community possono inviare messaggi", "Turkish" to "Yalnızca topluluk yöneticileri mesaj gönderebilir", "Dutch" to "Alleen community-beheerders kunnen berichten verzenden", "Vietnamese" to "Chỉ quản trị viên cộng đồng mới có thể gửi tin nhắn", "Polish" to "Tylko administratorzy społeczności mogą wysyłać wiadomości"
        ),
        "settings" to mapOf(
            "English" to "Settings", "Spanish" to "Configuración", "French" to "Paramètres", "German" to "Einstellungen", "Russian" to "Настройки",
            "Arabic" to "الإعدادات", "Chinese" to "设置", "Portuguese" to "Configurações", "Hindi" to "सेटिंग्स", "Japanese" to "設定",
            "Korean" to "설정", "Italian" to "Impostazioni", "Turkish" to "Ayarlar", "Dutch" to "Instellingen"
        ),
        "profile_title" to mapOf(
            "English" to "Profile", "Spanish" to "Perfil", "French" to "Profil", "German" to "Profil", "Russian" to "Профиль",
            "Arabic" to "الملف الشخصي", "Chinese" to "个人资料", "Portuguese" to "Perfil", "Hindi" to "प्रोफ़ाइल", "Japanese" to "プロフィール",
            "Korean" to "프로필", "Italian" to "Profilo", "Turkish" to "Profil", "Dutch" to "Profiel"
        ),
        "profile_subtitle" to mapOf(
            "English" to "View and edit personal profile, name, phone, and country",
            "Spanish" to "Ver y editar perfil personal, nombre, teléfono y país",
            "French" to "Afficher et modifier le profil personnel, le nom, le téléphone et le pays",
            "German" to "Persönliches Profil, Name, Telefonnummer und Land anzeigen und bearbeiten",
            "Russian" to "Просмотр и редактирование личного профиля, имени, телефона и страны",
            "Arabic" to "عرض وتعديل الملف الشخصي والاسم والهاتف والبلد",
            "Chinese" to "查看和编辑个人资料、姓名、电话和国家/地区",
            "Portuguese" to "Visualizar e editar perfil pessoal, nome, telefone e país",
            "Hindi" to "व्यक्तिगत प्रोफ़ाइल, नाम, फ़ोन और देश देखें और संपादित करें",
            "Japanese" to "プロフィールの表示と編集、名前、電話、国",
            "Korean" to "개인 프로필, 이름, 전화번호 및 국가 보기 및 편집",
            "Italian" to "Visualizza e modifica il profilo personale, nome, telefono e paese",
            "Turkish" to "Kişisel profili, adı, telefonu ve ülkeyi düzenleyin",
            "Dutch" to "Persoonlijk profiel, naam, telefoon en land bewerken"
        ),
        "account_title" to mapOf(
            "English" to "Account", "Spanish" to "Cuenta", "French" to "Compte", "German" to "Konto", "Russian" to "Аккаунт",
            "Arabic" to "الحساب", "Chinese" to "账户", "Portuguese" to "Conta", "Hindi" to "अकाउंट", "Japanese" to "アカウント",
            "Korean" to "계정", "Italian" to "Account", "Turkish" to "Hesap", "Dutch" to "Account"
        ),
        "account_subtitle" to mapOf(
            "English" to "Manage overall profile security and general settings",
            "Spanish" to "Administrar la seguridad del perfil y la configuración general",
            "French" to "Gérer la sécurité globale du profil et les paramètres généraux",
            "German" to "Allgemeine Profilsicherheit und Einstellungen verwalten",
            "Russian" to "Управление безопасностью профиля и общими настройками",
            "Arabic" to "إدارة أمان الحساب والإعدادات العامة",
            "Chinese" to "管理整体账户安全和常规设置",
            "Portuguese" to "Gerenciar segurança do perfil e configurações gerais",
            "Hindi" to "अकाउंट सुरक्षा और सामान्य सेटिंग्स प्रबंधित करें",
            "Japanese" to "プロファイルのセキュリティと全般設定の管理",
            "Korean" to "전반적인 프로필 보안 및 일반 설정 관리",
            "Italian" to "Gestisci la sicurezza del profilo e le impostazioni generali",
            "Turkish" to "Genel profil güvenliğini ve genel ayarları yönetin",
            "Dutch" to "Beheer algemene profielbeveiliging en algemene instellingen"
        ),
        "lists_title" to mapOf(
            "English" to "Lists", "Spanish" to "Listas", "French" to "Listes", "German" to "Listen", "Russian" to "Списки",
            "Arabic" to "القوائم", "Chinese" to "列表", "Portuguese" to "Listas", "Hindi" to "सूचियाँ", "Japanese" to "リスト",
            "Korean" to "목록", "Italian" to "Liste", "Turkish" to "Listeler", "Dutch" to "Lijsten"
        ),
        "lists_subtitle" to mapOf(
            "English" to "Manage custom categories like Favorites, Work, Family",
            "Spanish" to "Administrar categorías personalizadas como Favoritos, Trabajo, Familia",
            "French" to "Gérer les catégories personnalisées comme Favoris, Travail, Famille",
            "German" to "Kategorien wie Favoriten, Arbeit, Familie verwalten",
            "Russian" to "Управление категориями, такими как Избранное, Работа, Семья",
            "Arabic" to "إدارة الفئات المخصصة مثل المفضلة، العمل، العائلة",
            "Chinese" to "管理自定义类别，如收藏夹、工作、家庭",
            "Portuguese" to "Gerenciar categorias personalizadas como Favoritos, Trabalho, Família",
            "Hindi" to "पसंदीदा, कार्य, परिवार जैसी श्रेणियां प्रबंधित करें",
            "Japanese" to "お気に入り、仕事、家族などのカスタムカテゴリの管理",
            "Korean" to "즐겨찾기, 회사, 가족과 같은 맞춤 카테고리 관리",
            "Italian" to "Gestisci categorie personalizzate come Preferiti, Lavoro, Famiglia",
            "Turkish" to "Sık Kullanılanlar, İş, Aile gibi özel kategorileri yönetin",
            "Dutch" to "Beheer aangepaste categorieën zoals Favorieten, Werk, Familie"
        ),
        "chats_title" to mapOf(
            "English" to "Chats", "Spanish" to "Chats", "French" to "Discussions", "German" to "Chats", "Russian" to "Чаты",
            "Arabic" to "المحادثات", "Chinese" to "聊天", "Portuguese" to "Conversas", "Hindi" to "चैट्स", "Japanese" to "チャット",
            "Korean" to "채팅", "Italian" to "Chat", "Turkish" to "Sohbetler", "Dutch" to "Chats"
        ),
        "chats_subtitle" to mapOf(
            "English" to "Manage chat backups, font scaling, and wallpapers",
            "Spanish" to "Administrar copias de seguridad de chat, tamaño de fuente y fondos",
            "French" to "Gérer les sauvegardes, la taille des polices et les fonds d'écran",
            "German" to "Chat-Backups, Schriftgrößen und Hintergrundbilder verwalten",
            "Russian" to "Управление резервным копированием чатов, размером шрифта и обоями",
            "Arabic" to "إدارة نسخ الاحتياطية للمحادثات وحجم الخط والخلفيات",
            "Chinese" to "管理聊天备份、字体缩放和壁纸",
            "Portuguese" to "Gerenciar backups de conversa, tamanho da fonte e papéis de parede",
            "Hindi" to "चैट बैकअप, फ़ॉन्ट स्केल और वॉलपेपर प्रबंधित करें",
            "Japanese" to "チャットのバックアップ、フォントの拡大縮小、壁紙の管理",
            "Korean" to "채팅 백업, 글꼴 크기 및 배경화면 관리",
            "Italian" to "Gestisci backup delle chat, ridimensionamento dei caratteri e sfondi",
            "Turkish" to "Sohbet yedeklerini, yazı tipi ölçeklendirmeyi ve duvar kağıtlarını yönetin",
            "Dutch" to "Beheer chatback-ups, lettergrootte en achtergronden"
        ),
        "appearance_title" to mapOf(
            "English" to "Appearance", "Spanish" to "Apariencia", "French" to "Apparence", "German" to "Darstellung", "Russian" to "Внешний вид",
            "Arabic" to "المظهر", "Chinese" to "外观", "Portuguese" to "Aparência", "Hindi" to "रूप-रंग", "Japanese" to "外観",
            "Korean" to "테마", "Italian" to "Aspetto", "Turkish" to "Görünüm", "Dutch" to "Uiterlijk"
        ),
        "appearance_subtitle" to mapOf(
            "English" to "Switch between Light Mode, Absolute Black Dark Mode, and System Default",
            "Spanish" to "Alternar entre modo claro, modo oscuro absoluto y predeterminado del sistema",
            "French" to "Basculez entre le mode clair, le mode noir absolu et le système par défaut",
            "German" to "Zwischen Light Mode, Absolute Black Dark Mode und Systemstandard wechseln",
            "Russian" to "Переключение между светлым, абсолютно черным темным режимом и системным",
            "Arabic" to "التبديل بين الوضع الفاتح، والوضع الداكن المطلق، وافتراضي النظام",
            "Chinese" to "在浅色模式、极黑暗色模式和系统默认值之间切换",
            "Portuguese" to "Alternar entre Modo Claro, Modo Escuro Absoluto e Padrão do Sistema",
            "Hindi" to "लाइट मोड, एब्सोल्यूट ब्लैक डार्क मोड और सिस्टम डिफॉल्ट के बीच स्विच करें",
            "Japanese" to "ライトモード、ダークモード、システムデフォルトの切り替え",
            "Korean" to "라이트 모드, 블랙 다크 모드, 시스템 기본값 간 전환",
            "Italian" to "Passa da modalità chiara, scura assoluta e predefinita di sistema",
            "Turkish" to "Açık, Mutlak Siyah Karanlık ve Sistem Varsayılanı arasında geçiş yapın",
            "Dutch" to "Schakel tussen lichte modus, absoluut zwarte donkere modus en systeemstandaard"
        ),
        "notifications_title" to mapOf(
            "English" to "Notifications", "Spanish" to "Notificaciones", "French" to "Notifications", "German" to "Benachrichtigungen", "Russian" to "Уведомления",
            "Arabic" to "الإشعارات", "Chinese" to "通知", "Portuguese" to "Notificações", "Hindi" to "सूचनाएं", "Japanese" to "通知",
            "Korean" to "알림", "Italian" to "Notifiche", "Turkish" to "Bildirimler", "Dutch" to "Meldingen"
        ),
        "notifications_subtitle" to mapOf(
            "English" to "Toggle controls for alert tones, group notifications, and vibration",
            "Spanish" to "Alternar controles de tonos de alerta, notificaciones de grupo y vibración",
            "French" to "Activer/désactiver les tonalités d'alerte, les notifications de groupe et les vibrations",
            "German" to "Regler für Hinweistöne, Gruppenbenachrichtigungen und Vibration umschalten",
            "Russian" to "Управление звуками оповещений, групповыми уведомлениями и вибрацией",
            "Arabic" to "تحكم في نغمات التنبيه وإشعارات المجموعة والاهتزاز",
            "Chinese" to "切换提示音、群组通知和振动的控制",
            "Portuguese" to "Alternar controles de tons de alerta, notificações de grupo e vibração",
            "Hindi" to "अलर्ट टोन, समूह सूचनाएं और कंपन के लिए नियंत्रण टॉगल करें",
            "Japanese" to "アラート音、グループ通知、バイブレーションの切り替え",
            "Korean" to "경고음, 그룹 알림 및 진동 제어 설정",
            "Italian" to "Attiva/disattiva toni di avviso, notifiche di gruppo e vibrazione",
            "Turkish" to "Uyarı sesleri, grup bildirimleri ve titreşim kontrollerini açın/kapatın",
            "Dutch" to "Schakel regelaars in voor waarschuwingstonen, groepsnotificaties en trillen"
        ),
        "storage_title" to mapOf(
            "English" to "Storage and Data", "Spanish" to "Almacenamiento y datos", "French" to "Stockage et données", "German" to "Speicher und Daten", "Russian" to "Данные и память",
            "Arabic" to "التخزين والبيانات", "Chinese" to "存储和数据", "Portuguese" to "Armazenamento e dados", "Hindi" to "स्टोरेज और डेटा", "Japanese" to "ストレージとデータ",
            "Korean" to "저장 공간 및 데이터", "Italian" to "Spazio e dati", "Turkish" to "Depolama ve Veri", "Dutch" to "Opslag en gegevens"
        ),
        "storage_subtitle" to mapOf(
            "English" to "Network usage analytics and media auto-download parameters",
            "Spanish" to "Análisis de uso de red y parámetros de descarga automática de archivos",
            "French" to "Analyses de l'utilisation du réseau et paramètres de téléchargement automatique",
            "German" to "Netzwerknutzungsanalyse und Parameter für automatischen Medien-Download",
            "Russian" to "Аналитика использования сети и параметры автозагрузки медиафайлов",
            "Arabic" to "تحليلات استخدام الشبكة ومعلمات التنزيل التلقائي للوسائط",
            "Chinese" to "网络使用分析和媒体自动下载参数",
            "Portuguese" to "Análise de uso da rede e parâmetros de download automático de mídia",
            "Hindi" to "नेटवर्क उपयोग विश्लेषण और मीडिया ऑटो-डाउनलोड पैरामीटर",
            "Japanese" to "ネットワーク使用状況の分析とメディアの自動ダウンロード設定",
            "Korean" to "네트워크 사용량 분석 및 미디어 자동 다운로드 매개변수",
            "Italian" to "Analisi dell'utilizzo della rete e parametri di download automatico dei media",
            "Turkish" to "Ağ kullanımı analitiği ve medya otomatik indirme parametreleri",
            "Dutch" to "Netwerkgebruik analyses en parameters voor automatisch downloaden van media"
        ),
        "app_language_subtitle" to mapOf(
            "English" to "Current active language settings",
            "Spanish" to "Configuración de idioma activo actual",
            "French" to "Paramètres de langue active actuelle",
            "German" to "Aktuelle aktive Spracheinstellungen",
            "Russian" to "Текущие активные языковые настройки",
            "Arabic" to "إعدادات اللغة النشطة الحالية",
            "Chinese" to "当前启用的语言设置",
            "Portuguese" to "Configurações de idioma ativo atual",
            "Hindi" to "वर्तमान सक्रिय भाषा सेटिंग्स",
            "Japanese" to "現在アクティブな言語設定",
            "Korean" to "현재 활성화된 언어 설정",
            "Italian" to "Impostazioni della lingua attiva corrente",
            "Turkish" to "Mevcut aktif dil ayarları",
            "Dutch" to "Huidige actieve taalinstellingen"
        ),
        "help_title" to mapOf(
            "English" to "Help and Feedback", "Spanish" to "Ayuda y comentarios", "French" to "Aide et commentaires", "German" to "Hilfe und Feedback", "Russian" to "Справка и обратная связь",
            "Arabic" to "المساعدة والتعليقات", "Chinese" to "帮助和反馈", "Portuguese" to "Ajuda e feedback", "Hindi" to "सहायता और प्रतिक्रिया", "Japanese" to "ヘルプとフィードバック",
            "Korean" to "도움말 및 피드백", "Italian" to "Aiuto e feedback", "Turkish" to "Yardım ve Geri Bildirim", "Dutch" to "Hulp en feedback"
        ),
        "help_subtitle" to mapOf(
            "English" to "Access to FAQs, contact support, and suggestions",
            "Spanish" to "Acceso a preguntas frecuentes, contacto de soporte y sugerencias",
            "French" to "Accès aux FAQ, contactez le support et suggestions",
            "German" to "Zugriff auf FAQs, Support kontaktieren und Vorschläge",
            "Russian" to "Доступ к часто задаваемым вопросам, контактам службы поддержки и предложениям",
            "Arabic" to "الوصول إلى الأسئلة الشائعة والاتصال بالدعم والاقتراحات",
            "Chinese" to "访问常见问题解答、联系支持和建议",
            "Portuguese" to "Acesso a FAQs, contato do suporte e sugestões",
            "Hindi" to "अक्सर पूछे जाने वाले प्रश्नों, सहायता से संपर्क और सुझावों तक पहुंच",
            "Japanese" to "よくある質問、サポートへのお問い合わせ、ご提案へのアクセス",
            "Korean" to "자주 묻는 질문, 고객 지원 문의 및 제안에 액세스",
            "Italian" to "Accesso alle FAQ, contatta il supporto e suggerimenti",
            "Turkish" to "SSS'lere erişim, destekle iletişime geçme ve öneriler",
            "Dutch" to "Toegang tot veelgestelde vragen, contact opnemen met ondersteuning en suggesties"
        ),
        "invite_title" to mapOf(
            "English" to "Invite a Friend", "Spanish" to "Invitar a un amigo", "French" to "Inviter un ami", "German" to "Freunde einladen", "Russian" to "Пригласить друга",
            "Arabic" to "دعوة صديق", "Chinese" to "邀请朋友", "Portuguese" to "Convidar um amigo", "Hindi" to "एक मित्र को आमंत्रित करें", "Japanese" to "友達を招待する",
            "Korean" to "친구 초대하기", "Italian" to "Invita un amico", "Turkish" to "Arkadaşını Davet Et", "Dutch" to "Nodig een vriend uit"
        ),
        "invite_subtitle" to mapOf(
            "English" to "Launch a system share intent sheet with the app referral link",
            "Spanish" to "Lanzar hoja de compartir del sistema con el enlace de recomendación",
            "French" to "Lancer la feuille de partage système avec le lien de parrainage",
            "German" to "System-Share-Sheet mit dem App-Referral-Link starten",
            "Russian" to "Запуск системного окна отправки со ссылкой на приложение",
            "Arabic" to "إطلاق ورقة مشاركة النظام مع رابط إحالة التطبيق",
            "Chinese" to "使用应用推荐链接启动系统分享表单",
            "Portuguese" to "Inicie uma planilha de compartilhamento do sistema com o link de indicação",
            "Hindi" to "ऐप रेफ़रल लिंक के साथ एक सिस्टम शेयर शीट लॉन्च करें",
            "Japanese" to "アプリ紹介リンク付きのシステム共有シートを起動します",
            "Korean" to "앱 추천 링크로 시스템 공유 시트 실행",
            "Italian" to "Avvia foglio di condivisione di sistema con il link di invito dell'app",
            "Turkish" to "Uygulama referans bağlantısıyla sistem paylaşım sayfasını açın",
            "Dutch" to "Start een systeemdeelvenster met de app-verwijzingslink"
        ),
        "updates_title" to mapOf(
            "English" to "App Updates", "Spanish" to "Actualizaciones de la aplicación", "French" to "Mises à jour de l'application", "German" to "App-Updates", "Russian" to "Обновления приложения",
            "Arabic" to "تحديثات التطبيق", "Chinese" to "应用更新", "Portuguese" to "Atualizações do aplicativo", "Hindi" to "ऐप अपडेट", "Japanese" to "アプリのアップデート",
            "Korean" to "앱 업데이트", "Italian" to "Aggiornamenti dell'app", "Turkish" to "Uygulama Güncellemeleri", "Dutch" to "App-updates"
        ),
        "updates_subtitle" to mapOf(
            "English" to "Check build configurations and update hooks from the server",
            "Spanish" to "Verificar configuraciones de compilación y enlaces de actualización del servidor",
            "French" to "Vérifier les configurations de build et mettre à jour depuis le serveur",
            "German" to "Build-Konfigurationen und Update-Hooks vom Server prüfen",
            "Russian" to "Проверка конфигураций сборки и хуков обновления с сервера",
            "Arabic" to "تحقق من تكوينات البناء وتحديث الخطافات من الخادم",
            "Chinese" to "从服务器检查构建配置和更新挂钩",
            "Portuguese" to "Verifique as configurações de compilação e ganchos de atualização do servidor",
            "Hindi" to "सर्वर से बिल्ड कॉन्फ़िगरेशन और अपडेट हुक जांचें",
            "Japanese" to "ビルド設定とサーバーからの更新フックを確認します",
            "Korean" to "서버에서 빌드 구성 및 업데이트 후크 확인",
            "Italian" to "Verifica configurazioni di build e hook di aggiornamento dal server",
            "Turkish" to "Sunucudan derleme yapılandırmalarını ve güncelleme kancalarını kontrol edin",
            "Dutch" to "Controleer build-configuraties en update-hooks vanaf de server"
        ),
        "history_title" to mapOf(
            "English" to "History & Logs", "Spanish" to "Historial y registros", "French" to "Historique et journaux", "German" to "Verlauf & Protokolle", "Russian" to "История и логи",
            "Arabic" to "السجل والسجلات", "Chinese" to "历史与日志", "Portuguese" to "Histórico e logs", "Hindi" to "इतिहास और लॉग", "Japanese" to "履歴とログ",
            "Korean" to "기록 및 로그", "Italian" to "Cronologia e registri", "Turkish" to "Geçmiş ve Günlükler", "Dutch" to "Geschiedenis en logboeken"
        ),
        "history_subtitle" to mapOf(
            "English" to "View global chat/message and backup history logs",
            "Spanish" to "Ver el historial global de chat/mensajes y copias de seguridad",
            "French" to "Afficher l'historique global des discussions/messages et des sauvegardes",
            "German" to "Globale Chat-/Nachrichten- und Backup-Verlaufsprotokolle anzeigen",
            "Russian" to "Просмотр глобальных журналов чатов/сообщений и истории бэкапов",
            "Arabic" to "عرض سجلات تاريخ المحادثات/الرسائل والنسخ الاحتياطي العالمية",
            "Chinese" to "查看全局聊天/消息和备份历史日志",
            "Portuguese" to "Visualizar chat/mensagens globais e logs de histórico de backup",
            "Hindi" to "वैश्विक चैट/संदेश और बैकअप इतिहास लॉग देखें",
            "Japanese" to "グローバルなチャット/メッセージとバックアップ履歴ログを表示します",
            "Korean" to "전체 채팅/메시지 및 백अप 기록 로그 보기",
            "Italian" to "Visualizza cronologia chat/messaggi e log di backup globali",
            "Turkish" to "Genel sohbet/mesaj ve yedekleme geçmişi günlüklerini görüntüleyin",
            "Dutch" to "Bekijk globale chat/bericht- en back-upgeschiedenislogboeken"
        ),
        "personal_profile" to mapOf(
            "English" to "Personal Profile", "Spanish" to "Perfil Personal", "French" to "Profil Personnel", "German" to "Persönliches Profil", "Russian" to "Личный профиль",
            "Arabic" to "الملف الشخصي الشخصي", "Chinese" to "个人信息", "Portuguese" to "Perfil Pessoal", "Hindi" to "व्यक्तिगत प्रोफ़ाइल", "Japanese" to "個人プロフィール",
            "Korean" to "개인 프로필", "Italian" to "Profilo Personale", "Turkish" to "Kişisel Profil", "Dutch" to "Persoonlijk profiel"
        ),
        "full_name" to mapOf(
            "English" to "Full Name", "Spanish" to "Nombre completo", "French" to "Nom complet", "German" to "Vollständiger Name", "Russian" to "Полное имя",
            "Arabic" to "الاسم الكامل", "Chinese" to "姓名", "Portuguese" to "Nome completo", "Hindi" to "पूरा नाम", "Japanese" to "フルネーム",
            "Korean" to "이름", "Italian" to "Nome Completo", "Turkish" to "Tam Adı", "Dutch" to "Volledige naam"
        ),
        "phone" to mapOf(
            "English" to "Registered Phone Number", "Spanish" to "Número de teléfono", "French" to "Numéro de téléphone", "German" to "Telefonnummer", "Russian" to "Номер телефона",
            "Arabic" to "رقم الهاتف", "Chinese" to "电话号码", "Portuguese" to "Número de telefone", "Hindi" to "फ़ोन नंबर", "Japanese" to "電話番号",
            "Korean" to "전화번호", "Italian" to "Numero di telefono", "Turkish" to "Telefon Numarası", "Dutch" to "Telefoonnummer"
        ),
        "country" to mapOf(
            "English" to "Country", "Spanish" to "País", "French" to "Pays", "German" to "Land", "Russian" to "Страна",
            "Arabic" to "البلد", "Chinese" to "国家", "Portuguese" to "País", "Hindi" to "देश", "Japanese" to "国",
            "Korean" to "국가", "Italian" to "Paese", "Turkish" to "Ülke", "Dutch" to "Land"
        ),
        "email" to mapOf(
            "English" to "Email Address", "Spanish" to "Correo electrónico", "French" to "Adresse e-mail", "German" to "E-Mail-Adresse", "Russian" to "Адрес эл. почты",
            "Arabic" to "عنوان البريد الإلكتروني", "Chinese" to "电子邮件地址", "Portuguese" to "Endereço de e-mail", "Hindi" to "ईमेल पता", "Japanese" to "メールアドレス",
            "Korean" to "이메일 주소", "Italian" to "Indirizzo e-mail", "Turkish" to "E-posta Adresi", "Dutch" to "E-mailadres"
        ),
        "save_changes" to mapOf(
            "English" to "Save Changes", "Spanish" to "Guardar cambios", "French" to "Enregistrer les modifications", "German" to "Änderungen speichern", "Russian" to "Сохранить изменения",
            "Arabic" to "حفظ التغييرات", "Chinese" to "保存修改", "Portuguese" to "Salvar alterações", "Hindi" to "बदलाव सहेजें", "Japanese" to "変更を保存",
            "Korean" to "변경 사항 저장", "Italian" to "Salva modifiche", "Turkish" to "Değişiklikleri Kaydet", "Dutch" to "Wijzigingen opslaan"
        ),
        "account_management" to mapOf(
            "English" to "Account Management", "Spanish" to "Gestión de cuentas", "French" to "Gestion du compte", "German" to "Kontoverwaltung", "Russian" to "Управление аккаунтом",
            "Arabic" to "إدارة الحساب", "Chinese" to "账户管理", "Portuguese" to "Gerenciamento de conta", "Hindi" to "अकाउंट प्रबंधन", "Japanese" to "アカウント管理",
            "Korean" to "계정 관리", "Italian" to "Gestione Account", "Turkish" to "Hesap Yönetimi", "Dutch" to "Accountbeheer"
        ),
        "your_signin_info" to mapOf(
            "English" to "Your Sign-in Information", "Spanish" to "Su información de inicio de sesión", "French" to "Vos informations de connexion", "German" to "Ihre Anmeldeinformationen", "Russian" to "Ваша информация для входа",
            "Arabic" to "معلومات تسجيل الدخول الخاصة بك", "Chinese" to "您的登录信息", "Portuguese" to "Suas informações de login", "Hindi" to "आपकी साइन-इन जानकारी", "Japanese" to "サインイン情報",
            "Korean" to "로그인 정보", "Italian" to "Le tue informazioni di accesso", "Turkish" to "Giriş Bilgileriniz", "Dutch" to "Uw aanmeldingsgegevens"
        ),
        "add_account_desc" to mapOf(
            "English" to "Manual credentials input & Continue with Google integration stub", "Spanish" to "Ingreso manual de credenciales y enlace de Google", "French" to "Saisie manuelle des identifiants et liaison Google", "German" to "Manuelle Anmeldedaten und Google-Verknüpfung", "Russian" to "Вручную или через Google",
            "Arabic" to "إدخال يدوي للمصادقة وتكامل Google", "Chinese" to "手动输入凭据和 Google 账号集成", "Portuguese" to "Entrada manual de credenciais e integração do Google", "Hindi" to "मैन्युअल क्रेडेंशियल इनपुट और Google एकीकरण", "Japanese" to "手動認証入力および Google 連携",
            "Korean" to "수동 자격 증명 입력 및 Google 통합", "Italian" to "Inserimento manuale delle credenziali e Google integration", "Turkish" to "Manuel kimlik bilgisi girişi ve Google entegrasyonu", "Dutch" to "Handmatige invoer van inloggegevens en Google-integratie"
        ),
        "switch_account" to mapOf(
            "English" to "Switch Account", "Spanish" to "Cambiar de cuenta", "French" to "Changer de compte", "German" to "Konto wechseln", "Russian" to "Переключить аккаунт",
            "Arabic" to "تبديل الحساب", "Chinese" to "切换账户", "Portuguese" to "Alternar conta", "Hindi" to "अकाउंट बदलें", "Japanese" to "アカウント切り替え",
            "Korean" to "계정 전환", "Italian" to "Cambia Account", "Turkish" to "Hesap Değiştir", "Dutch" to "Account wisselen"
        ),
        "switch_account_desc" to mapOf(
            "English" to "Hot-swap between multiple active user sessions", "Spanish" to "Intercambio rápido entre múltiples sesiones activas", "French" to "Bascule rapide entre plusieurs sessions actives", "German" to "Schneller Wechsel zwischen aktiven Benutzersitzungen", "Russian" to "Быстрое переключение между сессиями",
            "Arabic" to "التبديل السريع بين جلسات المستخدم النشطة", "Chinese" to "在多个活动用户会话之间快速切换", "Portuguese" to "Troca rápida entre várias sessões ativas", "Hindi" to "कई सक्रिय उपयोगकर्ता सत्रों के बीच त्वरित अदला-बदली", "Japanese" to "複数のアクティブセッション間での切り替え",
            "Korean" to "여러 활성 사용자 세션 간 빠른 전환", "Italian" to "Passaggio rapido tra più sessioni utente attive", "Turkish" to "Birden çok aktif kullanıcı oturumu arasında geçiş yapın", "Dutch" to "Snel wisselen tussen meerdere actieve gebruikerssessies"
        ),
        "delete_account_desc" to mapOf(
            "English" to "Permanent data purge instructions and validation prompts", "Spanish" to "Instrucciones de purga permanente y avisos de validación", "French" to "Instructions de suppression définitive et invites de validation", "German" to "Anweisungen zur dauerhaften Löschung und Validierung", "Russian" to "Инструкции по удалению данных и проверки",
            "Arabic" to "تعليمات تطهير البيانات الدائمة ومطالبات التحقق", "Chinese" to "永久数据清除说明和验证提示", "Portuguese" to "Instruções de exclusão definitiva e prompts de validação", "Hindi" to "स्थायी डेटा हटाने के निर्देश और सत्यापन संकेत", "Japanese" to "永久的なデータ消去の手順と確認プロンプト",
            "Korean" to "영구적인 데이터 삭제 지침 및 유효성 검사 프롬프트", "Italian" to "Istruzioni per la rimozione permanente dei dati e convalida", "Turkish" to "Kalıcı veri silme talimatları ve doğrulama istemleri", "Dutch" to "Instructies voor permanente gegevensverwijdering en validatie"
        ),
        "logout_desc" to mapOf(
            "English" to "Clear session tokens and return to login screen safely", "Spanish" to "Limpiar tokens de sesión y volver al inicio de forma segura", "French" to "Effacer les jetons de session et revenir à la connexion", "German" to "Sitzungstokens löschen und sicher zum Login zurückkehren", "Russian" to "Очистить токены сессии и безопасно выйти",
            "Arabic" to "مسح رموز الجلسة والعودة بأمان لشاشة تسجيل الدخول", "Chinese" to "清除会话令牌并安全返回登录页面", "Portuguese" to "Limpar tokens de sessão e retornar ao login com segurança", "Hindi" to "सत्र टोकन साफ़ करें और सुरक्षित रूप से लॉगिन स्क्रीन पर वापस जाएँ", "Japanese" to "セッショントークンをクリアし安全にログイン画面に戻る",
            "Korean" to "세션 토큰을 지우고 로그인 화면으로 안전하게 돌아가기", "Italian" to "Cancella token di sessione e torna in sicurezza al login", "Turkish" to "Oturum belirteçlerini temizleyin ve güvenli bir şekilde oturum açma ekranına dönün", "Dutch" to "Sessietokens wissen and veilig terugkeren naar inlogscherm"
        ),
        "lists_settings_title" to mapOf(
            "English" to "Custom Lists", "Spanish" to "Listas Personalizadas", "French" to "Listes Personnalisées", "German" to "Eigene Listen", "Russian" to "Свои списки",
            "Arabic" to "القوائم المخصصة", "Chinese" to "自定义列表", "Portuguese" to "Listas Personalizadas", "Hindi" to "कस्टम सूचियाँ", "Japanese" to "カスタムリスト",
            "Korean" to "맞춤 목록", "Italian" to "Liste Personalizzate", "Turkish" to "Özel Listeler", "Dutch" to "Aangepaste lijsten"
        ),
        "lists_settings_desc" to mapOf(
            "English" to "Create and manage custom categories (e.g., Favorites, Work, Family) to filter your chats on the home screen.",
            "Spanish" to "Cree y administre categorías personalizadas (por ejemplo, Favoritos, Trabajo, Familia) para filtrar sus chats en la pantalla de inicio.",
            "French" to "Créez et gérez des catégories personnalisées (ex. Favoris, Travail, Famille) pour filtrer vos discussions sur l'écran d'accueil.",
            "German" to "Erstellen und verwalten Sie eigene Kategorien (z. B. Favoriten, Arbeit, Familie), um Ihre Chats zu filtern.",
            "Russian" to "Создавайте и управляйте своими категориями (например, Избранное, Работа, Семья) для фильтрации чатов на главном экране.",
            "Arabic" to "إنشاء وإدارة فئات مخصصة (مثل المفضلة، العمل، العائلة) لتصفية محادثاتك على الشاشة الرئيسية.",
            "Chinese" to "创建和管理自定义类别（例如收藏夹、工作、家庭）以在主屏幕上筛选您的聊天。",
            "Portuguese" to "Crie e gerencie categorias personalizadas (ex: Favoritos, Trabalho, Família) para filtrar suas conversas na tela inicial.",
            "Hindi" to "अपनी होम स्क्रीन पर चैट को फ़िल्टर करने के लिए कस्टम श्रेणियां (जैसे, पसंदीदा, कार्य, परिवार) बनाएं और प्रबंधित करें।",
            "Japanese" to "ホーム画面でチャットをフィルタリングするためのカスタムカテゴリ（お気に入り、仕事、家族など）を作成および管理します。",
            "Korean" to "홈 화면에서 채팅을 필터링하기 위해 맞춤 카테고리(예: 즐겨찾기, 회사, 가족)를 만들고 관리합니다.",
            "Italian" to "Crea e gestisci categorie personalizzate (es. Preferiti, Lavoro, Famiglia) per filtrare le tue chat nella schermata iniziale.",
            "Turkish" to "Sohbetlerinizi ana ekranda filtrelemek için özel kategoriler (ör. Sık Kullanılanlar, İş, Aile) oluşturun ve yönetin.",
            "Dutch" to "Maak en beheer aangepaste categorieën (bijv. Favorieten, Werk, Familie) om uw chats op het startscherm te filteren."
        ),
        "create_new_list" to mapOf(
            "English" to "Create New List", "Spanish" to "Crear nueva lista", "French" to "Créer une nouvelle liste", "German" to "Neue Liste erstellen", "Russian" to "Создать новый список",
            "Arabic" to "إنشاء قائمة جديدة", "Chinese" to "创建新列表", "Portuguese" to "Criar nova lista", "Hindi" to "नई सूची बनाएं", "Japanese" to "新規リスト作成",
            "Korean" to "새 목록 만들기", "Italian" to "Crea Nuova Lista", "Turkish" to "Yeni Liste Oluştur", "Dutch" to "Nieuwe lijst maken"
        ),
        "no_lists_yet" to mapOf(
            "English" to "No custom lists created yet.", "Spanish" to "Aún no se han creado listas personalizadas.", "French" to "Aucune liste personnalisée créée.", "German" to "Noch keine eigenen Listen erstellt.", "Russian" to "Пока нет своих списков.",
            "Arabic" to "لم يتم إنشاء قوائم مخصصة بعد.", "Chinese" to "尚未创建自定义列表。", "Portuguese" to "Nenhuma lista personalizada criada ainda.", "Hindi" to "अभी तक कोई कस्टम सूचियाँ नहीं बनाई गई हैं।", "Japanese" to "カスタムリストはまだ作成されていません。",
            "Korean" to "아직 맞춤 목록이 생성되지 않았습니다.", "Italian" to "Nessuna lista personalizzata creata.", "Turkish" to "Henüz özel liste oluşturulmadı.", "Dutch" to "Nog geen aangepaste lijsten gemaakt."
        ),
        "chats_settings_title" to mapOf(
            "English" to "Chats Settings", "Spanish" to "Configuración de chats", "French" to "Paramètres de discussion", "German" to "Chat-Einstellungen", "Russian" to "Настройки чатов",
            "Arabic" to "إعدادات المحادثات", "Chinese" to "聊天设置", "Portuguese" to "Configurações de conversa", "Hindi" to "चैट्स सेटिंग्स", "Japanese" to "チャット設定",
            "Korean" to "채팅 설정", "Italian" to "Impostazioni Chat", "Turkish" to "Sohbet Ayarları", "Dutch" to "Chat-instellingen"
        ),
        "chats_settings_desc" to mapOf(
            "English" to "Configure message backups, font sizes, and input parameters.",
            "Spanish" to "Configure copias de seguridad de mensajes, tamaños de fuente y parámetros de entrada.",
            "French" to "Configurez les sauvegardes de messages, les tailles de police et les paramètres d'entrée.",
            "German" to "Konfigurieren Sie Nachrichten-Backups, Schriftgrößen und Eingabeparameter.",
            "Russian" to "Настройка резервного копирования, размера шрифта и параметров ввода.",
            "Arabic" to "تكوين نسخ الاحتياطية للرسائل وأحجام الخطوط ومعلمات الإدخال.",
            "Chinese" to "配置消息备份、字体大小和输入参数。",
            "Portuguese" to "Configure backups de mensagens, tamanhos de fonte e parâmetros de entrada.",
            "Hindi" to "संदेश बैकअप, फ़ॉन्ट आकार और इनपुट पैरामीटर कॉन्फ़िगर करें।",
            "Japanese" to "メッセージのバックアップ、フォントサイズ、入力パラメータを設定します。",
            "Korean" to "메시지 백업, 글꼴 크기 및 입력 매개변수를 구성합니다.",
            "Italian" to "Configura backup dei messaggi, dimensioni dei caratteri e parametri di input.",
            "Turkish" to "Mesaj yedeklemelerini, yazı tipi boyutlarını ve giriş parametrelerini yapılandırın.",
            "Dutch" to "Configureer berichtback-ups, lettergroottes en invoerparameters."
        ),
        "font_size_title" to mapOf(
            "English" to "Font Size", "Spanish" to "Tamaño de fuente", "French" to "Taille de la police", "German" to "Schriftgröße", "Russian" to "Размер шрифта",
            "Arabic" to "حجم الخط", "Chinese" to "字体大小", "Portuguese" to "Tamanho da fonte", "Hindi" to "फ़ॉन्ट का आकार", "Japanese" to "フォントサイズ",
            "Korean" to "글꼴 크기", "Italian" to "Dimensione Carattere", "Turkish" to "Yazı Tipi Boyutu", "Dutch" to "Lettergrootte"
        ),
        "font_size_desc" to mapOf(
            "English" to "Adjust text display dimensions inside chat windows", "Spanish" to "Ajustar dimensiones del texto en chats", "French" to "Ajuster la taille du texte dans les chats", "German" to "Textgröße in Chatfenstern anpassen", "Russian" to "Размер отображаемого текста в чатах",
            "Arabic" to "ضبط أبعاد عرض النص داخل نوافذ المحادثة", "Chinese" to "调整聊天窗口内文字显示大小", "Portuguese" to "Ajustar dimensões de texto dentro das conversas", "Hindi" to "चैट विंडो के भीतर पाठ प्रदर्शन आयामों को समायोजित करें", "Japanese" to "チャット画面内の文字表示サイズを調整します",
            "Korean" to "채팅창 내부 텍스트 표시 크기 조절", "Italian" to "Regola le dimensioni del testo all'interno delle chat", "Turkish" to "Sohbet pencerelerindeki metin boyutunu ayarlayın", "Dutch" to "Pas tekstgrootte aan binnen chatvensters"
        ),
        "enter_is_send" to mapOf(
            "English" to "Enter is Send", "Spanish" to "Enter envía", "French" to "Entrée pour envoyer", "German" to "Enter zum Senden", "Russian" to "Enter для отправки",
            "Arabic" to "إدخال للإرسال", "Chinese" to "回车键发送", "Portuguese" to "Enter envia", "Hindi" to "एंटर भेजें है", "Japanese" to "Enterで送信",
            "Korean" to "Enter로 전송", "Italian" to "Invia con Enter", "Turkish" to "Enter Gönderir", "Dutch" to "Enter is verzenden"
        ),
        "enter_is_send_desc" to mapOf(
            "English" to "Pressing Enter key on keyboard will instantly send message", "Spanish" to "Presionar Enter en el teclado enviará el mensaje al instante", "French" to "Appuyer sur Entrée envoie instantanément le message", "German" to "Eingabetaste auf der Tastatur sendet die Nachricht sofort", "Russian" to "Нажатие Enter на клавиатуре мгновенно отправит сообщение",
            "Arabic" to "سيؤدي الضغط على مفتاح Enter على لوحة المفاتيح لإرسال الرسالة على الفور", "Chinese" to "按键盘上的回车键将立即发送消息", "Portuguese" to "Pressionar a tecla Enter enviará a mensagem instantaneamente", "Hindi" to "कीबोर्ड पर एंटर की दबाने से संदेश तुरंत भेजा जाएगा", "Japanese" to "キーボードのEnterキーを押すとメッセージが即座に送信されます",
            "Korean" to "키보드의 엔터 키를 누르면 메시지가 즉시 전송됩니다", "Italian" to "La pressione del tasto Invio invierà istantaneamente il messaggio", "Turkish" to "Klavyede Enter tuşuna basılması mesajı anında gönderir", "Dutch" to "Drukken op Enter verzendt het bericht direct"
        ),
        "media_visibility" to mapOf(
            "English" to "Media Visibility", "Spanish" to "Visibilidad de archivos", "French" to "Visibilité des médias", "German" to "Sichtbarkeit von Medien", "Russian" to "Видимость медиа",
            "Arabic" to "رؤية الوسائط", "Chinese" to "媒体可见性", "Portuguese" to "Visibilidade de mídia", "Hindi" to "मीडिया दृश्यता", "Japanese" to "メディアの表示",
            "Korean" to "미디어 표시", "Italian" to "Visibilità Media", "Turkish" to "Medya Görünürlüğü", "Dutch" to "Mediazichtbaarheid"
        ),
        "media_visibility_desc" to mapOf(
            "English" to "Show newly downloaded media in your device's gallery", "Spanish" to "Mostrar archivos descargados en la galería del dispositivo", "French" to "Afficher les nouveaux médias téléchargés dans la galerie", "German" to "Heruntergeladene Medien in der Galerie anzeigen", "Russian" to "Показывать новые загруженные файлы в галерее",
            "Arabic" to "إظهار الوسائط التي تم تنزيلها حديثًا في معرض جهازك", "Chinese" to "在设备的相册中显示新下载的媒体", "Portuguese" to "Mostrar mídia recém-baixada na galeria do dispositivo", "Hindi" to "अपने डिवाइस की गैलरी में हाल ही में डाउनलोड किए गए मीडिया को दिखाएं", "Japanese" to "新しくダウンロードしたメディアをギャラリーに表示します",
            "Korean" to "새로 다운로드한 미디어를 기기 갤러리에 표시", "Italian" to "Mostra i media scaricati di recente nella galleria del dispositivo", "Turkish" to "Yeni indirilen medyaları cihazınızın galerisinde gösterin", "Dutch" to "Toon nieuw gedownloade media in de apparaatgalerie"
        ),
        "chat_backup" to mapOf(
            "English" to "Chat Backup", "Spanish" to "Copia de seguridad", "French" to "Sauvegarde", "German" to "Chat-Backup", "Russian" to "Резервная копия",
            "Arabic" to "نسخ احتياطي للمحادثات", "Chinese" to "聊天备份", "Portuguese" to "Backup de conversas", "Hindi" to "चैट बैकअप", "Japanese" to "チャットのバックアップ",
            "Korean" to "채팅 백업", "Italian" to "Backup delle Chat", "Turkish" to "Sohbet Yedeği", "Dutch" to "Chatback-up"
        ),
        "chat_backup_desc" to mapOf(
            "English" to "Back up your messages and media to cloud storage", "Spanish" to "Resguardar mensajes y archivos en la nube", "French" to "Sauvegarder les messages et médias sur le cloud", "German" to "Nachrichten und Medien in der Cloud sichern", "Russian" to "Резервное копирование сообщений в облако",
            "Arabic" to "نسخ رسائلك ووسائطك احتياطيًا إلى التخزين السحابي", "Chinese" to "将您的消息和媒体备份到云端存储", "Portuguese" to "Faça backup de suas mensagens e mídia no armazenamento em nuvem", "Hindi" to "अपने संदेशों और मीडिया को क्लाउड स्टोरेज में बैकअप करें", "Japanese" to "メッセージとメディアをクラウドストレージにバックアップします",
            "Korean" to "메시지 및 미디어를 클라우드 저장소에 백업", "Italian" to "Esegui il backup di messaggi e media sul cloud storage", "Turkish" to "Mesajlarınızı ve medyalarınızı bulut depolama alanına yedekleyin", "Dutch" to "Maak een back-up van berichten en media in cloudopslag"
        ),
        "backup_now" to mapOf(
            "English" to "Back Up Now", "Spanish" to "Crear copia ahora", "French" to "Sauvegarder maintenant", "German" to "Jetzt sichern", "Russian" to "Создать копию",
            "Arabic" to "نسخ احتياطي الآن", "Chinese" to "立即备份", "Portuguese" to "Fazer backup agora", "Hindi" to "अभी बैकअप लें", "Japanese" to "今すぐバックアップ",
            "Korean" to "지금 백업", "Italian" to "Esegui Backup Ora", "Turkish" to "Şimdi Yedekle", "Dutch" to "Nu back-up maken"
        ),
        "backup_logs" to mapOf(
            "English" to "Backup Logs", "Spanish" to "Registros de copias", "French" to "Journaux de sauvegarde", "German" to "Sicherungsprotokolle", "Russian" to "Логи резервных копий",
            "Arabic" to "سجلات النسخ الاحتياطي", "Chinese" to "备份日志", "Portuguese" to "Logs de backup", "Hindi" to "बैकअप लॉग", "Japanese" to "バックアップログ",
            "Korean" to "백업 로그", "Italian" to "Log di Backup", "Turkish" to "Yedekleme Günlükleri", "Dutch" to "Back-uplogs"
        ),
        "no_backup_logs" to mapOf(
            "English" to "No backup logs recorded yet.", "Spanish" to "No hay registros de copias grabados.", "French" to "Aucun journal de sauvegarde enregistré.", "German" to "Noch keine Sicherungsprotokolle aufgezeichnet.", "Russian" to "Логи бэкапов пока отсутствуют.",
            "Arabic" to "لم يتم تسجيل سجلات نسخ احتياطي بعد.", "Chinese" to "尚未记录备份日志。", "Portuguese" to "Nenhum log de backup registrado ainda.", "Hindi" to "अभी तक कोई बैकअप लॉग दर्ज नहीं किया गया है।", "Japanese" to "バックアップ履歴はまだ記録されていません。",
            "Korean" to "아직 백업 로그가 기록되지 않았습니다.", "Italian" to "Nessun log di backup registrato.", "Turkish" to "Henüz yedekleme günlüğü kaydedilmedi.", "Dutch" to "Nog geen back-uplogs geregistreerd."
        ),
        "theme_mode" to mapOf(
            "English" to "Theme Mode", "Spanish" to "Modo de tema", "French" to "Mode thème", "German" to "Themen-Modus", "Russian" to "Режим темы",
            "Arabic" to "وضع المظهر", "Chinese" to "主题模式", "Portuguese" to "Modo de tema", "Hindi" to "थीम मोड", "Japanese" to "テーマモード",
            "Korean" to "테마 모드", "Italian" to "Modalità Tema", "Turkish" to "Tema Modu", "Dutch" to "Themamodus"
        ),
        "light_mode" to mapOf(
            "English" to "Light Mode", "Spanish" to "Modo claro", "French" to "Mode clair", "German" to "Heller Modus", "Russian" to "Светлая тема",
            "Arabic" to "الوضع الفاتح", "Chinese" to "浅色模式", "Portuguese" to "Modo claro", "Hindi" to "लाइट मोड", "Japanese" to "ライトモード",
            "Korean" to "라이트 모드", "Italian" to "Modalità Chiara", "Turkish" to "Açık Tema", "Dutch" to "Lichte modus"
        ),
        "dark_mode" to mapOf(
            "English" to "Dark Mode", "Spanish" to "Modo oscuro", "French" to "Mode sombre", "German" to "Dunkler Modus", "Russian" to "Темная тема",
            "Arabic" to "الوضع الداكن", "Chinese" to "深色模式", "Portuguese" to "Modo escuro", "Hindi" to "डार्क मोड", "Japanese" to "ダークモード",
            "Korean" to "다크 모드", "Italian" to "Modalità Scura", "Turkish" to "Karanlık Tema", "Dutch" to "Donkere modus"
        ),
        "system_default" to mapOf(
            "English" to "System Default", "Spanish" to "Predeterminado", "French" to "Système par défaut", "German" to "Systemstandard", "Russian" to "Системная тема",
            "Arabic" to "افتراضي النظام", "Chinese" to "系统默认", "Portuguese" to "Padrão do sistema", "Hindi" to "सिस्टम डिफ़ॉल्ट", "Japanese" to "システムデフォルト",
            "Korean" to "시스템 기본값", "Italian" to "Predefinito di Sistema", "Turkish" to "Sistem Varsayılanı", "Dutch" to "Systeemstandaard"
        ),
        "notifications_settings_title" to mapOf(
            "English" to "Notifications Settings", "Spanish" to "Ajustes de notificaciones", "French" to "Paramètres de notification", "German" to "Benachrichtigungseinstellungen", "Russian" to "Настройки уведомлений",
            "Arabic" to "إعدادات الإشعارات", "Chinese" to "通知设置", "Portuguese" to "Ajustes de notificações", "Hindi" to "सूचनाएं सेटिंग्स", "Japanese" to "通知設定",
            "Korean" to "알림 설정", "Italian" to "Impostazioni Notifiche", "Turkish" to "Bildirim Ayarları", "Dutch" to "Meldingen-instellingen"
        ),
        "notifications_settings_desc" to mapOf(
            "English" to "Configure alert tones, group notifications, and vibration preferences.",
            "Spanish" to "Configure tonos de alerta, notificaciones de grupo y preferencias de vibración.",
            "French" to "Configurez les tonalités d'alerte, les notifications de groupe et les préférences de vibration.",
            "German" to "Konfigurieren Sie Hinweistöne, Gruppenbenachrichtigungen und Vibration.",
            "Russian" to "Настройка звуков, уведомлений групп и вибрации.",
            "Arabic" to "تكوين نغمات التنبيه وإشعارات المجموعة وتفضيلات الاهتزاز.",
            "Chinese" to "配置提示音、群组通知和振动首选项。",
            "Portuguese" to "Configure tons de alerta, notificações de grupo e preferências de vibração.",
            "Hindi" to "अलर्ट टोन, समूह सूचनाएं और कंपन प्राथमिकताएं कॉन्फ़िगर करें।",
            "Japanese" to "アラート音、グループ通知、バイブレーション設定を設定します。",
            "Korean" to "경고음, 그룹 알림 및 진동 기본 설정을 구성합니다.",
            "Italian" to "Configura i toni di avviso, le notifiche di gruppo e le preferenze di vibrazione.",
            "Turkish" to "Uyarı seslerini, grup bildirimlerini ve titreşim tercihlerini yapılandırın.",
            "Dutch" to "Configureer waarschuwingstonen, groepsnotificaties en trilvoorkeuren."
        ),
        "message_tone" to mapOf(
            "English" to "Message Tone", "Spanish" to "Tono de mensajes", "French" to "Son des messages", "German" to "Nachrichtenton", "Russian" to "Звук сообщений",
            "Arabic" to "نغمة الرسائل", "Chinese" to "消息提示音", "Portuguese" to "Tom de mensagem", "Hindi" to "संदेश टोन", "Japanese" to "メッセージ音",
            "Korean" to "메시지 수신음", "Italian" to "Tono Messaggi", "Turkish" to "Mesaj Sesi", "Dutch" to "Berichttoon"
        ),
        "call_ringtone" to mapOf(
            "English" to "Call Ringtone", "Spanish" to "Tono de llamadas", "French" to "Sonnerie d'appel", "German" to "Anrufklingelton", "Russian" to "Мелодия звонка",
            "Arabic" to "نغمة المكالمات", "Chinese" to "来电铃声", "Portuguese" to "Toque de chamada", "Hindi" to "कॉल रिंगटोन", "Japanese" to "着信音",
            "Korean" to "벨소리", "Italian" to "Suoneria Chiamate", "Turkish" to "Arama Zil Sesi", "Dutch" to "Beltoon"
        ),
        "vibration" to mapOf(
            "English" to "Vibration", "Spanish" to "Vibración", "French" to "Vibration", "German" to "Vibration", "Russian" to "Вибрация",
            "Arabic" to "الاهتزاز", "Chinese" to "振动", "Portuguese" to "Vibração", "Hindi" to "कंपन", "Japanese" to "バイブレーション",
            "Korean" to "진동", "Italian" to "Vibrazione", "Turkish" to "Titreşim", "Dutch" to "Trillen"
        ),
        "reaction_alerts" to mapOf(
            "English" to "Reaction Alerts", "Spanish" to "Alertas de reacciones", "French" to "Alertes de réactions", "German" to "Reaktions-Hinweise", "Russian" to "Оповещения о реакциях",
            "Arabic" to "تنبيهات التفاعل", "Chinese" to "回应提醒", "Portuguese" to "Alertas de reação", "Hindi" to "प्रतिक्रिया अलर्ट", "Japanese" to "リアクション通知",
            "Korean" to "반응 알림", "Italian" to "Avvisi Reazioni", "Turkish" to "Tepki Uyarıları", "Dutch" to "Reactiemeldingen"
        ),
        "reaction_alerts_desc" to mapOf(
            "English" to "Show notifications for reactions to messages you send", "Spanish" to "Mostrar notificaciones de reacciones a sus mensajes", "French" to "Afficher les notifications pour les réactions reçues", "German" to "Meldungen bei Reaktionen auf Ihre Nachrichten anzeigen", "Russian" to "Уведомлять о реакциях на ваши сообщения",
            "Arabic" to "عرض إشعارات للتفاعلات على الرسائل التي ترسلها", "Chinese" to "为您发送的消息显示回应通知", "Portuguese" to "Mostrar notificações para reações às mensagens que você envia", "Hindi" to "आपके द्वारा भेजे जाने वाले संदेशों की प्रतिक्रियाओं के लिए सूचनाएं दिखाएं", "Japanese" to "送信したメッセージに対するリアクションの通知を表示します",
            "Korean" to "보낸 메시지에 대한 반응 알림 표시", "Italian" to "Mostra notifiche per le reazioni ai messaggi che invii", "Turkish" to "Gönderdiğiniz mesajlara verilen tepkiler için bildirimleri gösterin", "Dutch" to "Toon notificaties voor reacties op verzonden berichten"
        ),
        "storage_settings_title" to mapOf(
            "English" to "Storage & Data Settings", "Spanish" to "Almacenamiento y Datos", "French" to "Stockage et Données", "German" to "Speicher- & Dateneinstellungen", "Russian" to "Данные и хранилище",
            "Arabic" to "إعدادات التخزين والبيانات", "Chinese" to "存储与数据设置", "Portuguese" to "Ajustes de Armazenamento e Dados", "Hindi" to "स्टोरेज और डेटा सेटिंग्स", "Japanese" to "ストレージとデータ設定",
            "Korean" to "저장 공간 및 데이터 설정", "Italian" to "Spazio e Dati Impostazioni", "Turkish" to "Depolama ve Veri Ayarları", "Dutch" to "Opslag- en gegevensinstellingen"
        ),
        "storage_settings_desc" to mapOf(
            "English" to "Track network usage, manage cache, and configure media auto-download.",
            "Spanish" to "Rastree el uso de red, gestione el caché y configure descargas automáticas.",
            "French" to "Suivez l'usage réseau, gérez le cache et configurez les téléchargements.",
            "German" to "Netzwerknutzung verfolgen, Cache verwalten und Medien-Downloads konfigurieren.",
            "Russian" to "Контролируйте трафик, управляйте кэшем и настраивайте автозагрузку.",
            "Arabic" to "تتبع استخدام الشبكة، وإدارة ذاكرة التخزين المؤقت، وتكوين التنزيل التلقائي للوسائط.",
            "Chinese" to "跟踪网络使用情况，管理缓存并配置媒体自动下载。",
            "Portuguese" to "Rastreie o uso da rede, gerencie o cache e configure o download automático.",
            "Hindi" to "नेटवर्क उपयोग ट्रैक करें, कैश प्रबंधित करें, और मीडिया ऑटो-डाउनलोड कॉन्फ़िगर करें।",
            "Japanese" to "ネットワーク使用状況の追跡、キャッシュ管理、メディア自動ダウンロードの設定をします。",
            "Korean" to "네트워크 사용량 추적, 캐시 관리 및 미디어 자동 다운로드 구성.",
            "Italian" to "Monitora l'uso della rete, gestisci la cache e configura il download automatico dei media.",
            "Turkish" to "Ağ kullanımını takip edin, önbelleği yönetin ve medya otomatik indirmesini yapılandırın.",
            "Dutch" to "Volg netwerkgebruik, beheer cache en configureer automatisch downloaden van media."
        ),
        "network_usage" to mapOf(
            "English" to "Network Usage", "Spanish" to "Uso de red", "French" to "Utilisation réseau", "German" to "Netzwerknutzung", "Russian" to "Использование сети",
            "Arabic" to "استخدام الشبكة", "Chinese" to "网络 사용量", "Portuguese" to "Uso de rede", "Hindi" to "नेटवर्क उपयोग", "Japanese" to "ネットワーク使用量",
            "Korean" to "네트워크 사용량", "Italian" to "Utilizzo Rete", "Turkish" to "Ağ Kullanımı", "Dutch" to "Netwerkgebruik"
        ),
        "cache_usage" to mapOf(
            "English" to "Cache Usage", "Spanish" to "Uso de caché", "French" to "Espace cache", "German" to "Cache-Nutzung", "Russian" to "Использование кэша",
            "Arabic" to "استخدام التخزين المؤقت", "Chinese" to "缓存使用情况", "Portuguese" to "Uso de cache", "Hindi" to "कैश उपयोग", "Japanese" to "キャッシュ使用量",
            "Korean" to "캐시 사용량", "Italian" to "Utilizzo Cache", "Turkish" to "Önbellek Kullanımı", "Dutch" to "Cachegebruik"
        ),
        "voice_notes" to mapOf(
            "English" to "Voice Notes", "Spanish" to "Notas de voz", "French" to "Notes vocales", "German" to "Sprachnotizen", "Russian" to "Голосовые заметки",
            "Arabic" to "الرسائل الصوتية", "Chinese" to "语音消息", "Portuguese" to "Mensagens de voz", "Hindi" to "आवाज संदेश", "Japanese" to "音声メッセージ",
            "Korean" to "음성 메시지", "Italian" to "Note Vocali", "Turkish" to "Sesli Notlar", "Dutch" to "Gesproken berichten"
        ),
        "photos" to mapOf(
            "English" to "Photos", "Spanish" to "Fotos", "French" to "Photos", "German" to "Fotos", "Russian" to "Фотографии",
            "Arabic" to "الصور", "Chinese" to "图片", "Portuguese" to "Fotos", "Hindi" to "फ़ोटो", "Japanese" to "写真",
            "Korean" to "사진", "Italian" to "Foto", "Turkish" to "Fotoğraflar", "Dutch" to "Foto's"
        ),
        "pdfs" to mapOf(
            "English" to "PDFs", "Spanish" to "PDFs", "French" to "PDF", "German" to "PDFs", "Russian" to "PDF-файлы",
            "Arabic" to "ملفات PDF", "Chinese" to "PDF文档", "Portuguese" to "PDFs", "Hindi" to "पीडीएफ", "Japanese" to "PDF",
            "Korean" to "PDF", "Italian" to "PDF", "Turkish" to "PDF'ler", "Dutch" to "Pdf's"
        ),
        "docs" to mapOf(
            "English" to "Documents", "Spanish" to "Documentos", "French" to "Documents", "German" to "Dokumente", "Russian" to "Документы",
            "Arabic" to "المستندات", "Chinese" to "文档", "Portuguese" to "Documentos", "Hindi" to "दस्तावेज़", "Japanese" to "ドキュメント",
            "Korean" to "문서", "Italian" to "Documenti", "Turkish" to "Belgeler", "Dutch" to "Documenten"
        ),
        "clear_cache" to mapOf(
            "English" to "Clear Cache", "Spanish" to "Limpiar caché", "French" to "Vider le cache", "German" to "Cache leeren", "Russian" to "Очистить кэш",
            "Arabic" to "مسح التخزين المؤقت", "Chinese" to "清除缓存", "Portuguese" to "Limpar cache", "Hindi" to "कैश साफ़ करें", "Japanese" to "キャッシュをクリア",
            "Korean" to "캐시 지우기", "Italian" to "Svuota Cache", "Turkish" to "Önbelleği Temizle", "Dutch" to "Cache wissen"
        ),
        "media_auto_download" to mapOf(
            "English" to "Media Auto-Download", "Spanish" to "Descarga automática", "French" to "Téléchargement auto", "German" to "Medien automatisch laden", "Russian" to "Автозагрузка медиа",
            "Arabic" to "تنزيل الوسائط التلقائي", "Chinese" to "媒体自动下载", "Portuguese" to "Download automático", "Hindi" to "मीडिया ऑटो-डाउनलोड", "Japanese" to "メディアの自動ダウンロード",
            "Korean" to "미디어 자동 다운로드", "Italian" to "Download Automatico Media", "Turkish" to "Medya Otomatik İndirme", "Dutch" to "Media automatisch downloaden"
        ),
        "mobile_data" to mapOf(
            "English" to "When using mobile data", "Spanish" to "Con datos móviles", "French" to "En données mobiles", "German" to "Bei mobilen Daten", "Russian" to "Мобильная сеть",
            "Arabic" to "عند استخدام بيانات الهاتف", "Chinese" to "使用移动数据时", "Portuguese" to "Ao usar dados móveis", "Hindi" to "मोबाइल डेटा का उपयोग करते समय", "Japanese" to "モバイルデータ通信時",
            "Korean" to "모바일 데이터 사용할 때", "Italian" to "Durante l'uso dei dati mobili", "Turkish" to "Mobil veri kullanırken", "Dutch" to "Bij gebruik van mobiele data"
        ),
        "wifi" to mapOf(
            "English" to "When connected on Wi-Fi", "Spanish" to "Con Wi-Fi", "French" to "En Wi-Fi", "German" to "Über WLAN", "Russian" to "Сеть Wi-Fi",
            "Arabic" to "عند الاتصال بـ Wi-Fi", "Chinese" to "使用 Wi-Fi 时", "Portuguese" to "Ao usar Wi-Fi", "Hindi" to "वाई-फाई पर जुड़े होने पर", "Japanese" to "Wi-Fi接続時",
            "Korean" to "Wi-Fi 연결될 때", "Italian" to "Connesso a Wi-Fi", "Turkish" to "Wi-Fi'ye bağlıyken", "Dutch" to "Bij verbinding met wifi"
        ),
        "invite_friend" to mapOf(
            "English" to "Invite a Friend", "Spanish" to "Invitar Amigo", "French" to "Inviter un ami", "German" to "Freund einladen", "Russian" to "Пригласить друга",
            "Arabic" to "دعوة صديق", "Chinese" to "邀请好友", "Portuguese" to "Convidar Amigo", "Hindi" to "मित्र को आमंत्रित करें", "Japanese" to "友人を招待",
            "Korean" to "친구 초대", "Italian" to "Invita un Amico", "Turkish" to "Arkadaşını Davet Et", "Dutch" to "Nodig een vriend uit"
        ),
        "invite_desc" to mapOf(
            "English" to "Share SmartFit Wellness with your friends and help them lead a healthier life.",
            "Spanish" to "Comparta SmartFit Wellness con sus amigos y ayúdelos a tener una vida más sana.",
            "French" to "Partagez SmartFit Wellness avec vos amis pour les aider à vivre plus sainement.",
            "German" to "Teilen Sie SmartFit Wellness mit Ihren Freunden und helfen Sie ihnen, gesünder zu leben.",
            "Russian" to "Поделитесь SmartFit Wellness с друзьями и помогите им вести здоровый образ жизни.",
            "Arabic" to "شارك سمارت فيت للصحة مع أصدقائك وساعدهم على عيش حياة صحية.",
            "Chinese" to "与您的朋友分享 SmartFit Wellness，帮助他们过上更健康的生活。",
            "Portuguese" to "Compartilhe o SmartFit Wellness com seus amigos e ajude-os a ter uma vida saudável.",
            "Hindi" to "अपने दोस्तों के साथ स्मार्टफिट वेलनेस साझा करें और उन्हें एक स्वस्थ जीवन जीने में मदद करें।",
            "Japanese" to "スマートフィットウェルネスを友達と共有して、健康的な生活を送りましょう。",
            "Korean" to "친구들과 SmartFit Wellness를 공유하고 그들이 더 건강한 삶을 살 수 있도록 도와주세요.",
            "Italian" to "Condividi SmartFit Wellness con i tuoi amici e aiutali a condurre una vita più sana.",
            "Turkish" to "SmartFit Sağlık uygulamasını arkadaşlarınızla paylaşın ve daha sağlıklı yaşamalarına yardımcı olun.",
            "Dutch" to "Deel SmartFit Wellness met uw vrienden en help hen een gezonder leven te leiden."
        ),
        "share_referral_link" to mapOf(
            "English" to "Share Referral Link", "Spanish" to "Compartir enlace", "French" to "Partager le lien", "German" to "Empfehlungslink teilen", "Russian" to "Поделиться ссылкой",
            "Arabic" to "مشاركة رابط الإحالة", "Chinese" to "分享推荐链接", "Portuguese" to "Compartilhar link de indicação", "Hindi" to "रेफ़रल लिंक साझा करें", "Japanese" to "紹介リンクを共有",
            "Korean" to "추천 링크 공유", "Italian" to "Condividi Link", "Turkish" to "Referans Bağlantısını Paylaş", "Dutch" to "Deel verwijzingslink"
        ),
        "back" to mapOf(
            "English" to "Back", "Spanish" to "Atrás", "French" to "Retour", "German" to "Zurück", "Russian" to "Назад",
            "Arabic" to "رجوع", "Chinese" to "返回", "Portuguese" to "Voltar", "Hindi" to "पीछे", "Japanese" to "戻る",
            "Korean" to "뒤로", "Italian" to "Indietro", "Turkish" to "Geri", "Dutch" to "Terug"
        ),
        "select_country_title" to mapOf(
            "English" to "Select Your Country", "Spanish" to "Seleccione su país", "French" to "Sélectionnez votre pays", "German" to "Wählen Sie Ihr Land", "Russian" to "Выберите страну",
            "Arabic" to "اختر بلدك", "Chinese" to "选择您的国家", "Portuguese" to "Selecione seu país", "Hindi" to "अपना देश चुनें", "Japanese" to "国を選択",
            "Korean" to "국가 선택", "Italian" to "Seleziona il tuo Paese", "Turkish" to "Ülkenizi Seçin", "Dutch" to "Selecteer uw land"
        ),
        "select_country_desc" to mapOf(
            "English" to "Configure default country code parameters and privacy terms", "Spanish" to "Configurar código de país y términos de privacidad", "French" to "Configurer l'indicatif pays et les conditions de confidentialité", "German" to "Landesvorwahl und Datenschutzbedingungen konfigurieren", "Russian" to "Настройка телефонного кода страны и условий",
            "Arabic" to "تكوين معلمات رمز البلد الافتراضي وشروط الخصوصية", "Chinese" to "配置默认国家代码参数和隐私条款", "Portuguese" to "Configurar parâmetros de código de país e termos de privacidade", "Hindi" to "डिफ़ॉल्ट देश कोड पैरामीटर और गोपनीयता शर्तें कॉन्फ़िगर करें", "Japanese" to "デフォルトの国番号とプライバシー条件を設定します",
            "Korean" to "기본 국가 코드 매개변수 및 개인정보 보호 약관 구성", "Italian" to "Configura il prefisso internazionale e i termini della privacy", "Turkish" to "Varsayılan ülke kodu parametrelerini ve gizlilik şartlarını yapılandırın", "Dutch" to "Configureer standaard landcodeparameters en privacyvoorwaarden"
        ),
        "search_country_placeholder" to mapOf(
            "English" to "Search Country...", "Spanish" to "Buscar país...", "French" to "Rechercher un pays...", "German" to "Land suchen...", "Russian" to "Поиск страны...",
            "Arabic" to "البحث عن بلد...", "Chinese" to "搜索国家...", "Portuguese" to "Pesquisar país...", "Hindi" to "देश खोजें...", "Japanese" to "国を検索...",
            "Korean" to "국가 검색...", "Italian" to "Cerca Paese...", "Turkish" to "Ülke Ara...", "Dutch" to "Land zoeken..."
        ),
        "proceed_lang_selection" to mapOf(
            "English" to "Proceed to Language Selection", "Spanish" to "Continuar al idioma", "French" to "Passer à la sélection de langue", "German" to "Weiter zur Sprachauswahl", "Russian" to "Перейти к выбору языка",
            "Arabic" to "الانتقال إلى اختيار اللغة", "Chinese" to "继续选择语言", "Portuguese" to "Prosseguir para seleção de idioma", "Hindi" to "भाषा चयन पर आगे बढ़ें", "Japanese" to "言語選択へ進む",
            "Korean" to "언어 선택으로 진행", "Italian" to "Procedi alla Selezione della Lingua", "Turkish" to "Dil Seçimine Devam Et", "Dutch" to "Doorgaan naar taalselectie"
        ),
        "permissions_title" to mapOf(
            "English" to "Permissions Required", "Spanish" to "Permisos requeridos", "French" to "Autorisations requises", "German" to "Berechtigungen erforderlich", "Russian" to "Требуются разрешения",
            "Arabic" to "الأذونات المطلوبة", "Chinese" to "需要权限", "Portuguese" to "Permissões Necessárias", "Hindi" to "अनुमतियाँ आवश्यक हैं", "Japanese" to "必要な権限",
            "Korean" to "권한 필요", "Italian" to "Permessi Richiesti", "Turkish" to "İzinler Gerekli", "Dutch" to "Machtigingen vereist"
        ),
        "permissions_desc" to mapOf(
            "English" to "Please authorize permissions to ensure dynamic wellness features work seamlessly", "Spanish" to "Autorice los permisos para el correcto funcionamiento de las funciones de bienestar", "French" to "Veuillez autoriser les permissions pour le bon fonctionnement des services de bien-être", "German" to "Bitte erteilen Sie Berechtigungen für eine einwandfreie Funktion", "Russian" to "Пожалуйста, предоставьте доступ для корректной работы приложения",
            "Arabic" to "يرجى تفويض الأذونات لضمان عمل ميزات الصحة بسلاسة", "Chinese" to "请授权相关权限以确保健康功能无缝运行", "Portuguese" to "Autorize as permissões para garantir o funcionamento correto", "Hindi" to "कृपया गतिशील वेलनेस सुविधाओं को सुचारू रूप से चलाने के लिए अनुमतियाँ अधिकृत करें", "Japanese" to "ウェルネス機能がスムーズに動作するよう権限を許可してください",
            "Korean" to "웰니스 기능이 원활하게 작동하도록 권한을 허용해 주세요", "Italian" to "Autorizza i permessi per garantire il corretto funzionamento delle funzionalità", "Turkish" to "Lütfen sağlık özelliklerinin sorunsuz çalışması için izinleri onaylayın", "Dutch" to "Geef machtigingen om ervoor te zorgen dat wellnessfuncties naadloos werken"
        ),
        "permissions_media_title" to mapOf(
            "English" to "Media Storage", "Spanish" to "Almacenamiento", "French" to "Stockage médias", "German" to "Medienspeicher", "Russian" to "Доступ к файлам",
            "Arabic" to "تخزين الوسائط", "Chinese" to "媒体存储", "Portuguese" to "Armazenamento", "Hindi" to "मीडिया स्टोरेज", "Japanese" to "メディアストレージ",
            "Korean" to "미디어 저장소", "Italian" to "Archivio Media", "Turkish" to "Medya Depolama", "Dutch" to "Media-opslag"
        ),
        "permissions_media_desc" to mapOf(
            "English" to "Access to load user avatars and backup files locally", "Spanish" to "Permite cargar avatares y guardar copias de seguridad", "French" to "Permet de charger des avatars et sauvegarder localement", "German" to "Ermöglicht das Laden von Avataren und Sichern von Dateien", "Russian" to "Позволяет загружать аватары и сохранять бэкапы",
            "Arabic" to "الوصول لتحميل الصور الشخصية والملفات الاحتياطية محليًا", "Chinese" to "允许在本地加载用户头像和备份文件", "Portuguese" to "Acesso para carregar avatares e arquivos de backup localmente", "Hindi" to "उपयोगकर्ता अवतार और बैकअप फ़ाइलों को स्थानीय रूप से लोड करने की अनुमति", "Japanese" to "アバター画像やバックアップファイルを読み込むために必要です",
            "Korean" to "사용자 아바타 및 백업 파일을 로컬에서 로드하는 권한", "Italian" to "Accesso per caricare gli avatar degli utenti e i file di backup", "Turkish" to "Kullanıcı avatarlarını yüklemek ve dosyaları yerel olarak yedeklemek için erişim", "Dutch" to "Toegang om gebruikersavatars en back-upbestanden lokaal te laden"
        ),
        "permissions_contacts_title" to mapOf(
            "English" to "Contact List", "Spanish" to "Contactos", "French" to "Contacts", "German" to "Kontakte", "Russian" to "Контакты",
            "Arabic" to "جهات الاتصال", "Chinese" to "联系人", "Portuguese" to "Contatos", "Hindi" to "संपर्क सूची", "Japanese" to "連絡先リスト",
            "Korean" to "연락처 목록", "Italian" to "Contatti", "Turkish" to "Kişi Listesi", "Dutch" to "Contactenlijst"
        ),
        "permissions_contacts_desc" to mapOf(
            "English" to "Read phone contacts for automated WhatsApp messages and lists", "Spanish" to "Permite leer contactos para automatizaciones y listas", "French" to "Permet de lire les contacts pour les automatisations", "German" to "Ermöglicht das Lesen von Kontakten für Automatisierungen", "Russian" to "Доступ к контактам для автоматических рассылок",
            "Arabic" to "قراءة جهات الاتصال للرسائل التلقائية والقوائم", "Chinese" to "读取电话联系人以进行自动消息发送和分组", "Portuguese" to "Permite ler contatos para mensagens automatizadas e listas", "Hindi" to "स्वचालित संदेशों और सूचियों के लिए फ़ोन संपर्कों को पढ़ें", "Japanese" to "自動メッセージ送信用に連絡先を読み込むために必要です",
            "Korean" to "자동 메시지 및 그룹 목록을 위해 연락처를 읽는 권한", "Italian" to "Lettura dei contatti per invio messaggi automatici e liste", "Turkish" to "Otomatik WhatsApp mesajları ve listeleri için kişi listesini okuyun", "Dutch" to "Lees contacten voor automatische berichten en lijsten"
        ),
        "permissions_bg_title" to mapOf(
            "English" to "Background Wakeup", "Spanish" to "Segundo plano", "French" to "Réveil en arrière-plan", "German" to "Hintergrund-Aktivität", "Russian" to "Фоновый режим",
            "Arabic" to "الاستيقاظ في الخلفية", "Chinese" to "后台运行", "Portuguese" to "Atividade em segundo plano", "Hindi" to "पृष्ठभूमि जागृति", "Japanese" to "バックグラウンド起動",
            "Korean" to "백업 작동 백그라운드", "Italian" to "Attività in Background", "Turkish" to "Arka Plandan Uyandırma", "Dutch" to "Achtergrondactiviteit"
        ),
        "permissions_bg_desc" to mapOf(
            "English" to "Run reminders and scheduled tasks reliably in the background", "Spanish" to "Permite ejecutar tareas programadas de forma confiable en segundo plano", "French" to "Exécuter les rappels planifiés de manière fiable en arrière-plan", "German" to "Erinnerungen im Hintergrund zuverlässig ausführen", "Russian" to "Обеспечивает надежную работу напоминаний в фоне",
            "Arabic" to "تشغيل التذكيرات والمهام المجدولة بشكل موثوق في الخلفية", "Chinese" to "在后台可靠地运行提醒和计划的任务", "Portuguese" to "Executar lembretes e tarefas agendadas em segundo plano de forma confiável", "Hindi" to "पृष्ठभूमि में मज़बूती से अनुस्मारक और निर्धारित कार्य चलाएं", "Japanese" to "バックグラウンドでリマインダーを確実に実行するために必要です",
            "Korean" to "백그라운드에서 알림 및 예정된 작업을 안정적으로 실행", "Italian" to "Esegui promemoria e attività pianificate in background", "Turkish" to "Hatırlatıcıları ve planlanan görevleri arka planda güvenle çalıştırın", "Dutch" to "Voer herinneringen en geplande taken betrouwbaar uit in de achtergrond"
        ),
        "next" to mapOf(
            "English" to "Next", "Spanish" to "Siguiente", "French" to "Suivant", "German" to "Weiter", "Russian" to "Далее",
            "Arabic" to "التالي", "Chinese" to "下一步", "Portuguese" to "Avançar", "Hindi" to "आगे", "Japanese" to "次へ",
            "Korean" to "다음", "Italian" to "Avanti", "Turkish" to "Sonraki", "Dutch" to "Volgende"
        ),
        "back_btn" to mapOf(
            "English" to "Back", "Spanish" to "Atrás", "French" to "Retour", "German" to "Zurück", "Russian" to "Назад",
            "Arabic" to "رجوع", "Chinese" to "返回", "Portuguese" to "Voltar", "Hindi" to "पीछे", "Japanese" to "戻る",
            "Korean" to "뒤로", "Italian" to "Indietro", "Turkish" to "Geri", "Dutch" to "Terug"
        ),
        "select_lang_title" to mapOf(
            "English" to "Select Application Language", "Spanish" to "Seleccione el idioma de la aplicación", "French" to "Sélectionnez la langue de l'application", "German" to "App-Sprache wählen", "Russian" to "Выберите язык приложения",
            "Arabic" to "اختر لغة التطبيق", "Chinese" to "选择应用语言", "Portuguese" to "Selecione o idioma do aplicativo", "Hindi" to "एप्लिकेशन भाषा चुनें", "Japanese" to "アプリの言語を選択",
            "Korean" to "앱 언어 선택", "Italian" to "Seleziona Lingua dell'Applicazione", "Turkish" to "Uygulama Dilini Seçin", "Dutch" to "Selecteer applicatietaal"
        ),
        "selected_region" to mapOf(
            "English" to "Selected Region", "Spanish" to "Región seleccionada", "French" to "Région sélectionnée", "German" to "Ausgewählte Region", "Russian" to "Выбранный регион",
            "Arabic" to "المنطقة المحددة", "Chinese" to "选择的地区", "Portuguese" to "Região selecionada", "Hindi" to "चयनित क्षेत्र", "Japanese" to "選択された地域",
            "Korean" to "선택된 지역", "Italian" to "Regione Selezionata", "Turkish" to "Seçilen Bölge", "Dutch" to "Geselecteerde regio"
        ),
        "search_lang_placeholder" to mapOf(
            "English" to "Search Languages...", "Spanish" to "Buscar idiomas...", "French" to "Rechercher des langues...", "German" to "Sprachen suchen...", "Russian" to "Поиск языков...",
            "Arabic" to "البحث عن اللغات...", "Chinese" to "搜索语言...", "Portuguese" to "Pesquisar idiomas...", "Hindi" to "भाषाएं खोजें...", "Japanese" to "言語を検索...",
            "Korean" to "언어 검색...", "Italian" to "Cerca Lingue...", "Turkish" to "Dil Ara...", "Dutch" to "Talen zoeken..."
        ),
        "onboarding_guidance" to mapOf(
            "English" to "Onboarding Guidance", "Spanish" to "Guía de inicio", "French" to "Guide d'intégration", "German" to "Einführungshandbuch", "Russian" to "Руководство по началу работы",
            "Arabic" to "دليل البدء", "Chinese" to "入门指南", "Portuguese" to "Guia de integração", "Hindi" to "ऑनबोर्डिंग मार्गदर्शन", "Japanese" to "オンボーディングガイダンス",
            "Korean" to "온보딩 안내", "Italian" to "Guida Introduttiva", "Turkish" to "Başlangıç Kılavuzu", "Dutch" to "Introductiebegeleiding"
        ),
        "proceed_signin" to mapOf(
            "English" to "Proceed to Secure Sign-In", "Spanish" to "Ir a inicio de sesión", "French" to "Passer à la connexion sécurisée", "German" to "Weiter zum sicheren Login", "Russian" to "Перейти к безопасному входу",
            "Arabic" to "الانتقال إلى تسجيل الدخول الآمن", "Chinese" to "继续进行安全登录", "Portuguese" to "Prosseguir para login seguro", "Hindi" to "सुरक्षित साइन-इन पर आगे बढ़ें", "Japanese" to "安全なログインへ進む",
            "Korean" to "안전한 로그인으로 진행", "Italian" to "Procedi al Login Sicuro", "Turkish" to "Güvenli Girişe Devam Et", "Dutch" to "Doorgaan naar beveiligd inloggen"
        ),
        "select_country" to mapOf(
            "English" to "Select Country", "Spanish" to "Seleccionar país", "French" to "Sélectionner le pays", "German" to "Land auswählen", "Russian" to "Выбрать страну",
            "Arabic" to "اختر البلد", "Chinese" to "选择国家", "Portuguese" to "Selecionar país", "Hindi" to "देश चुनें", "Japanese" to "国を選択",
            "Korean" to "국가 선택", "Italian" to "Seleziona Paese", "Turkish" to "Ülke Seç", "Dutch" to "Land selecteren"
        ),
        "user_profile_updated" to mapOf(
            "English" to "Profile updated successfully", "Spanish" to "Perfil actualizado con éxito", "French" to "Profil mis à jour", "German" to "Profil erfolgreich aktualisiert", "Russian" to "Профиль успешно обновлен",
            "Arabic" to "تم تحديث الملف الشخصي بنجاح", "Chinese" to "个人资料更新成功", "Portuguese" to "Perfil atualizado com sucesso", "Hindi" to "प्रोफ़ाइल सफलतापूर्वक अपडेट की गई", "Japanese" to "プロフィールが更新されました",
            "Korean" to "프로필이 성공적으로 업데이트되었습니다", "Italian" to "Profilo aggiornato con successo", "Turkish" to "Profil başarıyla güncellendi", "Dutch" to "Profiel succesvol bijgewerkt"
        ),
        "online" to mapOf(
            "English" to "Online", "Spanish" to "En línea", "French" to "En ligne", "German" to "Online", "Russian" to "В сети",
            "Arabic" to "نشط الآن", "Chinese" to "在线", "Portuguese" to "Online", "Hindi" to "ऑनलाइन", "Japanese" to "オンライン",
            "Korean" to "온라인", "Italian" to "Online", "Turkish" to "Çevrimiçi", "Dutch" to "Online", "Vietnamese" to "Trực tuyến", "Polish" to "Aktywny"
        ),
        "offline" to mapOf(
            "English" to "Offline", "Spanish" to "Desconectado", "French" to "Hors ligne", "German" to "Offline", "Russian" to "Не в сети",
            "Arabic" to "غير متصل", "Chinese" to "离线", "Portuguese" to "Offline", "Hindi" to "ऑफ़लाइन", "Japanese" to "オフライン",
            "Korean" to "오프라인", "Italian" to "Offline", "Turkish" to "Çevrimdışı", "Dutch" to "Offline", "Vietnamese" to "Ngoại tuyến", "Polish" to "Niedostępny"
        ),
        "last_seen" to mapOf(
            "English" to "Last seen", "Spanish" to "Última vez", "French" to "Dernière vue", "German" to "Zuletzt gesehen", "Russian" to "Был(а) в сети",
            "Arabic" to "آخر ظهور", "Chinese" to "上次看到", "Portuguese" to "Visto por último", "Hindi" to "अंतिम बार देखा गया", "Japanese" to "最終閲覧",
            "Korean" to "마지막 본 시간", "Italian" to "Ultimo accesso", "Turkish" to "Son görülme", "Dutch" to "Laatst gezien", "Vietnamese" to "Hoạt động lần cuối", "Polish" to "Ostatnio widziany"
        ),
        "typing" to mapOf(
            "English" to "Typing...", "Spanish" to "Escribiendo...", "French" to "Écrit...", "German" to "Schreibt...", "Russian" to "Печатает...",
            "Arabic" to "يكتب...", "Chinese" to "正在输入...", "Portuguese" to "Digitando...", "Hindi" to "टाइपिंग...", "Japanese" to "入力中...",
            "Korean" to "입력 중...", "Italian" to "Sta scrivendo...", "Turkish" to "Yazıyor...", "Dutch" to "Typen...", "Vietnamese" to "Đang soạn tin...", "Polish" to "Pisze..."
        ),
        "delete_for_me" to mapOf(
            "English" to "Delete for Me", "Spanish" to "Eliminar para mí", "French" to "Supprimer pour moi", "German" to "Für mich löschen", "Russian" to "Удалить у меня",
            "Arabic" to "حذف بالنسبة لي", "Chinese" to "为我删除", "Portuguese" to "Excluir para mim", "Hindi" to "मेरे लिए हटाएं", "Japanese" to "自分用に削除",
            "Korean" to "나에게서 삭제", "Italian" to "Elimina per me", "Turkish" to "Benim için sil", "Dutch" to "Verwijder voor mij", "Vietnamese" to "Xóa đối với tôi", "Polish" to "Usuń dla mnie"
        ),
        "delete_for_everyone" to mapOf(
            "English" to "Delete for Everyone", "Spanish" to "Eliminar para todos", "French" to "Supprimer pour tous", "German" to "Für alle löschen", "Russian" to "Удалить у всех",
            "Arabic" to "حذف للجميع", "Chinese" to "为所有人删除", "Portuguese" to "Excluir para todos", "Hindi" to "सभी के लिए हटाएं", "Japanese" to "全員から削除",
            "Korean" to "모든 사람에게서 삭제", "Italian" to "Elimina per tutti", "Turkish" to "Herkes için sil", "Dutch" to "Verwijder voor iedereen", "Vietnamese" to "Xóa đối với mọi người", "Polish" to "Usuń dla wszystkich"
        ),
        "select_app_language" to mapOf(
            "English" to "Select App Language", "Spanish" to "Seleccionar idioma de la aplicación", "French" to "Sélectionner la langue de l'application", "German" to "App-Sprache auswählen", "Russian" to "Выбрать язык приложения",
            "Arabic" to "اختر لغة التطبيق", "Chinese" to "选择应用语言", "Portuguese" to "Selecionar idioma do aplicativo", "Hindi" to "ऐप की भाषा चुनें", "Japanese" to "アプリの言語を選択",
            "Korean" to "앱 언어 선택", "Italian" to "Seleziona lingua dell'app", "Turkish" to "Uygulama Dilini Seç", "Dutch" to "Selecteer app-taal", "Vietnamese" to "Chọn ngôn ngữ ứng dụng", "Polish" to "Wybierz język aplikacji"
        ),
        "close" to mapOf(
            "English" to "Close", "Spanish" to "Cerrar", "French" to "Fermer", "German" to "Schließen", "Russian" to "Закрыть",
            "Arabic" to "إغلاق", "Chinese" to "关闭", "Portuguese" to "Fechar", "Hindi" to "बंद करें", "Japanese" to "閉じる",
            "Korean" to "닫기", "Italian" to "Chiudi", "Turkish" to "Kapat", "Dutch" to "Sluiten", "Vietnamese" to "Đóng", "Polish" to "Zamknij"
        ),
        "contact_options" to mapOf(
            "English" to "Contact Options", "Spanish" to "Opciones de contacto", "French" to "Options de contact", "German" to "Kontaktoptionen", "Russian" to "Опции контакта",
            "Arabic" to "خيارات الاتصال", "Chinese" to "联系人选项", "Portuguese" to "Opções de contato", "Hindi" to "संपर्क विकल्प", "Japanese" to "連絡先のオプション",
            "Korean" to "연락처 옵션", "Italian" to "Opzioni contatto", "Turkish" to "Kişi Seçenekleri", "Dutch" to "Contactopties", "Vietnamese" to "Tùy chọn liên hệ", "Polish" to "Opcje kontaktu"
        ),
        "unpin_contact" to mapOf(
            "English" to "Unpin Contact", "Spanish" to "Desfijar contacto", "French" to "Désépingler le contact", "German" to "Kontakt loslösen", "Russian" to "Открепить контакт",
            "Arabic" to "إلغاء تثبيت جهة الاتصال", "Chinese" to "取消 pin 联系人", "Portuguese" to "Desafixar contato", "Hindi" to "संपर्क अनपिन करें", "Japanese" to "連絡先のピン留めを解除",
            "Korean" to "연락처 고정 해제", "Italian" to "Sblocca contatto", "Turkish" to "Kişiyi Sabitlemeden Çıkar", "Dutch" to "Contact losmaken", "Vietnamese" to "Bỏ ghim liên hệ", "Polish" to "Odepnij kontakt"
        ),
        "pin_contact" to mapOf(
            "English" to "Pin Contact", "Spanish" to "Fijar contacto", "French" to "Épingler le contact", "German" to "Kontakt anheften", "Russian" to "Закрепить контакт",
            "Arabic" to "تثبيت جهة الاتصال", "Chinese" to "Pin 联系人", "Portuguese" to "Fixar contato", "Hindi" to "संपर्क पिन करें", "Japanese" to "連絡先をピン留めする",
            "Korean" to "연락처 고정", "Italian" to "Fissa contatto", "Turkish" to "Kişiyi Sabitle", "Dutch" to "Contact vastpinnen", "Vietnamese" to "Ghim liên hệ", "Polish" to "Przypnij kontakt"
        ),
        "delete_contact" to mapOf(
            "English" to "Delete Contact", "Spanish" to "Eliminar contacto", "French" to "Supprimer le contact", "German" to "Kontakt löschen", "Russian" to "Удалить контакт",
            "Arabic" to "حذف جهة الاتصال", "Chinese" to "删除联系人", "Portuguese" to "Excluir contato", "Hindi" to "संपर्क हटाएं", "Japanese" to "連絡先を削除する",
            "Korean" to "연락처 삭제", "Italian" to "Elimina contatto", "Turkish" to "Kişiyi Sil", "Dutch" to "Contact verwijderen", "Vietnamese" to "Xóa liên hệ", "Polish" to "Usuń kontakt"
        ),
        "block_contact" to mapOf(
            "English" to "Block Contact", "Spanish" to "Bloquear contacto", "French" to "Bloquer le contact", "German" to "Kontakt blockieren", "Russian" to "Заблокировать контакт",
            "Arabic" to "حظر جهة الاتصال", "Chinese" to "拉黑联系人", "Portuguese" to "Bloquear contato", "Hindi" to "संपर्क ब्लॉक करें", "Japanese" to "連絡先をブロックする",
            "Korean" to "연락처 차단", "Italian" to "Blocca contatto", "Turkish" to "Kişiyi Engelle", "Dutch" to "Contact blokkeren", "Vietnamese" to "Chặn liên hệ", "Polish" to "Zablokuj kontakt"
        ),
        "cancel" to mapOf(
            "English" to "Cancel", "Spanish" to "Cancelar", "French" to "Annuler", "German" to "Abbrechen", "Russian" to "Отмена",
            "Arabic" to "إلغاء", "Chinese" to "取消", "Portuguese" to "Cancelar", "Hindi" to "रद्द करें", "Japanese" to "キャンセル",
            "Korean" to "취소", "Italian" to "Annulla", "Turkish" to "İptal", "Dutch" to "Annuleren", "Vietnamese" to "Hủy", "Polish" to "Anuluj"
        ),
        "start_chat_with" to mapOf(
            "English" to "Start chat with", "Spanish" to "Iniciar chat con", "French" to "Démarrer le chat avec", "German" to "Chat starten mit", "Russian" to "Начать чат с",
            "Arabic" to "بدء الدردشة مع", "Chinese" to "开始聊天", "Portuguese" to "Iniciar conversa com", "Hindi" to "के साथ चैट शुरू करें", "Japanese" to "チャットを開始する",
            "Korean" to "대화 시작", "Italian" to "Inizia chat con", "Turkish" to "Sohbeti başlat", "Dutch" to "Chat starten met", "Vietnamese" to "Bắt đầu trò chuyện với", "Polish" to "Rozpocznij czat z"
        ),
        "tap_to_message_unsaved" to mapOf(
            "English" to "Tap to message unsaved phone number", "Spanish" to "Toque para enviar un mensaje al número no guardado", "French" to "Appuyez pour envoyer un message", "German" to "Tippen, um ungespeicherter Nummer zu schreiben", "Russian" to "Нажмите, чтобы отправить сообщение",
            "Arabic" to "انقر لمراسلة رقم غير محفوظ", "Chinese" to "点击发送消息给未保存的号码", "Portuguese" to "Toque para enviar mensagem para número não salvo", "Hindi" to "बिना सहेजे नंबर पर संदेश भेजने के लिए टैप करें", "Japanese" to "タップして未保存の番号にメッセージを送信する",
            "Korean" to "저장되지 않은 번호로 메시지를 보내려면 누르세요", "Italian" to "Tocca per messaggiare numero non salvato", "Turkish" to "Kaydedilmemiş numaraya mesaj göndermek için dokunun", "Dutch" to "Tik om niet-opgeslagen nummer te berichten", "Vietnamese" to "Nhấn để nhắn tin cho số chưa lưu", "Polish" to "Dotknij, aby wysłać wiadomość do niezapisanego numeru"
        ),
        "not_on_smartfit" to mapOf(
            "English" to "This number is not on SmartFit Wellness", "Spanish" to "Este número no está en SmartFit Wellness", "French" to "Ce numéro n'est pas sur SmartFit Wellness", "German" to "Diese Nummer ist nicht bei SmartFit Wellness", "Russian" to "Этого номера нет в SmartFit Wellness",
            "Arabic" to "هذا الرقم ليس في SmartFit Wellness", "Chinese" to "该号码不在 SmartFit Wellness", "Portuguese" to "Este número não está no SmartFit Wellness", "Hindi" to "यह नंबर SmartFit Wellness पर नहीं है", "Japanese" to "この番号は SmartFit Wellness にありません",
            "Korean" to "이 번호는 SmartFit Wellness에 없습니다", "Italian" to "Questo numero non è su SmartFit Wellness", "Turkish" to "Bu numara SmartFit Wellness'ta değil", "Dutch" to "Dit nummer is niet op SmartFit Wellness", "Vietnamese" to "Số này không có trên SmartFit Wellness", "Polish" to "Ten numer nie należy do SmartFit Wellness"
        ),
        "not_using_app" to mapOf(
            "English" to "This person is not using this app", "Spanish" to "Esta persona no está usando esta aplicación", "French" to "Cette personne n'utilise pas cette application", "German" to "Diese Person verwendet diese App nicht", "Russian" to "Этот человек не использует это приложение",
            "Arabic" to "هذا الشخص لا يستخدم هذا التطبيق", "Chinese" to "此人未使用此应用", "Portuguese" to "Esta pessoa não está usando este aplicativo", "Hindi" to "यह व्यक्ति इस ऐप का उपयोग नहीं कर रहा है", "Japanese" to "この人物はアプリを使用していません",
            "Korean" to "이 사람은 이 앱을 사용하지 않습니다", "Italian" to "Questa persona non usa questa app", "Turkish" to "Bu kişi bu uygulamayı kullanmıyor", "Dutch" to "Deze persoon gebruikt deze app niet", "Vietnamese" to "Người này không sử dụng ứng dụng này", "Polish" to "Ta osoba nie korzysta z tej aplikacji"
        ),
        "share_content_media" to mapOf(
            "English" to "Share Content & Media", "Spanish" to "Compartir contenido y archivos", "French" to "Partager du contenu et des médias", "German" to "Inhalte & Medien teilen", "Russian" to "Поделиться контентом и медиа",
            "Arabic" to "مشاركة المحتوى والوسائط", "Chinese" to "分享内容和媒体", "Portuguese" to "Compartilhar conteúdo e mídia", "Hindi" to "सामग्री और मीडिया साझा करें", "Japanese" to "コンテンツとメディアの共有",
            "Korean" to "콘텐츠 및 미디어 공유", "Italian" to "Condividi contenuti e media", "Turkish" to "İçerik ve Medya Paylaş", "Dutch" to "Inhoud & media delen", "Vietnamese" to "Chia sẻ Nội dung & Phương tiện", "Polish" to "Udostępnij zawartość i multimedia"
        ),
        "camera" to mapOf(
            "English" to "Camera", "Spanish" to "Cámara", "French" to "Appareil photo", "German" to "Kamera", "Russian" to "Камера",
            "Arabic" to "الكاميرا", "Chinese" to "相机", "Portuguese" to "Câmera", "Hindi" to "कैमरा", "Japanese" to "カメラ",
            "Korean" to "카메라", "Italian" to "Fotocamera", "Turkish" to "Kamera", "Dutch" to "Camera", "Vietnamese" to "Máy ảnh", "Polish" to "Aparat"
        ),
        "live" to mapOf(
            "English" to "Live", "Spanish" to "En vivo", "French" to "En direct", "German" to "Live", "Russian" to "Прямой эфир",
            "Arabic" to "مباشر", "Chinese" to "直播", "Portuguese" to "Ao vivo", "Hindi" to "लाइव", "Japanese" to "ライブ",
            "Korean" to "라이브", "Italian" to "Dal vivo", "Turkish" to "Canlı", "Dutch" to "Live", "Vietnamese" to "Trực tiếp", "Polish" to "Na żywo"
        ),
        "new_connection_established" to mapOf(
            "English" to "New connection established", "Spanish" to "Nueva conexión establecida", "French" to "Nouvelle connexion établie", "German" to "Neue Verbindung hergestellt", "Russian" to "Новое соединение установлено",
            "Arabic" to "تم إنشاء اتصال جديد", "Chinese" to "新连接已建立", "Portuguese" to "Nova conexão estabelecida", "Hindi" to "नया कनेक्शन स्थापित हुआ", "Japanese" to "新しい接続が確立されました",
            "Korean" to "새로운 연결이 설정되었습니다", "Italian" to "Nuova connessione stabilita", "Turkish" to "Yeni bağlantı kuruldu", "Dutch" to "Nieuwe verbinding tot stand gebracht", "Vietnamese" to "Kết nối mới đã được thiết lập", "Polish" to "Nawiązano nowe połączenie"
        ),
        "direct_chat_started" to mapOf(
            "English" to "Direct chat started", "Spanish" to "Chat directo iniciado", "French" to "Chat direct commencé", "German" to "Direkter Chat gestartet", "Russian" to "Прямой чат начат",
            "Arabic" to "بدأت الدردشة المباشرة", "Chinese" to "直接聊天已开始", "Portuguese" to "Conversa direta iniciada", "Hindi" to "सीधी बातचीत शुरू हुई", "Japanese" to "直接チャットが開始されました",
            "Korean" to "직접 채팅이 시작되었습니다", "Italian" to "Chat diretta avviata", "Turkish" to "Doğrudan sohbet başlatıldı", "Dutch" to "Directe chat gestart", "Vietnamese" to "Cuộc trò chuyện trực tiếp đã bắt đầu", "Polish" to "Rozpoczęto czat bezpośredni"
        ),
        "yesterday" to mapOf(
            "English" to "Yesterday", "Spanish" to "Ayer", "French" to "Hier", "German" to "Gestern", "Russian" to "Вчера",
            "Arabic" to "أمس", "Chinese" to "昨天", "Portuguese" to "Ontem", "Hindi" to "कल", "Japanese" to "昨日",
            "Korean" to "어제", "Italian" to "Ieri", "Turkish" to "Dün", "Dutch" to "Gisteren", "Vietnamese" to "Hôm qua", "Polish" to "Wczoraj"
        ),
        "just_now" to mapOf(
            "English" to "Just now", "Spanish" to "Ahora mismo", "French" to "À l'instant", "German" to "Gerade eben", "Russian" to "Только что",
            "Arabic" to "الآن", "Chinese" to "刚刚", "Portuguese" to "Agora mesmo", "Hindi" to "अभी-अभी", "Japanese" to "たった今",
            "Korean" to "방금", "Italian" to "Proprio ora", "Turkish" to "Az önce", "Dutch" to "Zojuist", "Vietnamese" to "Vừa xong", "Polish" to "Przed chwilą"
        ),
        "message_options" to mapOf(
            "English" to "Message Options", "Spanish" to "Opciones de mensaje", "French" to "Options de message", "German" to "Nachrichtenoptionen", "Russian" to "Опции сообщения",
            "Arabic" to "خيارات الرسالة", "Chinese" to "消息选项", "Portuguese" to "Opções de mensagem", "Hindi" to "संदेश विकल्प", "Japanese" to "メッセージのオプション",
            "Korean" to "메시지 옵션", "Italian" to "Opzioni messaggio", "Turkish" to "Mesaj Seçenekleri", "Dutch" to "Berichtopties", "Vietnamese" to "Tùy chọn tin nhắn", "Polish" to "Opcje wiadomości"
        ),
        "star_message" to mapOf(
            "English" to "Star Message", "Spanish" to "Destacar mensaje", "French" to "Étoiler le message", "German" to "Nachricht markieren", "Russian" to "Пометить звездочкой",
            "Arabic" to "تمييز الرسالة بنجمة", "Chinese" to "收藏消息", "Portuguese" to "Favoritar mensagem", "Hindi" to "संदेश को स्टार करें", "Japanese" to "メッセージをスターに登録する",
            "Korean" to "메시지 즐겨찾기", "Italian" to "Fissa messaggio", "Turkish" to "Mesajı Yıldızla", "Dutch" to "Bericht sterren", "Vietnamese" to "Ghim tin nhắn", "Polish" to "Oznacz gwiazdką"
        ),
        "unstar_message" to mapOf(
            "English" to "Unstar Message", "Spanish" to "Quitar destacar mensaje", "French" to "Désétoiler le message", "German" to "Markierung aufheben", "Russian" to "Снять звездочку",
            "Arabic" to "إلغاء النجمة عن الرسالة", "Chinese" to "取消收藏消息", "Portuguese" to "Desfavoritar mensagem", "Hindi" to "संदेश से स्टार हटाएं", "Japanese" to "メッセージのスターを解除する",
            "Korean" to "메시지 즐겨찾기 해제", "Italian" to "Rimuovi fissa messaggio", "Turkish" to "Yıldızı Kaldır", "Dutch" to "Sterre r weg", "Vietnamese" to "Bỏ ghim tin nhắn", "Polish" to "Usuń gwiazdkę"
        ),
        "star_for_recipient" to mapOf(
            "English" to "Star for Recipient", "Spanish" to "Destacar para el destinatario", "French" to "Étoiler pour le destinataire", "German" to "Für Empfänger markieren", "Russian" to "Пометить для получателя",
            "Arabic" to "تمييز للمستلم بنجمة", "Chinese" to "为接收者收藏", "Portuguese" to "Favoritar para o destinatário", "Hindi" to "प्राप्तकर्ता के लिए स्टार करें", "Japanese" to "受信者のためにスターを付ける",
            "Korean" to "수신자를 위해 즐겨찾기", "Italian" to "Fissa per il destinatario", "Turkish" to "Alıcı İçin Yıldızla", "Dutch" to "Ster voor ontvanger", "Vietnamese" to "Ghim cho người nhận", "Polish" to "Przypnij dla odbiorcy"
        ),
        "unstar_for_recipient" to mapOf(
            "English" to "Unstar for Recipient", "Spanish" to "Quitar destacar para destinatario", "French" to "Désétoiler pour le destinataire", "German" to "Empfänger-Markierung aufheben", "Russian" to "Снять для получателя",
            "Arabic" to "إلغاء النجمة للمستلم", "Chinese" to "为接收者取消收藏", "Portuguese" to "Desfavoritar para o destinatário", "Hindi" to "प्राप्तकर्ता के लिए स्टार हटाएं", "Japanese" to "受信者のスターを解除する",
            "Korean" to "수신자 즐겨찾기 해제", "Italian" to "Rimuovi fissa per il destinatario", "Turkish" to "Alıcı İçin Yıldızı Kaldır", "Dutch" to "Ster weg voor ontvanger", "Vietnamese" to "Bỏ ghim cho người nhận", "Polish" to "Odepnij dla odbiorcy"
        ),
        "voice_note" to mapOf(
            "English" to "Voice Note", "Spanish" to "Nota de voz", "French" to "Note vocale", "German" to "Sprachnotiz", "Russian" to "Голосовая заметка",
            "Arabic" to "ملاحظة صوتية", "Chinese" to "语音消息", "Portuguese" to "Nota de voz", "Hindi" to "वॉयस नोट", "Japanese" to "音声メモ",
            "Korean" to "음성 메시지", "Italian" to "Nota vocale", "Turkish" to "Sesli Not", "Dutch" to "Gesproken notitie", "Vietnamese" to "Ghi âm thoại", "Polish" to "Notatka głosowa"
        ),
        "shared_photo" to mapOf(
            "English" to "Shared Photo", "Spanish" to "Foto compartida", "French" to "Photo partagée", "German" to "Geteiltes Foto", "Russian" to "Shared Photo",
            "Arabic" to "صورة مشتركة", "Chinese" to "分享的照片", "Portuguese" to "Foto compartilhada", "Hindi" to "साझा की गई फोटो", "Japanese" to "共有された写真",
            "Korean" to "공유된 사진", "Italian" to "Foto condivisa", "Turkish" to "Paylaşılan Fotoğraf", "Dutch" to "Gedeelde foto", "Vietnamese" to "Ảnh đã chia sẻ", "Polish" to "Udostępnione zdjęcie"
        ),
        "video_preview" to mapOf(
            "English" to "Video Preview", "Spanish" to "Vista previa de video", "French" to "Aperçu de la vidéo", "German" to "Video-Vorschau", "Russian" to "Превью видео",
            "Arabic" to "معاينة الفيديو", "Chinese" to "视频预览", "Portuguese" to "Prévia do vídeo", "Hindi" to "वीडियो पूर्वावलोकन", "Japanese" to "動画プレビュー",
            "Korean" to "동영상 미리보기", "Italian" to "Anteprima video", "Turkish" to "Video Önizleme", "Dutch" to "Videovoorbeeld", "Vietnamese" to "Xem trước video", "Polish" to "Podgląd wideo"
        ),
        "document" to mapOf(
            "English" to "Document", "Spanish" to "Documento", "French" to "Document", "German" to "Dokument", "Russian" to "Документ",
            "Arabic" to "مستند", "Chinese" to "文档", "Portuguese" to "Documento", "Hindi" to "दस्तावेज़", "Japanese" to "ドキュメント",
            "Korean" to "문서", "Italian" to "Documento", "Turkish" to "Belge", "Dutch" to "Document", "Vietnamese" to "Tài liệu", "Polish" to "Dokument"
        ),
        "pdf_document" to mapOf(
            "English" to "PDF Document", "Spanish" to "Documento PDF", "French" to "Document PDF", "German" to "PDF-Dokument", "Russian" to "PDF-документ",
            "Arabic" to "مستند PDF", "Chinese" to "PDF 文档", "Portuguese" to "Documento PDF", "Hindi" to "पीडीएफ दस्तावेज़", "Japanese" to "PDFドキュメント",
            "Korean" to "PDF 문서", "Italian" to "Documento PDF", "Turkish" to "PDF Belgesi", "Dutch" to "PDF-document", "Vietnamese" to "Tài liệu PDF", "Polish" to "Dokument PDF"
        )
    )

    fun get(key: String, lang: String): String {
        // 1. Check extra translations first
        val extraMap = extraTranslations[key]
        if (extraMap != null) {
            val trans = extraMap[lang] ?: extraMap["English"]
            if (trans != null) return trans
        }
        // 2. Check existing map
        val langMap = map[lang] ?: map["English"]
        if (langMap != null && langMap.containsKey(key)) {
            return langMap[key]!!
        }
        val englishText = map["English"]?.get(key) ?: key
        return translateFallback(englishText, lang)
    }

    private fun translateFallback(text: String, lang: String): String {
        return text
    }

    private val map = mapOf(
        "English" to mapOf(
            "app_title" to "SmartFit Wellness",
            "home" to "Home",
            "chats" to "Chats",
            "reminders" to "Reminders",
            "account" to "Account",
            "search_contacts" to "Search contacts...",
            "secure_auth" to "Secure Authentication",
            "phone_number" to "Phone Number",
            "email" to "Email Address",
            "continue_google" to "Continue with Google",
            "verify_otp" to "Verify OTP Code",
            "enter_otp" to "Enter 4-digit code (e.g. 1234)",
            "my_reminders" to "My Reminders",
            "linked_devices" to "Linked Devices",
            "app_language" to "App Language",
            "add_account" to "Add Account",
            "delete_account" to "How to Delete My Account",
            "export_data" to "Export My Data",
            "log_out" to "Log Out",
            "category_name" to "Category Name (e.g., Water Reminder)",
            "select_contact" to "Select Contact",
            "set_time" to "Set Time (IST)",
            "recurrence" to "Recurrence Rule",
            "custom_message" to "Custom Message (up to 5000 words)",
            "save_reminder" to "Save & Schedule Reminder",
            "sent_status" to "Sent Status",
            "scheduled" to "Scheduled",
            "sent" to "Sent",
            "settings" to "Settings",
            "help" to "Help & Support",
            "about" to "About SmartFit Wellness"
        ),
        "Hindi" to mapOf(
            "app_title" to "स्मार्टफिट वेलनेस",
            "home" to "होम",
            "chats" to "चैट्स",
            "reminders" to "रिमाइंडर्स",
            "account" to "अकाउंट",
            "search_contacts" to "संपर्क खोजें...",
            "secure_auth" to "सुरक्षित प्रमाणीकरण",
            "phone_number" to "फ़ोन नंबर",
            "email" to "ईमेल पता",
            "continue_google" to "गूगल के साथ जारी रखें",
            "verify_otp" to "OTP सत्यापित करें",
            "enter_otp" to "4-अंकों का कोड दर्ज करें (उदा. 1234)",
            "my_reminders" to "मेरे रिमाइंडर्स",
            "linked_devices" to "लिंक्ड डिवाइस",
            "app_language" to "ऐप भाषा",
            "add_account" to "अकाउंट जोड़ें",
            "delete_account" to "अकाउंट कैसे डिलीट करें",
            "export_data" to "मेरा डेटा एक्सपोर्ट करें",
            "log_out" to "लॉग आउट",
            "category_name" to "श्रेणी का नाम",
            "select_contact" to "संपर्क चुनें",
            "set_time" to "समय निर्धारित करें (IST)",
            "recurrence" to "पुनरावृत्ति नियम",
            "custom_message" to "कस्टम संदेश",
            "save_reminder" to "रिमाइंड सहेजें",
            "sent_status" to "भेजने की स्थिति",
            "scheduled" to "निर्धारित",
            "sent" to "भेजा गया",
            "settings" to "सेटिंग्स",
            "help" to "सहायता",
            "about" to "ऐप के बारे में"
        ),
        "Japanese" to mapOf(
            "app_title" to "スマートフィット ウェルネス",
            "home" to "ホーム",
            "chats" to "チャット",
            "reminders" to "リマインダー",
            "account" to "アカウント",
            "search_contacts" to "連絡先を検索...",
            "secure_auth" to "セキュア認証",
            "phone_number" to "電話番号",
            "email" to "メールアドレス",
            "continue_google" to "Googleで続行",
            "verify_otp" to "OTP確認",
            "enter_otp" to "4桁のコードを入力",
            "my_reminders" to "マイリマインダー",
            "linked_devices" to "リンクされたデバイス",
            "app_language" to "アプリ言語",
            "add_account" to "アカウントを追加",
            "delete_account" to "アカウントの削除方法",
            "export_data" to "データをエクスポート",
            "log_out" to "ログアウト",
            "category_name" to "カテゴリ名",
            "select_contact" to "連絡先を選択",
            "set_time" to "時刻設定 (IST)",
            "recurrence" to "繰り返しルール",
            "custom_message" to "カスタムメッセージ",
            "save_reminder" to "リマインダーを保存",
            "sent_status" to "送信ステータス",
            "scheduled" to "予定",
            "sent" to "送信済み",
            "settings" to "設定",
            "help" to "ヘルプ",
            "about" to "アプリについて"
        ),
        "Chinese" to mapOf(
            "app_title" to "智能健身健康",
            "home" to "首页",
            "chats" to "聊天",
            "reminders" to "提醒",
            "account" to "账户",
            "search_contacts" to "搜索联系人...",
            "secure_auth" to "安全认证",
            "phone_number" to "电话号码",
            "email" to "电子邮箱",
            "continue_google" to "使用 Google 继续",
            "verify_otp" to "验证验证码",
            "enter_otp" to "输入4位验证码",
            "my_reminders" to "我的提醒",
            "linked_devices" to "已链接设备",
            "app_language" to "应用语言",
            "add_account" to "添加账户",
            "delete_account" to "如何注销账户",
            "export_data" to "导出我的数据",
            "log_out" to "退出登录",
            "category_name" to "类别名称",
            "select_contact" to "选择联系人",
            "set_time" to "设置时间 (IST)",
            "recurrence" to "重复规则",
            "custom_message" to "自定义消息",
            "save_reminder" to "保存提醒",
            "sent_status" to "发送状态",
            "scheduled" to "已计划",
            "sent" to "已发送",
            "settings" to "设置",
            "help" to "帮助",
            "about" to "关于应用"
        ),
        "Spanish" to mapOf(
            "app_title" to "SmartFit Bienestar",
            "home" to "Inicio",
            "chats" to "Chats",
            "reminders" to "Recordatorios",
            "account" to "Cuenta",
            "search_contacts" to "Buscar contactos...",
            "secure_auth" to "Autenticación Segura",
            "phone_number" to "Número de Teléfono",
            "email" to "Correo Electrónico",
            "continue_google" to "Continuar con Google",
            "verify_otp" to "Verificar código OTP",
            "enter_otp" to "Ingrese código de 4 dígitos",
            "my_reminders" to "Mis Recordatorios",
            "linked_devices" to "Dispositivos Vinculados",
            "app_language" to "Idioma de la Aplicación",
            "add_account" to "Agregar Cuenta",
            "delete_account" to "Cómo Eliminar Mi Cuenta",
            "export_data" to "Exportar Mis Datos",
            "log_out" to "Cerrar Sesión",
            "settings" to "Configuración",
            "help" to "Ayuda y Soporte",
            "about" to "Acerca de"
        ),
        "French" to mapOf(
            "app_title" to "SmartFit Bien-être",
            "home" to "Accueil",
            "chats" to "Discussions",
            "reminders" to "Rappels",
            "account" to "Compte",
            "search_contacts" to "Rechercher des contacts...",
            "secure_auth" to "Authentification Sécurisée",
            "phone_number" to "Numéro de Téléphone",
            "email" to "Adresse E-mail",
            "continue_google" to "Continuer avec Google",
            "verify_otp" to "Vérifier le code OTP",
            "enter_otp" to "Entrer le code à 4 chiffres",
            "my_reminders" to "Mes Rappels",
            "linked_devices" to "Appareils Liés",
            "app_language" to "Langue de l'application",
            "add_account" to "Ajouter un Compte",
            "delete_account" to "Comment Supprimer Mon Compte",
            "export_data" to "Exporter Mes Données",
            "log_out" to "Se Déconnecter",
            "settings" to "Paramètres",
            "help" to "Aide et Support",
            "about" to "À propos"
        ),
        "German" to mapOf(
            "app_title" to "SmartFit Wellness",
            "home" to "Startseite",
            "chats" to "Chats",
            "reminders" to "Erinnerungen",
            "account" to "Konto",
            "search_contacts" to "Kontakte suchen...",
            "secure_auth" to "Sichere Authentifizierung",
            "phone_number" to "Telefonnummer",
            "email" to "E-Mail-Adresse",
            "continue_google" to "Mit Google fortfahren",
            "verify_otp" to "OTP-Code verifizieren",
            "enter_otp" to "4-stelligen Code eingeben",
            "my_reminders" to "Meine Erinnerungen",
            "linked_devices" to "Verknüpfte Geräte",
            "app_language" to "App-Sprache",
            "add_account" to "Konto hinzufügen",
            "delete_account" to "Wie ich mein Konto lösche",
            "export_data" to "Meine Daten exportieren",
            "log_out" to "Abmelden",
            "settings" to "Einstellungen",
            "help" to "Hilfe & Support",
            "about" to "Über SmartFit"
        ),
        "Arabic" to mapOf(
            "app_title" to "سمارت فिट للصحة",
            "home" to "الرئيسية",
            "chats" to "المحادثات",
            "reminders" to "التذكيرات",
            "account" to "الحساب",
            "search_contacts" to "بحث عن جهات الاتصال...",
            "secure_auth" to "المصادقة الآمنة",
            "phone_number" to "رقم الهاتف",
            "email" to "البريد الإلكتروني",
            "continue_google" to "المتابعة باستخدام Google",
            "verify_otp" to "تحقق من رمز OTP",
            "enter_otp" to "أدخل الرمز المكون من 4 أرقام",
            "my_reminders" to "تذكيراتي",
            "linked_devices" to "الأجهزة المرتبطة",
            "app_language" to "لغة التطبيق",
            "add_account" to "إضافة حساب",
            "delete_account" to "كيفية حذف حسابي",
            "export_data" to "تصدير بياناتي",
            "log_out" to "تسجيل الخروج",
            "settings" to "الإعدادات",
            "help" to "المساعدة والدعم",
            "about" to "حول التطبيق"
        ),
        "Russian" to mapOf(
            "app_title" to "SmartFit Фитнес",
            "home" to "Главная",
            "chats" to "Чаты",
            "reminders" to "Напоминания",
            "account" to "Аккаунт",
            "search_contacts" to "Поиск контактов...",
            "secure_auth" to "Безопасная аутентификация",
            "phone_number" to "Номер телефона",
            "email" to "Электронная почта",
            "continue_google" to "Продолжить с Google",
            "verify_otp" to "Подтвердить OTP",
            "enter_otp" to "Введите 4-значный код",
            "my_reminders" to "Мои напоминания",
            "linked_devices" to "Связанные устройства",
            "app_language" to "Язык приложения",
            "add_account" to "Добавить аккаунт",
            "delete_account" to "Как удалить мой аккаунт",
            "export_data" to "Экспорт данных",
            "log_out" to "Выйти",
            "settings" to "Настройки",
            "help" to "Помощь и поддержка",
            "about" to "O приложении"
        ),
        "Portuguese" to mapOf(
            "app_title" to "SmartFit Bem-estar",
            "home" to "Início",
            "chats" to "Conversas",
            "reminders" to "Lembretes",
            "account" to "Conta",
            "search_contacts" to "Pesquisar contatos...",
            "secure_auth" to "Autenticação Segura",
            "phone_number" to "Número de Telefone",
            "email" to "Endereço de E-mail",
            "continue_google" to "Continuar com o Google",
            "verify_otp" to "Verificar código OTP",
            "enter_otp" to "Digite o código de 4 dígitos",
            "my_reminders" to "Meus Lembretes",
            "linked_devices" to "Dispositivos Vinculados",
            "app_language" to "Idioma do Aplicativo",
            "add_account" to "Adicionar Conta",
            "delete_account" to "Como Excluir Minha Conta",
            "export_data" to "Exportar Meus Dados",
            "log_out" to "Sair",
            "settings" to "Configurações",
            "help" to "Ajuda e Suporte",
            "about" to "Sobre"
        ),
        "Italian" to mapOf(
            "app_title" to "SmartFit Benessere",
            "home" to "Home",
            "chats" to "Chat",
            "reminders" to "Promemoria",
            "account" to "Account",
            "search_contacts" to "Cerca contatti...",
            "secure_auth" to "Autenticazione Sicura",
            "phone_number" to "Numero di Telefono",
            "email" to "Indirizzo Email",
            "continue_google" to "Continua con Google",
            "verify_otp" to "Verifica codice OTP",
            "enter_otp" to "Inserisci codice a 4 cifre",
            "my_reminders" to "I miei promemoria",
            "linked_devices" to "Dispositivi Collegati",
            "app_language" to "Lingua dell'app",
            "add_account" to "Aggiungi Account",
            "delete_account" to "Come Eliminare il mio Account",
            "export_data" to "Esporta i miei dati",
            "log_out" to "Esci",
            "settings" to "Impostazioni",
            "help" to "Aiuto e Supporto",
            "about" to "Informazioni"
        ),
        "Korean" to mapOf(
            "app_title" to "스마트핏 웰니스",
            "home" to "홈",
            "chats" to "채팅",
            "reminders" to "알림",
            "account" to "계정",
            "search_contacts" to "연락처 검색...",
            "secure_auth" to "보안 인증",
            "phone_number" to "전화번호",
            "email" to "이메일 주소",
            "continue_google" to "Google로 계속하기",
            "verify_otp" to "OTP 코드 확인",
            "enter_otp" to "4자리 코드 입력",
            "my_reminders" to "내 알림",
            "linked_devices" to "연동된 기기",
            "app_language" to "앱 언어",
            "add_account" to "계정 추가",
            "delete_account" to "계정 삭제 방법",
            "export_data" to "데이터 내보내기",
            "log_out" to "로그아웃",
            "settings" to "설정",
            "help" to "도움말 및 지원",
            "about" to "정보"
        ),
        "Turkish" to mapOf(
            "app_title" to "SmartFit Sağlık",
            "home" to "Ana Sayfa",
            "chats" to "Sohbetler",
            "reminders" to "Hatırlatıcılar",
            "account" to "Hesap",
            "search_contacts" to "Kişileri ara...",
            "secure_auth" to "Güvenli Kimlik Doğrulama",
            "phone_number" to "Telefon Numarası",
            "email" to "E-posta Adresi",
            "continue_google" to "Google ile Devam Et",
            "verify_otp" to "OTP Kodunu Doğrula",
            "enter_otp" to "4 haneli kodu girin",
            "my_reminders" to "Hatırlatıcılarım",
            "linked_devices" to "Bağlı Cihazlar",
            "app_language" to "Uygulama Dili",
            "add_account" to "Hesap Ekle",
            "delete_account" to "Hesabımı Nasıl Silerim",
            "export_data" to "Verilerimi Dışa Aktar",
            "log_out" to "Çıkış Yap",
            "settings" to "Ayarlar",
            "help" to "Yardım ve Destek",
            "about" to "Hakkında"
        ),
        "Dutch" to mapOf(
            "app_title" to "SmartFit Welzijn",
            "home" to "Home",
            "chats" to "Chats",
            "reminders" to "Herinneringen",
            "account" to "Account",
            "search_contacts" to "Zoek contacten...",
            "secure_auth" to "Beveiligde Authenticatie",
            "phone_number" to "Telefoonnummer",
            "email" to "E-mailadres",
            "continue_google" to "Doorgaan met Google",
            "verify_otp" to "OTP-code verifiëren",
            "enter_otp" to "Voer 4-cijferige code in",
            "my_reminders" to "Mijn Herinneringen",
            "linked_devices" to "Gekoppelde Apparaten",
            "app_language" to "App-taal",
            "add_account" to "Account toevoegen",
            "delete_account" to "Hoe verwijder ik mijn account",
            "export_data" to "Mijn gegevens exporteren",
            "log_out" to "Uitloggen",
            "settings" to "Instellingen",
            "help" to "Help & Ondersteuning",
            "about" to "Over"
        ),
        "Vietnamese" to mapOf(
            "app_title" to "SmartFit Sức Khỏe",
            "home" to "Trang Chủ",
            "chats" to "Trò Chuyện",
            "reminders" to "Nhắc Nhở",
            "account" to "Tài Khoản",
            "search_contacts" to "Tìm kiếm liên hệ...",
            "secure_auth" to "Xác Thực An Toàn",
            "phone_number" to "Số Điện Thoại",
            "email" to "Địa Chỉ Email",
            "continue_google" to "Tiếp tục với Google",
            "verify_otp" to "Xác thực mã OTP",
            "enter_otp" to "Nhập mã 4 chữ số",
            "my_reminders" to "Nhắc Nhở Của Tôi",
            "linked_devices" to "Thiết Bị Đã Liên Kết",
            "app_language" to "Ngôn Ngữ Ứng Dụng",
            "add_account" to "Thêm Tài Khoản",
            "delete_account" to "Cách Xóa Tài Khoản Của Tôi",
            "export_data" to "Xuất Dữ Liệu",
            "log_out" to "Đăng Xuất",
            "settings" to "Cài Đặt",
            "help" to "Trợ Giúp & Hỗ Trợ",
            "about" to "Giới Thiệu"
        ),
        "Polish" to mapOf(
            "app_title" to "SmartFit Wellness",
            "home" to "Strona główna",
            "chats" to "Czaty",
            "reminders" to "Przypomnienia",
            "account" to "Konto",
            "search_contacts" to "Szukaj kontaktów...",
            "secure_auth" to "Bezpieczne uwierzytelnianie",
            "phone_number" to "Numer telefonu",
            "email" to "Adres e-mail",
            "continue_google" to "Kontynuuj z Google",
            "verify_otp" to "Zweryfikuj kod OTP",
            "enter_otp" to "Wpisz 4-cyfrowy kod",
            "my_reminders" to "Moje przypomnienia",
            "linked_devices" to "Połączone urządzenia",
            "app_language" to "Język aplikacji",
            "add_account" to "Dodaj konto",
            "delete_account" to "Jak usunąć moje konto",
            "export_data" to "Eksportuj dane",
            "log_out" to "Wyloguj",
            "settings" to "Ustawienia",
            "help" to "Pomoc i wsparcie",
            "about" to "O aplikacji"
        )
    )
}
