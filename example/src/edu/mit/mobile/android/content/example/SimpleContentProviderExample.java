package edu.mit.mobile.android.content.example;

import java.util.Random;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SimpleContentProviderExample extends ListActivity implements
		OnClickListener {

	private ListAdapter mListAdapter;

	private static final String[] TITLES = { "Party Rock Anthem",
			"Give Me Everything", "Rolling In The Deep",
			"Last Friday Night (T.G.I.F.)", "Super Bass", "The Edge Of Glory",
			"How To Love", "Good Life", "Tonight Tonight", "E.T." };

	private static final String[] BODIES = {
			"AWESOME!",
			"seriously this video was trying WAYYY tooo hard.. it was not at all funny nor amusing, i was getting disgusted by the whole thing.",
			"anyone knows whats the name of the remix?",
			"I enjoy the song though(:", "what the heck????",
			"That wuz funny", "i love this video", "best vid eva!!!",
			"like kanye west version alot better",
			"you done an amzing job with the lyrics" };

	private final Random mRand = new Random();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		getListView().setEmptyView(findViewById(android.R.id.empty));

		findViewById(R.id.add).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);

		// the column names that data will be loaded from
		final String[] from = new String[] { Message.TITLE, Message.BODY };

		// the resource IDs that the data will be loaded into
		final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		// the columns to query.
		final String[] projection = new String[] { Message._ID, Message.TITLE,
				Message.BODY };

		final String sortOrder = Message.CREATED_DATE + " DESC";

		// this makes the actual database query, returning a cursor that can be
		// read directly
		// or using an Adapter.
		final Cursor c = managedQuery(Message.CONTENT_URI, projection, null,
				null, sortOrder);

		// This adapter binds the data from the cursor to the specified view.
		// Android provides two simple list views:
		// android.R.layout.simple_list_item_2 which has two text views
		// and android.R.layout.simple_list_item_1 which has only one
		mListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, c, from, to);

		// A ListActivity has a simple ListView by default and this tells it
		// which adapter to use
		setListAdapter(mListAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// based on the ID provided by the list, reconstruct the message URI
		final Uri message = ContentUris.withAppendedId(Message.CONTENT_URI, id);

		// Once we have the message URI, one can simply call the VIEW action on it:
		final Intent viewMessage = new Intent(Intent.ACTION_VIEW, message);
		startActivity(viewMessage);

		// Android will see which activit(y|ies) are capable of VIEWing a message
		// item by looking through the Manifest to find the right Activity for the given
		// content MIME type and action. If more than one activity is found, it will prompt
		// the user and ask which Activity they would like to use for this type.
	}

	/**
	 * Generates and adds a random item.
	 */
	private void addItem() {
		// place your content inside a ContentValues object.
		final ContentValues cv = new ContentValues();
		cv.put(Message.TITLE, TITLES[mRand.nextInt(TITLES.length)]);
		cv.put(Message.BODY, BODIES[mRand.nextInt(BODIES.length)]);

		// the URI of the newly created item is returned. Feel free to do whatever
		// you wish with this URI, as this is the public interface to the content.
		final Uri newItem = getContentResolver().insert(Message.CONTENT_URI, cv);
		if (newItem == null) {
			Toast.makeText(this,
					"Error inserting item. insert() returned null",
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Deletes all the items from the database.
	 */
	private void clearAllItems() {
		// delete() with null in the where and selectionArgs parameters will
		// delete all the content.
		final int count = getContentResolver().delete(Message.CONTENT_URI,
				null, null);
		Toast.makeText(this, count + " rows deleted", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add:
			addItem();
			break;

		case R.id.clear:
			clearAllItems();
			break;
		}
	}
}