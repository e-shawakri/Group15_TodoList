package com.group15.todoList.model;

import javax.ws.rs.*;
import java.util.List;


@Path("/dataitems")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface TodoItemCRUDAccessor {
	
	@GET
	public List<TodoItem> readAllItems();
	
	@POST
	public TodoItem createItem(TodoItem item);

	@DELETE
	@Path("/{itemId}")
	public boolean deleteItem(@PathParam("itemId") long itemId);

	@PUT
	public TodoItem updateItem(TodoItem item);
}
