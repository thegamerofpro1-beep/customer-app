// Portable build runner: Node.js 18+, JDK 17+, Android SDK for APK builds.
import { existsSync, mkdirSync, readdirSync, copyFileSync, mkdtempSync } from 'node:fs';
import { join, dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { execFileSync } from 'node:child_process';
import { homedir } from 'node:os';

const root = dirname(fileURLToPath(import.meta.url));
const windows = process.platform === 'win32';
const option = name => {
  const index = process.argv.indexOf(name);
  if (index < 0) return undefined;
  const value = process.argv[index + 1];
  if (!value || value.startsWith('--')) throw new Error(`Missing value for ${name}`);
  return resolve(value);
};
const firstExisting = paths => paths.filter(Boolean).find(existsSync);
const javaHome = option('--java-home') || process.env.JAVA_HOME || firstExisting([
  windows && join(process.env.ProgramFiles || 'C:/Program Files', 'Android/Android Studio/jbr'),
  process.platform === 'darwin' && '/Applications/Android Studio.app/Contents/jbr/Contents/Home',
]);
const tool = name => javaHome ? join(javaHome, 'bin', name + (windows ? '.exe' : '')) : name;
const run = (name, args, cwd = root) => {
  try { execFileSync(tool(name), args, { cwd, stdio: 'inherit', windowsHide: true }); }
  catch (error) {
    if (error.code === 'ENOENT') throw new Error(`Cannot find ${name}. Install JDK 17+ and set JAVA_HOME or use --java-home.`);
    throw error;
  }
};
const walk = path => readdirSync(path, { withFileTypes: true }).flatMap(entry => {
  const child = join(path, entry.name);
  return entry.isDirectory() ? walk(child) : [child];
});
function main() {
  if (process.argv.includes('--help')) {
    console.log('node build.mjs [--test] [--sdk PATH] [--java-home PATH] [--sign]\nDefault: test and build an unsigned APK. --test needs no Android SDK.\nSigning requires COLLECTION_DESK_KEYSTORE and COLLECTION_DESK_KEYSTORE_PASSWORD; optional COLLECTION_DESK_KEY_ALIAS (default collectiondesk).');
    return;
  }
  const build = join(root, 'build');
  mkdirSync(build, { recursive: true });
  // Fresh per-run directories prevent removed classes from leaking into an APK.
  const session = mkdtempSync(join(build, 'run-'));
  const tests = join(session, 'tests');
  mkdirSync(tests);
  run('javac', ['-encoding', 'UTF-8', '--release', '8', '-d', tests,
    join(root, 'src/com/collectiondesk/app/Ledger.java'),
    join(root, 'test/com/collectiondesk/app/LedgerTest.java')]);
  run('java', ['-cp', tests, 'com.collectiondesk.app.LedgerTest']);
  if (process.argv.includes('--test')) return;
  const sdk = option('--sdk') || process.env.ANDROID_HOME || process.env.ANDROID_SDK_ROOT || firstExisting([
    windows && process.env.LOCALAPPDATA && join(process.env.LOCALAPPDATA, 'Android/Sdk'),
    process.platform === 'darwin' && join(homedir(), 'Library/Android/sdk'),
    join(homedir(), 'Android/Sdk'),
  ]);
  if (!sdk) throw new Error('Android SDK not found. Set ANDROID_HOME or pass --sdk.');
  const tools = join(sdk, 'build-tools/36.0.0');
  const androidJar = join(session, 'android.jar');
  const platformJar = join(sdk, 'platforms/android-34/android.jar');
  for (const path of [platformJar, join(tools, 'lib/d8.jar'), join(tools, 'aapt' + (windows ? '.exe' : ''))]) {
    if (!existsSync(path)) throw new Error('Install Android SDK platform 34 and Build Tools 36.0.0. Missing: ' + path);
  }
  copyFileSync(platformJar, androidJar);
  const classes = join(session, 'classes'), dex = join(session, 'dex');
  mkdirSync(classes); mkdirSync(dex);
  run('javac', ['-encoding', 'UTF-8', '-source', '8', '-target', '8', '-classpath', androidJar, '-d', classes,
    ...walk(join(root, 'src')).filter(path => path.endsWith('.java'))]);
  const classJar = join(session, 'classes.jar');
  run('jar', ['cf', classJar, '-C', classes, '.']);
  run('java', ['-cp', join(tools, 'lib/d8.jar'), 'com.android.tools.r8.D8', '--lib', androidJar, '--min-api', '26', '--output', dex, classJar]);
  const androidTool = (name, args, cwd = root) => execFileSync(join(tools, name + (windows ? '.exe' : '')), args, { cwd, stdio: 'inherit', windowsHide: true });
  const rawApk = join(session, 'raw.apk');
  androidTool('aapt', ['package', '-f', '-M', join(root, 'AndroidManifest.xml'), '-S', join(root, 'res'), '-I', androidJar, '-F', rawApk]);
  androidTool('aapt', ['add', rawApk, ...readdirSync(dex).filter(file => file.endsWith('.dex'))], dex);
  const unsigned = join(session, 'Collection-Desk-unsigned.apk');
  androidTool('zipalign', ['-f', '4', rawApk, unsigned]);
  if (process.argv.includes('--sign')) {
    const key = process.env.COLLECTION_DESK_KEYSTORE;
    if (!key || !existsSync(key) || !process.env.COLLECTION_DESK_KEYSTORE_PASSWORD) throw new Error('Signing needs an existing COLLECTION_DESK_KEYSTORE file and COLLECTION_DESK_KEYSTORE_PASSWORD environment variable.');
    const signed = join(session, 'Collection-Desk.apk');
    run('java', ['-jar', join(tools, 'lib/apksigner.jar'), 'sign', '--ks', resolve(key), '--ks-key-alias', process.env.COLLECTION_DESK_KEY_ALIAS || 'collectiondesk', '--ks-pass', 'env:COLLECTION_DESK_KEYSTORE_PASSWORD', '--key-pass', 'env:COLLECTION_DESK_KEYSTORE_PASSWORD', '--out', signed, unsigned]);
    run('java', ['-jar', join(tools, 'lib/apksigner.jar'), 'verify', '--verbose', signed]);
    copyFileSync(signed, join(build, 'Collection-Desk.apk'));
    console.log('Signed APK: build/Collection-Desk.apk');
  } else {
    copyFileSync(unsigned, join(build, 'Collection-Desk-unsigned.apk'));
    console.log('Unsigned APK: build/Collection-Desk-unsigned.apk (must be signed before installation)');
  }
}
try { main(); } catch (error) { console.error(error.message); process.exitCode = 1; }
