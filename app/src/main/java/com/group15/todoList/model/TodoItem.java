package com.group15.todoList.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class TodoItem implements Serializable {

	static final long startId = 0;

	private long id;
	private String name;
	private String description;
	private boolean favourite;
	private long date;
	private int importance;
	private boolean done;
	private LatLng coords;

	public TodoItem() {}

	public TodoItem(long id, String name, String description, boolean favourite, long date, int importance,
					boolean done) {
		this.setId(id);
		this.setName(name);
		this.setDescription(description);
		this.setDate(date);
		this.setFavourite(favourite);
		this.setImportance(importance);
		this.setDone(done);
//		this.setCoords(coords);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isFavourite() {
		return favourite;
	}

	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}

	public TodoItem updateFrom(TodoItem item) {
		this.setName(item.getName());
		this.setDescription(item.getDescription());
		this.setFavourite(item.isFavourite());
		this.setDate(item.getDate());
		this.setImportance(item.getImportance());
		this.setCoords(item.getCoords());
		return this;
	}

	public String toString() {
		return "{id:" + this.getId()
				+ ", name:" + this.getName()
				+ ", description:" + this.getDescription()
				+ ", coords:" + this.getCoords()+ "};";
	}

	public LatLng getCoords() {
		return coords;
	}

	public void setCoords(LatLng coords) {
		this.coords = coords;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isDone() {
		return done;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
