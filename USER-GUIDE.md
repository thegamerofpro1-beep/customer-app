# Collection Desk for Android

## Install
1. Copy **Collection-Desk.apk** to your Android phone (Android 8.0 or newer).
2. Open the file and, if Android requests it, allow that file manager to install this app.
3. Open **Collection Desk** and go to **Settings**. Enter your business name and three-letter currency code, such as MYR or USD, and save.

This is a directly installable, locally signed APK, not a Google Play listing.

## Collect payments
1. Add a customer: name, telephone and address are required. Emergency contact name and telephone are optional, and entered together.
2. Open the customer and create an invoice with an amount, reference and due date.
3. Open the invoice and choose **Record payment**. Enter the amount, date, method and optional receipt reference.
4. View the dashboard for outstanding balances and overdue accounts.

Payments must be entered in date order. Overpayments and future payments are blocked. The latest active payment on each invoice can be voided with a reason, leaving its history visible. Customer details can be edited. Saved invoices cannot be edited or deleted in this version; review them before saving.

## Late payment calculation
- Days 1–7 after the due date: no late charge.
- Default: from day 8, charge **1.5% per week prorated daily** on unpaid principal: principal × 0.015 ÷ 7 for each chargeable day.
- Example: an unpaid 1,000.00 invoice has a charge of 2.14 on day 8 and 15.00 on day 14.
- An alternative in Settings charges a full 1.5% for each started week, on days 8, 15, 22 and so on. That alternative was included because a charging preference was not supplied.
- Interest does not compound. Payments pay accrued late charges first and then principal. Charges are calculated through the payment date before that payment reduces principal. Money is rounded to two decimal places; calculations retain fractional precision between payments.
- Each invoice keeps the charging method selected when it was created. Changing Settings affects new invoices only.
- All records use one currency. The currency label is locked after the first invoice. This app does not convert currencies.
- Balances use the phone's current local calendar date. Keep its date correct.

## Print and WhatsApp
Open a customer and choose **Customer statement / WhatsApp** to preview an individual statement. Choose **Print / Save as PDF** for Android's print dialog, or **Share PDF via WhatsApp** to select the recipient and confirm sending in WhatsApp. WhatsApp Business is also supported as a fallback. If neither is installed, the app offers other sharing apps.

The **Reports** tab produces an all-customer collection report. Use an individual statement when sending to one customer. Emergency contact details and private customer notes are excluded from reports. Statements include invoice balances and payment history. A compatible Android print service or printer is required for physical printing.

## Storage and backups
The app stores data in a private SQLite database on the phone. It has no cloud account, multi-user access or cross-device synchronization. The app itself requires no Internet or contacts permission; WhatsApp uses its own connection.

In Settings, use **Export database backup** to save a JSON file to a location you control. Use **Restore database backup** to replace the local data from a backup. Restore checks the data and rolls back invalid records. Backups contain customer information, are not encrypted by the app, and should be stored privately. Export a backup before uninstalling: uninstalling deletes the phone's local records. Reports are not a full database backup.

## Build and verification
The APK compiled and passed Android APK signature verification (v2 and v3). Nineteen automated calculation checks passed, covering grace-period boundaries, both charging methods, partial and full payments, fee allocation, rounding, and invalid amounts.

There was no connected Android device or installed emulator image available. Installation, screen layout, backup picker, printing and WhatsApp handoff have not been verified on a device. Test those flows with a sample customer before entering operational records.

This repository includes the source, manifest, tests and build script. The signing key is excluded and retained privately by the owner. See the repository README for rebuilding.

