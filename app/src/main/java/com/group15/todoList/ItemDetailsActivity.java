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
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItem.ItemTypes;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Date;

public class ItemDetailsActivity extends AppCompatActivity implements OnClickListener {

	protected static final String logger = ItemDetailsActivity.class
			.getSimpleName();

	public static final String ARG_ITEM_OBJECT = "itemObject";

	public static final int RESPONSE_ITEM_UPDATED = 1;

	public static final int RESPONSE_ITEM_DELETED = 2;

	public static final int RESPONSE_NOCHANGE = -1;

	private DataItem item;

	private EditText itemName;
	private EditText itemDescription;
	private CheckBox itemFavourite;
	private CalendarView itemDate;

	private Button saveButton;
	private Button deleteButton;

	protected Spinner iconpathSpinner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemview);

		try {
			this.itemName = (EditText) findViewById(R.id.item_name);
			this.itemDescription = (EditText) findViewById(R.id.item_description);
			this.itemFavourite = (CheckBox) findViewById(R.id.item_favourite);
			this.itemDate = (CalendarView) findViewById(R.id.item_date);
			this.saveButton = (Button) findViewById(R.id.saveButton);
			this.deleteButton = (Button) findViewById(R.id.deleteButton);
			this.iconpathSpinner = (Spinner) findViewById(R.id.item_iconname);

			setTitle(this.getClass().getName()
					.substring(this.getClass().getName().lastIndexOf(".") + 1));

			this.item = (DataItem) getIntent().getSerializableExtra(
					ARG_ITEM_OBJECT);
			Log.i(logger, "obtained item from intent: " + this.item);

			if (this.item != null) {
				itemName.setText(item.getName());
				itemDescription.setText(item.getDescription());
				itemFavourite.setChecked(item.isFavourite());
				itemDate.setDate(item.getDate());
			} else {
				this.item = new DataItem(-1, ItemTypes.TYPE1, "New Item", "",
						false, itemDate.getMinDate());
			}

			this.saveButton.setOnClickListener(this);
			this.deleteButton.setOnClickListener(this);

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

	protected void processItemSave() {
		// re-set the fields of the item
		this.item.setName(this.itemName.getText().toString());
		this.item.setDescription(this.itemDescription.getText().toString());
		this.item.setFavourite(this.itemFavourite.isChecked());
		this.item.setDate(this.itemDate.getDate());

		Intent returnIntent = new Intent();

		returnIntent.putExtra(ARG_ITEM_OBJECT, this.item);

		setResult(RESPONSE_ITEM_UPDATED, returnIntent);

		finish();
	}
	protected void processItemDelete() {

		Intent returnIntent = new Intent();

		returnIntent.putExtra(ARG_ITEM_OBJECT, this.item);

		setResult(RESPONSE_ITEM_DELETED, returnIntent);

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

	protected int getIconIdIndex(String iconId) {
		return Arrays.asList(
				getResources()
						.getStringArray(R.array.background_resources_list))
				.indexOf(iconId);
	}

	protected String getIconId4Iconname(String name) {
		return getResources().getStringArray(R.array.background_resources_list)[Arrays
				.asList(getResources().getStringArray(R.array.background_list))
				.indexOf(name)];
	}

	private void updateBackgroundImage(final String resourcepath) {

		Log.d(logger, "updating background image using resource: " + resourcepath);

		new AsyncTask<Void, Void, Object>() {

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
