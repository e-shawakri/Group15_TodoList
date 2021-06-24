package com.group15.todoList.model;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/todoitems")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface TodoItemCRUDAccessor {
	
	@GET
	public List<TodoItem> readAllItems();
	
	@POST
	public TodoItem createItem(TodoItem item);

	@PUT
	public TodoItem updateItem(TodoItem item);

	@DELETE
	@Path("/{itemId}")
	public boolean deleteItem(@PathParam("itemId") long itemId);

}
