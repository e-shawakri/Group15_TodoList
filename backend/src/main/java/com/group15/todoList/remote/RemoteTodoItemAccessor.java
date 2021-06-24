package com.group15.todoList.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;

public class RemoteTodoItemAccessor implements TodoItemCRUDAccessor {

	protected static Logger logger = Logger
			.getLogger(RemoteTodoItemAccessor.class);

	private static List<TodoItem> itemlist = new ArrayList<TodoItem>();

	public RemoteTodoItemAccessor() {}

	@Override
	public List<TodoItem> readAllItems() {
		return itemlist;
	}

	@Override
	public TodoItem createItem(TodoItem item) {
		itemlist.add(item);
		return item;
	}

	@Override
	public TodoItem updateItem(TodoItem item) {
		List<TodoItem> updatedItemList = itemlist.stream()
				.map(i -> i.getId() == item.getId() ? item : i)
				.collect(Collectors.toList());

		itemlist.clear();
		itemlist.addAll(updatedItemList);

		return item;
	}

	@Override
	public boolean deleteItem(final long itemId) {
		List<TodoItem> filteredItemList = itemlist.stream()
				.filter(i -> i.getId() != itemId)
				.collect(Collectors.toList());

		itemlist.clear();
		itemlist.addAll(filteredItemList);

		return true;
	}
}
