package com.group15.todoList.model.accessors;

import android.util.Log;
import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpURLConnectionTodoItemCRUDAccessor implements
		TodoItemCRUDAccessor {

	protected static String logger = HttpURLConnectionTodoItemCRUDAccessor.class
			.getSimpleName();

	private String baseUrl;

	private ObjectMapper mObjectMapper = new ObjectMapper();

	public HttpURLConnectionTodoItemCRUDAccessor(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public List<TodoItem> readAllItems() {
		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl))
					.openConnection();
			con.setRequestProperty("accept", "application/json");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestMethod("GET");
			InputStream is = con.getInputStream();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				final List <TodoItem> items = mObjectMapper.readValue(is, new TypeReference<List<TodoItem>>() {});

				return items;
			} else {
				Log.e(logger, "readAllItems(): got response code: " + con.getResponseCode());
			}
		} catch (Exception e) {
			Log.e(logger, "readAllItems(): got exception: " + e);
		}

		return new ArrayList<TodoItem>();
	}

	@Override
	public TodoItem createItem(TodoItem item) {
		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl))
					.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(mObjectMapper.writeValueAsString(item).getBytes());
			InputStream is = con.getInputStream();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				TodoItem ret = mObjectMapper.readValue(is, TodoItem.class);
				Log.i(logger, "createItem(): " + ret);

				return ret;
			} else {
				Log.e(logger,
						"createItem(): got response code: "
								+ con.getResponseCode());
			}
		} catch (Exception e) {
			Log.e(logger, "createItem(): got exception: " + e);
		}

		return null;
	}

	@Override
	public TodoItem updateItem(TodoItem item) {
		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl))
					.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(mObjectMapper.writeValueAsString(item).getBytes());
			InputStream is = con.getInputStream();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				final TodoItem result = mObjectMapper.readValue(is, TodoItem.class);

				return result;
			} else {
				Log.e(logger, "updateItem(): got response code: " + con.getResponseCode());
			}
		} catch (Exception e) {
			Log.e(logger, "updateItem(): got exception: " + e);
		}

		return null;
	}


	@Override
	public boolean deleteItem(long itemId) {
		try {
			HttpURLConnection con = (HttpURLConnection) (new URL(baseUrl + "/"
					+ itemId)).openConnection();
			con.setRequestMethod("DELETE");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(mObjectMapper.writeValueAsString(itemId).getBytes());
			InputStream is = con.getInputStream();
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return mObjectMapper.readValue(is, Boolean.class);
			} else {
				Log.e(logger, "deleteItem(): got response code: " + con.getResponseCode());
			}
		} catch (Exception e) {
			Log.e(logger, "deleteItem(): got exception: " + e);
		}

		return false;
	}
}
