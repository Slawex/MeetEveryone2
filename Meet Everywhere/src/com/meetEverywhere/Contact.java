package com.meetEverywhere;

import android.widget.Checkable;

public class Contact implements Checkable{

	private String name;
	private String token;
	private boolean checked=false;
	
	public Contact(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean arg0) {
		checked=arg0;
		
	}

	public void toggle() {
		if(checked)
			checked=false;
		else
			checked=true;
		
	}
	
}
