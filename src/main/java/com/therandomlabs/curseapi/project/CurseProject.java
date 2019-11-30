package com.therandomlabs.curseapi.project;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.game.CurseGame;
import com.therandomlabs.curseapi.member.CurseMember;
import com.therandomlabs.curseapi.util.JsoupUtils;
import com.therandomlabs.curseapi.util.OkHttpUtils;
import okhttp3.HttpUrl;
import org.jsoup.nodes.Element;

public interface CurseProject {
	int id();

	String name();

	Set<? extends CurseMember> authors();

	HttpUrl avatarURL();

	HttpUrl avatarThumbnailURL();

	default BufferedImage avatar() throws CurseException {
		return OkHttpUtils.readImage(avatarURL());
	}

	default BufferedImage avatarThumbnail() throws CurseException {
		return OkHttpUtils.readImage(avatarThumbnailURL());
	}

	HttpUrl url();

	int gameID();

	default Optional<? extends CurseGame> game() {
		return Optional.empty();
	}

	String summary();

	Element description() throws CurseException;

	default String descriptionPlainText() throws CurseException {
		return descriptionPlainText(Integer.MAX_VALUE);
	}

	default String descriptionPlainText(int maxLineLength) throws CurseException {
		Preconditions.checkArgument(maxLineLength > 0, "maxLineLength should be greater than 0");
		return JsoupUtils.getPlainText(description(), maxLineLength);
	}

	int downloadCount();

	List<? extends CurseFile> latestFiles();
}
