# CurseAPI

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

[![Build](https://jitci.com/gh/TheRandomLabs/CurseAPI/svg)](https://jitci.com/gh/TheRandomLabs/CurseAPI)
[![Dependabot](https://flat.badgen.net/dependabot/jqno/equalsverifier?icon=dependabot)](https://dependabot.com/)
[![SemVer stability](https://api.dependabot.com/badges/compatibility_score?dependency-name=nl.jqno.equalsverifier:equalsverifier&package-manager=maven&version-scheme=semver)](https://dependabot.com/compatibility-score/?dependency-name=nl.jqno.equalsverifier:equalsverifier&package-manager=maven&version-scheme=semver)

[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/TheRandomLabs/CurseAPI.svg)](http://isitmaintained.com/project/TheRandomLabs/CurseAPI "Average time to resolve an issue")

<!-- [![Maven Central](https://img.shields.io/maven-central/v/com.therandomlabs.curseapi/curseapi.svg?style=shield)](https://maven-badges.herokuapp.com/maven-central/com.therandomlabs.curseapi/curseapi/)

[comment]: # [![Javadoc](https://javadoc.io/badge/com.therandomlabs.curseapi/curseapi.svg?color=blue)](https://javadoc.io/doc/com.therandomlabs.curseapi/curseapi)-->

A Java library for handling interactions with CurseForge.

All public-facing code is documented with Javadoc and (mostly) tested with JUnit.

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
	new CurseFileFilter().gameVersionStrings("1.12.2").apply(files);
	//Or:
	//files.filter(new CurseFileFilter().gameVersionStrings("1.12.2"));
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

CurseAPI uses SLF4J to log warnings, errors and information messages.
