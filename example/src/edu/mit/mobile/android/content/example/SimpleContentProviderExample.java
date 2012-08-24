package edu.mit.mobile.android.content.example;

import java.util.Random;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SimpleContentProviderExample extends ListActivity implements OnClickListener {

    private ListAdapter mListAdapter;

    private static final String[] TITLES = { "Party Rock Anthem", "Give Me Everything",
            "Rolling In The Deep", "Last Friday Night (T.G.I.F.)", "Super Bass",
            "The Edge Of Glory", "How To Love", "Good Life", "Tonight Tonight", "E.T." };

    private static final String[] BODIES = {
            "AWESOME!",
            "seriously this video was trying WAYYY tooo hard.. it was not at all funny nor amusing, i was getting disgusted by the whole thing.",
            "anyone knows whats the name of the remix?", "I enjoy the song though(:",
            "what the heck????", "That wuz funny", "i love this video", "best vid eva!!!",
            "like kanye west version alot better", "you done an amzing job with the lyrics" };

    private final Random mRand = new Random();

    /** Called when the activity is first created. */
    // the deprecation here is due to the managedQuery() calls. In the latest Android version,
    // Loaders should be used.
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getListView().setEmptyView(findViewById(android.R.id.empty));

        // The button bar is only needed if there is no action bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            findViewById(R.id.button_bar).setVisibility(View.GONE);
        }

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.add_random).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);

        // the column names that data will be loaded from
        final String[] from = new String[] { Message.TITLE, Message.BODY };

        // the resource IDs that the data will be loaded into
        final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

        // the columns to query.
        final String[] projection = new String[] { Message._ID, Message.TITLE, Message.BODY };

        final String sortOrder = Message.CREATED_DATE + " DESC";

        // this makes the actual database query, returning a cursor that can be
        // read directly
        // or using an Adapter.
        final Cursor c = managedQuery(Message.CONTENT_URI, projection, null, null, sortOrder);

        // This adapter binds the data from the cursor to the specified view.
        // Android provides two simple list views:
        // android.R.layout.simple_list_item_2 which has two text views
        // and android.R.layout.simple_list_item_1 which has only one
        mListAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, from,
                to);

        // A ListActivity has a simple ListView by default and this tells it
        // which adapter to use
        setListAdapter(mListAdapter);

        registerForContextMenu(getListView());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // based on the ID provided by the list, reconstruct the message URI
        final Uri message = ContentUris.withAppendedId(Message.CONTENT_URI, id);

        // Once we have the message URI, one can simply call the VIEW action on it:
        final Intent viewMessage = new Intent(Intent.ACTION_VIEW, message);
        startActivity(viewMessage);

        // Android will see which activity or activities are capable of VIEWing a message
        // item by looking through the Manifest to find the right Activity for the given
        // content MIME type and action. If more than one activity is found, it will prompt
        // the user and ask which Activity they would like to use for this type.
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (final ClassCastException e) {

            return false;
        }

        final Uri itemUri = ContentUris.withAppendedId(Message.CONTENT_URI, info.id);

        switch (item.getItemId()) {
            case R.id.view:
                startActivity(new Intent(Intent.ACTION_VIEW, itemUri));
                return true;

            case R.id.edit:
                startActivity(new Intent(Intent.ACTION_EDIT, itemUri));
                return true;

            case R.id.delete:
                deleteItem(itemUri);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // load the action bar / menu bar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                createNewItem();
                return true;

            case R.id.add_random:
                addItem();
                return true;

            case R.id.clear:
                clearAllItems();
                return true;

            case R.id.add_many:
                addManyItems();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Starts a new activity to prompt the user with the contents of a new item.
     */
    private void createNewItem() {
        // the URI must be specified here, so we know what needs have a new item.
        startActivity(new Intent(Intent.ACTION_INSERT, Message.CONTENT_URI));
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
            Toast.makeText(this, "Error inserting item. insert() returned null", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * Adds a bunch of items to the database. This interactively demonstrates the use of
     * {@link ContentResolver#bulkInsert(Uri, ContentValues[])}.
     */
    private void addManyItems() {
        final int total = 100;
        final ContentValues manyCv[] = new ContentValues[total];

        for (int i = 0; i < total; i++) {
            // place your content inside a ContentValues object.
            final ContentValues cv = new ContentValues();
            cv.put(Message.TITLE, TITLES[mRand.nextInt(TITLES.length)]);
            cv.put(Message.BODY, BODIES[mRand.nextInt(BODIES.length)]);

            manyCv[i] = cv;
        }

        final int count = getContentResolver().bulkInsert(Message.CONTENT_URI, manyCv);

        Toast.makeText(this, count + " items added", Toast.LENGTH_SHORT).show();
    }

    /**
     * Deletes the selected item from the database.
     */
    private void deleteItem(Uri item) {
        // the second two arguments are null here, as the row is specified using the URI
        final int count = getContentResolver().delete(item, null, null);
        Toast.makeText(this, count + " rows deleted", Toast.LENGTH_SHORT).show();

    }

    /**
     * Deletes all the items from the database.
     */
    private void clearAllItems() {
        // Specify the dir URI, along with null in the where and selectionArgs
        // to delete everything.
        deleteItem(Message.CONTENT_URI);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                createNewItem();
                break;

            case R.id.add_random:
                addItem();
                break;

            case R.id.clear:
                clearAllItems();
                break;

        }
    }
}