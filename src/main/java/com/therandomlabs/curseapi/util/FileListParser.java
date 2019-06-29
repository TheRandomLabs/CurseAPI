package com.therandomlabs.curseapi.util;

import java.net.URL;
import java.util.List;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.ReleaseType;
import com.therandomlabs.curseapi.game.Game;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.widget.FileInfo;
import com.therandomlabs.utils.collection.ArrayUtils;
import org.jsoup.nodes.Element;

public final class FileListParser {
	private FileListParser() {}

	public static void getFiles(int projectID, Game game, Element document,
			List<CurseFile> files) throws CurseException {
		try {
			actuallyGetFiles(null, projectID, game, document, files);
		} catch(NullPointerException | NumberFormatException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	public static void getFiles(CurseProject project, Element document, List<CurseFile> files)
			throws CurseException {
		try {
			actuallyGetFiles(project, 0, null, document, files);
		} catch(NullPointerException | NumberFormatException ex) {
			throw CurseException.fromThrowable(ex);
		}
	}

	private static void actuallyGetFiles(CurseProject project, int projectID, Game game,
			Element document, List<CurseFile> files) throws CurseException {
		boolean first = true;

		for(Element file : document.getElementsByTag("tr")) {
			if(first) {
				first = false;
				continue;
			}

			final int id = Integer.parseInt(ArrayUtils.last(Documents.getValue(
					file, "tag=a;attr=href"
			).split("/")));

			final URL url = URLs.of(Documents.getValue(
					file,
					"tag=a;attr=href;absUrl=href"
			));

			final String name = Documents.getValue(file, "tag=a;text");

			//<div class="alpha-phase tip">
			final ReleaseType type =
					ReleaseType.fromName(Documents.getValue(file, "tag=span;text"));

			final String[] versions = new String[] {
					Documents.getValue(file, "class=mr-2;text")
			};

			/*if(file.getElementsByClass("additional-versions").isEmpty()) {
				final String version = Documents.getValue(file, "class=version-label;text");

				if(version.equals("-")) {
					versions = new String[0];
				} else {
					versions = new String[] {
							version
					};
				}
			} else {
				String value = Documents.getValue(file, "class=additional-versions;attr=title");

				value = value.substring(5, value.length() - 6);

				versions = value.split("</div><div>");
			}*/

			final String fileSize = Documents.getValue(file, "tag=td=2;text");

			final int downloads = Integer.parseInt(Documents.getValue(
					file, "tag=td=5;text"
			).replaceAll(",", ""));

			final String uploadedAt =
					Documents.getValue(file, "tag=abbr;attr=data-epoch");

			final FileInfo fileInfo = new FileInfo(
					id, url, name, type, versions, fileSize, downloads, uploadedAt
			);

			if(project == null) {
				files.add(new CurseFile(projectID, game, fileInfo));
			} else {
				files.add(new CurseFile(project, fileInfo));
			}
		}
	}
}
