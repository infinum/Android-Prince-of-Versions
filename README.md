# Prince of Versions

## Table of contents

* [Queen of Version](#queen-of-versions-library)
* [Prince of Versions](#prince-of-versions-library)
* [Contributing](#contributing)
* [License](#license)
* [Credits](#credits)

## Queen of Versions library

Library checks update availability using In-App updates.

#### Getting via central

```groovy
implementation 'co.infinum:queen-of-versions:0.3.3'
```

#### Features

  * Check update availability using **In-App updates**.
  * Integrate with Prince of Versions to determine **required update**.
  * Supports automatic and manual **update type resolution**.
  * Provides functionality to **cancel** check once started.

#### Check out [integration guide](./queen-of-versions/README.md).


## Prince of Versions library

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

We believe that the community can help us improve and build better a product.
Please refer to our [contributing guide](CONTRIBUTING.md) to learn about the types of contributions we accept and the process for submitting them.

To ensure that our community remains respectful and professional, we defined a [code of conduct](CODE_OF_CONDUCT.md) that we expect all contributors to follow.

We appreciate your interest and look forward to your contributions.

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

<p align="center">
  <a href='https://infinum.com'>
    <picture>
        <source srcset="https://assets.infinum.com/brand/logo/static/white.svg" media="(prefers-color-scheme: dark)">
        <img src="https://assets.infinum.com/brand/logo/static/default.svg">
    </picture>
  </a>
</p>
