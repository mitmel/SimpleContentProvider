package edu.mit.mobile.android.utils;

/*
 * AddressUtils.java
 * Copyright (C) 2010 MIT Mobile Experience Lab
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import android.location.Address;
import android.util.Log;

public class AddressUtils {
	public final static String TAG = AddressUtils.class.getSimpleName();
	// this regular expression should match what would be considered a bad description of the address.
	private static String  BAD_DESCRIPTION_RE = "^[\\d\\s-]+$";

	public static String addressToName(Address address){
		
		final Address thisLocation = address;
		Log.d(TAG, "Location: " + thisLocation);
		String title = thisLocation.getFeatureName();
		
		if (title == null || title.matches(BAD_DESCRIPTION_RE)){
			title = thisLocation.getAddressLine(0);
		}
		
		if (title == null || title.matches(BAD_DESCRIPTION_RE)){
			title = thisLocation.getSubLocality();
		}

		if (title == null){
			title = thisLocation.getLocality() + ", " + thisLocation.getCountryName();
		}else{
			title += ", " + thisLocation.getLocality();
		}
		
		return title;
	}
}
