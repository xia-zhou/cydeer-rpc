package com.cydeer.rpc.common.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by zhangsong on 2017/6/2.
 */
public class RpcRequest implements Serializable {
	private String requestId;
	private String serviceName;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("  \"requestId\":\"").append(requestId).append('\"');
		sb.append(", \"serviceName\":\"").append(serviceName).append('\"');
		sb.append(", \"methodName\":\"").append(methodName).append('\"');
		sb.append(", \"parameterTypes\":").append(Arrays.toString(parameterTypes));
		sb.append(", \"parameters\":").append(Arrays.toString(parameters));
		sb.append('}');
		return sb.toString();
	}
}
