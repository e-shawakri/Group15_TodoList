package com.group15.todoList.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class TodoItem implements Serializable {

	static final long startId = 0;

	private long id;
	private String name;
	private String description;
//	private boolean isDone;
//	private long date;
//	private LatLng coords;

	public TodoItem() {}

	public TodoItem(long id, String name, String description) {
		this.setId(id);
		this.setName(name);
		this.setDescription(description);
//		this.setIsDone(isDone);
//		this.setDate(date);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public void setIsDone(boolean isDone) { this.isDone = isDone; }

//	public void setDate(long date) { this.date = date; }

//	public void setCoords(LatLng coords) { this.coords = coords; }

	// boolean isDone, long date

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

//	public boolean isDone() { return this.isDone; }

//	public long getDate() { return this.date; }

//	public LatLng getCoords() { return this.coords; }

	public TodoItem updateFrom(TodoItem item) {
		this.setName(item.getName());
		this.setDescription(item.getDescription());
//		this.setType(item.getType());
//		this.setIsDone(item.isDone());
//		this.setDate(item.getDate());
//		this.setCoords(item.getCoords());
		return this;
	}

	public String toString() {
		return "{id:" + this.getId() + ", name:" + this.getName() + ", description:" + this.getDescription() + "};";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
