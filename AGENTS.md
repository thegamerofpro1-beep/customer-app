# Collection Desk project instructions

## Start here
- Read README.md, WORK-FROM-ANY-PC.md and USER-GUIDE.md. The project lives in this repository; do not rely on earlier chat history or paths from another PC.
- This is an offline native Java Android app, package com.collectiondesk.app. It uses SQLite, Android printing and PDF sharing. It is not a website or Gradle project.
- Explain outcomes in plain language. Preserve existing customer records and payment history when making changes.

## Source map
- MainActivity.java: screens, forms, reporting, backup/restore, sharing and printing.
- Database.java: SQLite schema and access; database version 1.
- Ledger.java: monetary calculations; LedgerTest.java: standalone checks.
- AndroidManifest.xml: Android 8+ (API 26 minimum), target API 34.

## Build and validation
- `node build.mjs --test` runs ledger tests with Node.js 18+ and JDK 17+, without Android SDK or signing secrets.
- `node build.mjs` tests and builds an unsigned APK with Android platform 34 and Build Tools 36.0.0.
- Use JAVA_HOME and ANDROID_HOME, or --java-home and --sdk, rather than hardcoded machine paths.
- `node build.mjs --sign` uses the owner's separately supplied signing key; see WORK-FROM-ANY-PC.md. Never generate a replacement key and claim it can update the existing app.
- After changing money rules, run ledger tests and add meaningful boundary checks. For UI/database/reporting changes, build and test the affected Android flow where a device is available. Clearly report any device checks not performed.

## Preserve these business rules unless the user changes them
- Customer name, phone and address required; emergency name and phone optional together.
- Seven-day grace period. Default 1.5% weekly prorated daily starting day 8, on unpaid principal, without compounding.
- Optional 1.5% per started week is fixed per invoice when created.
- Integer cents, fee-first payment allocation, chronological payments; no future payments or overpayments.
- Currency is fixed after the first invoice. Payment voids retain the history and reason.
- Reports exclude emergency contacts and private notes. WhatsApp sending remains user-confirmed.
- Schema changes require a migration that preserves data. Backup/restore must remain compatible or document migration.

## Continuity and private files
- Use `codex/` branches for changes and publish commits or a pull request to the user's repository when requested. Never discard another PC's work or force-push to resolve an unexpected difference.
- Update project documentation with significant changes and known limitations so another Codex session can continue.
- Keep keystores, signing passwords, customer databases, backups and .env files out of Git. The repository is public.
- Do not assume this chat, uncommitted edits, installed SDKs or phone databases synchronize through GitHub.
