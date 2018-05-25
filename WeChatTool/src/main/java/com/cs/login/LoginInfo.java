package com.cs.login;

import java.util.TreeMap;

public class LoginInfo extends TreeMap<String, String> {
	private static final long serialVersionUID = 1L;

	public static final String S_KEY = "skey";

	public static final String WX_SID = "wxsid";

	public static final String WSX_UIN = "wxuin";

	public static final String PASS_TICKET = "pass_ticket";

	public static final String IS_LOGIN = "isLogin";

	public LoginInfo() {
		super.put(S_KEY, "");
		super.put(WX_SID, "");
		super.put(WSX_UIN, "");
		super.put(PASS_TICKET, "");
		super.put(IS_LOGIN, "");
	}

	public void setSkey(String sKey) {
		super.put(S_KEY, sKey);
	}

	public void setSid(String wxSid) {
		super.put(WX_SID, wxSid);
	}

	public void setUin(String wxUin) {
		super.put(WSX_UIN, wxUin);
	}

	public void setPassTicket(String passTicket) {
		super.put(PASS_TICKET, passTicket);
	}

	public void isLogin(String isLogin) {
		super.put(IS_LOGIN, isLogin);
	}

}
