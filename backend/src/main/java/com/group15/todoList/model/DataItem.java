package com.group15.todoList.model;

import java.io.Serializable;

public class DataItem implements Serializable {
	private long id;
	private String name;
	private String description;

	public DataItem() {};

	public DataItem(long id, String name, String description) {
		this.setId(id);
		this.setName(name);
		this.setDescription(description);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void update(String name, String description) {
		this.setName(name);
		this.setDescription(description);
	}
}
