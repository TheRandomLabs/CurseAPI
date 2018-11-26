# CurseAPI

A WIP Java API that handles interactions with Curse.
Example Gradle buildscript:

	apply plugin: "java"

	repositories {
		jcenter()
		maven {
			url "https://jitpack.io"
		}
	}

	dependencies {
		compile "com.github.TheRandomLabs:CurseAPI:-SNAPSHOT"
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

Because CurseAPI is still WIP, things will change a lot, so don't actually use `-SNAPSHOT`.
Instead, choose a specific version at [Jitpack](https://jitpack.io/#TheRandomLabs/CurseAPI).

The CurseForge class is for basic methods, mainly to do with URLs.
Use CurseProject instead wherever possible.

**NOTE**: CurseAPI refers to CurseForge for authors as just CurseForge, and the main CurseForge site
as "Main CurseForge".

Features:
* Get a project from a project ID
* Get a project from a URL
* Get project details, including name, description, license, relations, etc.
* Get file details, including name, game versions, file URL, dependencies
(which works recursively), etc.
* Supports all CurseForge sites (Minecraft, World of Warcraft, etc.)
* Supports retrieving data from CurseForge HTML, CurseMeta and the Curse widget API
* Basic event handling

CurseAPI does not use the Curse widget API by default as it does not provide as much information
as the other sources and is currently not being updated.

CurseAPI can choose whether to retrieve information from CurseForge or CurseMeta depending on
context, and as a result, is able to provide more accurate information than either.

TODO:
* More project, file and member data
* Better category support

Dependent example:

	getLogger().info(CurseProject.fromSlug("minecraft", "endercore").dependents(RelationType.REQUIRED_LIBRARY));

The above code should print a list of mods that require EnderCore.

Project information example:

	getLogger().disableDebug();

	final CurseProject project = CurseProject.fromSlug("minecraft", "randomtweaks");

	getLogger().info("ID: " + project.id());
	getLogger().info("URL: " + project.url());
	getLogger().info("Main CurseForge URL: " + project.mainCurseForgeURL());
	getLogger().info("Title: " + project.title());
	getLogger().info("Type: " + project.type().fullSingularName());
	getLogger().info("Avatar: " + project.avatarURL());
	getLogger().info("Thumbnail URL: " + project.thumbnailURL());
	getLogger().info("Members: " + project.members());
	getLogger().info("Downloads: " + project.downloads());
	getLogger().info("Creation time: " + project.creationTime());
	getLogger().info("Last update time: " + project.lastUpdateTime());
	getLogger().info("Donate URL: " + project.donateURL());
	getLogger().info("License name: " + project.licenseName());
	getLogger().info("License text: " + project.licenseText());
	getLogger().info("Short description: " + project.shortDescription());
	getLogger().info("Categories: " + project.categories());
	getLogger().info("Files: " + project.files());
	getLogger().info("Random dependent: " + RandomUtils.randomElement(project.dependents()));

The above code at the time of writing prints:

	[INFO] ID: 258205
	[INFO] URL: https://minecraft.curseforge.com/projects/randomtweaks
	[INFO] Main CurseForge URL: https://www.curseforge.com/minecraft/mc-mods/randomtweaks
	[INFO] Title: RandomTweaks
	[INFO] Type: Minecraft Mod
	[INFO] Avatar: https://media.forgecdn.net/avatars/86/874/636211726646572646.png
	[INFO] Thumbnail URL: https://media.forgecdn.net/avatars/thumbnails/86/874/62/62/636211726646572646.png
	[INFO] Members: [Member[type="Owner",username="TheRandomLabs"]]
	[INFO] Downloads: 272472
	[INFO] Creation time: 2017-01-28T09:57:02Z
	[INFO] Last update time: 2018-05-26T00:57:07Z
	[INFO] Donate URL: null
	[INFO] License name: Custom License
	[INFO] License text: Copyright (c) 2018 TheRandomLabs

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

	[INFO] Short description: A bunch of miscellaneous vanilla-compatible tweaks for Minecraft, such as sound system reloading and a time of day overlay.
	[INFO] Categories: [[name="API and Library",url="https://minecraft.curseforge.com/mc-mods/library-api",thumbnailURL="https://media.forgecdn.net/avatars/thumbnails/6/36/32/32/635351496947765531.png"], [name="Cosmetic",url="https://minecraft.curseforge.com/mc-mods/cosmetic",thumbnailURL="https://media.forgecdn.net/avatars/thumbnails/6/39/32/32/635351497555976928.png"], [name="Map and Information",url="https://minecraft.curseforge.com/mc-mods/map-information",thumbnailURL="https://media.forgecdn.net/avatars/thumbnails/6/38/32/32/635351497437388438.png"], [name="Miscellaneous",url="https://minecraft.curseforge.com/mc-mods/mc-miscellaneous",thumbnailURL="https://media.forgecdn.net/avatars/thumbnails/6/40/32/32/635351497693711265.png"], [name="Server Utility",url="https://minecraft.curseforge.com/mc-mods/server-utility",thumbnailURL="https://media.forgecdn.net/avatars/thumbnails/6/48/32/32/635351498950580836.png"]]
	[INFO] Files: [[id=2565899,name="RandomTweaks 1.12.2-1.19.1.1"], [id=2565898,name="RandomTweaks 1.11.2-1.19.1.1"], [id=2560111,name="RandomTweaks 1.12.2-1.19.1.0"], [id=2560109,name="RandomTweaks 1.11.2-1.19.1.0"], [id=2559439,name="RandomTweaks 1.12.2-1.19.0.0"], [id=2559438,name="RandomTweaks 1.11.2-1.19.0.0"], [id=2558566,name="RandomTweaks 1.12.2-1.18.3.0"], [id=2558565,name="RandomTweaks 1.11.2-1.18.3.0"], [id=2557073,name="RandomTweaks 1.12.2-1.18.2.0"], [id=2557072,name="RandomTweaks 1.11.2-1.18.2.0"], [id=2555263,name="RandomTweaks 1.12.2-1.18.1.0"], [id=2555260,name="RandomTweaks 1.11.2-1.18.1.0"], [id=2550987,name="RandomTweaks 1.12.2-1.18.0.1"], [id=2550985,name="RandomTweaks 1.11.2-1.18.0.1"], [id=2550720,name="RandomTweaks 1.12.2-1.18.0.0"], [id=2550719,name="RandomTweaks 1.11.2-1.18.0.0"], [id=2537787,name="RandomTweaks 1.11.2-1.17.1.0"], [id=2537786,name="RandomTweaks 1.12.2-1.17.1.0"], [id=2530517,name="RandomTweaks 1.12.2-1.17.0.1"], [id=2530514,name="RandomTweaks 1.11.2-1.17.0.1"], [id=2524027,name="RandomTweaks 1.12.2-1.17.0.0"], [id=2524025,name="RandomTweaks 1.11.2-1.17.0.0"], [id=2523624,name="RandomTweaks 1.12.2-1.16.0.0"], [id=2523622,name="RandomTweaks 1.11.2-1.16.0.0"], [id=2523561,name="RandomTweaks 1.12.2-1.15.1.0"], [id=2523560,name="RandomTweaks 1.11.2-1.15.1.0"], [id=2521767,name="RandomTweaks 1.11.2-1.15.0.0"], [id=2521766,name="RandomTweaks 1.12.2-1.15.0.0"], [id=2511595,name="RandomTweaks 1.12.2-1.14.1.1"], [id=2511594,name="RandomTweaks 1.11.2-1.14.1.1"], [id=2510585,name="RandomTweaks 1.12.2-1.14.1.0"], [id=2510584,name="RandomTweaks 1.11.2-1.14.1.0"], [id=2508291,name="RandomTweaks 1.12.2-1.14.0.0"], [id=2508290,name="RandomTweaks 1.11.2-1.14.0.0"], [id=2506671,name="RandomTweaks 1.12.2-1.13.1.0"], [id=2506670,name="RandomTweaks 1.11.2-1.13.1.0"], [id=2504582,name="RandomTweaks 1.12.2-1.13.0.0"], [id=2504581,name="RandomTweaks 1.11.2-1.13.0.0"], [id=2492993,name="RandomTweaks 1.12.2-1.12.0.0"], [id=2492992,name="RandomTweaks 1.11.2-1.12.0.0"], [id=2492145,name="RandomTweaks 1.12.2-1.11.0.1"], [id=2492144,name="RandomTweaks 1.11.2-1.11.0.1"], [id=2490220,name="RandomTweaks 1.12.2-1.11.0.0"], [id=2490219,name="RandomTweaks 1.11.2-1.11.0.0"], [id=2478710,name="RandomTweaks 1.12.1-1.10.0.1"], [id=2478709,name="RandomTweaks 1.11.2-1.10.0.1"], [id=2478165,name="RandomTweaks 1.12.1-1.10.0.0"], [id=2478164,name="RandomTweaks 1.11.2-1.10.0.0"], [id=2466874,name="RandomTweaks 1.12.1-2.1.0.1"], [id=2466872,name="RandomTweaks 1.11.2-1.9.0.1"], [id=2463887,name="RandomTweaks 1.12.1-2.0.0.0"], [id=2463883,name="RandomTweaks 1.11.2-1.8.0.4"], [id=2463151,name="RandomTweaks 1.11.2-1.8.0.3"], [id=2413693,name="RandomTweaks 1.11.2-1.8.0.2"], [id=2404624,name="RandomTweaks 1.11.2-1.8.0.1"], [id=2403438,name="RandomTweaks 1.11.2-1.8.0.0"], [id=2393675,name="RandomTweaks 1.11.2-1.7.0.4"], [id=2393672,name="RandomTweaks 1.11.2-1.7.0.3"], [id=2393300,name="RandomTweaks 1.11.2-1.7.0.2"], [id=2392591,name="RandomTweaks 1.11.2-1.7.0.1"], [id=2392532,name="RandomTweaks 1.11.2-1.7.0.0"], [id=2392100,name="RandomTweaks 1.11.2-1.6.0.0"], [id=2389023,name="RandomTweaks 1.11.2-1.5.0.0"], [id=2386083,name="RandomTweaks 1.11.2-1.4.0.0"], [id=2386020,name="RandomTweaks 1.11.2-1.3.0.0"], [id=2382332,name="RandomTweaks 1.11.2-1.2.0.0"], [id=2380550,name="RandomTweaks 1.11.2-1.1.1.1"], [id=2380235,name="RandomTweaks 1.11.2-1.1.1.0"], [id=2380169,name="RandomTweaks 1.11.2-1.1.0.0"], [id=2374297,name="RandomTweaks 1.11.2-1.0.0.1"], [id=2374012,name="RandomTweaks 1.11.2-1.0.0.0"]]
	[INFO] Random dependent: [title="EOD: VoxelVerse",url="https://minecraft.curseforge.com/projects/eodvoxelverse"]

File information example (the Minecraft versions depend on CurseAPI-Minecraft):

	getLogger().disableDebug();

	final CurseFile file = CurseProject.fromSlug("minecraft", "randomtweaks").files().get(3);

	getLogger().info("Project ID: " + file.projectID());
	getLogger().info("File ID: " + file.id());
	getLogger().info("Status: " + file.status());
	getLogger().info("URL: " + file.urlString());
	getLogger().info("Name: " + file.name());
	getLogger().info("Name on disk: " + file.nameOnDisk());
	getLogger().info("Download URL: " + file.downloadURLString());
	getLogger().info("Release type: " + file.releaseType());
	getLogger().info("Upload time: " + file.uploadTime());
	getLogger().info("File size: " + file.fileSize());
	getLogger().info("Downloads: " + file.downloads());
	getLogger().info("MD5: " + file.md5());
	getLogger().info("Uploader: " + file.uploader());
	getLogger().info("Dependencies: " + file.dependencies());
	getLogger().info("Minecraft versions: " + file.gameVersions());
	getLogger().info("Changelog: " + file.changelog());

The above code at the time of writing prints:

	[INFO] Project ID: 258205
	[INFO] File ID: 2622074
	[INFO] Status: Normal
	[INFO] URL: https://minecraft.curseforge.com/projects/randomtweaks/files/2622074
	[INFO] Name: RandomTweaks 1.12.2-2.1.6.0
	[INFO] Name on disk: randomtweaks-1.12.2-2.1.6.0.jar
	[INFO] Download URL: https://media.forgecdn.net/files/2622/74/randomtweaks-1.12.2-2.1.6.0.jar
	[INFO] Release type: beta
	[INFO] Upload time: 2018-09-29T09:04:08Z
	[INFO] File size: 155.75 KB
	[INFO] Downloads: 155
	[INFO] MD5: 867b5f8d7a49e671a10ed2ffd577365e
	[INFO] Uploader: Member[type="Owner",username="TheRandomLabs"]
	[INFO] Dependencies: []
	[INFO] Minecraft versions: [1.12.2]
	[INFO] Changelog: Cleaned up the code.
	Added better button names, which are disabled by default.
