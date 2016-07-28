Prince of versions
=================
[![CircleCI](https://circleci.com/gh/infinum/Android-prince-of-versions/tree/master.svg?style=svg&circle-token=cb8ad23c030527474dd91da95b8f1f3b56fa0022)](https://circleci.com/gh/infinum/Android-prince-of-versions/tree/master)

Library checks for updates using configuration from some resource.

Features
--------
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

If you are using a default parser, version in JSON file has to follow [Semantic Versioning](http://semver.org/) and the file has to look like this:

```json
{
	ios: {
		minimum_version: "1.2.3",
		optional_update: {
			version: "2.4.5",
			notification_type: "ALWAYS"
		}
	},
	android: {
		minimum_version: "1.2.3",
		minimum_version_code: 14235,
		optional_update: {
			version: "2.4.5",
			version_code: 42354,
			notification_type: "ONCE"
		}
	}
}
```

Depending on <code>notification_type</code> property, the user can be notified <code>ONCE</code> or <code>ALWAYS</code>. The library handles this for you, and if notification type is set to <code>ONCE</code>, it will notify you via <code>onNewUpdate(String version, boolean isMandatory)</code> method only once. Every other time the library will return <code>onNoUpdate</code> for that specific version.


Examples
-------------
Full example application is available [here](ExampleApp).

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
	        public void onNewUpdate(String version, boolean isMandatory) {
	        }
	
	        @Override
	        public void onNoUpdate() {
	        }
	
	        @Override
	        public void onError(@ErrorCode int error) {
	        }
	    };
	```

4. Use updater with previously created loader factory and callback. Call <code>checkForUpdates</code> method to start update check.
	```java
	  CheckForUpdates context = updater.checkForUpdates(loaderFactory, callback);
	```

5. Optionally, to cancel update check, call <code>cancel</code> method on calling context provided by <code>checkForUpdates</code> method.


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
