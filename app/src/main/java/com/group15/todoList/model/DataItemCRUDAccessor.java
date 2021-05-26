package com.group15.todoList.model;

import javax.ws.rs.*;
import java.util.List;


@Path("/dataitems")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface DataItemCRUDAccessor {
	
	@GET
	public List<DataItem> readAllItems();
	
	@POST
	public DataItem createItem(DataItem item);

	@DELETE
	@Path("/{itemId}")
	public boolean deleteItem(@PathParam("itemId") long itemId);

	@PUT
	public DataItem updateItem(DataItem item);
}
