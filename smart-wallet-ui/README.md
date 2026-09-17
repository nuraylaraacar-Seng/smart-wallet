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
├── screenshots/             # Ekran görüntüleri
│   ├── dashboard-overview.png
│   ├── transfer-panel.png
│   ├── transfer-success.png
│   ├── deposit-success.png
│   ├── transaction-history.png
│   ├── transfer-by-email.png
│   └── wallet-balance.png
├── src/                     # Kaynak kodlar
│   ├── assets/              # Görsel varlıkları
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
