package com.meetEverywhere;

import java.io.Serializable;

public class TextMessage implements Serializable{

	private static final long serialVersionUID = 2922796847639314334L;
	private final String text;
	/* Jeœli null - wiadomoœc napisana przez siebie, jeœli nie - otrzymana. */
	private final String from;

	public TextMessage(String text, String from) {
		this.text = text;
		this.from = from;
	}

	public String getText() {
		return text;
	}

	public boolean isLocal() {
		if (BluetoothDispatcher.getInstance().getOwnData().getNickname().equals(from)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (isLocal()) {
			return "Ja: " + text;
		} else {
			return from + ": " + text;
		}
	}

}
