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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class StreamUtils {
	
	/**
	 * Reads a single line from the given input stream,
	 * blocking until it gets a newline.
	 * 
	 * @param in InputStream to read from.
	 * @return A single line, without the newline at the end or null if no data could be read.
	 * @throws IOException
	 */
	static public String readLine(InputStream in) throws IOException{
		final StringBuffer buf = new StringBuffer(512);
		
		final InputStreamReader in_reader = new InputStreamReader(in);
		
		boolean receivedData = false;
		
		for (int b = in_reader.read();
			b != -1;
			b = in_reader.read()) {
			receivedData = true;
			if (b == '\r') {
				continue;
			}
			if (b == '\n') {
				break;
			}
			buf.append((char)b);
		}
		
		if (!receivedData) {
			return null;
		}
		
		return buf.toString();
	}
	
	/**
	 * Read an InputStream into a String until it hits EOF.
	 *  
	 * @param in
	 * @return the complete contents of the InputStream
	 * @throws IOException
	 */
	static public String inputStreamToString(InputStream in) throws IOException{
		final int bufsize = 8196;
		final char[] cbuf = new char[bufsize];
		
		final StringBuffer buf = new StringBuffer(bufsize);
		
		final InputStreamReader in_reader = new InputStreamReader(in);
		
		for (int readBytes = in_reader.read(cbuf, 0, bufsize);
			readBytes > 0;
			readBytes = in_reader.read(cbuf, 0, bufsize)) {
			buf.append(cbuf, 0, readBytes);
		}

		return buf.toString();
	}
	
	/**
	 * Reads from an inputstream, dumps to an outputstream
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	static public void inputStreamToOutputStream(InputStream is, OutputStream os) throws IOException{
		final int bufsize = 8196;
		final byte[] cbuf = new byte[bufsize];
		
		for (int readBytes = is.read(cbuf, 0, bufsize);
			readBytes > 0;
			readBytes = is.read(cbuf, 0, bufsize)) {
			os.write(cbuf, 0, readBytes);
		}
	}
}
