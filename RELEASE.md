## Release guide

You must know **BINTRAY_USER** and **BINTRAY_API_KEY**.

Bintray API key can be found under Edit Profile -> API Key.

### Deploying a new version

1. Bump `libraryVersion` in `versions.gradle`
2. Run tests `./gradlew testDebugUnitTest`
3. `./gradlew clean build generatePomFileForMavenPublication bintrayUpload -PbintrayUser=<bintray username> -PbintrayKey=<bintray api key> -PdryRun=false`
4. Add a new entry in the [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/CHANGELOG.md)

### Bintray publish

Manually go to [Bintray page](https://bintray.com/infinum/android), check that all 8 files are there and publish them if everything is OK.