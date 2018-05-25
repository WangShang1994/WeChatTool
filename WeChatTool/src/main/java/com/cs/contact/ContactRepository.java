package com.cs.contact;

import java.util.HashMap;
import java.util.Map;

public class ContactRepository {

	static Map<String, ContactUserInfo> contactUserInfos = new HashMap<>();

	public void addContactUser(ContactUserInfo c) {
		contactUserInfos.put(c.getUserName(), c);
	}

	public static ContactUserInfo getContactUser(String userName) {
		return contactUserInfos.get(userName);
	}

}
