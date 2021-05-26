package com.group15.todoList.model;

import java.io.IOException;
import java.io.InputStream;

public interface MediaResourceAccessor {

	public InputStream readMediaResource(String url) throws IOException;

	public String getBaseUrl();
	
}
