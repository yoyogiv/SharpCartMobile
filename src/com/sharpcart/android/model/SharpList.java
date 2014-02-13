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

    public void setName(final String name) {
	Name = name;
    }

    public String getDate() {
	return Date;
    }

    public void setDate(final String date) {
	Date = date;
    }

    public int getId() {
	return Id;
    }

    public void setId(final int id) {
	Id = id;
    }

    public int getStatus() {
	return Status;
    }

    public void setStatus(final int status) {
	Status = status;
    }

    public String getItems() {
	return Items;
    }

    public void setItems(final String items) {
	Items = items;
    }

    @Override
    public String toString() {
	return "SharpList [name=" + Name + "]";
    }

}
