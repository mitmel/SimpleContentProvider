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
import android.widget.TextView;
import android.widget.Toast;

public class MessageEdit extends Activity implements OnClickListener{
	private EditText mId, mTitle, mBody;
	private Button mSave, mCancel;
	private String mAction = "";
	private TextView mLabelId;

	private static final String[] PROJECTION = { Message._ID, Message.TITLE,
		Message.BODY };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAction = this.getIntent().getAction();

		setContentView(R.layout.edit);

		mTitle = (EditText) findViewById(R.id.edtTitle);
		mBody = (EditText) findViewById(R.id.edtBody);
		mId = (EditText) findViewById(R.id.edtId);

		mSave = (Button) findViewById(R.id.btn_save);
		mCancel = (Button) findViewById(R.id.btn_cancel);

		mLabelId = (TextView)findViewById(R.id.id_label);

		final boolean isInsertAction = Intent.ACTION_INSERT.equals(mAction);

		if (isInsertAction) {
			mId.setVisibility(View.GONE);
			mLabelId.setVisibility(View.GONE);
			mSave.setText(R.string.button_add);
			mSave.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_add, 0, 0, 0);
		}

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

		} else if (Intent.ACTION_INSERT.equals(action)){
			// nothing to do, this is a valid action. See the save method.

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

	/**
	 * Validates the form and alerts the user to any invalid data
	 *
	 * @return true if the filled-in form is valid
	 */
	private boolean validate(){

		// setError is a great way to display error messages to users, as it
		// keeps the message close to the source of the error.
		if(mTitle.getText().length() == 0){
			mTitle.setError("Please enter a title");
			mTitle.requestFocus();
			return false;
		}

		if(mBody.getText().length() == 0){
			mBody.setError("Please enter a body");
			mBody.requestFocus();
			return false;
		}

		return true;
	}

	/**
	 * Convert the currently filled-in form to a ContentValues object that can be saved
	 *
	 * @return the current user-entered data as ContentValues
	 */
	private ContentValues toContentValues(){
		// place your content inside a ContentValues object.
		final ContentValues cv = new ContentValues();
		cv.put(Message.TITLE, mTitle.getText().toString());
		cv.put(Message.BODY, mBody.getText().toString());

		return cv;
	}

	private void saveChanges() {
		// validate the form
		if (!validate()){
			return;
		}

		// get the user-entered data
		final ContentValues cv = toContentValues();

		// The URI of the message (eg content://.../message/4) is stored in the intent
		// that we used when starting this activity.
		final Intent intent = getIntent();
		final Uri message = intent.getData();
		final String action = intent.getAction();

		if (Intent.ACTION_EDIT.equals(action) && message != null){
			final int count = getContentResolver().update(message, cv, null, null);

			if (count <= 0) {
				Toast.makeText(this,
						"Error updating item. update() returned " + count,
						Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this,
						"Message was updated" ,
						Toast.LENGTH_LONG).show();
				finish();
			}

		// the URI for ACTION_INSERT should be the index that the content should be inserted into.
		}else if (Intent.ACTION_INSERT.equals(action)){
			final Uri newUri = getContentResolver().insert(message, cv);
			if (newUri == null){
				Toast.makeText(this, "Error creating new message", Toast.LENGTH_LONG).show();
				return;
			}
			Toast.makeText(this, "New message created at: " + newUri, Toast.LENGTH_LONG).show();

			// the below isn't needed, but it will make it so that we could potentially get the newly created URI
			// if we needed it using startActivityForResult()
			setResult(RESULT_OK, new Intent().setData(newUri));

			finish();

		}else{
			Toast.makeText(this, "saveChanges() was called for an unhandled intent:" + intent, Toast.LENGTH_LONG).show();
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
