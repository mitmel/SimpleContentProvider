package edu.mit.mobile.android.content;
/*
 * Copyright (C) 2011 MIT Mobile Experience Lab
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

public class SQLGenerationException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1987806236697877222L;

    public SQLGenerationException() {
        super();
    }

    public SQLGenerationException(String message) {
        super(message);
    }

    public SQLGenerationException(String message, Throwable initCause) {
        super(message);
        initCause(initCause);
    }
}
