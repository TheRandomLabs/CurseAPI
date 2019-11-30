package com.therandomlabs.curseapi.file;

import java.time.ZonedDateTime;

import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

public interface CurseFile {
	int projectID();

	int id();

	String displayName();

	String fileName();

	ZonedDateTime uploadTime();

	long fileSize();

	HttpUrl downloadURL();

	//Dependencies

	//Modules

	//Fingerprint

	//Game versions

	Element changelog();
}
