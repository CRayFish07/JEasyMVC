package net.roseboy.jeasy.core;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
/**
 * 类型转换
 * 部分来自jfinal
 * 部分来自Struts2
 * 感谢
 * @author roseboy.net
 *
 */
public class Converter {
	private final Map<Class, Object> primitiveDefaults;

	public Converter() {
		Map<Class, Object> map = new HashMap<Class, Object>();
		map.put(Boolean.TYPE, Boolean.FALSE);
		map.put(Byte.TYPE, Byte.valueOf((byte) 0));
		map.put(Short.TYPE, Short.valueOf((short) 0));
		map.put(Character.TYPE, new Character((char) 0));
		map.put(Integer.TYPE, Integer.valueOf(0));
		map.put(Long.TYPE, Long.valueOf(0L));
		map.put(Float.TYPE, new Float(0.0f));
		map.put(Double.TYPE, new Double(0.0));
		map.put(BigInteger.class, new BigInteger("0"));
		map.put(BigDecimal.class, new BigDecimal(0.0));
		primitiveDefaults = Collections.unmodifiableMap(map);
	}

	/**
	 * Returns the value converted numerically to the given class type
	 * 
	 * This method also detects when arrays are being converted and converts the
	 * components of one array to the type of the other.
	 * 
	 * @param value
	 *            an object to be converted to the given type
	 * @param toType
	 *            class type to be converted to
	 * @return converted value of the type given, or value if the value cannot
	 *         be converted to the given type.
	 */
	@SuppressWarnings("unchecked")
	public Object convertValue(Object value, Class toType) {
		Object result = null;

		if (value != null) {
			/* If array -> array then convert components of array individually */
			if (value.getClass().isArray() && toType.isArray()) {
				Class componentType = toType.getComponentType();
				result = Array.newInstance(componentType, Array.getLength(value));
				for (int i = 0, icount = Array.getLength(value); i < icount; i++) {
					Array.set(result, i, convertValue(Array.get(value, i), componentType));
				}
			} else {
				if ((toType == Integer.class) || (toType == Integer.TYPE))
					result = Integer.valueOf((int) longValue(value));
				if ((toType == Double.class) || (toType == Double.TYPE))
					result = new Double(doubleValue(value));
				if ((toType == Boolean.class) || (toType == Boolean.TYPE))
					result = booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
				if ((toType == Byte.class) || (toType == Byte.TYPE))
					result = Byte.valueOf((byte) longValue(value));
				if ((toType == Character.class) || (toType == Character.TYPE))
					result = new Character((char) longValue(value));
				if ((toType == Short.class) || (toType == Short.TYPE))
					result = Short.valueOf((short) longValue(value));
				if ((toType == Long.class) || (toType == Long.TYPE))
					result = Long.valueOf(longValue(value));
				if ((toType == Float.class) || (toType == Float.TYPE))
					result = new Float(doubleValue(value));
				if (toType == BigInteger.class)
					result = bigIntValue(value);
				if (toType == BigDecimal.class)
					result = bigDecValue(value);
				if (toType == String.class)
					result = stringValue(value);
				if (Enum.class.isAssignableFrom(toType))
					result = enumValue((Class<Enum>) toType, value);
			}
		} else {
			if (toType.isPrimitive()) {
				result = primitiveDefaults.get(toType);
			}
		}
		return result;
	}

	/**
	 * Evaluates the given object as a boolean: if it is a Boolean object, it's
	 * easy; if it's a Number or a Character, returns true for non-zero objects;
	 * and otherwise returns true for non-null objects.
	 * 
	 * @param value
	 *            an object to interpret as a boolean
	 * @return the boolean value implied by the given object
	 */
	public static boolean booleanValue(Object value) {
		if (value == null)
			return false;
		Class c = value.getClass();
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue();
		// if ( c == String.class )
		// return ((String)value).length() > 0;
		if (c == Character.class)
			return ((Character) value).charValue() != 0;
		if (c == String.class)
			return "true".equals(value);
		if (value instanceof Number)
			return ((Number) value).doubleValue() != 0;
		return true; // non-null
	}

	@SuppressWarnings("unchecked")
	public Enum<?> enumValue(Class toClass, Object o) {
		Enum<?> result = null;
		if (o == null) {
			result = null;
		} else if (o instanceof String[]) {
			result = Enum.valueOf(toClass, ((String[]) o)[0]);
		} else if (o instanceof String) {
			result = Enum.valueOf(toClass, (String) o);
		}
		return result;
	}

