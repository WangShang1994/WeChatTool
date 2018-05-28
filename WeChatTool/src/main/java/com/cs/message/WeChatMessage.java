package com.cs.message;

public class WeChatMessage {
	String msgId;
	String fromUserName;
	String toUserName;
	Integer msgType;
	String content;
	String newMsgId;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNewMsgId() {
		return newMsgId;
	}

	public void setNewMsgId(String newMsgId) {
		this.newMsgId = newMsgId;
	}

}
