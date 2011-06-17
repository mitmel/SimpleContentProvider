package edu.mit.mobile.android.content.column;
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
import android.database.Cursor;

public class BlobColumn extends DBColumnType<byte[]> {

	@Override
	public String toCreateColumn(String colName) {
		return toColumnDef(colName, "BLOB");
	}

	@Override
	public byte[] get(Cursor c, int colNumber) {

		return c.getBlob(colNumber);
	}
}
