# Smart Wallet UI 💳

React 19 ve Vite altyapısıyla geliştirilmiş, modern fintech standartlarına uygun dijital cüzdan ön yüz (frontend) uygulaması.

---

## 📂 Proje Dosya Yapısı

Projenin `smart-wallet-ui` altındaki dizin ve dosya organizasyonu şu şekildedir:

```text
smart-wallet-ui/
├── public/                  # Statik dosyalar ve favicon
│   ├── favicon.svg
│   └── icons.svg
├── screenshots/             # README ve dokümantasyon ekran görüntüleri
│   ├── screen1.png          # Dashboard & Wallet Overview
│   ├── screen2.png          # Login Screen
│   ├── screen3.png          # Register Screen
│   ├── screen4.png          # Transfer Panel
│   ├── screen5.png          # Transaction History
│   ├── screen6.png          # Toast Notification
│   └── screen7.png          # Skeleton Loading
├── src/                     # Kaynak kodlar
│   ├── assets/              # Görsel ve SVG varlıkları
│   ├── components/          # UI bileşenleri
│   │   ├── Dashboard.jsx
│   │   ├── LoginForm.jsx
│   │   ├── Logo.jsx
│   │   ├── RegisterForm.jsx
│   │   ├── Sidebar.jsx
│   │   ├── Skeleton.jsx
│   │   ├── Topbar.jsx
│   │   ├── TransactionHistory.jsx
│   │   ├── TransferPanel.jsx
│   │   └── WalletCard.jsx
│   ├── context/             # Global State yönetimleri
│   │   ├── AuthContext.jsx
│   │   └── Toastcontext.jsx
│   ├── services/            # API istek katmanı
│   │   └── api.js
│   ├── App.css
│   ├── App.jsx              # Ana uygulama yönlendiricisi
│   ├── index.css            # Tailwind ve temel stiller
│   ├── main.jsx             # React DOM kök bağlayıcısı
│   └── Theme.js             # Renk paleti ve tasarım tokenları
├── .gitignore
├── eslint.config.js
├── index.html
├── package.json
├── postcss.config.js
├── tailwind.config.js
└── vite.config.js
