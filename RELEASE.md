## Release guide

You must know **BINTRAY_USER** and **BINTRAY_API_KEY**.

Bintray API key can be found under Edit Profile -> API Key.

### Deploying a new version

1. Bump `versions.prince` and `versions.queen` in `versions.gradle`
2. Run tests `./gradlew testDebugUnitTest`
3. `./gradlew deployAll`
4. Add a new entry in [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/prince-of-versions/CHANGELOG.md) and [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/queen-of-versions/CHANGELOG.md)

### Bintray publish

Manually go to [Bintray page](https://bintray.com/infinum/android), check that all 8 files are there and publish them if everything is OK.