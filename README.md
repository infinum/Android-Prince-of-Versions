# Prince of Versions

## Queen of Versions  [![Download](https://api.bintray.com/packages/infinum/android/queen-of-versions/images/download.svg)](https://bintray.com/infinum/android/queen-of-versions/_latestVersion)

Library checks update availability using In-App updates.

#### Getting via jcenter

```groovy
implementation 'co.infinum:queen-of-versions:0.3.0'
```

#### Features

  * Check update availability using **In-App updates**.
  * Integrate with Prince of Versions to determine **required update**.
  * Supports automatic and manual **update type resolution**.
  * Provides functionality to **cancel** check once started.

#### Check out [integration guide](./queen-of-versions/README.md).


## Prince of Versions  [![Download](https://api.bintray.com/packages/infinum/android/prince-of-versions/images/download.svg)](https://bintray.com/infinum/android/prince-of-versions/_latestVersion)

Library checks for updates using configuration from remote or local resource.

#### Getting via jcenter

```groovy
implementation 'co.infinum:prince-of-versions:4.0.3'
```

#### Features

  * Load update configuration from **network** resource or from generic **stream** resource.
  * Accepts **custom loader** for loading update configuration resource.
  * Use predefined parser for parsing update configuration in **JSON format**.
  * Accept **custom parser** for parsing update configuration.
  * Make **asynchronous** update check and use **callback** for notifying result.
  * Supports **synchronous** update check.
  * Loading and verifying versions happens **outside of the UI thread**.
  * Use **thread pool** to cap concurrent resource usage.
  * Provides functionality to **cancel** verification once started.

#### Check out [integration guide](./prince-of-versions/README.md).


## Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](./LICENCE).
