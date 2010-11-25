/*
 * Copyright (C) 2009-2010 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.preferences;

import android.content.Context;

import org.geometerplus.zlibrary.core.util.ZLBoolean3;
import org.geometerplus.zlibrary.core.options.ZLBoolean3Option;
import org.geometerplus.zlibrary.core.resources.ZLResource;

class ZLBoolean3Preference extends ZLStringListPreference {
	private static final String ON = "on";
	private static final String OFF = "off";
	private static final String UNCHANGED = "unchanged";

	private final ZLBoolean3Option myOption;

	ZLBoolean3Preference(Context context, ZLResource resource, String resourceKey, ZLBoolean3Option option) {
		super(context, resource, resourceKey);

		myOption = option;
		setList(new String[] { ON, OFF, UNCHANGED });

		switch (option.getValue()) {
			case ZLBoolean3.B3_TRUE:
				setInitialValue(ON);
				break;
			case ZLBoolean3.B3_FALSE:
				setInitialValue(OFF);
				break;
			case ZLBoolean3.B3_UNDEFINED:
				setInitialValue(UNCHANGED);
				break;
		}
	}

	public void onAccept() {
		final String value = getValue();
		if (ON.equals(value)) {
			myOption.setValue(ZLBoolean3.B3_TRUE);
		} else if (OFF.equals(value)) {
			myOption.setValue(ZLBoolean3.B3_FALSE);
		} else {
			myOption.setValue(ZLBoolean3.B3_UNDEFINED);
		}
	}
}
