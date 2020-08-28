# Changelog

## Version 4.0.2

_2020-08-28_

- integrate default requirements checker when using constructor
    Default requirements checkers that PrinceOfVersions supports are from now on integrated even when using constructor
    to create instance of the PrinceOfVersions class.

## Version 4.0.1

_2020-06-05_

- fix resolution of missing custom requirements checker
    Any requirement specified in the configuration is from now on required to be satisfied to be able to use that update.
    If application doesn't support that requirement, update won't be considered valid for that device and it will be skipped.

## Version 4.0.0

_2020-03-29_

- new configuration syntax
- update check is done by comparing version codes
- support for custom requirements
- merge metadata and collect complex object into it

## Version 3.0.0

_2018-11-07_

- instead of error code, `onError` method in callback accepts `Throwable`.
- add support for synchronous update check
- add call mechanism - from now it is possible to create a call instance in advance and `enqueue` or `execute` it later on

_See the [Migration guide](https://github.com/infinum/Android-Prince-of-Versions/wiki/Migration-guide)._

## Version 2.1.0

_2017-02-14_

- library now supports declaring minSdk in the configuration file

## Version 2.0.3

_2017-01-02_

- Allow not specifying minimum version
- Ignore non-string metadata

## Version 2.0.2

_2016-11-17_

- Fix updating logic when update is available and notification is equal to `ALWAYS`.

## Version 2.0.1

_2016-11-08_

- Fixed potential memory leak, cleaned up libraries manifest and resources

## Version 2.0.0

_2016-10-26_

- Renamed `optional_update` to `latest_version` for clarity

## Version 1.0.1

_2016-09-19_

- Fixed an issue where the lib would not show the update if there is both an optional and mandatory update

## Version 1.0.0

_2016-09-09_

- initial release
