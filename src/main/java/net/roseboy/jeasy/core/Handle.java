package net.roseboy.jeasy.core;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.roseboy.jeasy.annotation.ActionMethod;
import net.roseboy.jeasy.annotation.ActionResult;
import net.roseboy.jeasy.annotation.Result;
import net.roseboy.jeasy.emum.ResultType;
import net.roseboy.jeasy.upload.DownloadFile;
import net.roseboy.jeasy.util.Log;
import net.roseboy.jeasy.util.ReflectUtil;

public class Handle {
	private String target;// URL
	private String[] uri;// 分割的uri
	private String action;// action的名称
	private String actcls;// action对应的处理类
	private Object obj = null;// 用户action实例
	private String method;// 方法名称
	private Method AnnotationMethod;// 调用方法对应的方法实例
	private StringBuffer result = new StringBuffer();// 引用传值，接收action-mwtghod的返回值
	private Map<String, String> actmap;// action Map
	private List<String> clslist;// 用户的类

	/**
	 * 初始化处理类
	 * 提取rest url中的action以及方法
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws UnsupportedEncodingException
	 */
	public void init(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding(Loader.getInstance().getEncoding());//设置编码
		response.setCharacterEncoding(Loader.getInstance().getEncoding());

		target = request.getRequestURI();
		String webapp = request.getContextPath();
		if (target.startsWith(webapp)) {// 去掉wepapp目录
			target = target.substring(webapp.length() + 1);
		}
		uri = target.split("/");
		action = uri[0].trim();// Action名称
		method = uri.length > 1 ? uri[1].trim() : "index";// method名称
		actmap = Loader.getInstance().getActionMapping();
		clslist = Loader.getInstance().getClassList();
		actcls = actmap.get(action);// Action对应的class全名
	}

	/**
	 * 处理请求入口
	 * 调用用户Action重的处理过程
	 * 
	 * @param req ServletRequest
	 * @param res ServletResponse
	 * @throws ServletException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public boolean action(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (actcls != null) {
			Class<?> cls = Class.forName(actcls);
			Method[] methods = cls.getDeclaredMethods();
			ActionMethod am;
			for (Method met : methods) {
				if (met.isAnnotationPresent(ActionMethod.class)) {// 方法上的注解是ActionMethod
					am = met.getAnnotation(ActionMethod.class);
					if (am.value().equalsIgnoreCase(method)) {// 注解与请求方法相等，不区分大小写
						AnnotationMethod = met;
						// System.out.println("="+met.getName()+":"+am.value());
						obj = cls.newInstance();
						initAction(request, response, target, obj);// 初始化action
						setActionFiled(request, obj);// 设置用户action的属性
						setModuleFiled(request, obj);// 设置module的属性
						if (Loader.getInstance().getIsDevMode()) {
							Log.print("URL:" + target + "==>" + actcls + "." + met.getName());
						}
						return ReflectUtil.invokeMethod(obj, met.getName(), result);// 调用所请求的方法
					}
				}
			}// for
			return false;
		} else {
			return false;
		}
	}

	/**
	 * 初始化用户action 
	 * 如果继承自jeasy.Action则初始化父类
	 * 如果定义了Action类型的变量，则初始化这个变量
	 * 
	 * @param obj 用户的action
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param urlPara url的参数
	 */
	public void initAction(HttpServletRequest request, HttpServletResponse response, String urlPara, Object obj) {
		// 初始化父类
		if (Action.class == obj.getClass().getSuperclass()) {// 如果继承
			ReflectUtil.invokeSuperSet(obj, "request", request);
			ReflectUtil.invokeSuperSet(obj, "response", response);
			ReflectUtil.invokeSuperSet(obj, "urlPara", urlPara);
		}

		// 初始化内部Action实例
		Field f[] = obj.getClass().getDeclaredFields();// 把属性的信息提取出来，并且存放到field类的对象中，因为每个field的对象只能存放一个属性的信息所以要用数组去接收
		for (int i = 0; i < f.length; i++) {
			if (Action.class == f[i].getType()) {
				Action act = new Action();
				act.init(request, response, urlPara);
				ReflectUtil.invokeSet(obj, f[i].getName(), act);
				break;
			}
		}
	}

