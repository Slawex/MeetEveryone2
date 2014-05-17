package com.meetEverywhere;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable{

	private static final long serialVersionUID = -437242741203572594L;
	private List<String> hashTags;
	private final String nickname;
	
	public User(String nickname, List<String> hashTags){
		this.setHashTags(hashTags);
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public List<String> getHashTags() {
		return hashTags;
	}

	public void setHashTags(List<String> hashTags) {
		this.hashTags = hashTags;
	}
	
	
	
}
