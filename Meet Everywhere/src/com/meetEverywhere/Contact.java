package com.meetEverywhere;

public class Contact {

	private int id;
	private String name;
	private String number;
	
	public Contact(int id, String name, String number){
		this.setId(id);
		this.name = name;
		this.number = number;
	}
	
	public Contact(String name, String num){
		this(-1, name, num);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object o2){
		Contact c2 = (Contact) o2;
		return name.equals(c2.name) && number.equals(c2.number);
	}
}
