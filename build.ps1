$ErrorActionPreference = 'Stop'
if (!$env:COLLECTION_DESK_KEYSTORE_PASSWORD) { throw 'Set COLLECTION_DESK_KEYSTORE_PASSWORD locally before building. Never commit signing credentials.' }
$appRoot = $PSScriptRoot
$sdkRoot = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$javaRoot = 'C:\Program Files\Android\Android Studio\jbr'
$buildTools = Join-Path $sdkRoot 'build-tools\36.0.0'
$sdkAndroidJar = Join-Path $sdkRoot 'platforms\android-34\android.jar'
$env:JAVA_HOME = $javaRoot
$env:PATH = "$javaRoot\bin;$env:PATH"
Set-Location $appRoot
New-Item -ItemType Directory -Force build,build\classes,build\dex,build\tests | Out-Null
Copy-Item $sdkAndroidJar build\android.jar -Force
$androidJar = Join-Path $appRoot 'build\android.jar'
function Check-Exit { if ($LASTEXITCODE -ne 0) { throw "Build command failed: $LASTEXITCODE" } }
$sources = @(Get-ChildItem src -Recurse -Filter '*.java' | ForEach-Object FullName)
& "$javaRoot\bin\javac.exe" -encoding UTF-8 -source 8 -target 8 -classpath $androidJar -d build\classes $sources
Check-Exit
& "$javaRoot\bin\javac.exe" -encoding UTF-8 -d build\tests src\com\collectiondesk\app\Ledger.java test\com\collectiondesk\app\LedgerTest.java
Check-Exit
& "$javaRoot\bin\java.exe" -cp build\tests com.collectiondesk.app.LedgerTest
Check-Exit
& "$javaRoot\bin\jar.exe" cf build\classes.jar -C build\classes .
Check-Exit
& "$buildTools\d8.bat" --lib $androidJar --min-api 26 --output build\dex build\classes.jar
Check-Exit
& "$buildTools\aapt.exe" package -f -M AndroidManifest.xml -S res -I $androidJar -F build\unsigned.apk
Check-Exit
Copy-Item build\dex\classes.dex build\classes.dex -Force
Push-Location build
& "$buildTools\aapt.exe" add unsigned.apk classes.dex
Check-Exit
Pop-Location
& "$buildTools\zipalign.exe" -f 4 build\unsigned.apk build\aligned.apk
Check-Exit
if (!(Test-Path build\collection-desk.jks)) {
 & "$javaRoot\bin\keytool.exe" -genkeypair -keystore build\collection-desk.jks -alias collectiondesk -storepass:env COLLECTION_DESK_KEYSTORE_PASSWORD -keypass:env COLLECTION_DESK_KEYSTORE_PASSWORD -keyalg RSA -keysize 2048 -validity 10000 -dname 'CN=Collection Desk Local Build'
 Check-Exit
}
& "$buildTools\apksigner.bat" sign --ks build\collection-desk.jks --ks-key-alias collectiondesk --ks-pass env:COLLECTION_DESK_KEYSTORE_PASSWORD --key-pass env:COLLECTION_DESK_KEYSTORE_PASSWORD --out build\Collection-Desk.apk build\aligned.apk
Check-Exit
& "$buildTools\apksigner.bat" verify --verbose build\Collection-Desk.apk
Check-Exit

