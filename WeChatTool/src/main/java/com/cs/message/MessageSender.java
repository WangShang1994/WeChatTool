package com.cs.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.cs.constant.URL;
import com.cs.login.LoginController;
import com.cs.login.LoginInfo;
import com.cs.wechat.httpclient.WeChatHttpClinet;

public class MessageSender {

	WeChatMessage respMessage = null;

	public MessageSender(WeChatMessage respMessage) {
		this.respMessage = respMessage;
	}

	public void send(String replyMessage) {
		try {
			WeChatHttpClinet httpClient = new WeChatHttpClinet();
			HttpEntity entity = httpClient.doPost(buildURL(), buildParas(replyMessage));
			System.out.println(EntityUtils.toString(entity));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String buildURL() {
		StringBuilder sb = new StringBuilder(URL.SEND_MSG_URL);
		sb.append("?pass_ticket=").append(LoginController.loginInfo.get(LoginInfo.PASS_TICKET));
		return sb.toString();
	}

	private String buildParas(String replyMessage) {
		Map<String, Object> baseMap = new HashMap<>();
		Map<String, Object> baseRequest = new HashMap<>();
		baseRequest.put("Uin", LoginController.loginInfo.get(LoginInfo.WSX_UIN));
		baseRequest.put("Sid", LoginController.loginInfo.get(LoginInfo.WX_SID));
		baseRequest.put("Skey", LoginController.loginInfo.get(LoginInfo.S_KEY));
		baseRequest.put("DeviceID", generateDeviceID());
		baseMap.put("BaseRequest", baseRequest);
		Map<String, Object> msg = new HashMap<>();
		msg.put("Type", "1");
		msg.put("Content", replyMessage);
		msg.put("FromUserName", LoginController.myselfInfo.getUserName());
		msg.put("ToUserName", respMessage.getFromUserName());
		msg.put("LocalID", generateLocalIDAndClientMsgId());
		msg.put("ClientMsgId", msg.get("LocalID"));
		baseMap.put("Msg: ", msg);
		return JSON.toJSONString(baseMap);
	}

	public String generateDeviceID() {
		StringBuilder sb = new StringBuilder("e");
		for (int i = 0; i <= 14; i++) {
			Random r = new Random();
			sb.append(r.nextInt(9));
		}
		return sb.toString();
	}

	private static String generateLocalIDAndClientMsgId() {
		String time = String.valueOf(System.currentTimeMillis());
		String head = time.substring(0, 9);
		String tail = time.substring(9, time.length());
		StringBuilder sb = new StringBuilder(head);
		for (int i = 0; i <= 2; i++) {
			Random r = new Random();
			sb.append(r.nextInt(9));
		}
		sb.append(tail);
		return sb.toString();
	}
}
