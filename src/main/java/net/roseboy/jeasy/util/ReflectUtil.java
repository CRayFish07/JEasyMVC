package net.roseboy.jeasy.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ���乤����
 * @author roseboy.net
 *
 */
public class ReflectUtil {

	/**
	 * ����get����
	 * @param o ʵ��
	 * @param fieldName �ֶ���
	 * @return ֵ
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
	 * ���ø���get����
	 * @param o ʵ��
	 * @param fieldName �ֶ���
	 * @return ֵ
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
	 * ����set
	 * @param o ʵ��
	 * @param fieldName �ֶ�
	 * @param value ֵ
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
	 * ���ø���set
	 * @param o ʵ��
	 * @param fieldName �ֶ�
	 * @param value ֵ
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
	 * ����ĳ����
	 * @param o ����ʵ��
	 * @param m ��������
	 * @return �����Ƿ�ִ�гɹ�
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
	 * ����ĳ����
	 * @param o ����ʵ��
	 * @param m ��������
	 * @param result ���ڷ��ؽ��
	 * @return �����Ƿ�ִ�гɹ�
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
			Log.print("�Ҳ����÷���[" + o.getClass().getName() + "." + m + "]");
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
			Log.print("Ŀ�귽��[" + o.getClass().getName() + "." + m + "]����");
			return false;
		}
		return true;
	}
}
