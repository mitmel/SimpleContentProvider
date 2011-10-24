Simple Content Provider
=======================

This library aims to make creation of private and public ContentProviders trivial.

If your app is backed by a database, chances are you took one look at the
[Content Provider API][1] and decided that it wasn't worth the effort to use. This
library aims to help that by removing the need to do all the legwork of writing
all the basic CRUD operations that you'll encounter writing a basic content
provider.

By using this library, you shouldn't need to write *any* SQL for most
applications - table creation, query generation, etc. are all handled for you.

This library is loosely inspired by [Django][2] and follows some of its design
principles. It also follows the [REST][8]ful design principles that underly
Android's data-driven activity flow and encourages the use of URIs to represent
all data objects.

One can think of this library a bit like a super stripped down ORM - similar to
Hibernate or ORMlite.

Unlike ORMs, this library aims to have very little object creation in order to
minimize garbage collection churn. This follows Android's existing Content
Provider APIs closely, making it so that there should be very little difference
between the use of the content providers created by this library and any other
Content Provider Android already exposes (contacts, media, calendars, etc.).

Additionally, this library aims to be flexible enough to allow for easy
extension if what it provides is too simple for your application.

Using
-----

The Javadocs try to be extensive. You should start at
[SimpleContentProvider][3] which includes a brief walk-through of the system.

For a complete example, please see the [example code][6]. It's probably best to
start at [SampleProvider][4] and eventually make your way through [Message][5]
and the accompanying activities.

License
-------
Android Simple Content Provider  
Copyright (C) 2011 [MIT Mobile Experience Lab][7]

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

[1]: http://developer.android.com/intl/de/guide/topics/providers/content-providers.html
[2]: https://www.djangoproject.com/
[3]: src/edu/mit/mobile/android/content/SimpleContentProvider.java
[4]: example/src/edu/mit/mobile/android/content/example/SampleProvider.java
[5]: example/src/edu/mit/mobile/android/content/example/Message.java
[6]: example/
[7]: http://mobile.mit.edu/
[8]: http://en.wikipedia.org/wiki/Representational_State_Transfer
