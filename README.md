# CurseAPI

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Build](https://github.com/TheRandomLabs/CurseAPI/workflows/build/badge.svg)

A Java library for handling interactions with CurseForge.

All code will soon be documented with Javadoc and tested with JUnit.

## Usage

The methods found in `CurseAPI` can be used to retrieve information about projects and files.
* `CurseAPI#project(int)` can be used to retrieve a `CurseProject` instance for a project ID.
This `CurseProject` instance can then be used to retrieve information about a CurseForge project.
* `CurseAPI#files(int)` and `CurseProject#files` can be used to retrieve a `CurseFiles` instance
containing all files for a project. `CurseFiles` extends `TreeSet` and contains a few utility
methods for `CurseFile`s.
* `CurseAPI#file(int, int)` can be used to retrieve a `CurseFile` instance for a project and
file ID. `CurseFiles#fileWithID(int)` can be used instead if a `CurseFiles` instance is already
available.
* `CurseAPI#fileDownloadURL(int, int)` can be used to retrieve a download URL for a project and
file ID. If a `CurseFile` is already available, `CurseFile#downloadURL()` can be used instead.
* `CurseFiles` instances can be filtered using `CurseFileFilter`s:
```java
final Optional<CurseFiles> optionalFiles = CurseAPI.files(285612);

if (optionalFiles.isPresent()) {
	final CurseFiles files = optionalFiles.get();
	files.filter(new CurseFileFilter().gameVersions("1.12.2"));
	logger.info("Latest 1.12.2 file: {}", files.first());
}
```
* In general, `null` values are not returned. Methods in the `CurseAPI` class return `Optional`s.

## Using with Gradle

CurseAPI uses SLF4J to log warnings, errors and information messages.
Put the following in your buildscript to enable SLF4J with Log4j 2:

```groovy

repositories {
	mavenCentral()
}

dependencies {
	implementation "org.slf4j:slf4j-api:2.0.0-alpha1"
	implementation "org.apache.logging.log4j:log4j-core:2.12.1"
	implementation "org.apache.logging.log4j:log4j-slf4j18-impl:2.12.1"
}
```
