/* 
	Description:
		ZK Essentials
	History:
		Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.mvvm;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;

import java.util.*;

public class TodoListViewModel {

	//data for the view
	private String subject;
	private ListModelList<Todo> todoListModel;
	//service class
	private TodoListService todoListService = new TodoListService();

	public TodoListViewModel(){
		//get data from the service layer
		List<Todo> todoList = todoListService.getTodoList();
		//ListModelList optimizes rendering performance for ZK components
		todoListModel = new ListModelList<Todo>(todoList);
	}

	@Command //@Command declares a command method
	@NotifyChange("subject") //@NotifyChange tells ZK which property changed by this method
	public void addTodo(){
		if(Strings.isBlank(subject)){
			Clients.showNotification("Subject is blank, nothing to do ?");
		}else{
			//save data
			Todo todo = new Todo(subject);
			todo = todoListService.saveTodo(todo);
			//update the model, by using ListModelList, you don't need to notify todoListModel change
			//it is efficient that only update one item of the listbox
			todoListModel.add(todo);

			//clear value for the next todo.
			subject = null;
		}
	}

	@Command 
	public void completeTodo(@BindingParam("todo") Todo todo){
		//update to the database
		todoListService.updateTodo(todo);
		BindUtils.postNotifyChange(null, null, todo, "complete");
	}

	@Command
	public void deleteTodo(@BindingParam("todo") Todo todo){
		//delete the todo in the database
		todoListService.deleteTodo(todo);

		//update ListModel, by using ListModelList, you don't need to notify todoListModel change
		todoListModel.remove(todo);
	}

	@Command
	public void updateTodo(@BindingParam("todo") Todo todo){
		//update todo in the database
		todoListService.updateTodo(todo);
	}

	//getter & setter for the binding of the view

	public ListModelList<Todo> getTodoListModel() {
		return todoListModel;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
