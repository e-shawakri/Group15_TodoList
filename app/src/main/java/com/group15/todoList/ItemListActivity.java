package com.group15.todoList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import androidx.appcompat.app.AppCompatActivity;
import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItemCRUDAccessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Show a list of items
 * 
 * @author Joern Kreutel
 * 
 */
public class ItemListActivity extends AppCompatActivity {

	// the logger
	protected static final String logger = ItemListActivity.class
			.getSimpleName();

	/**
	 * the constant for the subview request
	 */
	public static final int REQUEST_ITEM_DETAILS = 2;

	/**
	 * the constant for the new item request
	 */
	public static final int REQUEST_ITEM_CREATION = 1;

	/**
	 * the items that will be display
	 */
	private List<DataItem> itemlist;

	/**
	 * the listview that will display the items
	 */
	private ListView listview;

	/**
	 * the adapter that mediates between the itemlist and the view
	 */
	private ArrayAdapter<DataItem> adapter;

	/**
	 * the data accessor for the data items
	 */
	private DataItemCRUDAccessor accessor;

	static private class ViewHolder {
		private TextView mTextView;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlistview);

		try {
			// access the listview
			/*
			 * access the list view for the options to be displayed
			 */
			listview = (ListView) findViewById(R.id.list);

			// the button for adding new items
			Button newitemButton = (Button) findViewById(R.id.newitemButton);

			// obtain the accessor from the application, and pass it the
			// accesorId we have been passed ourselves by the intent
			accessor = ((DataAccessRemoteApplication) getApplication())
					.getDataItemAccessor(getIntent().getExtras().getInt(
							"accessorId"));

			// set the title of the activity given the accessor class
			setTitle(accessor
					.getClass()
					.getName()
					.substring(
							accessor.getClass().getName().lastIndexOf(".") + 1));

			Log.i(logger, "will use accessor: " + accessor);

			// initialise the item list
			this.itemlist = new ArrayList<DataItem>();

			// create the adapter
			this.adapter = new ArrayAdapter<DataItem>(this,
					R.layout.item_in_listview, itemlist) {

				// we override getView and manually create the views for each
				// list element
				@Override
				public View getView(int position, View itemView,
						ViewGroup parent) {
					Log.d(logger, "getView() has been invoked for item: "
							+ itemlist.get(position));
					View listitemView;
					if (itemView == null) {
						listitemView = (ViewGroup) getLayoutInflater().inflate(R.layout.item_in_listview, null);

						final ViewHolder viewHolder = new ViewHolder();
						viewHolder.mTextView = (TextView) listitemView
								.findViewById(R.id.itemName);

						listitemView.setTag(viewHolder);
					} else {
						listitemView = itemView;
					}
					ViewHolder holder = (ViewHolder) listitemView.getTag();
					holder.mTextView.setText(itemlist.get(position).getName());

					return listitemView;
				}
			};
			// the adapter is set to display changes immediately
			this.adapter.setNotifyOnChange(true);

			// set the adapter on the list view
			listview.setAdapter(this.adapter);

			// set a listener that reacts to the selection of an element
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView,
						View itemView, int itemPosition, long itemId) {

					Log.i(logger, "onItemClick: position is: " + itemPosition
							+ ", id is: " + itemId);

					DataItem item = itemlist.get(itemPosition);

					processItemSelection(item);
				}

			});

			// set the listview as scrollable
			listview.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);

			// set a listener for the newItemButton
			newitemButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					processNewItemRequest();
				}

			});

			// finally, we add the itemlist asynchronously
			new AsyncTask<Void, Void, List<DataItem>>() {
				@Override
				protected List<DataItem> doInBackground(Void... items) {
					return ItemListActivity.this.accessor.readAllItems();
				}

				@Override
				protected void onPostExecute(List<DataItem> items) {
					itemlist.addAll(items);
					adapter.notifyDataSetChanged();
				}
			}.execute();

		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			((DataAccessRemoteApplication) getApplication()).reportError(this,
					err);
		}

	}

	protected void processNewItemRequest() {
		Log.i(logger, "processNewItemRequest()");
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);

		// start the details activity with the intent
		startActivityForResult(intent, REQUEST_ITEM_CREATION);
	}

	protected void processItemSelection(DataItem item) {
		Log.i(logger, "processItemSelection(): " + item);
		// create an intent for opening the details view
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);
		// pass the item to the intent
		intent.putExtra(ItemDetailsActivity.ARG_ITEM_OBJECT, item);

		// start the details activity with the intent
		startActivityForResult(intent, REQUEST_ITEM_DETAILS);
	}

	/**
	 * process the result of the item details subactivity, which may be the
	 * creation, modification or deletion of an item.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Log.i(logger, "onActivityResult(): " + data);

		DataItem item = data != null ? (DataItem) data
				.getSerializableExtra(ItemDetailsActivity.ARG_ITEM_OBJECT)
				: null;

		// check which request we had
		if (requestCode == REQUEST_ITEM_CREATION
				&& resultCode == ItemDetailsActivity.RESPONSE_ITEM_UPDATED) {
			Log.i(logger, "onActivityResult(): adding the created item");

			/**
			 * all accessor calls are executed asynchronously
			 */
			new AsyncTask<DataItem, Void, DataItem>() {
				@Override
				protected DataItem doInBackground(DataItem... items) {
					return ItemListActivity.this.accessor.createItem(items[0]);
				}

				@Override
				protected void onPostExecute(DataItem item) {
					if (item != null) {
						adapter.add(item);
					}
				}
			}.execute(item);

		} else if (requestCode == REQUEST_ITEM_DETAILS) {
			if (resultCode == ItemDetailsActivity.RESPONSE_ITEM_UPDATED) {
				Log.i(logger, "onActivityResult(): updating the edited item");

				new AsyncTask<DataItem, Void, DataItem>() {
					@Override
					protected DataItem doInBackground(DataItem... items) {
						return ItemListActivity.this.accessor
								.updateItem(items[0]);
					}

					@Override
					protected void onPostExecute(DataItem item) {
						if (item != null) {
							// read out the item from the list and update it
							itemlist.get(itemlist.indexOf(item)).updateFrom(
									item);
							// notify the adapter that the item has been changed
							adapter.notifyDataSetChanged();
						}
					}
				}.execute(item);

			} else if (resultCode == ItemDetailsActivity.RESPONSE_ITEM_DELETED) {

				new AsyncTask<DataItem, Void, DataItem>() {
					@Override
					protected DataItem doInBackground(DataItem... items) {
						if (ItemListActivity.this.accessor.deleteItem(items[0]
								.getId())) {
							return items[0];
						} else {
							Log.e(logger, "the item" + items[0]
									+ " could not be deleted by the accessor!");
							return null;
						}
					}

					@Override
					protected void onPostExecute(DataItem item) {
						if (item != null) {
							adapter.remove(item);
						}
					}
				}.execute(item);
			}
		}

	}

}
