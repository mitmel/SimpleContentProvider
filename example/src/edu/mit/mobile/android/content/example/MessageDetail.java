package edu.mit.mobile.android.content.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A detail view of a single message. Responds to the VIEW intent for a single message content item.
 *
 * @author Steve Pomeroy
 *
 */
public class MessageDetail extends Activity {
	private TextView mTitle, mBody, mDate;

	private static final String[] PROJECTION = { Message._ID, Message.TITLE, Message.BODY,
			Message.CREATED_DATE };

	private static final int DIALOG_CONFIRM_DELETE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		mTitle = (TextView) findViewById(R.id.title);
		mBody = (TextView) findViewById(R.id.body);
		mDate = (TextView) findViewById(R.id.date);

		final Intent intent = getIntent();

		final Uri message = intent.getData();
		final String action = intent.getAction();

		// While we declared an intent filter in the manifest specifying the
		// VIEW action and the message content type, it's still possible that
		// the activity might be started explicitly using the classname. This is
		// just a sanity check to prevent any null pointer exceptions or
		// to alert of any programmer errors.
		if (Intent.ACTION_VIEW.equals(action) && message != null) {

			// load the content on a background thread
			new ContentLoadTask().execute(message);
		} else {
			Toast.makeText(
					this,
					MessageDetail.class.getSimpleName()
							+ " doesn't know how to handle the intent: " + intent,
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// the item context menu can be easily reused for a detail action bar
		getMenuInflater().inflate(R.menu.item_context, menu);

		// this is the view screen, so we hide this.
		menu.findItem(R.id.view).setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.edit:
				startActivity(new Intent(Intent.ACTION_EDIT, getIntent().getData()));
				break;

			case R.id.delete:
				showDialog(DIALOG_CONFIRM_DELETE);
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_CONFIRM_DELETE:
				return new AlertDialog.Builder(this).setCancelable(true)
						.setPositiveButton(R.string.delete, mDeleteDialogListener)
						.setNegativeButton(android.R.string.cancel, mDeleteDialogListener).create();
			default:
				return super.onCreateDialog(id);
		}
	}

	private Cursor queryDatabase(Uri message) {
		@SuppressWarnings("deprecation")
		final Cursor c = managedQuery(message, PROJECTION, null, null, null);
		return c;
	}

	/**
	 * Loads the content into the Activity.
	 *
	 * @param message
	 *            a cursor which contains the content of the message to load.
	 */
	private void loadContent(Cursor c) {

		// One should always check that the returned cursor has content. The
		// cursor will automatically seek to the first position, so
		// this does both.
		if (c.moveToFirst()) {
			// If one is loading many items that all use the same column index,
			// it's most efficient to cache the column indexes to local
			// variables. Otherwise, one can just use the shortcut shown below:
			final String title = c.getString(c.getColumnIndex(Message.TITLE));

			mTitle.setText(title);
			setTitle(title);

			mBody.setText(c.getString(c.getColumnIndex(Message.BODY)));

			// This shows an example of using dates.
			mDate.setText("Created: "

					// There are many useful date formatting functions in
					// DateUtils.
					+ DateUtils.formatDateTime(this,
							c.getLong(c.getColumnIndex(Message.CREATED_DATE)),
							DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME
									| DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR));
		} else {
			// If moveToFirst didn't return true, that means our URI no longer
			// resolves. The most common reason for this is that the content was
			// deleted.
			finish();
		}
	}

	private void deleteItem() {
		getContentResolver().delete(getIntent().getData(), null, null);
		finish();
	}

	/**
	 * A background thread that shows an indeterminate progress bar in the window title while the
	 * content is loading.
	 *
	 */
	private class ContentLoadTask extends AsyncTask<Uri, Void, Cursor> {

		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Cursor doInBackground(Uri... params) {
			return queryDatabase(params[0]);
		}

		@Override
		protected void onPostExecute(Cursor result) {
			// load content always needs to be run on a UI thread as it modifies UI widgets
			loadContent(result);
			setProgressBarIndeterminateVisibility(false);
		}
	}

	private final OnClickListener mDeleteDialogListener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					deleteItem();
					break;
			}
		}
	};
}