	/**
	 * Evaluates the given object as a long integer.
	 * 
	 * @param value
	 *            an object to interpret as a long integer
	 * @return the long integer value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a long integer
	 */
	public static long longValue(Object value) throws NumberFormatException {
		if (value == null)
			return 0L;
		Class c = value.getClass();
		if (c.getSuperclass() == Number.class)
			return ((Number) value).longValue();
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		if (c == Character.class)
			return ((Character) value).charValue();
		return Long.parseLong(stringValue(value, true));
	}

	/**
	 * Evaluates the given object as a double-precision floating-point number.
	 * 
	 * @param value
	 *            an object to interpret as a double
	 * @return the double value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a double
	 */
	public static double doubleValue(Object value) throws NumberFormatException {
		if (value == null)
			return 0.0;
		Class c = value.getClass();
		if (c.getSuperclass() == Number.class)
			return ((Number) value).doubleValue();
		if (c == Boolean.class)
			return ((Boolean) value).booleanValue() ? 1 : 0;
		if (c == Character.class)
			return ((Character) value).charValue();
		String s = stringValue(value, true);

		return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
		/*
		 * For 1.1 parseDouble() is not available
		 */
		// return Double.valueOf( value.toString() ).doubleValue();
	}

	/**
	 * Evaluates the given object as a BigInteger.
	 * 
	 * @param value
	 *            an object to interpret as a BigInteger
	 * @return the BigInteger value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a BigInteger
	 */
	public static BigInteger bigIntValue(Object value) throws NumberFormatException {
		if (value == null)
			return BigInteger.valueOf(0L);
		Class c = value.getClass();
		if (c == BigInteger.class)
			return (BigInteger) value;
		if (c == BigDecimal.class)
			return ((BigDecimal) value).toBigInteger();
		if (c.getSuperclass() == Number.class)
			return BigInteger.valueOf(((Number) value).longValue());
		if (c == Boolean.class)
			return BigInteger.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
		if (c == Character.class)
			return BigInteger.valueOf(((Character) value).charValue());
		return new BigInteger(stringValue(value, true));
	}

	/**
	 * Evaluates the given object as a BigDecimal.
	 * 
	 * @param value
	 *            an object to interpret as a BigDecimal
	 * @return the BigDecimal value implied by the given object
	 * @throws NumberFormatException
	 *             if the given object can't be understood as a BigDecimal
	 */
	public static BigDecimal bigDecValue(Object value) throws NumberFormatException {
		if (value == null)
			return BigDecimal.valueOf(0L);
		Class c = value.getClass();
		if (c == BigDecimal.class)
			return (BigDecimal) value;
		if (c == BigInteger.class)
			return new BigDecimal((BigInteger) value);
		if (c.getSuperclass() == Number.class)
			return new BigDecimal(((Number) value).doubleValue());
		if (c == Boolean.class)
			return BigDecimal.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
		if (c == Character.class)
			return BigDecimal.valueOf(((Character) value).charValue());
		return new BigDecimal(stringValue(value, true));
	}

	/**
	 * Evaluates the given object as a String and trims it if the trim flag is
	 * true.
	 * 
	 * @param value
	 *            an object to interpret as a String
	 * @return the String value implied by the given object as returned by the
	 *         toString() method, or "null" if the object is null.
	 */
	public static String stringValue(Object value, boolean trim) {
		String result;

		if (value == null) {
			result = "null";
		} else {
			result = value.toString();
			if (trim) {
				result = result.trim();
			}
		}
		return result;
	}

	/**
	 * Evaluates the given object as a String.
	 * 
	 * @param value
	 *            an object to interpret as a String
	 * @return the String value implied by the given object as returned by the
	 *         toString() method, or "null" if the object is null.
	 */
	public static String stringValue(Object value) {
		return stringValue(value, false);
	}

	// *******************************************************************************
	/**
	 * 以下是来自一Jfinal 逗比工程师的代码
	 */

	private static final int timeStampLen = "2011-01-18 16:18:18".length();
	private static final String timeStampPattern = "yyyy-MM-dd HH:mm:ss";
	private static final String datePattern = "yyyy-MM-dd";

