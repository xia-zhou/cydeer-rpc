package com.cydeer.rpc.common.bean;

import java.io.Serializable;

/**
 * Created by zhangsong on 2017/6/2.
 */
public class RpcResponse implements Serializable {
	private String requestId;
	private Exception exception;
	private Object result;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("  \"requestId\":\"").append(requestId).append('\"');
		sb.append(", \"exception\":").append(exception);
		sb.append(", \"result\":").append(result);
		sb.append('}');
		return sb.toString();
	}
}
