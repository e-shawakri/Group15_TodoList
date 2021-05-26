package com.group15.todoList.model;

import java.io.Serializable;

/**
 * A single data item
 * 
 * @author Joern Kreutel
 * 
 */
public class DataItem implements Serializable {

	/**
	 * some static id assignment
	 */
	private static int ID = 0;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7481912314472891511L;

	/**
	 * the types enumeration
	 */
	public enum ItemTypes {
		TYPE1, TYPE2, TYPE3
	}

	/**
	 * the fields
	 */
	private long id;
	private ItemTypes type;
	private String name;
	private String description;
	private String iconId;

	public void setType(ItemTypes type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public DataItem(long id, ItemTypes type, String name, String description,
			String iconId) {
		this.setId(id == -1 ? ID++ : id);
		this.setType(type);
		this.setName(name);
		this.setDescription(description);
		this.setIconId(iconId);
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

	/**
	 * @return
	 */
	public String getIconId() {
		return this.iconId;
	}

	/**
	 * update an item given the content of anothr one
	 * 
	 * @param item
	 */
	public DataItem updateFrom(DataItem item) {
		this.setName(item.getName());
		this.setDescription(item.getDescription());
		this.setType(item.getType());
		this.setIconId(item.getIconId());

		return this;
	}

	public String toString() {
		return "{DataItem " + this.getId() + " " + this.getName() + " "
				+ this.iconId + " ... }";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * compare two elements
	 */
	public boolean equals(Object other) {

		// we cannot compare getClass() because classes do not coincide in case
		// of delete, where we create an anonymous inner class that extends
		// DataItem
		if (other == null || !(other instanceof DataItem)) {
			return false;
		} else {
			return ((DataItem) other).getId() == this.getId();
		}

	}

}
