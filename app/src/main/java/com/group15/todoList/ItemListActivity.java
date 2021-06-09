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

import com.group15.todoList.model.accessors.HttpURLConnectionDataItemCRUDAccessor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity {

	protected static final String logger = ItemListActivity.class
			.getSimpleName();

	public static final int REQUEST_ITEM_DETAILS = 2;

	public static final int REQUEST_ITEM_CREATION = 1;

	private List<DataItem> itemlist;

	private ListView listview;

	private ArrayAdapter<DataItem> adapter;

	private DataItemCRUDAccessor accessor;

	static private class ViewHolder {
		private TextView mTextView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlistview);

		try {
			listview = (ListView) findViewById(R.id.list);

			Button newitemButton = (Button) findViewById(R.id.newitemButton);

			accessor = new HttpURLConnectionDataItemCRUDAccessor("http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest/dataitems");

			setTitle("Todo's");

			Log.i(logger, "will use accessor: " + accessor);

			this.itemlist = new ArrayList<DataItem>();

			this.adapter = new ArrayAdapter<DataItem>(this,
					R.layout.item_in_listview, itemlist) {

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
			this.adapter.setNotifyOnChange(true);

			listview.setAdapter(this.adapter);

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

			listview.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);

			newitemButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					processNewItemRequest();
				}

			});

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

		startActivityForResult(intent, REQUEST_ITEM_CREATION);
	}

	protected void processItemSelection(DataItem item) {
		Log.i(logger, "processItemSelection(): " + item);
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);
		intent.putExtra(ItemDetailsActivity.ARG_ITEM_OBJECT, item);

		startActivityForResult(intent, REQUEST_ITEM_DETAILS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Log.i(logger, "onActivityResult(): " + data);

		DataItem item = data != null ? (DataItem) data
				.getSerializableExtra(ItemDetailsActivity.ARG_ITEM_OBJECT)
				: null;

		if (requestCode == REQUEST_ITEM_CREATION
				&& resultCode == ItemDetailsActivity.RESPONSE_ITEM_UPDATED) {
			Log.i(logger, "item: ");
			Log.i(logger, "onActivityResult(): adding the created item");

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
