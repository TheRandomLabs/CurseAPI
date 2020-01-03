/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2020 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.therandomlabs.curseapi.file;

import com.therandomlabs.curseapi.CurseException;

/**
 * Represents an alternate file on CurseForge.
 * <p>
 * Implementations of this class should be effectively immutable.
 */
public abstract class CurseAlternateFile extends BasicCurseFile implements ExistingCurseFile {
	/**
	 * Returns the ID of this alternate file's main file.
	 *
	 * @return the ID of this alternate file's main file.
	 */
	public abstract int mainFileID();

	/**
	 * Returns this alternate file's main file.
	 * This value may be refreshed by calling {@link #clearMainFileCache()}.
	 *
	 * @return this alternate file's main file as a {@link CurseFile}.
	 * @throws CurseException if an error occurs.
	 */
	public abstract CurseFile mainFile() throws CurseException;

	/**
	 * If this {@link CurseAlternateFile} implementation caches the value returned by
	 * {@link #mainFile()} and supports clearing this cache, this method clears this cached value.
	 */
	public abstract void clearMainFileCache();
}
