📰 Ruang Berita

Aplikasi Berita Terkini Indonesia

Ruang Berita adalah aplikasi Android native yang menyediakan berita terkini dari berbagai kategori dengan antarmuka yang modern dan fitur-fitur lengkap untuk pengalaman membaca berita yang optimal.

 🚀 Fitur Utama
📱Navigasi Intuitif
- Bottom Navigation  dengan 2 tab utama: Beranda dan Tersimpan
- Splash Screen  dengan branding aplikasi
- Search Bar untuk pencarian berita cepat
- Category Chips untuk filter berita berdasarkan kategori

📰 Manajemen Berita
- List Berita Terkini dari CNN Indonesia
- Filter Kategori: Nasional, Internasional, Ekonomi, Olahraga, Teknologi, Hiburan, Gaya Hidup
- Search Functionality dengan real-time filtering
- Bookmark System untuk menyimpan berita favorit
- Share Berita melalui platform lain
- Detail View dengan konten lengkap berita

🎨 Pengalaman Pengguna
- Dark/Light Theme dengan toggle manual
- Swipe to Refresh untuk update berita terbaru
- Material Designdengan komponen modern
- Responsive Layout untuk berbagai ukuran layar
- Offline Reading untuk berita yang disimpan

🔧 Fitur Teknis
- Network State Handling dengan error states
- Image Caching menggunakan Glide
- Database Lokal untuk berita tersimpan
- API Integration dengan error handling
- Loading States dengan progress indicators

🛠 Teknologi yang Digunakan
Development Stack
- Language:Java
- Platform: Android (API 24+)
- Architecture: Fragment-based dengan Bottom Navigation
- UI Framework: Material Design Components

Libraries & Dependencies
gradle
// UI & Layout
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0
androidx.constraintlayout:constraintlayout:2.1.4
androidx.navigation:navigation-fragment:2.7.6
androidx.navigation:navigation-ui:2.7.6

// Lifecycle & ViewModel
androidx.lifecycle:lifecycle-viewmodel:2.7.0
androidx.lifecycle:lifecycle-livedata:2.7.0

// RecyclerView & UI Components
androidx.recyclerview:recyclerview:1.3.2
androidx.cardview:cardview:1.0.0
androidx.swiperefreshlayout:swiperefreshlayout:1.1.0

// Networking
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.squareup.okhttp3:okhttp:4.12.0
com.squareup.okhttp3:logging-interceptor:4.12.0

// Image Loading
com.github.bumptech.glide:glide:4.16.0

// JSON Parsing
com.google.code.gson:gson:2.10.1

🏗 Arsitektur Aplikasi
Package Structure
com.example.ruangberita/
├── activity/
│   ├── SplashActivity.java      # Splash screen
│   ├── MainActivity.java        # Container utama
│   └── DetailActivity.java      # Detail berita
├── fragment/
│   ├── HomeFragment.java        # Halaman beranda
│   └── SavedFragment.java       # Berita tersimpan
├── adapters/
│   ├── NewsAdapter.java         # Adapter berita
│   └── CategoryAdapter.java     # Adapter kategori
├── api/
│   ├── ApiClient.java           # Retrofit client
│   └── ApiService.java          # API endpoints
├── database/
│   └── DatabaseHelper.java     # SQLite database
└── models/
    ├── News.java               # Model berita
    ├── NewsImage.java          # Model gambar
    └── NewsResponse.java       # Model response API

Architecture Pattern
- MVC (Model-View-Controller) pattern
- Repository Pattern untuk data layer
- Observer Pattern untuk UI updates
- Singleton Pattern untuk API client

🌐 API Integration
Base URL
https://berita-indo-api-next.vercel.app/api/


Endpoints
| Endpoint | Method | Description |
|----------|--------|-------------|
| /cnn-news/ | GET | Mengambil semua berita terkini |
| /cnn-news/{category} | GET | Mengambil berita berdasarkan kategori |

Kategori Available
- nasional - Berita Nasional
- internasional - Berita Internasional  
- ekonomi - Berita Ekonomi
- olahraga - Berita Olahraga
- teknologi - Berita Teknologi
- hiburan - Berita Hiburan
- gaya-hidup - Berita Gaya Hidup

