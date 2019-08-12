# CURRENCY CONVERTER DEMO - ANDROID APP

## BUILD A RELEASE APK 

### Environment Variables

Adding the necessary environment values to build the release:

```bash
cp .env.build-release-example .env.build-release
```

Now edit the file to add your passwords for the key store and private key.

### Build the Release APK

Afterwards you can run the helper bash script to build the APK:

```bash
./build-release
```

### Test the Release APK

To test the release APK in your device:

```bash
adb install app/build/outputs/apk/release/app-release.apk
```


android:networkSecurityConfig="@xml/network_security_config"
