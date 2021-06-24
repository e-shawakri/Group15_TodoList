package com.group15.todoList.model;

import java.io.Serializable;

public class TodoItem implements Serializable {
	private long id;
	private String name;
	private String description;
	private boolean favourite;
	private long date;

	public TodoItem() {};

	public TodoItem(long id, String name, String description, boolean favourite, long date) {
		this.setId(id);
		this.setName(name);
		this.setDescription(description);
		this.setFavourite(favourite);
		this.setDate(date);
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

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getDate() {
		return date;
	}

	public void update(String name, String description, boolean favourite, long date) {
		this.setName(name);
		this.setDescription(description);
		this.setFavourite(favourite);
		this.setDate(date);
	}
}
