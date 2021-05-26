package com.group15.todoList;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItem.ItemTypes;

import java.util.Arrays;

/**
 * Show the details of an item
 *
 * this activity exemplifies the case where the activity itself implements the
 * OnClickListener interface
 *
 * @author Joern Kreutel
 *
 */
public class ItemDetailsActivity extends AppCompatActivity implements OnClickListener {

	/**
	 * the logger
	 */
	protected static final String logger = ItemDetailsActivity.class
			.getSimpleName();

	/**
	 * the argname with which we will pass the item to the subview
	 */
	public static final String ARG_ITEM_OBJECT = "itemObject";

	/**
	 * the result code that indicates that some item was changed
	 */
	public static final int RESPONSE_ITEM_UPDATED = 1;

	/**
	 * the result code that indicates that the item shall be deleted
	 */
	public static final int RESPONSE_ITEM_DELETED = 2;

	/**
	 * the result code that indicates that nothing has been changed
	 */
	public static final int RESPONSE_NOCHANGE = -1;

	/**
	 * the item we are dealing with (which might be created by ourselves in case
	 * the user has chosen the create item action
	 */
	private DataItem item;

	/**
	 * the ui fields the hold the name and the description of the item
	 */
	private EditText itemName;
	private EditText itemDescription;

	/**
	 * the buttons for save and delete (we need to declare them as instance
	 * attributes in order to implement the onClick method in the activity
	 * itself, rather than using anonymous inner classes! Note that a further
	 * alternative would be to use the android:onClick attribute inside of the
	 * layout.)
	 */
	private Button saveButton;
	private Button deleteButton;

	/**
	 * the spinner for selecting the iconpath
	 */
	protected Spinner iconpathSpinner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemview);

		try {
			// obtain the ui elements
			this.itemName = (EditText) findViewById(R.id.item_name);
			this.itemDescription = (EditText) findViewById(R.id.item_description);
			this.saveButton = (Button) findViewById(R.id.saveButton);
			this.deleteButton = (Button) findViewById(R.id.deleteButton);
			this.iconpathSpinner = (Spinner) findViewById(R.id.item_iconname);

			// set the title of the activity given our own class
			setTitle(this.getClass().getName()
					.substring(this.getClass().getName().lastIndexOf(".") + 1));

			// check whether we have been passed an item
			this.item = (DataItem) getIntent().getSerializableExtra(
					ARG_ITEM_OBJECT);
			Log.i(logger, "obtained item from intent: " + this.item);

			// if we do not have an item, we assume we need to create a new one
			if (this.item != null) {
				// set name and description
				itemName.setText(item.getName());
				itemDescription.setText(item.getDescription());
				if (item.getIconId() != null && !"".equals(item.getIconId())) {
					iconpathSpinner.setSelection(getIconIdIndex(item
							.getIconId()));
				}
			} else {
				this.item = new DataItem(-1, ItemTypes.TYPE1, "New Item", "",
						null);
			}

			// we set ourselves as listener on the buttons and the text fields
			this.saveButton.setOnClickListener(this);
			this.deleteButton.setOnClickListener(this);

			// set a listener on the spinner
			iconpathSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							Log.d(logger, "got a selection: " + arg2);
							String selectedItem = (String) arg0
									.getSelectedItem();

							String imgpath = getIconId4Iconname(selectedItem);

							updateBackgroundImage(imgpath);
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}
					});

		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			((DataAccessRemoteApplication) getApplication()).reportError(this,
					err);
		}

	}

	/**
	 * save the item and finish
	 */
	protected void processItemSave() {
		// re-set the fields of the item
		this.item.setName(this.itemName.getText().toString());
		this.item.setDescription(this.itemDescription.getText().toString());
		this.item.setIconId(getIconId4Iconname(String.valueOf(iconpathSpinner
				.getSelectedItem())));

		// and return to the calling activity
		Intent returnIntent = new Intent();

		// set the item on the intent
		returnIntent.putExtra(ARG_ITEM_OBJECT, this.item);

		// set the result code
		setResult(RESPONSE_ITEM_UPDATED, returnIntent);

		// and finish
		finish();
	}

	/**
	 * delete the item and finish
	 */
	protected void processItemDelete() {
		// and return to the calling activity
		Intent returnIntent = new Intent();

		// set the item
		returnIntent.putExtra(ARG_ITEM_OBJECT, this.item);

		// set the result code
		setResult(RESPONSE_ITEM_DELETED, returnIntent);

		// and finish
		finish();
	}

	@Override
	public void onClick(View view) {
		if (view == this.saveButton) {
			Log.i(logger, "got onClick() on saveButton");
			processItemSave();
		} else if (view == this.deleteButton) {
			Log.i(logger, "got onClick() ond deleteButton");
			processItemDelete();
		} else {
			Log.w(logger,
					"got onClick() on view where it will not be handled: "
							+ view);
		}
	}

	// we obtain the index of the iconId in the array background_resources_list,
	// which contains the system internal names for the icons (in contrast, the
	// spinner displays the names in German)
	/**
	 *
	 * @param iconId
	 * @return
	 */
	protected int getIconIdIndex(String iconId) {
		return Arrays.asList(
				getResources()
						.getStringArray(R.array.background_resources_list))
				.indexOf(iconId);
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	protected String getIconId4Iconname(String name) {
		return getResources().getStringArray(R.array.background_resources_list)[Arrays
				.asList(getResources().getStringArray(R.array.background_list))
				.indexOf(name)];
	}

	/**
	 * update the background image
	 */
	private void updateBackgroundImage(final String resourcepath) {

		Log.d(logger, "updating background image using resource: " + resourcepath);

		// we use an async task for loading the image
		new AsyncTask<Void, Void, Object>() {

			/*
			 * the "background process": load the image from the url
			 */
			@Override
			protected Object doInBackground(Void... arg) {
				try {
					final Drawable imgcontent = new BitmapDrawable(getResources(),
							BitmapFactory
									.decodeStream(((DataAccessRemoteApplication) getApplication())
											.getMediaResourceAccessor()
											.readMediaResource(resourcepath + ".png")));
					Log.d(logger, "got content: " + imgcontent + " of class " + imgcontent != null ? imgcontent.getClass().getName() : "<null>");

					return imgcontent;
				} catch (Exception e) {
					return e;
				}
			}

			/**
			 * once the process has been terminated: update the background or
			 * display an error message
			 */
			@Override
			protected void onPostExecute(Object result) {
				if (result instanceof Drawable) {
					((ViewGroup) findViewById(R.id.saveButton).getParent())
							.setBackground((Drawable) result);
				} else {
					((DataAccessRemoteApplication) getApplication()).reportError(
							ItemDetailsActivity.this,
							((Exception) result).getMessage());
					((Exception)result).printStackTrace();
				}
			}

		}.execute();
	}

}
