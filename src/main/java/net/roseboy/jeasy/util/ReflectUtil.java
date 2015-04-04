package net.roseboy.jeasy.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * @author roseboy.net
 *
 */
public class ReflectUtil {

	/**
	 * 调用get方法
	 * @param o 实例
	 * @param fieldName 字段名
	 * @return 值
	 */
	public static Object invokeGet(Object o, String fieldName) {
		StringBuffer sb = new StringBuffer();
		sb.append("get");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		try {
			Method method = o.getClass().getMethod(sb.toString());
			return method.invoke(o, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 调用父类get方法
	 * @param o 实例
	 * @param fieldName 字段名
	 * @return 值
	 */
	public static Object getSuperGetMethod(Object o, String fieldName) {
		StringBuffer sb = new StringBuffer();
		sb.append("get");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		try {
			Method method = o.getClass().getSuperclass().getMethod(sb.toString());
			return method.invoke(o, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 调用set
	 * @param o 实例
	 * @param fieldName 字段
	 * @param value 值
	 */
	@SuppressWarnings("rawtypes")
	public static void invokeSet(Object o, String fieldName, Object value) {
		try {
			Class[] parameterTypes = new Class[1];
			Field field = o.getClass().getDeclaredField(fieldName);
			parameterTypes[0] = field.getType();

			StringBuffer sb = new StringBuffer();
			sb.append("set");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));
			Method method = o.getClass().getMethod(sb.toString(), parameterTypes);
			method.invoke(o, new Object[] { value });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用父类set
	 * @param o 实例
	 * @param fieldName 字段
	 * @param value 值
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void invokeSuperSet(Object o, String fieldName, Object value) {
		try {
			Class[] parameterTypes = new Class[1];
			Field field = o.getClass().getSuperclass().getDeclaredField(fieldName);
			parameterTypes[0] = field.getType();
			StringBuffer sb = new StringBuffer();
			sb.append("set");
			sb.append(fieldName.substring(0, 1).toUpperCase());
			sb.append(fieldName.substring(1));

			Method method = o.getClass().getSuperclass().getMethod(sb.toString(), parameterTypes);
			method.invoke(o, new Object[] { value });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用某方法
	 * @param o 对象实例
	 * @param m 方法名称
	 * @return 返回是否执行成功
	 */
	public static boolean invokeMethod(Object o, String m) {
		Method method = null;
		try {
			method = o.getClass().getMethod(m);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
		try {
			method.invoke(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 调用某方法
	 * @param o 对象实例
	 * @param m 方法名称
	 * @param result 用于返回结果
	 * @return 返回是否执行成功
	 */
	public static boolean invokeMethod(Object o, String m, StringBuffer result) {
		Method method = null;
		try {
			method = o.getClass().getMethod(m);
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			Log.print("找不到该方法[" + o.getClass().getName() + "." + m + "]");
			return false;
		}
		try {
			Object r = method.invoke(o);
			if (r != null) {
				result.append(r);
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			Log.print("目标方法[" + o.getClass().getName() + "." + m + "]出错");
			return false;
		}
		return true;
	}
}
