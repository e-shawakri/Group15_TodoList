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


@Path("/dataitems")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface DataItemCRUDAccessor {
	
	@GET
	public List<DataItem> readAllItems();
	
	@POST
	public DataItem createItem(DataItem item);

	@PUT
	public DataItem updateItem(DataItem item);

	@DELETE
	@Path("/{itemId}")
	public boolean deleteItem(@PathParam("itemId") long itemId);

}
