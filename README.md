# Prince of Versions

[![CircleCI](https://circleci.com/gh/infinum/Android-Prince-of-Versions.svg?style=svg)](https://circleci.com/gh/infinum/Android-Prince-of-Versions)
[![Download](https://api.bintray.com/packages/infinum/android/prince-of-versions/images/download.svg)](https://bintray.com/infinum/android/prince-of-versions/_latestVersion)

Library checks for updates using configuration from remote or local resource.

## Getting via jcenter

```groovy
implementation 'co.infinum:prince-of-versions:latest_version'
```

## Features

  * Load update configuration from **network** resource or from generic **stream** resource.
  * Accepts **custom loader** for loading update configuration resource.
  * Use predefined parser for parsing update configuration in **JSON format**.
  * Accept **custom parser** for parsing update configuration.
  * Make **asynchronous** update check and use **callback** for notifying result.
  * Supports **synchronous** update check.
  * Loading and verifying versions happens **outside of the UI thread**.
  * Use **thread pool** to cap concurrent resource usage.
  * Provides functionality to **cancel** verification once started.


### Default parser and JSON file

If you use default parsers, version in your application and the JSON file has to follow [Semantic Versioning](http://semver.org/). JSON file has to look like this:

```json
{
	"ios": {
		"minimum_version": "1.2.3",
		"latest_version": {
			"version": "2.4.5",
			"notification_type": "ALWAYS"
		}
	},
	"android": {
		"minimum_version": "1.2.3",
		"minimum_version_min_sdk": 15,
		"latest_version": {
			"version": "2.4.5",
			"notification_type": "ONCE",
			"min_sdk":18
		}
	},
	"meta": {
		"key1": "value1",
		"key2": "value2"
	}
}
```

Depending on <code>notification_type</code> property, the user can be notified <code>ONCE</code> or <code>ALWAYS</code>. The library handles this for you, and if notification type is set to <code>ONCE</code>, it will notify you via <code>onNewUpdate(String version, boolean isMandatory)</code> method only once. Every other time the library will return <code>onNoUpdate</code> for that specific version. 
Key-value pairs under <code>"meta"</code> key are optional metadata of which any amount can be sent accompanying the required fields.

The library supports min sdk version. If defined, it will show a new update only if user's device is supported.

<code>minumum_version_min_sdk</code> represents the minSdk value of the minimum supported version of the application. <code>min_sdk</code> represents minSdk value of the latest version of the application.
Fields <code>minimum_version_min_sdk</code> and <code>min_sdk</code> are optional fields thus not including them makes no difference to the library implementation whatsoever.


## Examples

Full example application is available [here](https://github.com/infinum/Android-Prince-of-Versions/tree/dev/ExampleApp).

#### Most common usage - loading from network resource
1. Create new instance of the updater associated with application context.

```java
PrinceOfVersions updater = new PrinceOfVersions(this);
```
	
2. Create loader factory for loading from network passing resource URL.

```java
Loader loader = new NetworkLoader("http://pastebin.com/raw/41N8stUD");
```
	
3. Create concrete callback to get the update check results by implementing <code>co.infinum.princeofversions.callbacks.UpdaterCallback</code> interface.

```java
UpdaterCallback callback = new UpdaterCallback() {
		@Override
		public void onNewUpdate(String version, boolean isMandatory, Map<String, String> metadata) {
		}

		@Override
		public void onNoUpdate(Map<String, String> metadata) {
		}

		@Override
		public void onError(Throwable throwable) {
		}
};
```

4. Use updater with previously created loader and callback. Call <code>checkForUpdates</code> method to start update check.

```java
PrinceOfVersionsCancelable cancelable = updater.checkForUpdates(loaderFactory, callback);
```

5. To cancel update check, call <code>cancel</code> method on <code>PrinceOfVersionsCancelable</code> object.

#### UpdaterCall api

In version 2.0.0 we introduced a new UpdaterCall api.

1. Create new instance of the updater associated with application context.

```java
PrinceOfVersions updater = new PrinceOfVersions(this);
```

2. (Optional) Create loader factory for loading from network passing resource URL. Otherwise a default implementation will be used.

```java
Loader loader = new NetworkLoader("http://pastebin.com/raw/41N8stUD");
```

3. Create a new <code>PrinceOfVersionsCall</code> instance.

```java
PrinceOfVersionsCall call = updater.newCall("http://pastebin.com/raw/41N8stUD");

# If you previously created a Loader instace, you can pass it to the PrinceOfVersionsCall here.
PrinceOfVersionsCall call = updater.newCall(loader);
```

4. If you want to use call in an asynchronous manner:
4. 1. Create concrete callback to get the update check results by implementing <code>co.infinum.princeofversions.callbacks.UpdaterCallback</code> interface.

```java
UpdaterCallback callback = new UpdaterCallback() {
    @Override
    public void onNewUpdate(String version, boolean isMandatory, Map<String, String> metadata) {
    }

    @Override
    public void onNoUpdate(Map<String, String> metadata) {
    }

    @Override
    public void onError(Throwable throwable) {
    }
};
```

4. 2. Enqueue the call to be executed asynchronously, the result will be returned to your callback.

```java
call.enqueue(callback);
```

5. If you want to use the call in a synchronous manner call the <code>execute</code>. It returns a <code>Result</code> object containing the version check results.

```java
Result result = call.execute();
```

6. To cancel update check, call <code>cancel</code> method on <code>PrinceOfVersionsCancelable</code> object.

7. Be aware that once a call has been executed it cannot be reused, you must use a new instance.

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

#### Writing tests using mocked application version and min sdk number

All the steps are the same just like writing tests without minSdk values. In test environment you can provide application's version and min sdk value when creating instance of <code>PrinceOfVersion</code>. If you also mock storage, you can use build method without <code>Context</code> argument.

```java
PrinceOfVersions princeOfVersions = new PrinceOfVersions.Builder()
                .withStorage(new MockStorage())
                .withAppConfig(new MockApplicationConfiguration("2.3.4", 16))
                .build();
```

If you write tests with asynchronous version of update check included, you probably want all PrinceOfVersion's work to be executed on main test thread. You can do that by providing <code>Executor</code> instance to <code>checkForUpdate</code> method. Library includes <code>PrinceOfVersionsDefaultExecutor</code> class for executing update check on separate thread and <code>SingleThreadExecutor</code> for executing update check immediately.
```java
princeOfVersions.checkForUpdates(executor, loader, callback);
```

### Multiple flavors
If your application has multiple product flavors (e.g. paid/free) you might need more than one JSON configuration file. If that is the case, do not forget to set a different URL for each flavor configuration. 

### Deploying a new version

1. Bump `libraryVersion` in `build.gradle`
2. `./gradlew clean build generatePomFileForMavenPublication bintrayUpload -PbintrayUser=<bintray username> -PbintrayKey=<bintray api key> -PdryRun=false`
3. Add a new entry in the [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/CHANGELOG.md)

### Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](https://github.com/infinum/Android-prince-of-versions/blob/dev/LICENCE).
