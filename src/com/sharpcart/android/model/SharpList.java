package com.sharpcart.android.model;

public class SharpList {
    private String Name;
    private String Date;
    private int Id;
    private int Status;
    private String Items;

    public String getName() {
	return Name;
    }

    public void setName(String name) {
	this.Name = name;
    }

    public String getDate() {
	return Date;
    }

    public void setDate(String date) {
	this.Date = date;
    }

    public int getId() {
	return Id;
    }

    public void setId(int id) {
	this.Id = id;
    }

    public int getStatus() {
	return Status;
    }

    public void setStatus(int status) {
	this.Status = status;
    }

    public String getItems() {
	return Items;
    }

    public void setItems(String items) {
	Items = items;
    }

    @Override
    public String toString() {
	return "SharpList [name=" + Name + "]";
    }

}
