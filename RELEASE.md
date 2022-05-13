## Release guide

### Deploying a new version

Caution: Make sure you have followed the [preparations](https://infinum.com/handbook/books/android/library-development/preparations) steps in the Android Handbook.

1. Bump `versions.prince` and `versions.queen` in `versions.gradle`
2. Run tests `./gradlew testDebugUnitTest`
3. `./gradlew deploy`
4. Follow the steps in the Android Handbook in regards to [publishing](https://infinum.com/handbook/books/android/library-development/publishing), specifically the part with Nexus staging repositories.
5. After making sure all of the files are present & finishing the publishing, add a new entry in [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/prince-of-versions/CHANGELOG.md) and [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/queen-of-versions/CHANGELOG.md)