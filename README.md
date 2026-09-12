# Collection Desk

An offline Android customer payments database with customer contacts, invoices, payment history, late charges, printable PDF statements and WhatsApp sharing.

## Install

[Download Collection Desk 1.0 APK](downloads/Collection-Desk.apk?raw=true). Requires Android 8.0 or newer. Open the APK on your phone, then set your business name and currency in Settings before creating invoices.

Read the [user guide](docs/USER-GUIDE.md) for payments, reporting and backups.

## Features

- Customer name, telephone, address and emergency contact details
- Invoices, due dates, partial payments and payment history
- Corrections by voiding the latest payment while retaining the record
- Seven-day grace period, followed by 1.5% weekly late charges
- Default daily proration from day 8, or optional charging per started week
- Simple interest on unpaid principal; payments settle charges first
- Individual customer statements and all-customer reports
- PDF printing and sharing through WhatsApp or other apps
- Local SQLite database with JSON backup and restore

## Build from source

The app uses Java and native Android APIs without third-party runtime libraries. Minimum SDK is 26; target SDK is 34.

Install a JDK and Android SDK platform 34 with Build Tools 36.0.0. In `build.ps1`, set the SDK and JDK paths for your computer, set the local COLLECTION_DESK_KEYSTORE_PASSWORD environment variable to your private signing password, then run:

```powershell
./build.ps1
```

The script compiles the app, runs the ledger tests, packages and signs the APK, and verifies its signature. Output: `build/Collection-Desk.apk`.

The signing key for the downloadable APK is intentionally excluded. The owner must retain the original key privately to publish compatible updates. If no key exists in `build/collection-desk.jks`, the build script generates a development key. An APK signed with a different key cannot update an existing installation. Back up data before uninstalling. Do not commit signing keys or customer backups.

## Calculation and validation

`src/com/collectiondesk/app/Ledger.java` contains the monetary calculations; amounts are stored in integer cents. `test/com/collectiondesk/app/LedgerTest.java` covers grace-period boundaries, both interest methods, fee allocation, partial/full payments, rounding and invalid amounts.

The APK was built and its v2/v3 signature verified. All 19 calculation checks passed. Device installation, layout, printing, backup picker and WhatsApp handoff remain untested on a device.

## Current scope

One device and currency; no cloud synchronization or app account. Security relies on Android app storage and the phone's lock. Backups are not encrypted by the app. Saved invoices cannot be edited or deleted. Activity recreation does not preserve unsaved forms. Database upgrades require a preserving migration before a future schema change.

Reports exclude emergency contacts and private customer notes. Sharing opens the selected app; the user chooses the recipient and confirms sending.