Response Model
json
{
  "message": "success",
  "total": 20,
  "data": [
    {
      "title": "Judul Berita",
      "link": "https://...",
      "contentSnippet": "Ringkasan berita...",
      "isoDate": "2025-06-08T14:30:00.000Z",
      "image": {
        "small": "https://...",
        "large": "https://..."
      }
    }
  ]
}


💾 Database Schema

Tables

saved_news
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PK | Primary key |
| title | TEXT | Judul berita |
| link | TEXT UNIQUE | URL berita |
| content_snippet | TEXT | Ringkasan konten |
| iso_date | TEXT | Tanggal publikasi |
| image_small | TEXT | URL gambar kecil |
| image_large | TEXT | URL gambar besar |
| category | TEXT | Kategori berita |
| saved_timestamp | INTEGER | Waktu penyimpanan |

settings
| Column | Type | Description |
|--------|------|-------------|
| setting_key | TEXT PK | Key pengaturan |
| setting_value | TEXT | Value pengaturan |

🎨 Design System
Color Palette

Light Theme
- Primary: #D32F2F (Red 700)
- Primary Variant: #B71C1C (Red 900)
- Secondary: #FF5722 (Deep Orange 500)
- Background: #FFFFFF (White)
- Surface: #FFFFFF (White)

Dark Theme
- Primary: #F44336 (Red 500)
- Primary Variant: #D32F2F(Red 700)
- Secondary: #FF7043(Deep Orange 400)
- Background: #121212 (Dark Gray)
- Surface: #1E1E1E (Dark Surface)

Typography
- Headlines: Roboto Bold
- Body Text: Roboto Regular
- Captions: Roboto Medium

Spacing System
- Base Unit: 8dp
- Margins: 16dp, 24dp, 32dp
- Padding: 8dp, 12dp, 16dp

🚀 Installation & Setup
Prerequisites
- Android Studio Arctic Fox atau lebih baru
- Android SDK API 24 (Android 7.0) atau lebih tinggi
- Gradle 7.0+
- Java 11

Clone Repository
bash
git clone https://github.com/Musliati/ruang-berita.git
cd ruang-berita

Build & Run
1. Buka project di Android Studio
2. Sync Gradle files
3. Run Build → Clean Project
4. Run Build → Rebuild Project
5. Connect device atau start emulator
6. Click Run atau tekan Ctrl+F5

Build APK
bash
./gradlew assembleDebug
atau untuk release
./gradlew assembleRelease

🔧 Configuration
Network Configuration
Aplikasi menggunakan HTTP cleartext traffic untuk development. Untuk production, pastikan menggunakan HTTPS.

xml
<!-- AndroidManifest.xml -->
<application
    android:usesCleartextTraffic="true">

Permissions Required
xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

🧪 Testing
Manual Testing Checklist
- [ ] Splash screen muncul dan navigasi ke MainActivity
- [ ] List berita berhasil dimuat
- [ ] Filter kategori berfungsi
- [ ] Search berita working
- [ ] Bookmark save/unsave
- [ ] Share functionality
- [ ] Dark/Light theme toggle
- [ ] Swipe to refresh
- [ ] Detail berita dapat dibuka
- [ ] Offline reading berita tersimpan

Error Handling Testing
- [ ] No internet connection
- [ ] API server down
- [ ] Invalid response
- [ ] Empty search results
- [ ] Database errors

🐛 Known Issues & Limitations
Current Limitations
1. API Dependency: Aplikasi bergantung pada API eksternal yang mungkin tidak stabil
2. Image Caching: Gambar tidak tersimpan untuk offline viewing
3. Push Notifications: Belum implementasi notifikasi berita terbaru
4. Content Caching: Konten lengkap berita tidak di-cache untuk offline reading

Planned Improvements
- [ ] Implementasi offline caching untuk konten lengkap
- [ ] Push notifications untuk breaking news
- [ ] User preferences untuk notifikasi
- [ ] Social sharing dengan preview
- [ ] Reading history tracking
- [ ] Favorite categories selection
- [ ] Text size adjustment
- [ ] Reading mode optimization

👨‍💻 Author
[Musliati]
- GitHub: [@Musliati](https://github.com/Musliati)
- Email: musliati1101@gmail.com
