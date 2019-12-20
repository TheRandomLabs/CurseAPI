# CurseAPI

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build](https://jitci.com/gh/TheRandomLabs/CurseAPI/svg)](https://jitci.com/gh/TheRandomLabs/CurseAPI)

A Java library for handling interactions with CurseForge.

All code is documented with Javadoc and tested with JUnit.

## Usage

The methods found in `CurseAPI` can be used to retrieve information about projects and files.
* `CurseAPI#project(int)` can be used to retrieve a `CurseProject` instance for a project ID.
This `CurseProject` instance can then be used to retrieve information about a CurseForge project.
* `CurseAPI#searchProjects(CurseSearchQuery)` can be used with a `CurseSearchQuery` instance to
search for CurseForge projects.
* `CurseAPI#files(int)` and `CurseProject#files` can be used to retrieve a `CurseFiles` instance
containing all files for a project. `CurseFiles` extends `TreeSet` and contains a few utility
methods for `CurseFile`s.
* `CurseAPI#file(int, int)` can be used to retrieve a `CurseFile` instance for a project and
file ID. `CurseFiles#fileWithID(int)` can be used instead if a `CurseFiles` instance is already
available.
* `CurseAPI#fileDownloadURL(int, int)` can be used to retrieve a download URL for a project and
file ID. If a `CurseFile` is already available, `CurseFile#downloadURL()` can be used instead.
* `CurseAPI#downloadFile(int, int, Path)` and `CurseAPI#downloadFileToDirectory(int, int, Path)` can
be used to download a file with a specific project and file ID. If a `CurseFile` is already
available, `CurseFile#download(Path)` and `CurseFile#downloadToDirectory(Path)` can be used instead.
* `CurseFiles` instances can be filtered using `CurseFileFilter`s:
```java
final Optional<CurseFiles<CurseFile>> optionalFiles = CurseAPI.files(285612);

if (optionalFiles.isPresent()) {
	final CurseFiles<CurseFile> files = optionalFiles.get();
	files.filter(new CurseFileFilter().gameVersions("1.12.2"));
	logger.info("Latest 1.12.2 file: {}", files.first());
}
```
* `CurseAPI#games()` can be used to retrieve a `Set` containing `CurseGame` instances that
represent all supported games on CurseForge. `CurseAPI#streamGames()` can be used to stream these
games.
* `CurseAPI#game(int)` can be used to retrieve a `CurseGame` instance that represents the CurseForge
game with a specific ID.
* If an extension such as [CurseAPI-Minecraft](https://github.com/TheRandomLabs/CurseAPI-Minecraft)
is installed, `CurseAPI#gameVersions(int)` can be used to retrieve `CurseGameVersion` instances
representing versions of the specified game supported by CurseForge.
* `CurseAPI#categories()` and `CurseAPI#categories(int)` can be used to retrieve a `Set` of
`CurseCategory` instances representing CurseForge project categories. `CurseAPI#streamCategories()`
and `CurseAPI#streamCategories(int)` can be used to retrieve a `Stream` for these `Set`s.
* `CurseAPI#category(int)` can be used to retrieve a `CurseCategory` instance representing the
CurseForge project category with the specified ID.
* In general, `null` values are not returned. Methods in the `CurseAPI` class return `Optional`s.

## Using with Gradle

CurseAPI can be found on [Jitpack](https://jitpack.io/):

```groovy
repositories {
	mavenCentral()

	maven {
		url "https://jitpack.io"
	}
}

dependencies {
	api "com.github.TheRandomLabs:CurseAPI:master-SNAPSHOT"
}
```

CurseAPI uses SLF4J to log warnings, errors and information messages. To enable SLF4J with Log4j 2:

```groovy
dependencies {
	implementation "org.slf4j:slf4j-api:2.0.0-alpha1"
	implementation "org.apache.logging.log4j:log4j-core:2.12.1"
	implementation "org.apache.logging.log4j:log4j-slf4j18-impl:2.12.1"
}
```
