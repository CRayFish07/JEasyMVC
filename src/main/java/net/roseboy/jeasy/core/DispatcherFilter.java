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
 * 全局拦截
 * 用户需再web.xml配置
 * 拦截转发
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
	 * 拦截处理
	 * 初始化处理过程
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		Handle h = new Handle();
		h.init(request, response);
		try {
			if (h.action(request, response)) {// action逻辑部分执行成功
				h.result(request, response, chain);// 执行视图生成
			} else {
				chain.doFilter(req, res);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 * 初始化
	 * 加载配置
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
		DBPool.getInstance().initDBPool();// 读取完配置必须初始化
	}
}
