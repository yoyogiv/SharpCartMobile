package com.sharpcart.android.model;

public class Store {

    private String Name;
    private int Id;
    private int Status;

    public String getName() {
	return Name;
    }

    public void setName(String name) {
	Name = name;
    }

    public int getId() {
	return Id;
    }

    public void setId(int id) {
	Id = id;
    }

    public int getStatus() {
	return Status;
    }

    public void setStatus(int status) {
	Status = status;
    }

    @Override
    public String toString() {
	return "Store [Name=" + Name + "]";
    }

}
