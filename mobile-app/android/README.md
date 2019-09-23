# CURRENCY CONVERTER DEMO - ANDROID APP


## SETUP

### Stack Setup

Run from the root of the project:

```
$ ./stack setup    
'.env.example' -> '.env'
'./mobile-app/android/app/src/main/cpp/api_key.h.example' -> './mobile-app/android/app/src/main/cpp/api_key.h' 
```

### Create the Keystore

In order to build an APK we need to have a key-store, and we will create one just for this project.

Run from `./mobile-app/android/`:

```bash
$ ./bin/create-keystore.sh                                                                                                                                                                                  1 ↵
Enter keystore password:  
Re-enter new password: 
What is your first and last name?
  [Unknown]:  
What is the name of your organizational unit?
  [Unknown]:  
What is the name of your organization?
  [Unknown]:  
What is the name of your City or Locality?
  [Unknown]:  
What is the name of your State or Province?
  [Unknown]:  
What is the two-letter country code for this unit?
  [Unknown]:  
Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
  [no]:  y

Generating 2,048 bit RSA key pair and self-signed certificate (SHA256withRSA) with a validity of 10,000 days
  for: CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown
[Storing .local/approov.keystore.jks]

---> Adding, if not already present, properties to your ./local.properties file

---> Edit your ./local.properties file and add the passwords you have used when you first created the keystore.

```

As the output says you must edit your `local.properties` file and update it with the passwords you entered when creating the keystore.

If not already present in the `local.properties` file, the bash script will add all the properties listed in the [local.properties.example](local.properties.example) file.


### Add the Approov SDK

The project is already prepared to drop-in the Approov `approov-sdk.aar` file, because I already added it before, but once 
the file not being tracked by git, as per `approov-sdk/.gitignore`, we need to add it each time we clone the repo.

Run from `./mobile-app/android`:

```bash
$ approov sdk -getLibrary approov-sdk/approov-sdk.aar
Android SDK library 2.0.5(1212) written to approov-sdk/approov-sdk.aar
```

> **NOTE**: In a brand new project you will need to follow the [Approov documentation](https://approov.io/docs/v2.0/approov-usage-documentation/#importing-the-approov-sdk-into-android-studio) to add the APK via Android Studio.


## RELEASE APK

To release an APK, we need to build, register it in the Approov cloud service and then install it on the device.

### Build the APK 

Afterwards you can run the helper bash script to build the APK.

Run from `mobile-app/android/`:

```bash
./bin/build-release.bash
```

### Register the APK with Approov

In order to get valid attestations for the APK in the Approov cloud service we need to first [register the APK](https://approov.io/docs/v2.0/approov-usage-documentation/#managing-registrations) with it.

Run from `mobile-app/android/`:

```bash
$ approov registration -add app/build/outputs/apk/release/app-release.apk -expireAfter 600s
registering app Currency Converter Demo
 e9C6klG8MKMqWQhc1Wv3rcZIeb0reVuqHwCbyx0nG0o=com.criticalblue.currencyconverterdemo-1.0[1]-1212  SDK:Android(2.0.5)
registration successful, expires 2019-09-20 14:10:26
```

For production usage we do not normally use the `-expireAfter` flag, but for development is very useful.

### Install the APK

To play with the release APK in your device:

```bash
adb install app/build/outputs/apk/release/app-release.apk
```

Now if we open the app we should be able to perform currency conversions until we expire the `600s` that we register the
app with, and after we reach the expire time we should get an error message:

```
API server status code response: 401
``` 

Feel free to continue playing with the mobile by changing [registering](https://approov.io/docs/v2.0/approov-cli-tool-reference/#registration-command) the APK with different expire times (e.g. 2d, 3h, 4h30m, 300s) or even without any expire time, meaning that will be valid until you remove it).
