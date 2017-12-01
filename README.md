# CurseAPI
A Java API to handle interactions with Curse and Minecraft modpacks (mainly CurseForge).
Example Gradle buildscript:

	apply plugin: "java"

	repositories {
		jcenter()
		maven {
			url "https://jitpack.io"
		}
	}

	dependencies {
		compile "com.github.jhy:jsoup:master-SNAPSHOT"
		compile "com.github.google:gson:master-SNAPSHOT"
		compile "com.github.TheRandomLabs:TRLUtils:master-SNAPSHOT"
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

Documentation is not complete yet, but a lot of the methods should be self-explanatory.
The CurseForge class is for basic methods, mainly to do with URLs.
Use CurseProject instead where possible.

Features:
* Getting a project from a project ID
* Getting a project from a URL
* Getting project details, including name, description, license, etc.
* Getting file details, including name, game versions, file URL, etc.
* Supports all CurseForge sites (including Minecraft, World of Warcraft, etc.) (NOTE: CurseAPI does not fully support the new CurseForge because there is no reason to at the moment)
* Basic event handling
* And more.

Planned features:
* More Minecraft support (esp. modpacks)
* More project and file details

Example:

	getLogger().info(CurseProject.fromPath("minecraft", "endercore").dependents(RelationType.REQUIRED_LIBRARY));

The above code should print a list of mods that require EnderCore.
