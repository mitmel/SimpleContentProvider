package edu.mit.mobile.android.content.example;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageEdit extends Activity implements OnClickListener{
	private EditText mId, mTitle, mBody;
	private Button mSave, mCancel;	
	
	private static final String[] PROJECTION = { Message._ID, Message.TITLE,
		Message.BODY };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.edit);
		
		mTitle = (EditText) findViewById(R.id.edtTitle);
		mBody = (EditText) findViewById(R.id.edtBody);
		mId = (EditText) findViewById(R.id.edtId);
		
		mSave = (Button) findViewById(R.id.btn_save);
		mCancel = (Button) findViewById(R.id.btn_cancel);
		
		mCancel.setOnClickListener(this);
		mSave.setOnClickListener(this);
		
		final Intent intent = getIntent();
		final Uri message = intent.getData();
		final String action = intent.getAction();
		
		// While we declared an intent filter in the manifest specifying the
		// EDIT action and the message content type, it's still possible that
		// the activity might be started explicitly using the classname. This is
		// just a sanity check to prevent any null pointer exceptions or
		// to alert of any programmer errors.
		if (Intent.ACTION_EDIT.equals(action) && message != null) {
			// load the content on a background thread
			new ContentLoadTask().execute(message);
		} else {
			Toast.makeText(
					this,
					MessageDetail.class.getSimpleName()
							+ " doesn't know how to handle the intent: "
							+ intent, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onClick(View v) {	
		switch (v.getId()) {
		case R.id.btn_save:
			saveChanges();
			break;
		case R.id.btn_cancel:
		default:
			finish();
			break;
		}
	}

	// TODO: Use only one ContentLoadTask to MessageEdit and MessageDetail
	private Cursor queryDatabase(Uri message){
		final Cursor c = managedQuery(message, PROJECTION, null,
				null, null);
		return c;
	}
	
	/**
	 * TODO:
	 *   - Use only one ContentLoadTask to MessageEdit and MessageDetail
	 *   - Implement the 'updated' information on ContentProvider
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
			mId.setText(c.getString(c.getColumnIndex(Message._ID)));
		} else {
			// If moveToFirst didn't return true, that means our URI no longer
			// resolves. The most common reason for this is that the content was
			// deleted.
			finish();
		}
	}
	
	private void saveChanges() {
		// place your content inside a ContentValues object.
		final ContentValues cv = new ContentValues();
		cv.put(Message.TITLE, mTitle.getText().toString());
		cv.put(Message.BODY, mBody.getText().toString());

		// updating the register
		String id = mId.getText().toString();
		int count = getContentResolver().update(Message.CONTENT_URI, cv, Message._ID + "=" + id, null);
		
		if (count <= 0) {
			Toast.makeText(this,
					"Error updating item. update() returned " + count,
					Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this,
					"Register was updated" ,
					Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	// TODO: Use only one ContentLoadTask to MessageEdit and MessageDetail
	/**
	 * A background thread that shows an indeterminate progress bar in the window title
	 * while the content is loading.
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
}
