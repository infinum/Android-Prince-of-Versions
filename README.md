# Prince of Versions

## Queen of Versions

Library checks update availability using In-App updates.

#### Getting via central

```groovy
implementation 'co.infinum:queen-of-versions:0.3.2'
```

#### Features

  * Check update availability using **In-App updates**.
  * Integrate with Prince of Versions to determine **required update**.
  * Supports automatic and manual **update type resolution**.
  * Provides functionality to **cancel** check once started.

#### Check out [integration guide](./queen-of-versions/README.md).


## Prince of Versions

Library checks for updates using configuration from remote or local resource.

#### Getting via central

```groovy
implementation 'co.infinum:prince-of-versions:4.0.4'
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

## License

```
Copyright 2021 Infinum

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Credits

Maintained and sponsored by [Infinum](http://www.infinum.com).

<a href='https://infinum.com'>
  <img src='https://infinum.com/infinum.png' href='https://infinum.com' width='264'>
</a>
