package com.group15.todoList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;

import com.group15.todoList.model.accessors.HttpURLConnectionTodoItemCRUDAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemListActivity extends AppCompatActivity {

	protected static final String logger = ItemListActivity.class
			.getSimpleName();

	public static final int REQUEST_ITEM_DETAILS = 2;

	public static final int REQUEST_ITEM_CREATION = 1;

	private List<TodoItem> itemlist;

	private ListView listview;

	private ArrayAdapter<TodoItem> adapter;

	private TodoItemCRUDAccessor accessor;

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

			accessor = new HttpURLConnectionTodoItemCRUDAccessor("http://10.0.2.2:8080/backend-1.0-SNAPSHOT/rest/todoitems");

			setTitle("Todo's");

			Log.i(logger, "will use accessor: " + accessor);

			this.itemlist = new ArrayList<TodoItem>();

			this.adapter = new ArrayAdapter<TodoItem>(this, R.layout.item_in_listview, itemlist) {
				@Override
				public View getView(int position, View itemView, ViewGroup parent) {
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
				public void onItemClick(AdapterView<?> adapterView, View itemView, int itemPosition, long itemId) {
					TodoItem item = itemlist.get(itemPosition);

					processItemSelection(item);
				}
			});

			listview.setScrollBarStyle(ListView.SCROLLBARS_INSIDE_OVERLAY);

			newitemButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					processNewItemRequest();
				}

			});

			new AsyncTask<Void, Void, List<TodoItem>>() {
				@Override
				protected List<TodoItem> doInBackground(Void... items) {
					return ItemListActivity.this.accessor.readAllItems();
				}

				@Override
				protected void onPostExecute(List<TodoItem> items) {
					itemlist.addAll(items);

					adapter.notifyDataSetChanged();
				}
			}.execute();

		} catch (Exception e) {
			String err = "got exception: " + e;
			Log.e(logger, err, e);
			((DataAccessRemoteApplication) getApplication()).reportError(this, err);
		}
	}

	protected void processNewItemRequest() {
		Log.i(logger, "processNewItemRequest()");
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);

		startActivityForResult(intent, REQUEST_ITEM_CREATION);
	}

	protected void processItemSelection(TodoItem item) {
		Log.i(logger, "processItemSelection(): " + item.toString());
		Intent intent = new Intent(ItemListActivity.this,
				ItemDetailsActivity.class);
		intent.putExtra(ItemDetailsActivity.ARG_ITEM_OBJECT, item);

		startActivityForResult(intent, REQUEST_ITEM_DETAILS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Log.i(logger, "onActivityResult(): " + data);

		TodoItem item = data != null ? (TodoItem) data
				.getSerializableExtra(ItemDetailsActivity.ARG_ITEM_OBJECT)
				: null;

		if (requestCode == REQUEST_ITEM_CREATION
				&& resultCode == ItemDetailsActivity.RESPONSE_ITEM_UPDATED) {

			new AsyncTask<TodoItem, Void, TodoItem>() {
				@Override
				protected TodoItem doInBackground(TodoItem... item) {

					item[0].setId(itemlist.size());
					return ItemListActivity.this.accessor.createItem(item[0]);
				}

				@Override
				protected void onPostExecute(TodoItem item) {
					if (item != null) {
						adapter.add(item);
					}
				}
			}.execute(item);

		} else if (requestCode == REQUEST_ITEM_DETAILS) {
			if (resultCode == ItemDetailsActivity.RESPONSE_ITEM_UPDATED) {
				new AsyncTask<TodoItem, Void, TodoItem>() {
					@Override
					protected TodoItem doInBackground(TodoItem... items) {
						return ItemListActivity.this.accessor
								.updateItem(items[0]);
					}

					@RequiresApi(api = Build.VERSION_CODES.N)
					@Override
					protected void onPostExecute(TodoItem item) {
						if (item != null) {
							List<TodoItem> newItemList = itemlist.stream()
									.map(i -> i.getId() == item.getId() ? item : i)
									.collect(Collectors.toList());

							itemlist.clear();
							itemlist.addAll(newItemList);

							adapter.notifyDataSetChanged();
						}
					}
				}.execute(item);

			} else if (resultCode == ItemDetailsActivity.RESPONSE_ITEM_DELETED) {

				new AsyncTask<TodoItem, Void, TodoItem>() {
					@Override
					protected TodoItem doInBackground(TodoItem... items) {
						if (ItemListActivity.this.accessor.deleteItem(items[0].getId())) {
							return items[0];
						} else {
							return null;
						}
					}

					@RequiresApi(api = Build.VERSION_CODES.N)
					@Override
					protected void onPostExecute(TodoItem item) {
						if (item != null) {
							List<TodoItem> newItemList = itemlist.stream()
									.filter(i -> i.getId() != item.getId())
									.collect(Collectors.toList());

							itemlist.clear();
							itemlist.addAll(newItemList);

							adapter.notifyDataSetChanged();
						}
					}
				}.execute(item);
			}
		}

	}

}
