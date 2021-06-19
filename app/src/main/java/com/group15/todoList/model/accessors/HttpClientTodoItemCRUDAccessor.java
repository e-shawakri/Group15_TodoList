package com.group15.todoList.model.accessors;

import android.util.Log;
import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientTodoItemCRUDAccessor implements TodoItemCRUDAccessor {

	protected static String logger = HttpClientTodoItemCRUDAccessor.class
			.getSimpleName();

	public static final String MIME_TYPE = "application/json";

	/**
	 * we use an instance attribute for the client
	 */
	private HttpClient client;

	/**
	 * the base url
	 */
	private String baseUrl;

	private ObjectMapper mObjectMapper = new ObjectMapper();

	public HttpClientTodoItemCRUDAccessor(String baseUrl) {
		this.client = new DefaultHttpClient();
		this.baseUrl = baseUrl;
	}

	@Override
	public List<TodoItem> readAllItems() {

		Log.i(logger, "readAllItems()");

		try {
			// create a get method
			HttpGet method = new HttpGet(baseUrl);

			HttpResponse response = client.execute(method);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// obtain the input stream from the response
				InputStream is = response.getEntity().getContent();
				// try to read a json node from the stream
				

				List<TodoItem> readValue = mObjectMapper.readValue(is, new TypeReference<List<TodoItem>>(){});
				return readValue;

			} else {
				Log.e(logger,
						"readAllItems():  http request failed. Got status code: "
								+ response.getStatusLine());
			}

		} catch (Exception e) {
			Log.e(logger, "readAllItems(): got exception: " + e, e);
		}

		return new ArrayList<TodoItem>();

	}

	@Override
	public TodoItem createItem(TodoItem item) {

		Log.i(logger, "createItem(): " + item);

		try {
			// create a get method
			HttpPost method = new HttpPost(baseUrl);
			// add the data item as json object to the method's output stream
			method.setEntity(createHttpEntityFromDataItem(item));

			HttpResponse response = client.execute(method);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// obtain the input stream from the response
				InputStream is = response.getEntity().getContent();
				mObjectMapper = new ObjectMapper();
				TodoItem readValue = mObjectMapper.readValue(is, TodoItem.class);

				// create the itemlist from the json node
				return readValue;

			} else {
				Log.e(logger,
						"createItem():  http request failed. Got status code: "
								+ response.getStatusLine());
			}

		} catch (Exception e) {
			Log.e(logger, "createItem(): got exception: " + e, e);
		}

		return null;
	}

	@Override
	public boolean deleteItem(long itemId) {

		Log.i(logger, "deleteItem(): " + itemId);

		try {
			// create a get method
			HttpDelete method = new HttpDelete(baseUrl + "/" + itemId);

			HttpResponse response = client.execute(method);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// obtain the input stream from the response
				InputStream is = response.getEntity().getContent();

				return mObjectMapper.readValue(is, Boolean.class);
			} else {
				Log.e(logger,
						"deleteItem():  http request failed. Got status code: "
								+ response.getStatusLine());
			}

		} catch (Exception e) {
			Log.e(logger, "deleteItem(): got exception: " + e, e);
		}

		return false;
	
	}

	@Override
	public TodoItem updateItem(TodoItem item) {

		Log.i(logger, "upateItem(): " + item);

		try {
			// create a get method
			HttpPut method = new HttpPut(baseUrl);
			// add the data item as json object to the method's output stream
			method.setEntity(createHttpEntityFromDataItem(item));

			HttpResponse response = client.execute(method);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// obtain the input stream from the response
				InputStream is = response.getEntity().getContent();
				// try to read a json node from the stream
				TodoItem ret = mObjectMapper.readValue(is, TodoItem.class);
				// create the itemlist from the json node
				return ret;

			} else {
				Log.e(logger,
						"upateItem():  http request failed. Got status code: "
								+ response.getStatusLine());
			}

		} catch (Exception e) {
			Log.e(logger, "upateItem(): got exception: " + e, e);
		}

		return null;

	}

	protected HttpEntity createHttpEntityFromDataItem(TodoItem item)
			throws UnsupportedEncodingException, IOException {

		StringEntity se = new StringEntity(mObjectMapper.writeValueAsString(item));

		// add meta information for the mime type
		se.setContentType(MIME_TYPE);
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));

		return se;
	}

}
