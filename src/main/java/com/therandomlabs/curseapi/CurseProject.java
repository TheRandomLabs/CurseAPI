package com.therandomlabs.curseapi;

import java.awt.image.BufferedImage;
import java.time.ZonedDateTime;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

public abstract class CurseProject implements Comparable<CurseProject> {
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
	 * This method returns true if and only if the other object is also a {@link CurseProject} and
	 * the value returned by {@link #id()} is the same for both {@link CurseProject}s.
	 */
	@Override
	public final boolean equals(Object object) {
		return this == object ||
				(object instanceof CurseProject && id() == ((CurseProject) object).id());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).
				add("id", id()).
				add("name", name()).
				add("url", url()).
				toString();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link String#compareTo(String)} is used on the values returned by
	 * {@link #name()} to determine the value that this method returns.
	 */
	@Override
	public final int compareTo(CurseProject project) {
		return name().compareTo(project.name());
	}

	public abstract int id();

	public abstract String name();

	public abstract CurseMember author();

	public abstract Set<CurseMember> authors();

	public abstract HttpUrl avatarURL();

	public abstract HttpUrl avatarThumbnailURL();

	public BufferedImage avatar() throws CurseException {
		return OkHttpUtils.readImage(avatarURL());
	}

	public BufferedImage avatarThumbnail() throws CurseException {
		return OkHttpUtils.readImage(avatarThumbnailURL());
	}

	public abstract HttpUrl url();

	public abstract int gameID();

	public abstract String summary();

	public abstract Element description() throws CurseException;

	public abstract int downloadCount();

	public abstract CurseFiles latestFiles();

	public abstract CurseFiles files() throws CurseException;

	public abstract Set<CurseCategory> categories();

	public abstract CurseCategory primaryCategory();

	//categorySection

	public abstract String slug();

	public abstract ZonedDateTime creationTime();

	public abstract ZonedDateTime lastUpdateTime();

	public abstract ZonedDateTime lastModificationTime();

	public abstract boolean experimental();
}