	/**
	 * 属性驱动 
	 * 设置用户Action里的字段
	 * 
	 * @param request http请求
	 * @param obj 用户action
	 */
	public void setActionFiled(HttpServletRequest request, Object obj) {
		Object objResult;
		Converter con = new Converter();
		Field f[] = obj.getClass().getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			if (!clslist.contains(f[i].getType().getName())) {// 基本类型才转换set
				if (f[i].getType().isArray()) {
					objResult = con.convertValue(request.getParameterValues(f[i].getName()), f[i].getType());
				} else {
					objResult = con.convertValue(request.getParameter(f[i].getName()), f[i].getType());
				}
				if (objResult != null) {
					ReflectUtil.invokeSet(obj, f[i].getName(), objResult);
				}
			}
		}
	}

	/**
	 * 模型驱动 
	 * 设置module里的字段
	 * 
	 * @param request  http请求
	 * @param obj 用户action
	 */
	public void setModuleFiled(HttpServletRequest request, Object obj) {
		Object o = null;
		Class<?> cls = null;
		Object objResult = null;
		Converter con = new Converter();
		Field f[] = obj.getClass().getDeclaredFields();// action的所有字段
		for (int i = 0; i < f.length; i++) {
			if (clslist.contains(f[i].getType().getName()) && f[i].getType() != Action.class) {// 是用户的module
				try {
					cls = Class.forName(f[i].getType().getName());
					o = cls.newInstance();// action属性的实例
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				Field f2[] = o.getClass().getDeclaredFields();
				for (int j = 0; j < f2.length; j++) {
					if (!clslist.contains(f2[j].getType().getName())) {// 基本类型才转换set
						if (f2[j].getType().isArray()) {
							objResult = con.convertValue(request.getParameterValues(f[i].getName() + "." + f2[j].getName()), f2[j].getType());
						} else {
							objResult = con.convertValue(request.getParameter(f[i].getName() + "." + f2[j].getName()), f2[j].getType());
						}
						if (objResult != null) {
							ReflectUtil.invokeSet(o, f2[j].getName(), objResult);
						}
					}
				}
				ReflectUtil.invokeSet(obj, f[i].getName(), o);
			}
		}
	}

	// =========================================================================

	/**
	 * 返回结果(视图生成)
	 * 
	 * @param request HttpServletRequest
	 * @param response  HttpServletResponse
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws ServletException
	 */
	public void result(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ServletException, IOException {
		if (actcls != null && !"".equals(result.toString())) {
			Field f[] = obj.getClass().getDeclaredFields();
			for (int i = 0; i < f.length; i++) {
				if (f[i].getType() != Action.class) {
					request.setAttribute(f[i].getName(), ReflectUtil.invokeGet(obj, f[i].getName()));
				}
			}
			ActionResult ar;
			Result rs;
			if (AnnotationMethod.isAnnotationPresent(ActionResult.class)) {
				ar = AnnotationMethod.getAnnotation(ActionResult.class);
				for (int i = 0; i < ar.value().length; i++) {
					rs = ar.value()[i];
					if (result.toString().equals(rs.name())) {// 结果判断
						// 类型判断
						if (rs.type() == ResultType.Dispatcher) {// 转发
							request.getRequestDispatcher("/" + rs.view()).forward(request, response);
							return;
						} else if (rs.type() == ResultType.Redirect) {// 跳转
							String url = "";
							if (rs.view().toLowerCase().startsWith("http://")) {
								url = rs.view();
							} else if (rs.view().startsWith("/")) {
								url = request.getContextPath() + rs.view();
							} else {
								url = rs.view();
							}
							response.sendRedirect(url);
							return;
						} else if (rs.type() == ResultType.Stream) {// 数据，下载
							try {
								DownloadFile df = (DownloadFile) ReflectUtil.invokeGet(obj, rs.view());
								// 以流的形式下载文件
								byte[] buffer = new byte[df.getInputstream().available()];
								df.getInputstream().read(buffer);
								df.getInputstream().close();
								// 清空response
								response.reset();
								// 设置response的Header
								response.addHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(df.getFilename(), Loader.getInstance().getEncoding()));
								response.addHeader("Content-Length", "" + buffer.length);
								OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
								response.setContentType("application/octet-stream");
								toClient.write(buffer);
								toClient.flush();
								toClient.close();
							} catch (IOException ex) {
								ex.printStackTrace();
							}
							return;
						} else if (rs.type() == ResultType.PlainText) {// 文本
							String r = ReflectUtil.invokeGet(obj, rs.view()).toString();
							response.setContentType("text/plain; charset=" + Loader.getInstance().getEncoding());
							response.getWriter().print(r);
							return;
						} else if (rs.type() == ResultType.Chain) {
							chain.doFilter(request, response);
							return;
						}
					}
				}
			}
			Log.print("The result is not define!");
		}
	}
}
