package com.meetEverywhere;

import java.util.List;

import android.graphics.Bitmap;

public class ServUser {

	private final String nick;
	private int percentage;
	private Bitmap photo;
	private String decription;
	private List<Tag> tags;

	public ServUser(String nick, int percentage, Bitmap image,
			String description, List<Tag> tags) {
		this.nick=nick;
		this.percentage=percentage;
		this.photo=image;
		this.decription=description;
		this.tags=tags;
	}

	public Bitmap getBitmap() {
		return photo;
	}

	public String getNick() {
		return nick;
	}

	public String getPercentage() {
		return percentage+"";
	}

}
