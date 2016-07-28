Android-Updater
=================
[![Circle CI](https://circleci.com/gh/infinum/Android-Updater.svg?style=svg&circle-token=cb8ad23c030527474dd91da95b8f1f3b56fa0022)](https://circleci.com/gh/infinum/Android-Updater)

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
	  CheckForUpdatesCallingContext context = updater.checkForUpdates(loaderFactory, callback);
	```

5. Optionally, to cancel update check, call <code>cancel</code> method on calling context provided by <code>checkForUpdates</code> method.


#### Loading from stream
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
