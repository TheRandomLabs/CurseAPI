package com.therandomlabs.curseapi.cursemeta;

import java.util.Map;
import com.therandomlabs.curseapi.util.Utils;

public final class CMFeeds implements Cloneable {
	public int timestamp;
	public int[] game_ids;
	public Map<String, Integer> intervals;

	@Override
	public CMFeeds clone() {
		try {
			final CMFeeds feeds = (CMFeeds) super.clone();
			feeds.game_ids = game_ids.clone();
			feeds.intervals = Utils.tryClone(intervals);
		} catch(CloneNotSupportedException ignored) {}

		return null;
	}
}
