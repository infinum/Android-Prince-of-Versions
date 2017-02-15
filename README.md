# Prince of Versions

[![CircleCI](https://circleci.com/gh/infinum/Android-Prince-of-Versions.svg?style=svg)](https://circleci.com/gh/infinum/Android-Prince-of-Versions)
[![Download](https://api.bintray.com/packages/infinum/android/prince-of-versions/images/download.svg)](https://bintray.com/infinum/android/prince-of-versions/_latestVersion)

Library checks for updates using configuration from some resource.

## Getting via jcenter

```groovy
compile 'co.infinum:prince-of-versions:latest_version'
```

## Features

  * Load update configuration from **network** resource or from **input stream** resource
  * Accept **custom loader** for loading update configuration resource
  * Use predefined parser for parsing update configuration in **JSON format**
  * Accept **custom parser** for parsing update configuration
  * Make **asynchronous** loading and use **callback** for notifying result
  * Loading and verifying versions happen **outside of UI thread**
  * Use **thread pool** to cap concurrent resource usage.
  * Provide functionality for **canceling** once started verifications

----------

### Default parser and JSON file

If you are using a default parser, version in your application and the JSON file has to follow [Semantic Versioning](http://semver.org/). JSON file has to look like this:

```json
{
	"ios": {
		"minimum_version": "1.2.3",
		"latest_version": {
			"version": "2.4.5",
			"notification_type": "ALWAYS",
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
1. Create new instance of updater associated with application context.

	```java
PrinceOfVersions updater = new PrinceOfVersions(this);
	```
	
2. Create loader factory for loading from network passing resource URL.

	```java
LoaderFactory loaderFactory = new NetworkLoaderFactory("http://pastebin.com/raw/41N8stUD");
	```
	
3. Create concrete callback for result implementing <code>co.infinum.princeofversions.callbacks.UpdaterCallback</code> interface.

	```java
UpdaterCallback callback = new UpdaterCallback() {
		@Override
		public void onNewUpdate(String version, boolean isMandatory, Map<String, String> metadata) {
		}

		@Override
		public void onNoUpdate(Map<String, String> metadata) {
		}

		@Override
		public void onError(@ErrorCode int error) {
		}
};
	```

4. Use updater with previously created loader factory and callback. Call <code>checkForUpdates</code> method to start update check.

	```java
UpdaterResult result = updater.checkForUpdates(loaderFactory, callback);
	```

5. To cancel update check, call <code>cancel</code> method on <code>UpdaterResult</code> object.

#### Writing tests

For testing purposes you can create your own LoaderFactory. For ease of use, StreamLoader object exists in the library. Here is an example of loading a JSON file from raw. 

1. Create new instance of updater associated with application context.

	```java
PrinceOfVersions updater = new PrinceOfVersions(this);
	```
	
2. Create loader factory for creating stream loader by passing new input stream in its constructor.

	```java
LoaderFactory loaderFactory = new LoaderFactory() {
		@Override
	        public UpdateConfigLoader newInstance() {
	              return new StreamLoader(getResources().openRawResource(R.raw.update));
	        }
};
	```
> **Note:**
> Be aware that once used input stream in <code>StreamLoader</code> is read and closed. For that purpose always create new stream in <code>newInstance</code> method of <code>LoaderFactory</code>.

3rd, 4th and 5th step are same as in previous example.

#### Writing tests using minSdk value

All the steps are the same just like writing tests without minSdk values. The only and single difference in writing tests with minSdk values is the PrinceOfVersions object, to be more precise, it's constructor's arguments. 

```
PrinceOfVersions updater = new PrinceOfVersions(context, provider, repository, sdkVersionProvider);
```

Since we've added the support for minSdk values of the device you can mock and customize them when writing tests by using the interface <code>SdkVersionProvider</code>.

When creating a PrinceOfVersions object a few things need to be kept in mind:

* <code>context</code> argument in PrinceOfVersions constructor can be mocked using Mockito library.

* <code>provider</code> argument in PrinceOfVersions constructor can be mocked using Mockito library. 

```
provider = Mockito.mock(VersionVerifierFactory.class);
```

and it's used for creating a new instance of specific <code>VersionVerifier</code> and it has a single method that provides a new instance of <code>VersionVerifier</code> which is used for verifying updates and cancellation of verification.

* <code>repository</code> argument is used for representing repository which persists library data and is also mocked with Mockito library. 

```
repository = Mockito.mock(VersionRepository.class);
```

* And finally, <code>sdkVersionProvider</code> is an abstraction used to fetch <code>Build.Version.SDK_INT</code> value. In order to use <code>sdkVersionProvider</code> in tests you need to create a custom mock class which will accept a mock integer which represents the minSdkValue you wish to use in your test, e.g.

```
public class SdkVersionProviderMock implements SdkVersionProvider {

    private int sdkInt;

    public SdkVersionProviderMock(int sdkInt) {
        this.sdkInt = sdkInt;
    }

    @Override
    public int getSdkInt() {
        return sdkInt;
    }
}
```

### Multiple flavors
If your application has multiple product flavors (e.g. paid/free) you might need more than one JSON configuration file. If that is the case, do not forget to set a different URL for each flavor configuration. 

### Deploying a new version

1. Bump `libraryVersion` in `build.gradle`
2. `./gradlew clean build generatePomFileForMavenPublication bintrayUpload -PbintrayUser=<bintray username> -PbintrayKey=<bintray api key> -PdryRun=false`
3. Add a new entry in the [CHANGELOG](https://github.com/infinum/Android-Prince-of-Versions/blob/master/CHANGELOG.md)

### Contributing

Feedback and code contributions are very much welcome. Just make a pull request with a short description of your changes. By making contributions to this project you give permission for your code to be used under the same [license](https://github.com/infinum/Android-prince-of-versions/blob/dev/LICENCE).