	/**
	 * test for all types of mysql
	 * 
	 * 表单提交测试结果:
	 * 1: 表单中的域,就算不输入任何内容,也会传过来 "", 也即永远不可能为 null.
	 * 2: 如果输入空格也会提交上来
	 * 3: 需要考 model中的 string属性,在传过来 "" 时是该转成 null还是不该转换,
	 *    我想, 因为用户没有输入那么肯定是 null, 而不该是 ""
	 * 
	 * 注意: 1:当clazz参数不为String.class, 且参数s为空串blank的情况,
	 *       此情况下转换结果为 null, 而不应该抛出异常
	 *      2:调用者需要对被转换数据做 null 判断，参见 ModelInjector 的两处调用
	 */
	public static final Object convert(Class<?> clazz, String s) throws ParseException {
		// mysql type: varchar, char, enum, set, text, tinytext, mediumtext,
		// longtext
		if (clazz == String.class) {
			return ("".equals(s) ? null : s); // 用户在表单域中没有输入内容时将提交过来 "",
												// 因为没有输入,所以要转成 null.
		}
		s = s.trim();
		if ("".equals(s)) { // 前面的 String跳过以后,所有的空字符串全都转成 null, 这是合理的
			return null;
		}
		// 以上两种情况无需转换,直接返回, 注意, 本方法不接受null为 s 参数(经测试永远不可能传来null, 因为无输入传来的也是"")

		Object result = null;
		// mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
		if (clazz == Integer.class || clazz == int.class) {
			result = Integer.parseInt(s);
		}
		// mysql type: bigint
		else if (clazz == Long.class || clazz == long.class) {
			result = Long.parseLong(s);
		}
		// 经测试java.util.Data类型不会返回, java.sql.Date,
		// java.sql.Time,java.sql.Timestamp 全部直接继承自 java.util.Data, 所以
		// getDate可以返回这三类数据
		else if (clazz == java.util.Date.class) {
			if (s.length() >= timeStampLen) { // if (x < timeStampLen) 改用
												// datePattern 转换，更智能
				// Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]
				// result = new
				// java.util.Date(java.sql.Timestamp.valueOf(s).getTime()); //
				// error under jdk 64bit(maybe)
				result = new SimpleDateFormat(timeStampPattern).parse(s);
			} else {
				// result = new
				// java.util.Date(java.sql.Date.valueOf(s).getTime()); // error
				// under jdk 64bit
				result = new SimpleDateFormat(datePattern).parse(s);
			}
		}
		// mysql type: date, year
		else if (clazz == java.sql.Date.class) {
			if (s.length() >= timeStampLen) { // if (x < timeStampLen) 改用
												// datePattern 转换，更智能
				// result = new
				// java.sql.Date(java.sql.Timestamp.valueOf(s).getTime()); //
				// error under jdk 64bit(maybe)
				result = new java.sql.Date(new SimpleDateFormat(timeStampPattern).parse(s).getTime());
			} else {
				// result = new
				// java.sql.Date(java.sql.Date.valueOf(s).getTime()); // error
				// under jdk 64bit
				result = new java.sql.Date(new SimpleDateFormat(datePattern).parse(s).getTime());
			}
		}
		// mysql type: time
		else if (clazz == java.sql.Time.class) {
			result = java.sql.Time.valueOf(s);
		}
		// mysql type: timestamp, datetime
		else if (clazz == java.sql.Timestamp.class) {
			result = java.sql.Timestamp.valueOf(s);
		}
		// mysql type: real, double
		else if (clazz == Double.class) {
			result = Double.parseDouble(s);
		}
		// mysql type: float
		else if (clazz == Float.class) {
			result = Float.parseFloat(s);
		}
		// mysql type: bit, tinyint(1)
		else if (clazz == Boolean.class) {
			result = Boolean.parseBoolean(s) || "1".equals(s);
		}
		// mysql type: decimal, numeric
		else if (clazz == java.math.BigDecimal.class) {
			result = new java.math.BigDecimal(s);
		}
		// mysql type: unsigned bigint
		else if (clazz == java.math.BigInteger.class) {
			result = new java.math.BigInteger(s);
		}
		// mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob.
		// I have not finished the test.
		else if (clazz == byte[].class) {
			result = s.getBytes();
		} else {
			if (Loader.getInstance().getIsDevMode())
				throw new RuntimeException("Please add code in " + Converter.class + ". The type can't be converted: " + clazz.getName());
			else
				throw new RuntimeException(clazz.getName() + " can not be converted, please use other type of attributes in your model!");
		}

		return result;
	}
}
