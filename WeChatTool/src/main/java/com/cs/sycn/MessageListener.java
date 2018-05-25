package com.cs.sycn;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.cs.constant.URL;
import com.cs.login.LoginController;
import com.cs.login.LoginInfo;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class MessageListener extends Thread {

	String sid = null;
	String sKey = null;
	String passTicket = null;
	String uid = null;
	Map<Integer, String> sycnKeymap = null;
	String deviceid = null;

	public MessageListener() {
		this.sid = LoginController.loginInfo.get(LoginInfo.WX_SID);
		this.sKey = LoginController.loginInfo.get(LoginInfo.S_KEY);
		this.passTicket = LoginController.loginInfo.get(LoginInfo.PASS_TICKET);
		this.uid = LoginController.loginInfo.get(LoginInfo.WSX_UIN);
		this.sycnKeymap = LoginController.sycnKeyMap;
		this.deviceid = "";
	}

	@Override
	public void run() {
		while (true) {
			try {
				startListener();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// private void startListener() {
	// WeChatHttpClinet httpClient = new WeChatHttpClinet();
	// HttpEntity entry = null;
	// String msgReqponse = null;
	// try {
	// String url = buildURL();
	// String paras = buildParas();
	// System.out.println("SYCNURL:" + url);
	// System.out.println("SYCNParas:" + paras);
	// entry = httpClient.doPost(url, paras);
	// msgReqponse = WeChatHttpClinet.convertHttpEntry(entry);
	// procssMsgResponse(msgReqponse);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	private void startListener() throws InterruptedException {

		List<BasicNameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("uin", this.uid));
		params.add(new BasicNameValuePair("sid", this.sid));
		params.add(new BasicNameValuePair("skey", this.sKey));
		params.add(new BasicNameValuePair("deviceid", this.passTicket));
		params.add(new BasicNameValuePair("r", String.valueOf(System.currentTimeMillis())));
		params.add(new BasicNameValuePair("synckey", buildSycnKey()));
		params.add(new BasicNameValuePair("_", String.valueOf(System.currentTimeMillis())));
		Thread.sleep(1000);
		try {
			WeChatHttpClinet httpClient = new WeChatHttpClinet();
			HttpEntity entry = httpClient.doGet(URL.CHECK_SYCN_URL, params, true, null);
			procssSycnResponse(WeChatHttpClinet.convertHttpEntry(entry));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String buildCheckSycnURL() {
		StringBuilder url = new StringBuilder(URL.CHECK_SYCN_URL);
		String time = String.valueOf(System.currentTimeMillis());
		url.append("?r=").append(time);
		url.append("&sid=").append(this.sid);
		url.append("&uin=").append(this.uid);
		url.append("&skey=").append(this.sKey);
		url.append("&deviceid=").append(this.sid);
		url.append("&synckey=").append(buildSycnKey());
		url.append("&_=").append(time);
		System.out.println(url.toString());
		return url.toString();
	}

	private void procssSycnResponse(String strResp) {
		System.out.println(strResp);
	}

	// ?sid=xxx&skey=xxx&pass_ticket=xxx
	private String buildURL() {
		StringBuilder sb = new StringBuilder(URL.SYCN_MSG_URL);
		sb.append("?sid=").append(this.sid);
		sb.append("&skey=").append(this.sKey);
		sb.append("&pass_ticket=").append(this.deviceid);
		return sb.toString();
	}

	private String buildParas() {
		Map<String, Object> reqMap = new HashMap<>();
		Map<String, String> loginInfoMap = new HashMap<>();
		loginInfoMap.put("Uin", LoginController.loginInfo.get(LoginInfo.WSX_UIN));
		loginInfoMap.put("Sid", this.sid);
		loginInfoMap.put("Skey", this.sKey);
		loginInfoMap.put("DeviceID", this.deviceid);
		reqMap.put("BaseRequest", loginInfoMap);
		reqMap.put("SyncKey", buildSycnKey());
		reqMap.put("rr", -new Date().getTime() / 1000);
		return JSON.toJSONString(reqMap);
	}

	private String buildSycnKey() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, String> e : sycnKeymap.entrySet()) {
			sb.append(e.getKey()).append("_").append(e.getValue()).append("|");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public String generateDeviceID() {
		StringBuilder sb = new StringBuilder("e");
		for (int i = 0; i <= 14; i++) {
			Random r = new Random();
			sb.append(r.nextInt(9));
		}
		return sb.toString();
	}

}
