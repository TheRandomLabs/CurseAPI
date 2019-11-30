package com.therandomlabs.curseapi;

import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.forgesvc.ForgeSVCProvider;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.CheckedFunction;
import okhttp3.HttpUrl;

public final class CurseAPI {
	public static final HttpUrl PLACEHOLDER_PROJECT_AVATAR = HttpUrl.get(
			"https://www.curseforge.com/Content/2-0-7263-28137/Skins/Elerium/images/icons/" +
					"avatar-flame.png"
	);

	public static final HttpUrl PLACEHOLDER_PROJECT_THUMBNAIL =
			HttpUrl.get("https://media.forgecdn.net/avatars/0/93/635227964539626926.png");

	private static final List<CurseAPIProvider> providers =
			Lists.newArrayList(ForgeSVCProvider.INSTANCE);

	private CurseAPI() {}

	public static Optional<CurseProject> project(int id) throws CurseException {
		Preconditions.checkArgument(id >= 10, "id should be larger than 10");
		return get(provider -> provider.project(id));
	}

	public static Optional<CurseFile> file(int projectID, int fileID) throws CurseException {
		Preconditions.checkArgument(projectID >= 10, "projectID should be larger than 10");
		Preconditions.checkArgument(fileID >= 10, "fileID should be larger than 10");
		return get(provider -> provider.file(projectID, fileID));
	}

	public static void addProvider(CurseAPIProvider provider) {
		Preconditions.checkNotNull(provider, "provider should not be null");
		Preconditions.checkArgument(
				!providers.contains(provider), "provider should not already have been added"
		);
		providers.add(0, provider);
	}

	public static boolean removeProvider(CurseAPIProvider provider) {
		Preconditions.checkNotNull(provider, "provider should not be null");
		return providers.remove(provider);
	}

	public static List<CurseAPIProvider> getProviders() {
		return ImmutableList.copyOf(providers);
	}

	private static <T> Optional<T> get(
			CheckedFunction<CurseAPIProvider, T, CurseException> function
	) throws CurseException {
		for (CurseAPIProvider provider : providers) {
			final T t = function.apply(provider);

			if (t != null) {
				return Optional.of(t);
			}
		}

		return Optional.empty();
	}
}
