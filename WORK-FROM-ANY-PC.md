# Continue this project from any PC

The shared project is https://github.com/thegamerofpro1-beep/customer-app.
The source, instructions and tests travel with the repository. No files from the original computer are needed to edit the app or run calculation tests.

## Option 1: Codex in a browser

1. Open https://chatgpt.com/codex on the other computer and sign in with your ChatGPT account.
2. Connect GitHub using **thegamerofpro1-beep**, or another GitHub account with write access to this repository. Select **customer-app** for repository access.
3. Create/select a Codex cloud environment for this repository. Have Node.js 18+ and JDK 17+ available. Use `node build.mjs --test` as the initial test command. APK building additionally needs Android SDK platform 34 and Build Tools 36.0.0.
4. Start a task with the prompt below. Review changes, then create and merge a pull request so they are available from the next computer.

GitHub connection and the cloud environment are account settings; adding these repository files does not configure those settings automatically. The connector used during initial project creation belonged to a different GitHub account, so check the selected account if this repository is missing or read-only.

## Option 2: Codex desktop with a local project

Install Git and Codex, then clone the repository to a folder on the new computer:

```sh
git clone https://github.com/thegamerofpro1-beep/customer-app.git
```

Add/open that folder as a local Codex project and sign in to GitHub with an account allowed to push. A downloaded ZIP is useful for reading, but a Git clone retains synchronization with the repository.

Before starting on another PC, save your changes in Git and push the branch. On the next PC, fetch the latest changes and open the same branch, or pull main after the changes are merged. Ask Codex to help with this if you prefer. Local unsaved files and this original conversation are not part of the repository.

## Prompt to continue

> Continue work on Collection Desk in this repository. Read AGENTS.md and WORK-FROM-ANY-PC.md first. Inspect the current code and recent commits, run node build.mjs --test if the JDK is available, then help me with: [describe your change]. Keep customer data intact and save completed work to a codex/ branch for review.

## Build on Windows, macOS or Linux

Install Node.js 18+ and a JDK 17 or newer. Set JAVA_HOME to the JDK directory if Java is not on PATH. The runner also detects common Android Studio JDK installations on Windows/macOS.

```sh
node build.mjs --test
```

To compile an APK, also install Android SDK platform 34 and Build Tools 36.0.0. Set ANDROID_HOME to the SDK directory or provide it explicitly:

```sh
node build.mjs --sdk /path/to/Android/sdk --java-home /path/to/jdk
```

The default output is `build/Collection-Desk-unsigned.apk`. It is for build verification and cannot be installed until signed. The existing signed APK in the repository remains downloadable.

To sign a compatible update, supply these locally (never commit their values):

- COLLECTION_DESK_KEYSTORE: absolute path to the original private signing key.
- COLLECTION_DESK_KEYSTORE_PASSWORD: its private password.
- COLLECTION_DESK_KEY_ALIAS: optional, defaults to collectiondesk.

Then run `node build.mjs --sign`. Retain the original signing key privately and increment the manifest version before releasing an update. A new signing key cannot update the existing installed app. Signing is not required for normal editing, code review or ledger tests.

The portable runner was tested on Windows. macOS/Linux paths are supported by the script but have not been executed on those systems. Android device testing is still required for UI and sharing changes.

Phone customer data stays on the phone. Use the app's private backup/restore process to move records; never put customer backups in this public repository.

Official setup reference: https://learn.chatgpt.com/docs/cloud
Project instruction reference: https://learn.chatgpt.com/docs/agent-configuration/agents-md
