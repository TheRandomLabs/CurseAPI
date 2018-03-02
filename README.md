# CurseAPI
A WIP Java API to handle interactions with CurseForge.
Example Gradle buildscript:

	apply plugin: "java"

	repositories {
		jcenter()
		maven {
			url "https://jitpack.io"
		}
	}

	dependencies {
		compile "com.github.TheRandomLabs:CurseAPI:master-SNAPSHOT"
	}

	task sourcesJar(type: Jar, dependsOn: classes) {
		classifier = "sources"
		from sourceSets.main.allSource
	}

	artifacts {
		archives sourcesJar
	}

	jar {
		manifest {
			attributes "Main-Class": "com.example.MainClass"
		}

		from {
			configurations.compile.collect {
				it.isDirectory() ? it : zipTree(it)
			}
		}
	}

The CurseForge class is for basic methods, mainly to do with URLs.
Use CurseProject instead whereever possible.
Note: CurseAPI refers to CurseForge for authors as just CurseForge, and the main CurseForge site as
"Main CurseForge".

Features:
* Get a project from a project ID
* Get a project from a URL
* Get project details, including name, description, license, etc.
* Get file details, including name, game versions, file URL, etc.
* Supports all CurseForge sites (Minecraft, World of Warcraft, etc.)
* Basic event handling

Planned features:
* More project and file details
* Better error handling. At the moment, there's only one CurseException and CurseAPI doesn't retry most requests
* Documentation and a wiki

Example:

	getLogger().info(CurseProject.fromSlug("minecraft", "endercore").dependents(RelationType.REQUIRED_LIBRARY));

The above code should print a list of mods that require EnderCore.
