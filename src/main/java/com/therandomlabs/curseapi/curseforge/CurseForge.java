package com.therandomlabs.curseapi.curseforge;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.InvalidProjectIDException;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.misc.StringUtils;
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
			return DocumentUtils.isAvailable(URL);
		} catch(CurseException ex) {
			getLogger().printStackTrace(ex);
			getLogger().warning("Assuming CurseForge is not available...");
		}

		return false;
	}

	public static boolean is(String url) throws CurseException {
		if(url == null) {
			return false;
		}
		return is(URLUtils.url(url));
	}

	public static boolean is(URL url) {
		if(url == null) {
			return false;
		}
		return CurseForgeSite.HOST_PATTERN.matcher(url.getHost()).matches();
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

		String path = url.getPath();

		try {
			if(!is(url) || !PROJECT_PATH_PATTERN.matcher(path).matches()) {
				return false;
			}

			//CurseForge project titles:
			//<h1 class="project-title">
			//The index=0 is to ensure that it has child elements
			DocumentUtils.get(url, "class=project-title;index=0");
		} catch(IndexOutOfBoundsException | NullPointerException ex) {
			return false;
		}

		return true;
	}

	public static URL redirectIfNecessary(URL url) throws CurseException {
		return isUnredirected(url) ? URLUtils.redirect(url) : url;
	}

	public static boolean isUnredirected(URL url) {
		return is(url) && UNREDIRECTED_PROJECT_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static boolean isFile(String url) throws CurseException {
		URL urlObject;

		try {
			urlObject = new URL(url);
		} catch(MalformedURLException ex) {
			return false;
		}

		return isFile(urlObject);
	}

	public static boolean isFile(URL url) throws CurseException {
		String path = url.getPath();

		try {
			if(!is(url) || !FILE_PATH_PATTERN.matcher(path).matches()) {
				return false;
			}

			//Ensure that this is actually a file
			DocumentUtils.get(url, "class=project-file-release-type;index=0");
		} catch(IndexOutOfBoundsException | NullPointerException ex) {
			return false;
		}

		return true;
	}

	public static URL getProjectURLFromFile(URL url) throws CurseException {
		if(!isFile(url)) {
			return null;
		}

		try {
			return new URL(url.getProtocol(), url.getHost(),
					"projects/" + url.getPath().split("/")[1]);
		} catch(MalformedURLException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static boolean isUnredirected(String url) throws CurseException {
		return isUnredirected(URLUtils.url(url));
	}

	public static URL redirectIfNecessary(String url) throws CurseException {
		return redirectIfNecessary(URLUtils.url(url));
	}

	public static boolean isMainCurseForgeProject(String url) throws CurseException {
		return isMainCurseForgeProject(URLUtils.url(url));
	}

	public static boolean isMainCurseForgeProject(URL url) throws CurseException {
		final String path = url.getPath();

		try {
			if(!is(url) || !MainCurseForgeSite.PATH_PATTERN.matcher(path).matches()) {
				return false;
			}

			//Curse Mods project overviews:
			//<div id="project-overview" class="overview">
			//The index=0 is to ensure that it has child elements
			DocumentUtils.get(url, "class=overview;index=0");
		} catch(IndexOutOfBoundsException | NullPointerException ex) {
			return false;
		} catch(CurseException ex) {
			if(ex.getCause() instanceof FileNotFoundException) {
				return false;
			}

			throw ex;
		}

		return true;
	}

	public static URL fromMainCurseForgeProject(String url) throws CurseException {
		return fromMainCurseForgeProject(URLUtils.url(url));
	}

	public static URL fromMainCurseForgeProject(URL url) throws CurseException {
		CurseException.validateMainCurseForgeProject(url);
		return URLUtils.url(
				DocumentUtils.getValue(url, "class=curseforge;attr=href;absUrl=href"));
	}

	public static URL toMainCurseForgeProject(URL url) throws CurseException {
		CurseException.validateProject(url);

		final Elements viewOnCurse = DocumentUtils.get(url).getElementsByClass("view-on-curse");
		if(viewOnCurse.isEmpty()) {
			return null;
		}

		return URLUtils.url(
				DocumentUtils.getValue(viewOnCurse.get(0), "attr=href;absUrl=href"));
	}

	public static URL getFileURL(int projectID, int fileID) throws CurseException {
		CurseAPI.validateID(projectID, fileID);
		return URLUtils.redirect(fromID(projectID) + "/files/" + fileID + "/download");
	}

	public static URL fromID(int projectID) throws CurseException {
		CurseAPI.validateID(projectID);

		URL project = URLUtils.redirect(URL + "projects/" + projectID);
		if(!is(project) || !PROJECT_PATH_PATTERN.matcher(project.getPath()).matches()) {
			throw new InvalidProjectIDException(projectID);
		}

		try {
			DocumentUtils.get(project);
		} catch(CurseException ex) {
			if(ex.getCause() instanceof FileNotFoundException) {
				throw new InvalidProjectIDException(projectID);
			}
			throw new InvalidProjectIDException(projectID, ex);
		}

		return project;
	}

	public static int getFileID(String url) throws CurseException {
		return getFileID(URLUtils.url(url));
	}

	public static int getFileID(URL url) throws CurseException {
		CurseException.validateFile(url);
		return Integer.parseInt(ArrayUtils.last(StringUtils.split(url.getPath(), '/')));
	}

	public static int getID(String url) throws CurseException {
		return getID(URLUtils.url(url));
	}

	public static int getID(URL url) throws CurseException {
		//First, check if the path contains the project ID so we don't have to unnecessarily
		//download the document
		if(isUnredirected(url)) {
			try {
				//Example:
				//https://www.curseforge.com/projects/258205
				//We're trying to get the "258205"
				final String[] parts = url.getPath().split("/");
				return Integer.parseInt(ArrayUtils.last(parts));
			} catch(NumberFormatException ex) {
				//isUnredirected should rule this out as it checks if the last part is a number
				throw CurseException.fromThrowable(ex);
			}
		}

		CurseException.validateProject(url);

		try {
			return Integer.parseInt(DocumentUtils.getValue(url, "class=info-data;text"));
		} catch(NumberFormatException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}
}
