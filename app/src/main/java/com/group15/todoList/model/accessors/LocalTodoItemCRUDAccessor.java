package com.group15.todoList.model.accessors;

import com.group15.todoList.model.TodoItem;
import com.group15.todoList.model.TodoItemCRUDAccessor;

import java.util.ArrayList;
import java.util.List;

public class LocalTodoItemCRUDAccessor implements TodoItemCRUDAccessor {
	
	/**
	 * the list of data items(non-Javadoc)
	 */
	private List<TodoItem> itemlist = new ArrayList<TodoItem>();

	@Override
	public List<TodoItem> readAllItems() {
		return itemlist;
	}

	@Override
	public TodoItem createItem(TodoItem item) {
		this.itemlist.add(item);
		return item;
	}

	@Override
	public boolean deleteItem(final long itemId) {
		boolean removed = this.itemlist.remove(new TodoItem() {
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
	public TodoItem updateItem(TodoItem item) {
		return this.itemlist.get(itemlist.indexOf(item)).updateFrom(item);
	}

}
