# smart-wallet

<img width="2720" height="3600" alt="smart_wallet_full_stack_diagram" src="https://github.com/user-attachments/assets/6a566323-d70a-4e93-8537-4522eec9190b" />
 
## System Architecture 

<img width="1024" height="806" alt="1201ff2f-0a65-4919-8a8b-e46142397265" src="https://github.com/user-attachments/assets/98599b9e-3c3a-4753-8f97-a890e2abf7b9" />

<img width="1024" height="1024" alt="b02d5e54-aad8-4134-8750-698a09132cf7" src="https://github.com/user-attachments/assets/2da49a9e-5fef-43e8-9a30-acb1263c066f" />

<img width="1024" height="1024" alt="3a85f953-5866-4496-8ab6-503cfafff9bd" src="https://github.com/user-attachments/assets/93526d70-3d6f-41c7-aa5c-eaebb33159e6" />


<img width="2720" height="1960" alt="smart_wallet_hexagonal_architecture_final" src="https://github.com/user-attachments/assets/e58c8380-ff8f-4122-ba2d-6be866b279f0" />


# Smart Wallet

Dijital cüzdan backend'i. Kullanıcı hesap açar, para yatırır/çeker,
başka bir cüzdana transfer yapar. Basit gibi görünüyor ama asıl mesele şu:
**iki istek aynı anda aynı cüzdana dokunduğunda ne olacak?**

Bu proje o soruyu ciddiye alarak yazıldı.

## Neden var

Çoğu "cüzdan" örnek projesi CRUD'dan ibaret — kaydet, getir, güncelle.
Gerçek bir ödeme sisteminde bundan fazlası lazım: aynı isteği kullanıcı
yanlışlıkla iki kere gönderirse para iki kez gitmemeli, iki farklı transfer
aynı anda aynı cüzdanı güncellemeye çalışırsa veri bozulmamalı, ve her para
hareketinin silinemez bir kaydı olmalı. Bu projeyi bu üç problemi doğru
çözmek için kurdum.

## Mimari

Hexagonal (Ports & Adapters). Domain katmanı saf Java — hiçbir Spring
veya JPA anotasyonu içermiyor, iş kurallarını (negatif bakiye olamaz,
para birimi uyuşmalı) kendi içinde doğruluyor. Bu sayede iş mantığı
framework'ten tamamen bağımsız test edilebiliyor.

```
Client (HTTP)
    |
NGINX (TLS termination)
    |
Infrastructure  ->  REST controller, JWT filtresi, rate limiter,
    |                global exception handler
Application     ->  use case'ler + port arayüzleri (@Transactional sınırı burada)
    |
Domain          ->  Wallet, Transaction, Money, User — saf Java
    |
Infrastructure  ->  JPA repository, hibrit şifreleme, JWT provider
    |
PostgreSQL / Redis / RabbitMQ
```

Bağımlılık tek yönlü: infrastructure → application → domain. Domain hiçbir
zaman dışarıyı bilmiyor.

## Concurrency — asıl iş burada

Üç ayrı mekanizma, hepsi aynı anda çalışıyor:

- **Idempotency key** — her para hareketi bir `Idempotency-Key` header'ı
  taşıyor, veritabanında unique constraint'li bir tabloya yazılıyor. Aynı
  key ikinci kez gelirse işlem tekrar çalışmıyor, önceki sonuç dönüyor.
- **Deadlock-safe pessimistic locking** — transfer sırasında iki cüzdan
  kilitleniyor, ama her zaman ID'lerin küçükten büyüğe sıralamasıyla. A'dan
  B'ye ve B'den A'ya aynı anda gelen iki transfer isteği bu yüzden asla
  birbirini kilitlemiyor.
- **Immutable audit log** — her hareketin önceki/sonraki bakiyesi, tipi ve
  zamanı ayrı bir tabloda, sadece INSERT alan bir kayıt olarak tutuluyor.

Bunu test etmek için H2 kullanmadım — Postgres'in lock davranışını birebir
taklit etmiyor. Testcontainers ile gerçek bir Postgres container'ı üstünde,
iki eşzamanlı isteğin bakiyeyi doğru bıraktığını doğruladım.

## Auth ve güvenlik

- JWT, RS256 (asimetrik) ile imzalanıyor — access token 15 dakika,
  refresh token 5 gün ve her kullanımda yenileniyor.
- Kullanıcının IBAN'ı hibrit (envelope) şifrelemeyle korunuyor: veri
  AES-256 ile şifreleniyor, o AES anahtarı da ayrı bir RSA anahtar
  çiftiyle şifreleniyor. JWT'nin kendi anahtarından bilerek ayrı.
- Art arda başarısız girişte hesap Redis üzerinden geçici kilitleniyor.
- `/api/v1/auth/**` uçlarına IP başına dakikalık istek sınırı var.
- Şema tamamen Flyway ile versiyonlanıyor, Hibernate hiçbir zaman şemaya
  dokunmuyor — sadece doğruluyor.

## Stack

Java 21 · Spring Boot · PostgreSQL · Redis · RabbitMQ · Docker

## Durum

Backend çalışıyor, testler geçiyor, Docker image hazır. 
Sırada: Oracle Cloud'a deploy edeceğim, CI/CD pipeline'ı, 
ve ileride bir harcama analizi katmanı eklenecek.

<img width="789" height="555" alt="Ekran görüntüsü 2026-08-21 170531" src="https://github.com/user-attachments/assets/0531bd81-2c1d-4a2a-88f0-26b15c6c0e3a" />



