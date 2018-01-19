package com.therandomlabs.curseapi.curseforge;

import static com.therandomlabs.utils.logging.Logging.getLogger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.select.Elements;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.util.DocumentUtils;
import com.therandomlabs.curseapi.util.URLUtils;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.misc.Assertions;
import com.therandomlabs.utils.misc.StringUtils;

public final class CurseForge {
	public static final String HOST = "www.curseforge.com";
	public static final String URL = "https://" + HOST + "/";
	public static final Pattern PROJECT_PATH_PATTERN = Pattern.compile("^/projects/.*.[a-zA-Z].*");
	public static final Pattern UNREDIRECTED_PROJECT_PATH_PATTERN =
			Pattern.compile("^/projects/[0-9]+");

	private static final Map<String, Boolean> validPaths = new HashMap<>(50);

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
		return is(URLUtils.url(url));
	}

	public static boolean is(URL url) {
		return CurseForgeSite.HOST_PATTERN.matcher(url.getHost()).matches();
	}

	public static boolean isProject(String url) throws CurseException {
		URL urlObject = null;

		try {
			urlObject = new URL(url);
		} catch(MalformedURLException ex) {
			return false;
		}

		return isProject(urlObject);
	}

	public static boolean isProject(URL url) throws CurseException {
		url = redirectIfNecessary(url);

		String path = url.getPath();

		try {
			if(!is(url) || !PROJECT_PATH_PATTERN.matcher(path).matches()) {
				return false;
			}

			if(validPaths.containsKey(path)) {
				return validPaths.get(path);
			}

			//CurseForge project titles:
			//<h1 class="project-title">
			//The index=0 is to ensure that it has child elements
			DocumentUtils.get(url, "class=project-title;index=0");
		} catch(IndexOutOfBoundsException | NullPointerException ex) {
			validPaths.put(path, false);
			return false;
		}

		validPaths.put(path, true);
		return true;
	}

	public static boolean isUnredirected(String url) throws CurseException {
		return isUnredirected(URLUtils.url(url));
	}

	public static boolean isUnredirected(URL url) {
		return is(url) && UNREDIRECTED_PROJECT_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static URL redirectIfNecessary(String url) throws CurseException {
		return redirectIfNecessary(URLUtils.url(url));
	}

	public static URL redirectIfNecessary(URL url) throws CurseException {
		return isUnredirected(url) ? URLUtils.redirect(url) : url;
	}

	public static boolean isNewCurseForgeProject(String url) throws CurseException {
		return isNewCurseForgeProject(URLUtils.url(url));
	}

	public static boolean isNewCurseForgeProject(URL url) throws CurseException {
		final String path = url.getPath();

		try {
			if(!is(url) || !NewCurseForgeSite.PATH_PATTERN.matcher(path).matches()) {
				return false;
			}

			if(validPaths.containsKey(path)) {
				return validPaths.get(path);
			}

			//Curse Mods project overviews:
			//<div id="project-overview" class="overview">
			//The index=0 is to ensure that it has child elements
			DocumentUtils.get(url, "class=overview;index=0");
		} catch(IndexOutOfBoundsException | NullPointerException ex) {
			validPaths.put(path, false);
			return false;
		}

		validPaths.put(path, true);
		return true;
	}

	public static URL fromNewCurseForgeProject(String url) throws CurseException {
		return fromNewCurseForgeProject(URLUtils.url(url));
	}

	public static URL fromNewCurseForgeProject(URL url) throws CurseException {
		CurseException.validateNewCurseForgeProject(url);
		return URLUtils.url(
				DocumentUtils.getValue(url, "class=curseforge;attr=href;absUrl=href"));
	}

	public static URL toNewCurseForgeProject(URL url) throws CurseException {
		CurseException.validateProject(url);

		final Elements viewOnCurse = DocumentUtils.get(url).getElementsByClass("view-on-curse");
		if(viewOnCurse.isEmpty()) {
			return null;
		}

		return URLUtils.url(
					DocumentUtils.getValue(viewOnCurse.get(0), "attr=href;absUrl=href"));
	}

	public static URL getFileURL(int projectID, int fileID) throws CurseException {
		Assertions.larger(projectID, "projectID",
				CurseAPI.MIN_PROJECT_ID - 1, String.valueOf(CurseAPI.MIN_PROJECT_ID - 1));

		try {
			final URL url = URLUtils.redirect(fromID(projectID) + "/files/" + fileID +
					"/download");

			//Some redirected URLs have a space in the file names
			final String fileName = ArrayUtils.last(StringUtils.split(url.getPath(), '/'));
			final String encoded = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
			return new URL(url.toString().replace(fileName, encoded));
		} catch(IOException ex) {
			throw new CurseException(ex);
		}
	}

	public static URL fromID(int projectID) throws CurseException {
		Assertions.larger(projectID, "projectID",
				CurseAPI.MIN_PROJECT_ID - 1, String.valueOf(CurseAPI.MIN_PROJECT_ID - 1));

		final URL project = URLUtils.redirect(URL + "projects/" + projectID);

		if(!is(project) || !PROJECT_PATH_PATTERN.matcher(project.getPath()).matches()) {
			CurseException.invalidProjectID(projectID);
		}

		//The project may have been deleted
		try {
			DocumentUtils.get(project);
		} catch(CurseException ex) {
			if(ex.getCause() instanceof FileNotFoundException) {
				CurseException.invalidProjectID(projectID);
			}
			CurseException.invalidProjectID(projectID, ex);
		}

		return project;
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
				throw new CurseException(ex);
			}
		}

		CurseException.validateProject(url);

		try {
			return Integer.parseInt(DocumentUtils.getValue(url, "class=info-data;text"));
		} catch(NumberFormatException ex) {
			throw new CurseException(ex);
		}
	}

	public static void clearAvailabilityCache() {
		validPaths.clear();
	}
}
