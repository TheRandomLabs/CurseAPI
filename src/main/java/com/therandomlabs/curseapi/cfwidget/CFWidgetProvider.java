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

package com.therandomlabs.curseapi.cfwidget;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseAPIProvider;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.project.CurseProject;
import com.therandomlabs.curseapi.util.RetrofitUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A {@link CurseAPIProvider} that uses the CurseForge widget API at
 * <a href="https://www.cfwidget.com/">https://api.cfwidget.com/</a>.
 * <p>
 * This provider falls back on the methods declared in
 * {@link com.therandomlabs.curseapi.CurseAPI} wherever possible so that default behaviors
 * may be overridden.
 * <p>
 * Where possible, this class should not be accessed directly, and the methods declared in
 * {@link com.therandomlabs.curseapi.CurseAPI} should be favored.
 *
 * @see com.therandomlabs.curseapi.forgesvc.ForgeSvcProvider
 */
public final class CFWidgetProvider implements CurseAPIProvider {
	/**
	 * The singleton instance of {@link CFWidgetProvider}.
	 */
	public static final CFWidgetProvider instance = new CFWidgetProvider();

	private static final CFWidget cfWidget =
			RetrofitUtils.get("https://api.cfwidget.com/").create(CFWidget.class);

	private CFWidgetProvider() {}

	/**
	 * {@inheritDoc}
	 */
	@Nullable
	@Override
	public CurseProject project(String path) throws CurseException {
		if (path.charAt(0) == '/') {
			path = path.substring(1);
		}

		final CFWidgetProject project = RetrofitUtils.execute(cfWidget.getProject(path));
		return project == null ? null : CurseAPI.project(project.id).orElse(null);
	}
}
