package com.group15.todoList.model.accessors;

import android.util.Log;
import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLTodoItemCRUDAccessor implements TodoItemCRUDAccessor {

	protected static String logger = URLTodoItemCRUDAccessor.class
			.getSimpleName();

	// the url from which we read the items
	private URL url;

	private ObjectMapper mObjectMapper = new ObjectMapper();

	public URLTodoItemCRUDAccessor(String urlstring) {
		try {
			this.url = new URL(urlstring);
			Log.i(logger, "created url: " + url);
		} catch (Exception e) {
			Log.e(logger, "got exceotion trying to create url from string "
					+ urlstring + ": " + e, e);
		}
	}

	@Override
	public List<TodoItem> readAllItems() {
		try {
			// access the url
			InputStream is = this.url.openStream();
			return mObjectMapper.readValue(is, new TypeReference<List<TodoItem>>() {});
		} catch (Exception e) {
			Log.e(logger, "readAllItems(): got exception: " + e, e);
			return new ArrayList<TodoItem>();
		}
	}

	@Override
	public TodoItem createItem(TodoItem item) {
		Log.e(logger, "createItem(): cannot execute action...");

		return null;
	}

	@Override
	public boolean deleteItem(long itemId) {
		Log.e(logger, "deleteItem(): cannot execute action...");

		return false;
	}

	@Override
	public TodoItem updateItem(TodoItem item) {
		Log.e(logger, "updateItem(): cannot execute action...");

		return null;
	}

}
