package com.therandomlabs.curseapi.project;

import java.io.Serializable;
import java.net.URL;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.cursemeta.AddOnCategory;

public final class Category implements Serializable {
	private static final long serialVersionUID = 4578392820713062750L;

	private String name;
	private URL url;
	private URL thumbnailURL;

	Category(String name, URL url, URL thumbnailURL) {
		this.name = name;
		this.url = url;
		this.thumbnailURL = thumbnailURL;
	}

	public String name() {
		return name;
	}

	public URL url() {
		return url;
	}

	public String urlString() {
		return url.toString();
	}

	public URL thumbnailURL() {
		return thumbnailURL;
	}

	public String thumbnailURLString() {
		return thumbnailURL.toString();
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof Category && ((Category) object).url.equals(url);
	}

	@Override
	public String toString() {
		return "[name=\"" + name + "\",url=\"" + url + "\",thumbnailURL=\"" + thumbnailURL + "\"]";
	}

	public static Category[] fromAddOnCategories(AddOnCategory[] addOnCategories) {
		final Category[] categories = new Category[addOnCategories.length];
		for(int i = 0; i < addOnCategories.length; i++) {
			final AddOnCategory category = addOnCategories[i];
			categories[i] =
					new Category(category.Name, category.URL, CurseAPI.PLACEHOLDER_THUMBNAIL_URL);
		}
		return categories;
	}
}
