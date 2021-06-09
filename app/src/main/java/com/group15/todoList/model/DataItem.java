package com.group15.todoList.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.DateFormat;

public class DataItem implements Serializable {

	private static int ID = 0;

	private static final long serialVersionUID = -7481912314472891511L;

	public enum ItemTypes {
		TYPE1, TYPE2, TYPE3
	}

	private long id;
	private ItemTypes type;
	private String name;
	private String description;
	private boolean isDone;
	private long date;
	private LatLng coords;


	public void setType(ItemTypes type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIsDone(boolean isDone) { this.isDone = isDone; }

	public void setDate(long date) { this.date = date; }

	public void setCoords(LatLng coords) { this.coords = coords; }

	public DataItem(long id, ItemTypes type, String name, String description, boolean isDone, long date) {
		this.setId(id == -1 ? ID++ : id);
		this.setType(type);
		this.setName(name);
		this.setDescription(description);
		this.setIsDone(isDone);
		this.setDate(date);
	}

	public DataItem() {
		// TODO Auto-generated constructor stub
	}

	public ItemTypes getType() {
		return this.type;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isDone() { return this.isDone; }

	public long getDate() { return this.date; }

	public LatLng getCoords() { return this.coords; }

	public DataItem updateFrom(DataItem item) {
		this.setName(item.getName());
		this.setDescription(item.getDescription());
		this.setType(item.getType());
		this.setIsDone(item.isDone());
		this.setDate(item.getDate());
		this.setCoords(item.getCoords());
		return this;
	}

	public String toString() {
		return "{DataItem " + this.getId() + " " + this.getName() + " "
				+ this.isDone() + " " + this.getDate() + " ... }";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean equals(Object other) {
		if (other == null || !(other instanceof DataItem)) {
			return false;
		} else {
			return ((DataItem) other).getId() == this.getId();
		}

	}

}
