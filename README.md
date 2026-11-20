# CryptoTrading

REST backend for a crypto trading app. Users sign up, top up a wallet with Razorpay or Stripe, buy and sell coins at live CoinGecko prices, track their portfolio and watchlist, and request withdrawals that an admin approves.

**Stack:** Java 17 · Spring Boot 3.5 · Spring Security + JWT · Spring Data JPA (MySQL) · Razorpay · Stripe · Spring Mail · Docker Compose

## Running locally

1. Start MySQL. The default config expects it on `localhost:3307` (the port `docker-compose.yml` maps):
   ```bash
   docker compose up -d mysql
   ```
2. Set the configuration. Copy `CryptoTrading/env.example` and fill in your own values, or edit the local defaults in `CryptoTrading/src/main/resources/application.properties`.
3. Run the app:
   ```bash
   cd CryptoTrading
   ./mvnw spring-boot:run
   ```
   The API runs at `http://localhost:8080`.

### Configuration

| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MySQL connection (the app refuses to start if any are missing) |
| `RAZORPAY_API_KEY`, `RAZORPAY_API_SECRET` | Razorpay payment links (INR) |
| `STRIPE_API_KEY` | Stripe Checkout (USD) |
| `APP_BASE_URL` | Public URL of this backend. Payment gateways redirect to `<APP_BASE_URL>/api/wallet/deposit` |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP for OTP emails. For Gmail, use an App Password |

## API

All `/api/**` endpoints need an `Authorization: Bearer <jwt>` header. The one exception is `/api/wallet/deposit`, described below.

| Area | Endpoint | Description |
|---|---|---|
| Auth | `POST /auth/signup`, `POST /auth/signin` | Register or log in. Both return a JWT |
| | `POST /auth/two-factor/otp/{otp}?id=` | Finish logging in when 2FA is on |
| | `POST /auth/users/reset-password/send-otp`, `PATCH /auth/users/reset-password/verify-otp?id=` | Forgot password |
| User | `GET /api/users/profile` | Your profile |
| | `POST /api/users/varification/EMAIL/send-otp`, `PATCH /api/users/enable-two-factor/verify-otp/{otp}` | Turn on 2FA |
| Coins | `GET /coins?page=`, `/coins/top50`, `/coins/trending`, `/coins/search?q=`, `/coins/{id}/chart?days=`, `/coins/details/{id}` | Market data from CoinGecko. Listed coins are cached so they can be traded |
| Orders | `POST /api/orders/pay` `{coinId, quantity, orderType: BUY\|SELL}` | Buy or sell at the current price |
| | `GET /api/orders?order_type=&asset_symbol=`, `GET /api/orders/{id}` | Order history |
| Assets | `GET /api/asset`, `/api/asset/{id}`, `/api/asset/coin/{coinId}/user` | Portfolio |
| Wallet | `GET /api/wallet` | Balance |
| | `PUT /api/wallet/{walletId}/transfer` `{amount}` | Send money to another wallet |
| Top-up | `POST /api/payment/{RAZORPAY\|STRIPE}/amount/{amount}` | Returns a payment URL |
| | `GET\|POST\|PUT /api/wallet/deposit?order_id=` | Gateway redirect target. Checks the payment with the gateway and credits the wallet once |
| History | `GET /api/transactions` | Wallet transactions, newest first |
| Withdrawals | `POST /api/withdrawal/{amount}`, `GET /api/withdrawal` | Request a withdrawal, see history |
| | `GET /api/admin/withdrawal`, `PATCH /api/admin/withdrawal/{id}/proceed/{true\|false}` | Admin only: approve or decline. Declined requests are refunded |
| Bank details | `POST /api/payment-details`, `GET /api/payment-details` | Bank account used for withdrawals |
| Watchlist | `GET /api/watchlist/user`, `PATCH /api/watchlist/add/coin/{coinId}` | Coins you're watching |

The data model is shown in [`CryptoTrading/assets/er_diagram_trading_app.png`](CryptoTrading/assets/er_diagram_trading_app.png).

## Known limitations

- Passwords are stored as plain text. They should be hashed with BCrypt.
- JWTs don't carry roles. Admin endpoints check the role stored in the database.
