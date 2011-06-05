package edu.mit.mobile.android.utils;
/*
 * Copyright (C) 2011 MIT Mobile Experience Lab
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
import java.util.HashMap;

public class LastUpdatedMap<T> extends HashMap<T, Long> {
	/**
	 *
	 */
	private static final long serialVersionUID = -1046063858797590395L;

	private final long mTimeout;

	public LastUpdatedMap(long timeout) {
		mTimeout = timeout;
	}

	/**
	 * Mark the item as being recently updated.
	 * @param item
	 */
	public void markUpdated(T item){
		put(item, System.nanoTime());
	}

	/**
	 * @param item
	 * @return true if the item has been updated recently
	 */
	public boolean isUpdatedRecently(T item){
		final Long lastUpdated = get(item);
		if (lastUpdated != null){
			return (System.nanoTime() - lastUpdated) < mTimeout;
		}else{
			return false;
		}
	}
}
