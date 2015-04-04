package net.roseboy.jeasy.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.roseboy.jeasy.core.Converter;

/**
 * Property文件读取工具类
 * @author roseboy.net
 *
 */
public class PropertyUtil {
	/**
	 * 读取Property文件并转化map
	 * @param file 文件名
	 * @return map
	 */
	public static Map<String, String> readProperty(String file) {
		Map<String, String> map = new HashMap<String, String>();
		InputStream inputStream = PropertyUtil.class.getClassLoader().getResourceAsStream(file);
		Properties p = new Properties();
		try {
			p.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Enumeration<?> en = p.propertyNames();
		String key;
		while (en.hasMoreElements()) {
			key = (String) en.nextElement();
			map.put(key.toLowerCase(), p.getProperty(key, ""));
		}
		return map;
	}

	public static String getPropertyString(String file, String key, String defa) {
		Map<String, String> map = new HashMap<String, String>();
		map = readProperty(file);
		String r = map.get(key);
		if (r == null || "".equals(r)) {
			return defa;
		} else {
			return r;
		}
	}

	public static String getPropertyString(String file, String key) {
		return getPropertyString(file, key, "");
	}

	public static int getPropertyInt(String file, String key, int defa) {
		Map<String, String> map = new HashMap<String, String>();
		map = readProperty(file);
		String r = map.get(key);
		if (r == null || "".equals(r)) {
			return defa;
		} else {
			return Integer.valueOf(r);
		}
	}

	public static int getPropertyInt(String file, String key) {
		return getPropertyInt(file, key, 0);
	}

	public static boolean getPropertyBoolean(String file, String key, boolean defa) {
		Map<String, String> map = new HashMap<String, String>();
		map = readProperty(file);
		String r = map.get(key);
		if (r == null || "".equals(r)) {
			return defa;
		} else {
			return Converter.booleanValue(r);
		}
	}

	public static boolean getPropertyBoolean(String file, String key) {
		return getPropertyBoolean(file, key, false);
	}
}
