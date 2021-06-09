package com.group15.todoList.remote;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItemCRUDAccessor;

public class RemoteDataItemAccessor implements DataItemCRUDAccessor {

	protected static Logger logger = Logger
			.getLogger(RemoteDataItemAccessor.class);

	/**
	 * the list of data items, note that the list is *static* as for each client
	 * request a new instance of this class will be created!
	 */
	private static List<DataItem> itemlist = new ArrayList<DataItem>();

	/**
	 * we assign the ids here
	 */
	private static long idCount = 0;

	public RemoteDataItemAccessor() {
		itemlist.add(new DataItem(0, DataItem.ItemTypes.TYPE1, "Name 1", "Desc 1"));
	}

	@Override
	public List<DataItem> readAllItems() {
		logger.info("readAllItems(): " + itemlist);

		return itemlist;
	}

	@Override
	public DataItem createItem(DataItem item) {
		logger.info("createItem(): " + item);
		item.setId(idCount++);

		itemlist.add(item);
		return item;
	}

	@Override
	public boolean deleteItem(final long itemId) {
		logger.info("deleteItem(): " + itemId);

		boolean removed = itemlist.remove(new DataItem() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 71193783355593985L;

			@Override
			public long getId() {
				return itemId;
			}
		});

		return removed;
	}

	@Override
	public DataItem updateItem(DataItem item) {
		logger.info("updateItem(): " + item);

		return itemlist.get(itemlist.indexOf(item)).updateFrom(item);
	}
}
