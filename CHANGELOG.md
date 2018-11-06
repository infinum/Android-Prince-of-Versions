# Changelog

## Version 3.0.0

_2018-11-07_

- instead of error code, `onError` method in callback accepts `Throwable`.
- add support for synchronous update check
- add call mechanism - from now it is possible to create a call instance in advance and `enqueue` or `execute` it later on

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
