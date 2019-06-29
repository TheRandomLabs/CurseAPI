package com.therandomlabs.curseapi;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Pattern;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.project.InvalidProjectIDException;
import com.therandomlabs.curseapi.util.Documents;
import com.therandomlabs.curseapi.util.URLs;
import com.therandomlabs.utils.collection.ArrayUtils;
import com.therandomlabs.utils.misc.StringUtils;
import com.therandomlabs.utils.wrapper.Wrapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static com.therandomlabs.utils.logging.Logging.getLogger;

public final class CurseForge {
	public static final String HOST = "www.curseforge.com";
	public static final String URL = "https://" + HOST + "/";
	//TODO base this on ProjectType.MAIN_CURSEFORGE_PATH_PATTERN
	public static final Pattern PROJECT_PATH_PATTERN =
			Pattern.compile("^/[a-zA-Z-]+/[a-zA-Z-]+/[a-zA-Z|0-9-]+");
	public static final Pattern UNREDIRECTED_PROJECT_PATH_PATTERN =
			Pattern.compile("^/projects/[0-9]+$");
	//TODO base this on ProjectType.MAIN_CURSEFORGE_PATH_PATTERN
	public static final Pattern FILE_PATH_PATTERN =
			Pattern.compile("^/[a-zA-Z-]+/[a-zA-Z-]+/[a-zA-Z|0-9-]+/files/[0-9]+");

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
		return url != null && is(URLs.of(url));
	}

	public static boolean is(URL url) {
		if(url == null) {
			return false;
		}

		return HOST.equals(url.getHost());
	}

	public static boolean isUnredirected(String url) throws CurseException {
		return isUnredirected(URLs.of(url));
	}

	public static boolean isUnredirected(URL url) {
		return is(url) && UNREDIRECTED_PROJECT_PATH_PATTERN.matcher(url.getPath()).matches();
	}

	public static URL redirectIfNecessary(String url) throws CurseException {
		return redirectIfNecessary(URLs.of(url));
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
			//Ensure the project description and child elements exist
			Documents.get(document, "class=project-detail__content;index=0");
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
			Documents.get(document, "class=text-sm");
			return true;
		} catch(CurseException ignored) {}

		return false;
	}

	public static URL getProjectURLFromFile(URL url) throws CurseException {
		try {
			final String path = url.getPath();
			final String[] pathElements = StringUtils.split(path, '/');
			final String[] relevantPathElements = ArrayUtils.subArray(pathElements, 0, 3);
			return new URL(
					url.getProtocol(), url.getHost(), ArrayUtils.join(relevantPathElements, '/')
			);
		} catch(ArrayIndexOutOfBoundsException | MalformedURLException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static URL getFileURL(int projectID, int fileID) throws CurseException {
		CurseAPI.validateProjectID(projectID);
		CurseAPI.validateFileID(fileID);

		final String projectURL;

		if(CurseProject.isCached(projectID)) {
			projectURL = CurseProject.fromID(projectID).urlString();
		} else {
			projectURL = fromIDNoValidation(projectID).toString();
		}

		return URLs.redirect(projectURL + "/download/" + fileID);
	}

	public static Map.Entry<URL, Document> fromID(int projectID) throws CurseException {
		CurseAPI.validateProjectID(projectID);

		final URL url = fromIDNoValidation(projectID);
		final Wrapper<Map.Entry<URL, Document>> result = new Wrapper<>();

		CurseAPI.doWithRetries(() -> {
			try {
				final Document document = Documents.get(url);
				result.set(new AbstractMap.SimpleEntry<>(url, document));
			} catch(CurseException ex) {
				if(ex.getCause() instanceof FileNotFoundException) {
					throw new InvalidProjectIDException(projectID);
				}

				throw new InvalidProjectIDException(projectID, ex);
			}
		});

		return result.get();
	}

	public static URL fromIDNoValidation(int projectID) throws CurseException {
		final Wrapper<URL> urlWrapper = new Wrapper<>();

		CurseAPI.doWithRetries(() -> {
			final URL url = URLs.redirect(URL + "projects/" + projectID);

			if(!isValidProjectURL(url)) {
				URLs.clearRedirectionCache(url);
				throw new InvalidProjectIDException(projectID);
			}

			urlWrapper.set(url);
		});

		return urlWrapper.get();
	}

	public static int getFileID(String url) throws CurseException {
		return getFileID(URLs.of(url));
	}

	public static int getFileID(URL url) {
		return Integer.parseInt(ArrayUtils.last(StringUtils.split(url.getPath(), '/')));
	}

	public static int getID(String url) throws CurseException {
		return getID(URLs.of(url));
	}

	public static int getID(URL url) throws CurseException {
		//If isUnredirected, the last part should be an ID
		if(isUnredirected(url)) {
			return Integer.parseInt(url.getPath().split("/")[3]);
		}

		return getID(InvalidCurseForgeProjectException.validate(url));
	}

	public static int getID(Element document) throws CurseException {
		return Integer.parseInt(
				Documents.getValue(document, "class=pb-4;tag=span=1;text")
		);
	}
}
