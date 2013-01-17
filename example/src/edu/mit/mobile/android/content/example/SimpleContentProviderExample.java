package edu.mit.mobile.android.content.example;

import java.util.Random;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleContentProviderExample extends ListActivity implements OnClickListener {

    private SimpleCursorAdapter mListAdapter;

    private static final String[] TITLES = { "Party Rock Anthem", "Give Me Everything",
            "Rolling In The Deep", "Last Friday Night (T.G.I.F.)", "Super Bass",
            "The Edge Of Glory", "How To Love", "Good Life", "Tonight Tonight", "E.T." };

    private static final String[] BODIES = {
            "AWESOME!",
            "seriously this video was trying WAYYY tooo hard.. it was not at all funny nor amusing, i was getting disgusted by the whole thing.",
            "anyone knows whats the name of the remix?", "I enjoy the song though(:",
            "what the heck????", "That wuz funny", "i love this video", "best vid eva!!!",
            "like kanye west version alot better", "you done an amzing job with the lyrics" };

    private static final int DIALOG_FILTER = 100;

    private final Random mRand = new Random();

    // the column names that data will be loaded from
    private final static String[] FROM = new String[] { Message.TITLE, Message.BODY };

    // the resource IDs that the data will be loaded into
    private final static int[] TO = new int[] { android.R.id.text1, android.R.id.text2 };

    // the columns to query.
    private final static String[] PROJECTION = new String[] { Message._ID, Message.TITLE,
            Message.BODY };

    private final static String SORT_ORDER = Message.CREATED_DATE + " DESC";

    /** Called when the activity is first created. */
    // the deprecation here is due to the managedQuery() calls. In the latest Android version,
    // Loaders should be used.
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        onNewIntent(getIntent());

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


        // pull the desired content URI from the intent
        Uri content = getIntent().getData();

        // if there is none (as would be the case with a normal application launch from the
        // launcher), set it to the default list of all items.
        if (content == null) {
            content = Message.CONTENT_URI;
        }

        // This adapter binds the data from the cursor to the specified view.
        // Android provides two simple list views:
        // android.R.layout.simple_list_item_2 which has two text views
        // and android.R.layout.simple_list_item_1 which has only one
        mListAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null,
                FROM, TO);

        // this makes the actual database query, returning a cursor that can be
        // read directly or using an Adapter.
        loadContent(content);

        // A ListActivity has a simple ListView by default and this tells it
        // which adapter to use
        setListAdapter(mListAdapter);

        registerForContextMenu(getListView());
    }

    // this is called when the activity is re-started by the system with a new intent. This is used
    // with the filter and search.
    @Override
    protected void onNewIntent(Intent intent) {
        final String action = intent.getAction();
        final Uri data = intent.getData();

        // When an item from the search list is clicked, it will deliver this intent to this
        // activity. The action is defined in the res/xml/searchable and the data is defined in the
        // SampleProvider's SearchDBHelper configuration.
        if (Intent.ACTION_VIEW.equals(action) && data != null
                && Message.CONTENT_TYPE_ITEM.equals(getContentResolver().getType(data))) {

            // the intent delivered to this activity contains extra component information that
            // forces this activity to be displayed. We don't actually want that (it's
            // probably in place for security reasons), so we re-launch the intent without
            // specifying the activity to show. This will cause the system to look at all of the
            // Category.DEFAULT intent filters on the system that match the given action and content
            // type. See the onListItemClick below for another example of this.
            startActivity(new Intent(action, data));
            finish();
            return;

        } else if (Intent.ACTION_SEARCH.equals(action)) {
            // TODO add

            // this is called mostly by the filter dialog.
        }else if (Intent.ACTION_VIEW.equals(action) && data != null
                && Message.CONTENT_TYPE_DIR.equals(getContentResolver().getType(data))){
            loadContent(data);
        }

        setIntent(intent);
    }

    /**
     * Loads the given content into the adapter.
     *
     * @param data
     */
    @SuppressWarnings("deprecation")
    private void loadContent(Uri data) {
        try {
            // none of the cursors need to be closed, as managedQuery will take care of this. Note:
            // in later versions of Android, one should use a CursorLoader instead.
            final Cursor c = managedQuery(data, PROJECTION, null, null, SORT_ORDER);
            mListAdapter.swapCursor(c);

            // this exception is triggered when there's an invalid filter.
        } catch (final IllegalArgumentException e) {
            managedQuery(Message.CONTENT_URI, PROJECTION, null, null, SORT_ORDER);
            Toast.makeText(this, "Filter string is invalid: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG).show();
        }
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

    @SuppressWarnings("deprecation")
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

            case R.id.filter:
                showDialog(DIALOG_FILTER);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id, Bundle args) {
        switch (id) {
            case DIALOG_FILTER: {
                final View view = getLayoutInflater().inflate(R.layout.dialog_filter, null, false);
                final TextView filter = (TextView) view.findViewById(R.id.filter);

                return new AlertDialog.Builder(this)
                        .setView(view)
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // construct the filter URI based on user input. Note the
                                        // use of encodedQuery here, as otherwise the encoder would
                                        // escape our "|" and other such characters. Normally, in
                                        // code, you'd just use {@link QueryBuilder} to ensure
                                        // everything is properly escaped.
                                        final Uri filterUri = Message.CONTENT_URI.buildUpon()
                                                .encodedQuery(filter.getText().toString()).build();

                                        // once the filter URI is created, launch it! This will be
                                        // routed through Android (due to our declaration in our
                                        // Mainifest) and then onNewIntent() will be called with the
                                        // new URI.
                                        startActivity(new Intent(Intent.ACTION_VIEW, filterUri));
                                    }
                                }).create();
            }
            default:
                return super.onCreateDialog(id, args);
        }
    }

    @Override
    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        switch (id) {
            case DIALOG_FILTER: {
                // this ensures that the pre-filled filter matches the currently-displayed content
                final Uri data = getIntent().getData();
                final String query = data != null ? data.getEncodedQuery() : "";
                final EditText filter = ((EditText) dialog.findViewById(R.id.filter));
                filter.setText(query);
                filter.setSelection(query.length());
            }
                break;

            default:
                super.onPrepareDialog(id, dialog, args);
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