package edu.mit.mobile.android.utils;
/*
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
import java.util.Collection;

public class ListUtils {

	/**
	 * Join. Why is Collections missing this?
	 *
	 * @param list
	 * @param delim
	 * @return a string of the items in list joined by the delimiter.
	 * @see <a href="http://stackoverflow.com/questions/63150/whats-the-best-way-to-build-a-string-of-delimited-items-in-java">Stack Overflow - What's the best way to build a string of delimited items in Java?</a>
	 */
	public static String join(Collection<String> list, String delim) {

	    final StringBuilder sb = new StringBuilder();

	    String loopDelim = "";

	    for(final String s : list) {

	        sb.append(loopDelim);
	        sb.append(s);

	        loopDelim = delim;
	    }

	    return sb.toString();
	}

}
