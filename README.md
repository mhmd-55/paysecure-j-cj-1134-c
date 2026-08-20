# PaySecure-J — Lab Build (CJ-1134-C)

**Intentionally vulnerable. Local/lab use only. Do not deploy or expose this build to any network beyond localhost.**

This project reassembles the code excerpts from the CASE Java "PaySecure-J" assignment (Parts A–J) into a
runnable Spring Boot app, with nothing added except the minimal wiring (entities/repositories/templates/seed data)
needed to actually start it and click through it. Every vulnerability in the handout is preserved verbatim.

## Requirements
- Java 17+
- Maven 3.9+
- **MySQL 8.x running locally**, listening on the default port `3306`.

## One-time database setup
Open a MySQL client (MySQL Workbench, or `mysql -u root -p` from a terminal) and run:
```sql
CREATE DATABASE paysecure;
CREATE USER 'paysecure_admin'@'localhost' IDENTIFIED BY 'Admin@123456';
GRANT ALL PRIVILEGES ON paysecure.* TO 'paysecure_admin'@'localhost';
FLUSH PRIVILEGES;
```
These credentials intentionally match the hardcoded values in `AppConfiguration.java`
(Finding 1) — that's the point of that finding. Tables and seed data are created
automatically on first run (Hibernate `ddl-auto=update` + `data.sql`), you don't need
to write any `CREATE TABLE` statements yourself.

## Run it
```bash
cd paysecure-j
mvn spring-boot:run
```
The app starts on **http://localhost:8080**. Watch the console for the evidence banner:
```
PaySecure-J LAB BUILD | Evidence Tag: CJ-1134-C
Student ID: mhm0231134 | Variant: C
```
That banner + your system clock is your baseline authenticity screenshot.

To inspect the database directly, use MySQL Workbench or:
```bash
mysql -u paysecure_admin -p paysecure
```
(password `Admin@123456`), then e.g. `SELECT * FROM users;`

## Seed accounts (password for all: `Password123!`)
| id | username | role |
|----|----------|------|
| 1 | attacker | CUSTOMER |
| 1434 | testuser1434 | CUSTOMER | *(Variant C test profile ID)*
| 2434 | admin2434 | ADMIN | *(Variant C admin target ID)*

## Where each finding lives
| Finding | Location | Try this |
|---|---|---|
| 1. Hardcoded secrets | `config/AppConfiguration.java` | grep the source / decompile the jar |
| 2. Weak password hashing | `service/UserService.java` | register 2 users with the same password, compare stored hashes in H2 console |
| 3. Session fixation | `controller/AuthController.java` | capture `JSESSIONID` before and after login, confirm it's unchanged |
| 4. SQL injection | `controller/TransactionController.java` | `GET /transactions/search?keyword=%25' UNION SELECT id,username,password FROM users --` |
| 5. IDOR | `controller/ProfileController.java` | `GET /profile?userId=2434` while "logged in" as `attacker` |
| 6. Privilege escalation | `controller/AdminController.java` | `POST /admin/deleteUser?id=1434&role=ADMIN` |
| 7. Unrestricted upload | `controller/DocumentController.java` | multipart upload with filename `../../../../evil.txt` |
| 8. Predictable reset token | `controller/PasswordResetController.java` | `GET /reset/request?username=testuser1434`, note the token is in the response body and only 6 digits |
| 9. CSRF disabled + IDOR transfer | `controller/TransferController.java`, `config/SecurityConfig.java` | `POST /transfer?fromAccount=1434&toAccount=1&amount=349.80` while authenticated only as `attacker` |
| 10. Verbose errors | `exception/GlobalExceptionHandler.java` | `GET /profile?userId=notanumber` |

## Suggested workflow
1. `git init && git add -A && git commit -m "Initial unmodified PaySecure-J baseline - CJ-1134-C"`
2. For each finding: reproduce → screenshot/capture → fix → restart → retest → screenshot → commit.
3. Add JUnit tests under `src/test/java` (see `SecurityFindingsTest.java` for two starter tests to extend).
4. When all 10 are fixed, check out (or `git stash`) an early commit to demonstrate the attack chain end-to-end on the vulnerable version, then return to your fixed `main` branch.

## Note on `SecurityConfig`
`anyRequest().permitAll()` is deliberately permissive in this lab build so every endpoint above is reachable
without wiring a full Spring Security login flow first — this mirrors the handout's real-world point that the
app's *actual* protections are the individual (broken) checks inside each controller, not a working auth gate.
Your remediation for Finding 9 should tighten this properly.
