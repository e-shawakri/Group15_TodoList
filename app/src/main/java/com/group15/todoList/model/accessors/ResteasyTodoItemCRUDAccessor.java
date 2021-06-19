package com.group15.todoList.model.accessors;

import android.util.Log;
import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import java.util.List;

public class ResteasyTodoItemCRUDAccessor implements TodoItemCRUDAccessor {
	
	protected static String logger = ResteasyTodoItemCRUDAccessor.class.getSimpleName();

	/**
	 * the client via which we access the rest web interface provided by the server
	 */
	private TodoItemCRUDAccessor restClient;
	
	public ResteasyTodoItemCRUDAccessor(String baseUrl) {

		Log.i(logger,"initialising restClient for baseUrl: " + baseUrl);
		
		// create a client for the server-side implementation of the interface
		this.restClient = ProxyFactory.create(TodoItemCRUDAccessor.class,
				baseUrl,
				new ApacheHttpClient4Executor());
		
		Log.i(logger,"initialised restClient: " + restClient + " of class " + restClient.getClass());
	}

	@Override
	public List<TodoItem> readAllItems() {
		Log.i(logger, "readAllItems()");

		List<TodoItem> itemlist = restClient.readAllItems();
		
		Log.i(logger, "readAllItems(): got: " + itemlist);
	
		return itemlist;
	}

	@Override
	public TodoItem createItem(TodoItem item) {
		Log.i(logger, "createItem(): send: " + item);

		item = restClient.createItem(item);
		
		Log.i(logger, "createItem(): got: " + item);
	
		return item;
	}

	@Override
	public boolean deleteItem(long itemId) {
		Log.i(logger, "deleteItem(): send: " + itemId);

		boolean deleted = restClient.deleteItem(itemId);
		
		Log.i(logger, "deleteItem(): got: " + deleted);
	
		return deleted;
	}

	@Override
	public TodoItem updateItem(TodoItem item) {
		Log.i(logger, "updateItem(): send: " + item);

		item = restClient.updateItem(item);
		
		Log.i(logger, "updateItem(): got: " + item);
	
		return item;
	}

}
