package com.meetEverywhere;

public class ValidationError {
	
	private int messageKey;
	
	public ValidationError(int messageKey) {
		this.messageKey = messageKey;
	}
	
	public int getMessageKey() {
		return messageKey;
	}
}
