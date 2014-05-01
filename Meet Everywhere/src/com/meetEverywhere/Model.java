package com.meetEverywhere;

import android.os.IBinder;

public class Model {

	  public static enum Status{SAVED, IN_EDITION, EDITED};
	
	  private String name;
	  private boolean selected;
	  private boolean oryginalSelected;
	  private String tmpText;
	  private boolean giveFocus;
	  private IBinder token;
	  
	  private Status status;
	  private final int id;
	  
	  public Model(int id, String name, boolean selected, Status status, boolean giveFocus) {
		this.id = id;
	    this.name = name;
	    this.selected = selected;
	    oryginalSelected = selected;
	    tmpText = "";
	    this.status = status;
	    this.setGiveFocus(giveFocus);
	  }

	  public Model() {
			this(-1, "", false, Status.IN_EDITION, true);
		  }
	  
	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }

	  public boolean isSelected() {
	    return selected;
	  }

	  public void setSelected(boolean selected) {
	    this.selected = selected;
	  }

	public String getTmpText() {
		return tmpText;
	}

	public void setTmpText(String tmpText) {
		this.tmpText = tmpText;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status newStatus) {
		if(!newStatus.equals(Status.IN_EDITION) && status.equals(Status.IN_EDITION)){
			if(name.equals(tmpText))
				status = Status.SAVED;
			else{
				name = tmpText;
				status = newStatus;
			}
			
			tmpText = "";
		} else{
			if(newStatus.equals(Status.IN_EDITION) && !status.equals(Status.IN_EDITION)){
				tmpText = name;
			}
			
			status = newStatus;
		}
	}

	public boolean isInEdition(){
		if(status.equals(Status.IN_EDITION))
			return true;
		else
			return false;
	}
	
	public boolean isEdited(){
		if(status.equals(Status.EDITED) || oryginalSelected != selected)
			return true;
		else
			return false;
	}
	
	public boolean isGiveFocus() {
		return giveFocus;
	}

	public void setGiveFocus(boolean giveFocus) {
		this.giveFocus = giveFocus;
	}

	public IBinder getToken() {
		return token;
	}

	public void setToken(IBinder token) {
		this.token = token;
	}

	public int getId() {
		return id;
	}
}
