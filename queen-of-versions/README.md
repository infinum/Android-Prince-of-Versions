# Queen of Versions

![Bitrise](https://app.bitrise.io/app/5bc3f35c9c5f5b61/status.svg?token=XGiXvE7Yu6DXdL9wrEqiHQ&branch=dev)
[![Download](https://api.bintray.com/packages/infinum/android/queen-of-versions/images/download.svg)](https://bintray.com/infinum/android/queen-of-versions/_latestVersion)

Library checks update availability using In-App updates.

## Getting via jcenter

```groovy
implementation 'co.infinum:queen-of-versions:0.1.0'
```

## Features

  * Check update availability using **In-App updates**.
  * Integrate with Prince of Versions to determine **required update**.
  * Supports automatic and manual **update type resolution**.
  * Provides functionality to **cancel** check once started.


### Common usage

```java
// build the instance (use methods in builder to implement specific behavior)
QueenOfVersions queenOfVersions = new QueenOfVersions.Builder()
        .build(activity);

// build a callback (use methods in builder to implement specific behavior)
QueenOfVersions.Callback callback = new QueenOfVersions.Callback.Builder()  
        .build();

// start the update check
PrinceOfVersionsCancelable cancelable = queenOfVersions.checkForUpdates(loader, callback);

// stop the update check (callback won't be called anymore)
cancelable.cancel()
```

## Examples

#### Use configured Prince of Versions instance
```java
QueenOfVersions queenOfVersions = new QueenOfVersions.Builder()
    .withPrinceOfVersions(princeOfVersions)
    .build(activity);
```
#### Handle declined update by user
```kotlin
val callback = QueenOfVersions.Callback.Builder()
    .withOnUpdateDeclined { inAppUpdateInfo, updateStatus, updateResult ->
        // updateStatus is either
        //	 UpdateStatus.REQUIRED_UPDATE_NEEDED (if IMMEDIATE update flow declined)
        // or
        //	 UpdateStatus.NEW_UPDATE_AVAILABLE (if FLEXIBLE update flow declined)
        // use updateStatus to handle IMMEDIATE and FLEXIBLE flow specifically

        // inAppUpdateInfo contains information about the update from Google Play
        // updateResult is only available if Prince of Versions is used in the check

        // for example, if required update has to be installed before using the app
        if (updateStatus == UpdateStatus.REQUIRED_UPDATE_NEEDED) {
            // show an explanation that required update needs to be installed and restart the check
        } else {
            // nothing to do here, user canceled optional update
        }
    }
    .build()
```

#### Use only for required updates
If you would like to check update status on Google Play only for required updates (or generally only in some specific case) you can do that by overriding following method.

By default, Queen of Versions will check update status on Google Play for any Prince of Versions result, but it will start <code>IMMEDIATE</code> flow only if Prince of Versions retured <code>UpdateStatus.REQUIRED_UPDATE_NEEDED</code>.
```kotlin
val queenOfVersions = QueenOfVersions.Builder()
    .withPrinceOfVersionsSuccessHandler { updateResult ->
        if (updateResult.status == UpdateStatus.REQUIRED_UPDATE_NEEDED) {
            // if update is required proceed with IMMEDIATE update flow
            UpdateStatus.REQUIRED_UPDATE_NEEDED
        } else {
            // in any other case skip the update
            UpdateStatus.NO_UPDATE_AVAILABLE
        }
    }
    .build(this)
```
#### If required update isn't available on Google Play
If using Prince of Versions for checking if update should be threated as required or optional you can end up in the case that Prince of Versions mark a version as required update, but that version isn't available on Google Play yet.

By default Queen of Versions handles that case by notifying that there is no update, but there is an option to override that behavior.
```kotlin
val callback = QueenOfVersions.Callback.Builder()
    .withOnMandatoryUpdateNotAvailable { requiredVersion, inAppUpdateInfo, metadata, updateInfo ->
        // for example show a message that update is required for application to work, but isn't available yet.

        // requiredVersion is version code of the update Prince of Versions claims is required
        // inAppUpdateInfo contains information about the update from Google Play
        // for metadata and updateInfo check Prince of Versions documentation
    }
    .build()
```
#### Use In-App updates without Prince of Versions
By default, if update is available it will be presented in <code>FLEXIBLE</code> flow.
```kotlin
QueenOfVersions.checkForUpdates(
    activity,
    QueenOfVersions.Options.Builder()
        // use methods in builder to implement specific behavior
        // there are equivalent methods as in QueenOfVersions builder
        .build(),
    callback
)
```

#### Determine update type by In-App update information
```kotlin
QueenOfVersions.checkForUpdates(
    activity,
    QueenOfVersions.Options.Builder()
        .withOnInAppUpdateAvailable { updateStatus, inAppUpdateInfo, updateResult ->
            // or any other logic
            if (inAppUpdateInfo.updatePriority() > 2) {
                UpdateStatus.REQUIRED_UPDATE_NEEDED
            } else {
                UpdateStatus.NEW_UPDATE_AVAILABLE
            }
        }
        .build(),
    callback
)
```

### Multiple flavors
If your application has multiple product flavors or build type make sure to not use Queen of Versions if flavor is not available on Google Play. Use plain [Prince of Versions](./../prince-of-versions/README.md) check if that is the case.


### R8 / ProGuard

If you are using R8 or ProGuard add the options from
[this file](./queen-of-versions.pro).


### Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](./../LICENCE).
