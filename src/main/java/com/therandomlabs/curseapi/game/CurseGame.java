package com.therandomlabs.curseapi.game;

/**
 * Represents a game supported by CurseForge.
 * <p>
 * Implementations of this interface should be effectively immutable.
 */
public interface CurseGame {
	/**
	 * Returns the ID of this {@link CurseGame}.
	 *
	 * @return the ID of this {@link CurseGame}.
	 */
	int id();
}
