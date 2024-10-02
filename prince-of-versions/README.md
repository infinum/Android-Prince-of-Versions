# Prince of Versions

![Bitrise](https://app.bitrise.io/app/5bc3f35c9c5f5b61/status.svg?token=XGiXvE7Yu6DXdL9wrEqiHQ&branch=dev)
[![Download](https://maven-badges.herokuapp.com/maven-central/co.infinum/prince-of-versions/badge.png)](https://mvnrepository.com/artifact/co.infinum/prince-of-versions/latest)

## Description

Library checks for updates using configuration from remote or local resource.

## Table of contents

* [Requirements](#requirements)
* [Getting started](#getting-started)
* [Features](#features)
* [Examples](#examples)
* [Contributing](#contributing)

## Requirements

Minimum required API level to use Prince of Versions is 15

## Getting started

Library can be added from Maven Central with following dependency:

```groovy
implementation 'co.infinum:prince-of-versions:4.0.4'
```

## Features

  * Load update configuration from **network** resource or from generic **stream** resource.
  * Accepts **custom loader** for loading update configuration resource.
  * Use predefined parser for parsing update configuration in **JSON format**.
  * Accept **custom parser** for parsing update configuration.
  * Accept **custom requirements** for different updates.
  * Make **asynchronous** update check and use **callback** for notifying result.
  * Supports **synchronous** update check.
  * Loading and verifying versions happens **outside of the UI thread**.
  * Use **thread pool** to cap concurrent resource usage.
  * Provides functionality to **cancel** verification once started.


### Default parser and JSON file

JSON configuration should follow a structure as follows:

```json
{
	"android": [{
		"required_version": 10,
		"last_version_available":12,
		"notify_last_version_frequency":"ONCE",
		"requirements":{
		   "required_os_version":18
		},
		"meta":{
		   "key1":"value3"
		}
	},{
		"required_version": 10,
		"last_version_available":13,
		"notify_last_version_frequency":"ONCE",
		"requirements":{
		   "required_os_version":19
		},
		"meta":{
		   "key2":"value4"
		}
	}],
	"meta": {
		"key1": "value1",
		"key2": "value2"
	}
}
```
The most important part of the configuration for Android applications is  `android`  object. All properties in the object are optional, but at least one of `required_version` and `last_version_available`  should be provided.

As it is shown, the  `android` property contains an array of configurations, the library parses them one by one and selects the **first acceptable** configuration by **validating `requirements`**. If there is no `requirements` field, first element is selected, otherwise, it just means the first configuration that satisfies all the conditions.
If there is only one configuration array can be omitted and `android` can have the value of configuration object.

Property `required_version` specifies the **required version of application**, eg. if application has version lower than `required_version` - required update will be notified. Semantic of required update is that application has to be updated before any further use. Because of that, if required update exists it will be notified on each update check.

Property `last_version_available` defines a version of the **latest optional update**. If application has version lower that `last_version_available` - optional update will be notified.

Depending on  **`notify_last_version_frequency`**  property, application can be notified `ONCE` or `ALWAYS`. If notification type is set to  `ONCE`, it will notify you only first time for a specific version. In every following check the library would notify `onNoUpdate` for that specific version. This setting applies only for optional update and has no effect in case of required update. Default value for this property is `ONCE`.

`requirements` propery contains an object of different **update requirements**. Default requirement supported by the library is `required_os_version`. `required_os_version`  represents the minimum Android version a device has to support to be able to update to last available version. Eg. If `required_os_version` is set to  `18`, a device has to have at least Android version 18 or above to be able to receive an update. Apart from default `required_os_version`, `requirements` can contain a variety of different and customizable requirements specific for each update, but in that case developer has to take care of that by setting `RequirementsChecker`.

**Metadata** from the selected configuration has an advantage over the default metadata (in root), and when there is a metadata conflict, its resolved by overriding by value from the selected configuration. E.g. if the first update gets selected then the final metadata will have these keys: `key1: value3` and `key2: value2`. In case when there is no conflict of keys in metadata, then default metadata will be merged with selected configuration metadata.

If there is a need to **support both old** (before Prince of Versions `4.0.0`) **and new** (Prince of Versions `4.0.0` and above) **configuration**, for the new configuration  `android2`  name can be used instead of  `android`.

## Examples

Full example application is available [here](https://github.com/infinum/Android-Prince-of-Versions/tree/dev/ExampleApp).

#### Most common usage - loading from network resource
1. Create a new instance of updater associated with an application context.
```java
PrinceOfVersions updater = new PrinceOfVersions(this);
```

2. Create a loader factory for loading from the network passing resource URL.
```java
Loader loader = new NetworkLoader("...url with configuration...");
```

3. Create a concrete callback to get the update check results by implementing <code>UpdaterCallback</code> interface.
```java
UpdaterCallback callback = new UpdaterCallback() {
    @Override
    public void onSuccess(UpdateResult result) {
    }

    @Override
    public void onError(Throwable throwable) {
    }
};
```

4. Use the updater with previously created loader and callback. Call <code>checkForUpdates</code> method to start asynchronous update check.
```java
PrinceOfVersionsCancelable cancelable = updater.checkForUpdates(loader, callback);
```

5. To cancel the update check, call <code>cancel</code> method available in <code>PrinceOfVersionsCancelable</code> object.

#### UpdaterCall api
In version `3.0.0` a new UpdaterCall API has been introduced.

1. Create a new <code>PrinceOfVersionsCall</code> instance.
```java
PrinceOfVersionsCall call = updater.newCall("...url with configuration...");

// If you have previously created a Loader instance, you can use it to create a PrinceOfVersionsCall instance.
PrinceOfVersionsCall call = updater.newCall(loader);
```

2. If you want to use call in an asynchronous manner - enqueue the call to be executed asynchronously, the result will be returned to your callback on the main thread.
```java
call.enqueue(new UpdaterCallback() {
    @Override
    public void onSuccess(UpdateResult result) {
    }
    
    @Override
    public void onError(Throwable throwable) {
    }
});
```

3. If you want to use the call in a synchronous manner call the <code>execute</code> method. It returns a <code>UpdateResult</code> object containing the version check results.
```java
try {
    UpdateResult result = call.execute();
    // result.getStatus() returns REQUIRED_UPDATE_NEEDED, NEW_UPDATE_AVAILABLE or NO_UPDATE_AVAILABLE
// result.getInfo() returns update info - the information extracted from the configuration and used to check if the update exists
    // result.getMetadata() returns metadata about the update
} catch (Throwable throwable) {
    // handle error
}
```

4. To cancel the update check, call <code>cancel</code> method in <code>PrinceOfVersionsCall</code> object.

5. Be aware that once a call has been executed it cannot be reused, you must create a new instance.

#### Writing tests

For testing purposes you can create your own Loader instance. For ease of use, StreamLoader object exists in the library. Here is an example of loading a JSON file from raw resource.

1. Create new instance of updater associated with application context.
```java
PrinceOfVersions updater = new PrinceOfVersions(this);
```

2. Create loader factory for creating stream loader by passing new input stream in its constructor.
```java
Loader loader = new StreamLoader(getResources().openRawResource(R.raw.update))
```

> **Note:**
> Be aware that once used input stream in <code>StreamLoader</code> is read and closed. For that purpose always create new stream for every update check.

3rd, 4th and 5th step are same as in previous example.

If you write tests with asynchronous version of update check included, you probably want all PrinceOfVersion's work to be executed on main test thread. You can do that by providing <code>Executor</code> instance in update check method. Library includes <code>PrinceOfVersionsDefaultExecutor</code> class for executing update check on separate thread and <code>SingleThreadExecutor</code> for executing update check immediately.
```java
princeOfVersions.checkForUpdates(executor, loader, callback);
```

### Multiple flavors
If your application has multiple product flavors (e.g. paid/free) you might need more than one JSON configuration file. If that is the case, do not forget to set a different URL for each flavor configuration.


### R8 / ProGuard

If you are using R8 or ProGuard add the options from
[this file](./prince-of-versions.pro).


## Contributing

We believe that the community can help us improve and build better a product.
Please refer to our [contributing guide](../CONTRIBUTING.md) to learn about the types of contributions we accept and the process for submitting them.

To ensure that our community remains respectful and professional, we defined a [code of conduct](../CODE_OF_CONDUCT.md) that we expect all contributors to follow.

We appreciate your interest and look forward to your contributions.