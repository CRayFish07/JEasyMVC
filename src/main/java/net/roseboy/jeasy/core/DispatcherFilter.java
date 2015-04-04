package net.roseboy.jeasy.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.roseboy.jeasy.db.DBPool;

/**
 * Servlet Filter implementation class DispatcherFilter
 * ȫ������
 * �û�����web.xml����
 * ����ת��
 * @author roseboy.net
 *
 */
// @WebFilter("/*")
public class DispatcherFilter implements Filter {

	/**
	 * Default constructor. 
	 */
	public DispatcherFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 * ���ش���
	 * ��ʼ���������
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		Handle h = new Handle();
		h.init(request, response);
		try {
			if (h.action(request, response)) {// action�߼�����ִ�гɹ�
				h.result(request, response, chain);// ִ����ͼ����
			} else {
				chain.doFilter(req, res);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 * ��ʼ��
	 * ��������
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Load config
		Map<String, String> configMap = new HashMap<String, String>();
		Enumeration<String> x = fConfig.getInitParameterNames();
		String ele;
		while (x.hasMoreElements()) {
			ele = x.nextElement();
			configMap.put(ele.toLowerCase(), fConfig.getInitParameter(ele));
		}

		Loader.getInstance().setDevMode(Converter.booleanValue(configMap.get("devmode")));
		Loader.getInstance().setEncoding(configMap.get("encoding"));
		DBPool.getInstance().setDBSource(configMap.get("jdbc"));
		DBPool.getInstance().initDBPool();// ��ȡ�����ñ����ʼ��
	}
}
