package com.group15.todoList.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import com.group15.todoList.model.DataItem;
import com.group15.todoList.model.DataItemCRUDAccessor;

public class RemoteDataItemAccessor implements DataItemCRUDAccessor {

	protected static Logger logger = Logger
			.getLogger(RemoteDataItemAccessor.class);

	private static List<DataItem> itemlist = new ArrayList<DataItem>();

	public RemoteDataItemAccessor() {}

	@Override
	public List<DataItem> readAllItems() {
		return itemlist;
	}

	@Override
	public DataItem createItem(DataItem item) {
		itemlist.add(item);
		return item;
	}

	@Override
	public DataItem updateItem(DataItem item) {
		List<DataItem> updatedItemList = itemlist.stream()
				.map(i -> i.getId() == item.getId() ? item : i)
				.collect(Collectors.toList());

		itemlist.clear();
		itemlist.addAll(updatedItemList);

		return item;
	}

	@Override
	public boolean deleteItem(final long itemId) {
		List<DataItem> filteredItemList = itemlist.stream()
				.filter(i -> i.getId() != itemId)
				.collect(Collectors.toList());

		itemlist.clear();
		itemlist.addAll(filteredItemList);

		return true;
	}
}
