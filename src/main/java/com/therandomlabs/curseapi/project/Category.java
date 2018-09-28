package com.therandomlabs.curseapi.project;

import java.net.URL;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.cursemeta.AddOnCategory;

public final class Category {
	public static final Category UNKNOWN = new Category("Unknown", null, null);

	private final String name;
	private final URL url;
	private final String urlString;
	private final URL thumbnailURL;
	private final String thumbnailURLString;

	Category(String name, URL url, URL thumbnailURL) {
		this.name = name;
		this.url = url;
		this.urlString = url == null ? null : url.toString();
		this.thumbnailURL = thumbnailURL;
		this.thumbnailURLString = thumbnailURL == null ? null : thumbnailURL.toString();
	}

	@Override
	public int hashCode() {
		return urlString.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Category && ((Category) object).urlString.equals(urlString);
	}

	@Override
	public String toString() {
		return "[name=\"" + name + "\",url=\"" + urlString + "\",thumbnailURL=\"" +
				thumbnailURLString + "\"]";
	}

	public String name() {
		return name;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return urlString;
	}

	public URL thumbnailURL() {
		return thumbnailURL;
	}

	public String thumbnailURLString() {
		return thumbnailURLString;
	}

	public static Category[] fromAddOnCategories(AddOnCategory[] addOnCategories) {
		final Category[] categories = new Category[addOnCategories.length];

		for(int i = 0; i < addOnCategories.length; i++) {
			final AddOnCategory category = addOnCategories[i];
			categories[i] = new Category(category.Name,
					category.URL,
					CurseAPI.PLACEHOLDER_THUMBNAIL_URL
			);
		}

		return categories;
	}
}
