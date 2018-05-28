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
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cs.constant.URL;
import com.cs.login.LoginController;
import com.cs.login.LoginInfo;
import com.cs.message.MessageSender;
import com.cs.message.WeChatMessage;
import com.cs.robot.ChatRobot;
import com.cs.wechat.httpclient.MyHttpClient;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class MessageListener extends Thread {

	String sid = null;
	String sKey = null;
	String passTicket = null;
	String uid = null;
	Map<Integer, Object> sycnKeymap = null;
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
				sycnCheckMsg();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void getNewMessage() {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		HttpEntity entry = null;
		String msgReqponse = null;
		try {
			String url = buildURL();
			String paras = buildParas();
			entry = httpClient.doPost(url, paras);
			msgReqponse = EntityUtils.toString(entry);
			updateSycnKey(msgReqponse);
			List<WeChatMessage> newMsgList = procssMsgResponse(msgReqponse);
			for (WeChatMessage msg : newMsgList) {
				MessageSender ms = new MessageSender(msg);
				ChatRobot cr = new ChatRobot();
				ms.send(cr.talk(msg.getContent()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateSycnKey(String resp) {
		JSONObject respJson = JSON.parseObject(resp);
		JSONObject sycnObj = respJson.getJSONObject("SyncKey");
		LoginController.sycnKeyMap = new HashMap<>();
		LoginController.sycnKeyMap.put(0, sycnObj);
		JSONArray sycnKeyArr = sycnObj.getJSONArray("List");
		for (int i = 0; i < sycnKeyArr.size(); i++) {
			LoginController.sycnKeyMap.put(sycnKeyArr.getJSONObject(i).getInteger("Key"),
					sycnKeyArr.getJSONObject(i).getInteger("Val"));
		}

	}

	private List<WeChatMessage> procssMsgResponse(String msgResp) {
		JSONObject respJson = JSON.parseObject(msgResp);
		JSONArray messages = respJson.getJSONArray("AddMsgList");
		List<WeChatMessage> msgList = new ArrayList<>();
		for (int i = 0; i < messages.size(); i++) {
			JSONObject message = messages.getJSONObject(i);
			WeChatMessage w = new WeChatMessage();
			w.setContent(message.getString("Content"));
			w.setFromUserName(message.getString("FromUserName"));
			w.setMsgId(message.getString("MsgId"));
			w.setMsgType(message.getInteger("MsgType"));
			w.setNewMsgId(message.getString("NewMsgId"));
			w.setToUserName(message.getString("ToUserName"));
			msgList.add(w);
		}
		return msgList;

	}

	private void startListener() throws InterruptedException {

		List<BasicNameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("uin", this.uid));
		params.add(new BasicNameValuePair("sid", this.sid));
		params.add(new BasicNameValuePair("skey", this.sKey));
		params.add(new BasicNameValuePair("deviceid", generateDeviceID()));
		params.add(new BasicNameValuePair("r", String.valueOf(System.currentTimeMillis())));
		params.add(new BasicNameValuePair("synckey", buildSycnKey()));
		params.add(new BasicNameValuePair("_", String.valueOf(System.currentTimeMillis())));
		Thread.sleep(5000);
		try {
			WeChatHttpClinet httpClient = new WeChatHttpClinet();
			HttpEntity entry = httpClient.doGet(URL.CHECK_SYCN_URL, params, false, null);
			procssSycnResponse(EntityUtils.toString(entry));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sycnCheckMsg() {
		WeChatHttpClinet httpClient = new WeChatHttpClinet();
		try {
			HttpEntity entry = httpClient.doGet(URL.CHECK_SYCN_URL, buildCheckSycnParas(), false, null);
			procssSycnResponse(EntityUtils.toString(entry));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String buildCheckSycnURL() {
		StringBuilder url = new StringBuilder(URL.CHECK_SYCN_URL);
		url.append("?r=").append(String.valueOf(System.currentTimeMillis()));
		url.append("&skey=").append(this.sKey);
		url.append("&sid=").append(this.sid);
		url.append("&uin=").append(this.uid);
		url.append("&deviceid=").append(generateDeviceID());
		url.append("&synckey=").append(buildSycnKey());
		url.append("&_=").append(String.valueOf(System.currentTimeMillis()));
		return url.toString();
	}

	private List<BasicNameValuePair> buildCheckSycnParas() {
		BasicNameValuePair r = new BasicNameValuePair("r", String.valueOf(System.currentTimeMillis()));
		BasicNameValuePair sky = new BasicNameValuePair("skey", this.sKey);
		BasicNameValuePair sid = new BasicNameValuePair("sid", this.sid);
		BasicNameValuePair uin = new BasicNameValuePair("uin", this.uid);
		BasicNameValuePair deviceid = new BasicNameValuePair("deviceid", generateDeviceID());
		BasicNameValuePair synckey = new BasicNameValuePair("synckey", buildSycnKey());
		BasicNameValuePair _ = new BasicNameValuePair("_", String.valueOf(System.currentTimeMillis()));
		List<BasicNameValuePair> list = new ArrayList<>();
		list.add(r);
		list.add(sky);
		list.add(sid);
		list.add(uin);
		list.add(deviceid);
		list.add(synckey);
		list.add(_);
		return list;
	}

	private void procssSycnResponse(String strResp) {
		// Response: window.synccheck={retcode:"0",selector:"2"}
		String[] respArr = strResp.split("=");
		JSONObject resJson = JSON.parseObject(respArr[1]);
		if ("0".equals(resJson.getString("retcode"))) {
			System.out.println("SycnCheck Success! " + strResp);
			if ("2".equals(resJson.getString("selector"))) {
				System.out.println("Has new Message! Will get new Message!");
				getNewMessage();
			}
		}

		System.out.println(strResp);
	}

	// ?sid=xxx&skey=xxx&pass_ticket=xxx
	private String buildURL() {
		StringBuilder sb = new StringBuilder(LoginController.BASE_URL + "webwxsync");
		sb.append("?sid=").append(this.sid);
		sb.append("&skey=").append(this.sKey);
		sb.append("&lang=en_");
		sb.append("&pass_ticket=").append(this.passTicket);
		return sb.toString();
	}

	private String buildParas() {
		Map<String, Object> reqMap = new HashMap<>();
		Map<String, String> loginInfoMap = new HashMap<>();
		loginInfoMap.put("Uin", this.uid);
		loginInfoMap.put("Sid", this.sid);
		loginInfoMap.put("Skey", this.sKey);
		loginInfoMap.put("DeviceID", generateDeviceID());
		reqMap.put("BaseRequest", loginInfoMap);
		reqMap.put("SyncKey", LoginController.sycnKeyMap.get(0));
		reqMap.put("rr", String.valueOf(new Date().getTime() / 1000));
		return JSON.toJSONString(reqMap);
	}

	private String buildSycnKey() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, Object> e : LoginController.sycnKeyMap.entrySet()) {
			if (e.getKey() == 0) {
				continue;
			}
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

	public static void main(String[] args) {
		System.out.println(new Date().getTime() / 1000);
	}

}
