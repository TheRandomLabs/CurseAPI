package com.therandomlabs.curseapi;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.project.InvalidProjectIDException;
import com.therandomlabs.curseapi.project.ProjectType;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.misc.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public final class CurseForge {
	public static final String HOST = "www.curseforge.com";
	public static final String URL = "https://" + HOST + "/";
	public static final Pattern PROJECT_PATH_PATTERN =
			Pattern.compile("^/projects/[a-zA-Z]+([a-zA-Z|0-9]|-)*$");
	public static final Pattern UNREDIRECTED_PROJECT_PATH_PATTERN =
			Pattern.compile("^/projects/[0-9]+$");
	public static final Pattern FILE_PATH_PATTERN =
			Pattern.compile("^/projects/[a-zA-Z]+([a-zA-Z|0-9]|-)*/files/[0-9]+$");

	private CurseForge() {}

	public static boolean isAvailable() {
		try {
			return Documents.isAvailable(URL);
		} catch(CurseException ex) {
			getLogger().printStackTrace(ex);
			getLogger().warning("Assuming CurseForge is not available...");
		}

		return false;
	}

	public static boolean is(String url) throws CurseException {
		return url != null && is(URLs.url(url));
	}

	public static boolean is(URL url) {
		if(url == null) {
			return false;
		}

		return CurseForgeSite.HOST_PATTERN.matcher(url.getHost()).matches();
	}

	public static boolean isUnredirected(String url) throws CurseException {
		return isUnredirected(URLs.url(url));
	}

	public static boolean isUnredirected(URL url) {
		return is(url) && UNREDIRECTED_PROJECT_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static URL redirectIfNecessary(String url) throws CurseException {
		return redirectIfNecessary(URLs.url(url));
	}

	public static URL redirectIfNecessary(URL url) throws CurseException {
		return isUnredirected(url) ? URLs.redirect(url) : url;
	}

	public static boolean isProject(String url) throws CurseException {
		if(url == null) {
			return false;
		}

		URL urlObject;

		try {
			urlObject = new URL(url);
		} catch(MalformedURLException ex) {
			return false;
		}

		return isProject(urlObject);
	}

	public static boolean isProject(URL url) throws CurseException {
		if(url == null) {
			return false;
		}

		url = redirectIfNecessary(url);
		return isValidProjectURL(url) && isProject(Documents.get(url));
	}

	public static boolean isValidProjectURL(URL url) {
		return is(url) && PROJECT_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static boolean isProject(Element document) {
		try {
			//Ensure the project title and child elements exist
			Documents.get(document, "class=project-title;index=0");
			return true;
		} catch(CurseException ignored) {}

		return false;
	}

	public static boolean isFile(String url) throws CurseException {
		try {
			return isFile(new URL(url));
		} catch(MalformedURLException ignored) {}

		return false;
	}

	public static boolean isFile(URL url) throws CurseException {
		return isValidFileURL(url) && isFile(Documents.get(url));
	}

	public static boolean isValidFileURL(URL url) {
		return is(url) && !FILE_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static boolean isFile(Element document) {
		try {
			//Ensure the release type and child elements exist
			Documents.get(document, "class=project-file-release-type;index=0");
			return true;
		} catch(CurseException ignored) {}

		return false;
	}

	public static URL getProjectURLFromFile(URL url) throws CurseException {
		try {
			final String[] path = url.getPath().split("/");
			return new URL(url.getProtocol(), url.getHost(), "projects/" + path[1]);
		} catch(ArrayIndexOutOfBoundsException | MalformedURLException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static boolean isMainCurseForgeProject(String url) throws CurseException {
		return isMainCurseForgeProject(URLs.url(url));
	}

	public static boolean isMainCurseForgeProject(URL url) throws CurseException {
		return isValidMainCurseForgeProjectURL(url) &&
				isMainCurseForgeProject(Documents.get(url));
	}

	public static boolean isValidMainCurseForgeProjectURL(URL url) throws CurseException {
		return is(url) && ProjectType.MAIN_CURSEFORGE_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static boolean isMainCurseForgeProject(Element document) {
		try {
			Documents.get(document, "class=overview;index=0");
			return true;
		} catch(CurseException ignored) {}

		return false;
	}

	public static URL fromMainCurseForgeProject(String url) throws CurseException {
		return fromMainCurseForgeProject(URLs.url(url));
	}

	public static URL fromMainCurseForgeProject(URL url) throws CurseException {
		return fromMainCurseForgeProject(InvalidMainCurseForgeProjectException.validate(url));
	}

	public static URL fromMainCurseForgeProject(Element document) throws CurseException {
		return URLs.url(
				Documents.getValue(document, "class=curseforge;attr=href;absUrl=href"));
	}

	public static URL toMainCurseForgeProject(URL url) throws CurseException {
		return toMainCurseForgeProject(InvalidMainCurseForgeProjectException.validate(url));
	}

	public static URL toMainCurseForgeProject(Element document) throws CurseException {
		final Elements viewOnCurse = document.getElementsByClass("view-on-curse");

		if(viewOnCurse.isEmpty()) {
			return null;
		}

		return URLs.url(
				Documents.getValue(viewOnCurse.get(0), "attr=href;absUrl=href"));
	}

	public static URL getFileURL(int projectID, int fileID) throws CurseException {
		CurseAPI.validateID(projectID, fileID);
		return URLs.redirect(fromID(projectID) + "/files/" + fileID + "/download");
	}

	public static Map.Entry<URL, Document> fromID(int projectID) throws CurseException {
		CurseAPI.validateID(projectID);

		final URL url = URLs.redirect(URL + "projects/" + projectID);

		if(!isValidProjectURL(url)) {
			throw new InvalidProjectIDException(projectID);
		}

		try {
			final Document document = Documents.get(url);
			return new AbstractMap.SimpleEntry<>(url, document);
		} catch(CurseException ex) {
			if(ex.getCause() instanceof FileNotFoundException) {
				throw new InvalidProjectIDException(projectID);
			}

			throw new InvalidProjectIDException(projectID, ex);
		}
	}

	public static int getFileID(String url) throws CurseException {
		return getFileID(URLs.url(url));
	}

	public static int getFileID(URL url) throws CurseException {
		return Integer.parseInt(ArrayUtils.last(StringUtils.split(url.getPath(), '/')));
	}

	public static int getID(String url) throws CurseException {
		return getID(URLs.url(url));
	}

	public static int getID(URL url) throws CurseException {
		//If isUnredirected, the last part should be an ID
		if(isUnredirected(url)) {
			return Integer.parseInt(url.getPath().split("/")[3]);
		}

		return getID(InvalidCurseForgeProjectException.validate(url));
	}

	public static int getID(Element document) throws CurseException {
		return Integer.parseInt(Documents.getValue(document, "class=info-data;text"));
	}
}
