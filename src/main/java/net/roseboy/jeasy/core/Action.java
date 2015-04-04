package net.roseboy.jeasy.core;

import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.roseboy.jeasy.upload.MultipartRequest;
import net.roseboy.jeasy.upload.UploadFile;

@SuppressWarnings({ "unchecked", "rawtypes" })
/**
 * Action，封装了所有http有关的操作
 * 用户可在自己的Action中集成该类
 * 也可声明一个该类型的成员变量
 * 
 * @author roseboy.net
 *
 */
public class Action {
	// 这3个玩意赋值之后此类才能用

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String urlPara;

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getUrlPara() {
		return urlPara;
	}

	public void setUrlPara(String urlPara) {
		this.urlPara = urlPara;
	}

	/**
	 * 初始化Action
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param urlPara url传过来的参数rest
	 * @return
	 */
	public Action init(HttpServletRequest request, HttpServletResponse response, String urlPara) {
		this.request = request;
		this.response = response;
		this.urlPara = urlPara;
		return this;
	}

	/**
	 * 根据索引获取参数
	 * @param index 索引
	 * @return 值
	 */
	public String getPara(int index) {
		index += 2;
		String target = getPara();
		if (target != null) {
			String p[] = target.split("/");
			if (index > 0 && index < p.length) {
				return p[index];
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Stores an attribute in this request
	 * @param name a String specifying the name of the attribute
	 * @param value the Object to be stored
	 */
	public Action setAttr(String name, Object value) {
		request.setAttribute(name, value);
		return this;
	}

	/**
	 * Removes an attribute from this request
	 * @param name a String specifying the name of the attribute to remove
	 */
	public Action removeAttr(String name) {
		request.removeAttribute(name);
		return this;
	}

	/**
	 * Stores attributes in this request, key of the map as attribute name and value of the map as attribute value
	 * @param attrMap key and value as attribute of the map to be stored
	 */
	public Action setAttrs(Map<String, Object> attrMap) {
		for (Map.Entry<String, Object> entry : attrMap.entrySet())
			request.setAttribute(entry.getKey(), entry.getValue());
		return this;
	}

	/**
	 * Returns the value of a request parameter as a String, or null if the parameter does not exist.
	 * <p>
	 * You should only use this method when you are sure the parameter has only one value. If the 
	 * parameter might have more than one value, use getParaValues(java.lang.String). 
	 * <p>
	 * If you use this method with a multivalued parameter, the value returned is equal to the first 
	 * value in the array returned by getParameterValues.
	 * @param name a String specifying the name of the parameter
	 * @return a String representing the single value of the parameter
	 */
	public String getPara(String name) {
		return request.getParameter(name);
	}

	/**
	 * Returns the value of a request parameter as a String, or default value if the parameter does not exist.
	 * @param name a String specifying the name of the parameter
	 * @param defaultValue a String value be returned when the value of parameter is null
	 * @return a String representing the single value of the parameter
	 */
	public String getPara(String name, String defaultValue) {
		String result = request.getParameter(name);
		return result != null && !"".equals(result) ? result : defaultValue;
	}

	/**
	 * Returns the values of the request parameters as a Map.
	 * @return a Map contains all the parameters name and value
	 */
	public Map<String, String[]> getParaMap() {
		return request.getParameterMap();
	}

	/**
	 * Returns an Enumeration of String objects containing the names of the parameters
	 * contained in this request. If the request has no parameters, the method returns
	 * an empty Enumeration.
	 * @return an Enumeration of String objects, each String containing the name of 
	 * 			a request parameter; or an empty Enumeration if the request has no parameters
	 */
	public Enumeration<String> getParaNames() {
		return request.getParameterNames();
	}

	/**
	 * Returns an array of String objects containing all of the values the given request 
	 * parameter has, or null if the parameter does not exist. If the parameter has a 
	 * single value, the array has a length of 1.
	 * @param name a String containing the name of the parameter whose value is requested
	 * @return an array of String objects containing the parameter's values
	 */
	public String[] getParaValues(String name) {
		return request.getParameterValues(name);
	}

	/**
	 * Returns an array of Integer objects containing all of the values the given request 
	 * parameter has, or null if the parameter does not exist. If the parameter has a 
	 * single value, the array has a length of 1.
	 * @param name a String containing the name of the parameter whose value is requested
	 * @return an array of Integer objects containing the parameter's values
	 */
	public Integer[] getParaValuesToInt(String name) {
		String[] values = request.getParameterValues(name);
		if (values == null)
			return null;
		Integer[] result = new Integer[values.length];
		for (int i = 0; i < result.length; i++)
			result[i] = Integer.parseInt(values[i]);
		return result;
	}

	/**
	 * Returns an Enumeration containing the names of the attributes available to this request.
	 * This method returns an empty Enumeration if the request has no attributes available to it. 
	 * @return an Enumeration of strings containing the names of the request's attributes
	 */
	public Enumeration<String> getAttrNames() {
		return request.getAttributeNames();
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public <T> T getAttr(String name) {
		return (T) request.getAttribute(name);
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an String Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public String getAttrForStr(String name) {
		return (String) request.getAttribute(name);
	}

	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * @param name a String specifying the name of the attribute
	 * @return an Integer Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public Integer getAttrForInt(String name) {
		return (Integer) request.getAttribute(name);
	}

	private Integer toInt(String value, Integer defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;
		if (value.startsWith("N") || value.startsWith("n"))
			return -Integer.parseInt(value.substring(1));
		return Integer.parseInt(value);
	}

	/**
	 * Returns the value of a request parameter and convert to Integer.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Integer getParaToInt(String name) {
		return toInt(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Integer with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Integer getParaToInt(String name, Integer defaultValue) {
		return toInt(request.getParameter(name), defaultValue);
	}

	private Long toLong(String value, Long defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;
		if (value.startsWith("N") || value.startsWith("n"))
			return -Long.parseLong(value.substring(1));
		return Long.parseLong(value);
	}

	/**
	 * Returns the value of a request parameter and convert to Long.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Long getParaToLong(String name) {
		return toLong(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Long with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return a Integer representing the single value of the parameter
	 */
	public Long getParaToLong(String name, Long defaultValue) {
		return toLong(request.getParameter(name), defaultValue);
	}

	private Boolean toBoolean(String value, Boolean defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;
		value = value.trim().toLowerCase();
		if ("1".equals(value) || "true".equals(value))
			return Boolean.TRUE;
		else if ("0".equals(value) || "false".equals(value))
			return Boolean.FALSE;
		throw new RuntimeException("Can not parse the parameter \"" + value + "\" to boolean value.");
	}

	/**
	 * Returns the value of a request parameter and convert to Boolean.
	 * @param name a String specifying the name of the parameter
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", null if parameter is not exists
	 */
	public Boolean getParaToBoolean(String name) {
		return toBoolean(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Boolean with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return true if the value of the parameter is "true" or "1", false if it is "false" or "0", default value if it is null
	 */
	public Boolean getParaToBoolean(String name, Boolean defaultValue) {
		return toBoolean(request.getParameter(name), defaultValue);
	}

	/**
	 * Get all para from url and convert to Boolean
	 */
	public Boolean getParaToBoolean() {
		return toBoolean(getPara(), null);
	}

	/**
	 * Get para from url and conver to Boolean. The first index is 0
	 */
	public Boolean getParaToBoolean(int index) {
		return toBoolean(getPara(index), null);
	}

	/**
	 * Get para from url and conver to Boolean with default value if it is null.
	 */
	public Boolean getParaToBoolean(int index, Boolean defaultValue) {
		return toBoolean(getPara(index), defaultValue);
	}

	private Date toDate(String value, Date defaultValue) {
		if (value == null || "".equals(value.trim()))
			return defaultValue;
		try {
			return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the value of a request parameter and convert to Date.
	 * @param name a String specifying the name of the parameter
	 * @return a Date representing the single value of the parameter
	 */
	public Date getParaToDate(String name) {
		return toDate(request.getParameter(name), null);
	}

	/**
	 * Returns the value of a request parameter and convert to Date with a default value if it is null.
	 * @param name a String specifying the name of the parameter
	 * @return a Date representing the single value of the parameter
	 */
	public Date getParaToDate(String name, Date defaultValue) {
		return toDate(request.getParameter(name), defaultValue);
	}

	/**
	 * Get all para from url and convert to Date
	 */
	public Date getParaToDate() {
		return toDate(getPara(), null);
	}

	/**
	 * Return HttpSession.
	 */
	public HttpSession getSession() {
		return request.getSession();
	}

	/**
	 * Return HttpSession.
	 * @param create a boolean specifying create HttpSession if it not exists
	 */
	public HttpSession getSession(boolean create) {
		return request.getSession(create);
	}

	/**
	 * Return a Object from session.
	 * @param key a String specifying the key of the Object stored in session
	 */
	public <T> T getSessionAttr(String key) {
		HttpSession session = request.getSession(false);
		return session != null ? (T) session.getAttribute(key) : null;
	}

	/**
	 * Store Object to session.
	 * @param key a String specifying the key of the Object stored in session
	 * @param value a Object specifying the value stored in session
	 */
	public Action setSessionAttr(String key, Object value) {
		request.getSession().setAttribute(key, value);
		return this;
	}

	/**
	 * Remove Object in session.
	 * @param key a String specifying the key of the Object stored in session
	 */
	public Action removeSessionAttr(String key) {
		HttpSession session = request.getSession(false);
		if (session != null)
			session.removeAttribute(key);
		return this;
	}

	/**
	 * Get cookie value by cookie name.
	 */
	public String getCookie(String name, String defaultValue) {
		Cookie cookie = getCookieObject(name);
		return cookie != null ? cookie.getValue() : defaultValue;
	}

	/**
	 * Get cookie value by cookie name.
	 */
	public String getCookie(String name) {
		return getCookie(name, null);
	}

	/**
	 * Get cookie value by cookie name and convert to Integer.
	 */
	public Integer getCookieToInt(String name) {
		String result = getCookie(name);
		return result != null ? Integer.parseInt(result) : null;
	}

	/**
	 * Get cookie value by cookie name and convert to Integer.
	 */
	public Integer getCookieToInt(String name, Integer defaultValue) {
		String result = getCookie(name);
		return result != null ? Integer.parseInt(result) : defaultValue;
	}

	/**
	 * Get cookie value by cookie name and convert to Long.
	 */
	public Long getCookieToLong(String name) {
		String result = getCookie(name);
		return result != null ? Long.parseLong(result) : null;
	}

	/**
	 * Get cookie value by cookie name and convert to Long.
	 */
	public Long getCookieToLong(String name, Long defaultValue) {
		String result = getCookie(name);
		return result != null ? Long.parseLong(result) : defaultValue;
	}

	/**
	 * Get cookie object by cookie name.
	 */
	public Cookie getCookieObject(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}

	/**
	 * Get all cookie objects.
	 */
	public Cookie[] getCookieObjects() {
		Cookie[] result = request.getCookies();
		return result != null ? result : new Cookie[0];
	}

	/**
	 * Set Cookie to response.
	 */
	public Action setCookie(Cookie cookie) {
		response.addCookie(cookie);
		return this;
	}

	/**
	 * Set Cookie to response.
	 * @param name cookie name
	 * @param value cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path see Cookie.setPath(String)
	 */
	public Action setCookie(String name, String value, int maxAgeInSeconds, String path) {
		setCookie(name, value, maxAgeInSeconds, path, null);
		return this;
	}

	/**
	 * Set Cookie to response.
	 * @param name cookie name
	 * @param value cookie value
	 * @param maxAgeInSeconds -1: clear cookie when close browser. 0: clear cookie immediately.  n>0 : max age in n seconds.
	 * @param path see Cookie.setPath(String)
	 * @param domain the domain name within which this cookie is visible; form is according to RFC 2109
	 */
	public Action setCookie(String name, String value, int maxAgeInSeconds, String path, String domain) {
		Cookie cookie = new Cookie(name, value);
		if (domain != null)
			cookie.setDomain(domain);
		cookie.setMaxAge(maxAgeInSeconds);
		cookie.setPath(path);
		response.addCookie(cookie);
		return this;
	}

	/**
	 * Set Cookie with path = "/".
	 */
	public Action setCookie(String name, String value, int maxAgeInSeconds) {
		setCookie(name, value, maxAgeInSeconds, "/", null);
		return this;
	}

	/**
	 * Remove Cookie with path = "/".
	 */
	public Action removeCookie(String name) {
		setCookie(name, null, 0, "/", null);
		return this;
	}

	/**
	 * Remove Cookie.
	 */
	public Action removeCookie(String name, String path) {
		setCookie(name, null, 0, path, null);
		return this;
	}

	/**
	 * Remove Cookie.
	 */
	public Action removeCookie(String name, String path, String domain) {
		setCookie(name, null, 0, path, domain);
		return this;
	}

	// --------

	/**
	 * Get all para with separator char from url
	 */
	public String getPara() {
		if ("".equals(urlPara)){// urlPara maybe is "" see
			urlPara = null;
		}
		return urlPara;
	}

	/**
	 * Get para from url with default value if it is null or "".
	 */
	public String getPara(int index, String defaultValue) {
		String result = getPara(index);
		return result != null && !"".equals(result) ? result : defaultValue;
	}

	/**
	 * Get para from url and conver to Integer. The first index is 0
	 */
	public Integer getParaToInt(int index) {
		return toInt(getPara(index), null);
	}

	/**
	 * Get para from url and conver to Integer with default value if it is null.
	 */
	public Integer getParaToInt(int index, Integer defaultValue) {
		return toInt(getPara(index), defaultValue);
	}

	/**
	 * Get para from url and conver to Long.
	 */
	public Long getParaToLong(int index) {
		return toLong(getPara(index), null);
	}

	/**
	 * Get para from url and conver to Long with default value if it is null.
	 */
	public Long getParaToLong(int index, Long defaultValue) {
		return toLong(getPara(index), defaultValue);
	}

	/**
	 * Get all para from url and convert to Integer
	 */
	public Integer getParaToInt() {
		return toInt(getPara(), null);
	}

	/**
	 * Get all para from url and convert to Long
	 */
	public Long getParaToLong() {
		return toLong(getPara(), null);
	}

	/**
	 * Keep all parameter's value except model value
	 */
	public Action keepPara() {
		Map<String, String[]> map = request.getParameterMap();
		for (Entry<String, String[]> e : map.entrySet()) {
			String[] values = e.getValue();
			if (values.length == 1)
				request.setAttribute(e.getKey(), values[0]);
			else
				request.setAttribute(e.getKey(), values);
		}
		return this;
	}

	/**
	 * Keep parameter's value names pointed, model value can not be kept
	 */
	public Action keepPara(String... names) {
		for (String name : names) {
			String[] values = request.getParameterValues(name);
			if (values != null) {
				if (values.length == 1)
					request.setAttribute(name, values[0]);
				else
					request.setAttribute(name, values);
			}
		}
		return this;
	}

	/**
	 * Convert para to special type and keep it
	 */
	public Action keepPara(Class type, String name) {
		String[] values = request.getParameterValues(name);
		if (values != null) {
			if (values.length == 1)
				try {
					request.setAttribute(name, Converter.convert(type, values[0]));
				} catch (ParseException e) {
				}
			else
				request.setAttribute(name, values);
		}
		return this;
	}

	public Action keepPara(Class type, String... names) {
		if (type == String.class)
			return keepPara(names);

		if (names != null)
			for (String name : names)
				keepPara(type, name);
		return this;
	}

	/**
	 * Get upload file from multipart request.
	 */
	public List<UploadFile> getFiles(String saveDirectory, Integer maxPostSize, String encoding) {
		if (request instanceof MultipartRequest == false)
			request = new MultipartRequest(request, saveDirectory, maxPostSize, encoding);
		return ((MultipartRequest) request).getFiles();
	}

	public UploadFile getFile(String parameterName, String saveDirectory, Integer maxPostSize, String encoding) {
		getFiles(saveDirectory, maxPostSize, encoding);
		return getFile(parameterName);
	}

	public List<UploadFile> getFiles(String saveDirectory, int maxPostSize) {
		if (request instanceof MultipartRequest == false)
			request = new MultipartRequest(request, saveDirectory, maxPostSize);
		return ((MultipartRequest) request).getFiles();
	}

	public UploadFile getFile(String parameterName, String saveDirectory, int maxPostSize) {
		getFiles(saveDirectory, maxPostSize);
		return getFile(parameterName);
	}

	public List<UploadFile> getFiles(String saveDirectory) {
		if (request instanceof MultipartRequest == false)
			request = new MultipartRequest(request, saveDirectory);
		return ((MultipartRequest) request).getFiles();
	}

	public UploadFile getFile(String parameterName, String saveDirectory) {
		getFiles(saveDirectory);
		return getFile(parameterName);
	}

	public List<UploadFile> getFiles() {
		if (request instanceof MultipartRequest == false)
			request = new MultipartRequest(request);
		return ((MultipartRequest) request).getFiles();
	}

	public UploadFile getFile() {
		List<UploadFile> uploadFiles = getFiles();
		return uploadFiles.size() > 0 ? uploadFiles.get(0) : null;
	}

	public UploadFile getFile(String parameterName) {
		List<UploadFile> uploadFiles = getFiles();
		for (UploadFile uploadFile : uploadFiles) {
			if (uploadFile.getParameterName().equals(parameterName)) {
				return uploadFile;
			}
		}
		return null;
	}

}
