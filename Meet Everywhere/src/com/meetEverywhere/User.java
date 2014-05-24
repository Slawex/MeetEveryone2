package com.meetEverywhere;

import java.io.Serializable;
import java.util.List;

/**
 * Klasa User zawiera dane dotycz�ce u�ytkownika. Objekty tej klasy mog� by�
 * serializowane i wysy�ane za pomoc� InsecureRFCOMM lub sk�adowane lokalnie
 * (objekt dotycz�cy w�a�ciciela urz�dzenia). //TODO: rozbudowa� o
 * przechowywanie avatara u�ytkownika
 * 
 * @author marekmagik
 * 
 */
public class User implements Serializable {

	private static final long serialVersionUID = -437242741203572594L;
	private List<String> hashTags;
	private final String nickname;

	public User(String nickname, List<String> hashTags) {
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

	@Override
	public boolean equals(Object o1) {
		if (o1 != null && o1 instanceof User) {
			if (((User) o1).getNickname().equals(nickname)) {
				return true;
			}
		}
		return false;
	}

}
