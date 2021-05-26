package com.group15.todoList.model.accessors;

import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItemCRUDAccessor;

import java.util.ArrayList;
import java.util.List;

public class LocalDataItemCRUDAccessor implements DataItemCRUDAccessor {
	
	/**
	 * the list of data items(non-Javadoc)
	 */
	private List<DataItem> itemlist = new ArrayList<DataItem>();

	@Override
	public List<DataItem> readAllItems() {
		return itemlist;
	}

	@Override
	public DataItem createItem(DataItem item) {
		this.itemlist.add(item);
		return item;
	}

	@Override
	public boolean deleteItem(final long itemId) {
		boolean removed = this.itemlist.remove(new DataItem() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5186750365614757801L;

			@Override
			public long getId() {
				return itemId;
			}			
		});
		
		return removed;
	}

	@Override
	public DataItem updateItem(DataItem item) {
		return this.itemlist.get(itemlist.indexOf(item)).updateFrom(item);
	}

}
