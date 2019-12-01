package com.therandomlabs.curseapi;

import java.time.ZonedDateTime;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.util.JsoupUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

/**
 * Represents a CurseForge file.
 */
public abstract class CurseFile implements Comparable<CurseFile> {
	/**
	 * {@inheritDoc}
	 * <p>
	 * Calling this method is equivalent to calling {@link #id()}.
	 */
	@Override
	public final int hashCode() {
		return id();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method returns true if and only if the other object is also a {@link CurseFile} and
	 * the value returned by {@link #id()} is the same for both {@link CurseFile}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseFile && id() == ((CurseFile) object).id());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("projectID", projectID()).
				add("id", id()).
				add("displayName", displayName()).
				add("downloadURL", downloadURL()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Newer {@link CurseFile}s are represented as being greater than older {@link CurseFile}s.
	 */
	@Override
	public final int compareTo(CurseFile file) {
		return Integer.compare(file.id(), id());
	}

	public abstract int projectID();

	public abstract int id();

	public abstract String displayName();

	public abstract String fileName();

	public abstract ZonedDateTime uploadTime();

	public abstract long fileSize();

	public abstract HttpUrl downloadURL();

	//Dependencies

	//Modules

	//Fingerprint

	public abstract Set<String> gameVersions();

	public abstract Element changelog() throws CurseException;

	public String changelogPlainText() throws CurseException {
		return changelogPlainText(Integer.MAX_VALUE);
	}

	public String changelogPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(changelog(), maxLineLength);
	}

	public final boolean isOlderThan(CurseFile file) {
		return id() < file.id();
	}

	public final boolean isNewerThan(CurseFile file) {
		return id() > file.id();
	}
}
