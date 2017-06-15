package com.cydeer.rpc.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by zhangsong on 2017/6/14.
 */
public class LocalServerInfo {
	private static Logger logger = LoggerFactory.getLogger(LocalServerInfo.class);

	/**
	 * <pre>
	 * 获取本机主机名，若没获取到返回 "" 空串。 该数据未做缓存，请在使用的地方自行缓存，以提高性能
	 */
	public static String getHostName() {
		String hostName = null;
		try {
			InetAddress localIp = InetAddress.getLocalHost();
			hostName = localIp.getHostName();
			if (StringUtils.isNotBlank(hostName)) {
				return hostName;
			}
		} catch (UnknownHostException e) {
			logger.error("", e);
		}
		List<InetAddress> ipAddresses = getLocalAddresses();
		for (InetAddress inetAddress : ipAddresses) {
			hostName = inetAddress.getHostName();
			if (StringUtils.isNotBlank(hostName)) {
				return hostName;
			}
		}
		return "";
	}

	/**
	 * <pre>
	 * 获取本机主机IP地址，若没获取到返回 "" 空串。 该数据未做缓存，请在使用的地方自行缓存，以提高性能。
	 * 注：与Dubbo获取IP的方法保持一致
	 */
	public static String getHostIP() {
		InetAddress localAddress = null;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress.getHostAddress();
			}
		} catch (Throwable e) {
			logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
		}
		List<InetAddress> ipAddresses = getLocalAddresses();

		return ipAddresses.size() > 0 ? ipAddresses.get(0).getHostAddress() : "";
	}

	/**
	 * <pre>
	 * 获取本机所有IPv4的Inet地址列表
	 *
	 * @deprecated 该类不用了，可能会返回IPv6的地址
	 */
	public static List<InetAddress> getInet4Addresses() {
		return getLocalAddresses();
	}

	/**
	 * <pre>
	 * 获取本机所有会法的Inet地址列表
	 */
	public static List<InetAddress> getLocalAddresses() {
		List<InetAddress> ipList = new ArrayList<InetAddress>();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					// if (ip != null && !ip.isLoopbackAddress() && ip
					// instanceof Inet4Address) {
					if (isValidAddress(ip)) {
						ipList.add(ip);
					}
				}
			}
		} catch (SocketException e) {
			logger.error("", e);
		}
		return ipList;
	}
	/**
	 * <pre>
	 * 根据协议返回Scheme(HTTP)的端口值，如果没拿到返回""空串，适用于Tomcat服务器。
	 * 拿到值后请缓存，以提高性能。
	 *
	 * @return 返回 Tomcat 以 HTTP/1.1 连接器连接的 http:// 协议下的访问端口
	 */
	public static String getTomcatServerPort() {
		return getTomcatServerPort("http");
	}
	/**
	 * <pre>
	 * 根据协议返回Scheme(HTTP)的端口值，如果没拿到返回""空串，适用于Tomcat服务器。
	 * 拿到值后请缓存，以提高性能。
	 *
	 * @param connectorProtocal
	 *            连接器协议，可以是协议的前缀来匹配。如：AJP/1.3, HTTP/1.1
	 * @return
	 */
	public static String getTomcatServerPort(String connectorProtocal) {
		MBeanServer mBeanServer = null;
		if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
			mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
			try {
				Set<ObjectName> names = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
				Iterator<ObjectName> it = names.iterator();
				ObjectName oname = null;
				while (it.hasNext()) {
					oname = (ObjectName) it.next();
					String protocol = (String) mBeanServer.getAttribute(oname, "protocol");
					if (StringUtils.startsWithIgnoreCase(protocol, connectorProtocal)) {
						String scheme = (String) mBeanServer.getAttribute(oname, "scheme");
						if (StringUtils.equalsIgnoreCase("http", scheme)) {
							Object port = mBeanServer.getAttribute(oname, "port");
							if (port != null) {
								return port.toString();
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("无法读取Tomcat服务器端口信息", e);
			}
		}

		return "";
	}

	private static final String ANYHOST = "0.0.0.0";
	private static final String LOCALHOST = "127.0.0.1";
	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress())
			return false;
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
	}
}
