package com.group15.todoList;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;
import com.group15.todoList.model.TodoItemCRUDAccessor;
import com.group15.todoList.model.accessors.*;

public class DataAccessRemoteApplication extends Application {

	protected static String logger = DataAccessRemoteApplication.class
			.getSimpleName();

	private HttpURLConnectionTodoItemCRUDAccessor httpURLConnectionAccessor;

	public DataAccessRemoteApplication() {
		this.httpURLConnectionAccessor = new HttpURLConnectionTodoItemCRUDAccessor(
				getRestBaseUrl() + "/todoitems");
	}

	public TodoItemCRUDAccessor getDataItemAccessor(int accessorId) {
		TodoItemCRUDAccessor accessor;

		accessor = this.httpURLConnectionAccessor;

		return accessor;
	}

	public void reportError(Activity activity, String error) {
		Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
	}

	private String getWebappBaseUrl() {
		return "http://localhost:8080/backend-1.0-SNAPSHOT/";
	}

	private String getRestBaseUrl()
	{
		return getWebappBaseUrl() + "rest";
	}
}
