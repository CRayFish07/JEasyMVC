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
	private String[] uri;// �ָ��uri
	private String action;// action������
	private String actcls;// action��Ӧ�Ĵ�����
	private Object obj = null;// �û�actionʵ��
	private String method;// ��������
	private Method AnnotationMethod;// ���÷�����Ӧ�ķ���ʵ��
	private StringBuffer result = new StringBuffer();// ���ô�ֵ������action-mwtghod�ķ���ֵ
	private Map<String, String> actmap;// action Map
	private List<String> clslist;// �û�����

	/**
	 * ��ʼ��������
	 * ��ȡrest url�е�action�Լ�����
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws UnsupportedEncodingException
	 */
	public void init(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding(Loader.getInstance().getEncoding());//���ñ���
		response.setCharacterEncoding(Loader.getInstance().getEncoding());

		target = request.getRequestURI();
		String webapp = request.getContextPath();
		if (target.startsWith(webapp)) {// ȥ��wepappĿ¼
			target = target.substring(webapp.length() + 1);
		}
		uri = target.split("/");
		action = uri[0].trim();// Action����
		method = uri.length > 1 ? uri[1].trim() : "index";// method����
		actmap = Loader.getInstance().getActionMapping();
		clslist = Loader.getInstance().getClassList();
		actcls = actmap.get(action);// Action��Ӧ��classȫ��
	}

	/**
	 * �����������
	 * �����û�Action�صĴ������
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
				if (met.isAnnotationPresent(ActionMethod.class)) {// �����ϵ�ע����ActionMethod
					am = met.getAnnotation(ActionMethod.class);
					if (am.value().equalsIgnoreCase(method)) {// ע�������󷽷���ȣ������ִ�Сд
						AnnotationMethod = met;
						// System.out.println("="+met.getName()+":"+am.value());
						obj = cls.newInstance();
						initAction(request, response, target, obj);// ��ʼ��action
						setActionFiled(request, obj);// �����û�action������
						setModuleFiled(request, obj);// ����module������
						if (Loader.getInstance().getIsDevMode()) {
							Log.print("URL:" + target + "==>" + actcls + "." + met.getName());
						}
						return ReflectUtil.invokeMethod(obj, met.getName(), result);// ����������ķ���
					}
				}
			}// for
			return false;
		} else {
			return false;
		}
	}

	/**
	 * ��ʼ���û�action 
	 * ����̳���jeasy.Action���ʼ������
	 * ���������Action���͵ı��������ʼ���������
	 * 
	 * @param obj �û���action
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param urlPara url�Ĳ���
	 */
	public void initAction(HttpServletRequest request, HttpServletResponse response, String urlPara, Object obj) {
		// ��ʼ������
		if (Action.class == obj.getClass().getSuperclass()) {// ����̳�
			ReflectUtil.invokeSuperSet(obj, "request", request);
			ReflectUtil.invokeSuperSet(obj, "response", response);
			ReflectUtil.invokeSuperSet(obj, "urlPara", urlPara);
		}

		// ��ʼ���ڲ�Actionʵ��
		Field f[] = obj.getClass().getDeclaredFields();// �����Ե���Ϣ��ȡ���������Ҵ�ŵ�field��Ķ����У���Ϊÿ��field�Ķ���ֻ�ܴ��һ�����Ե���Ϣ����Ҫ������ȥ����
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
	 * �������� 
	 * �����û�Action����ֶ�
	 * 
	 * @param request http����
	 * @param obj �û�action
	 */
	public void setActionFiled(HttpServletRequest request, Object obj) {
		Object objResult;
		Converter con = new Converter();
		Field f[] = obj.getClass().getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			if (!clslist.contains(f[i].getType().getName())) {// �������Ͳ�ת��set
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
	 * ģ������ 
	 * ����module����ֶ�
	 * 
	 * @param request  http����
	 * @param obj �û�action
	 */
	public void setModuleFiled(HttpServletRequest request, Object obj) {
		Object o = null;
		Class<?> cls = null;
		Object objResult = null;
		Converter con = new Converter();
		Field f[] = obj.getClass().getDeclaredFields();// action�������ֶ�
		for (int i = 0; i < f.length; i++) {
			if (clslist.contains(f[i].getType().getName()) && f[i].getType() != Action.class) {// ���û���module
				try {
					cls = Class.forName(f[i].getType().getName());
					o = cls.newInstance();// action���Ե�ʵ��
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				Field f2[] = o.getClass().getDeclaredFields();
				for (int j = 0; j < f2.length; j++) {
					if (!clslist.contains(f2[j].getType().getName())) {// �������Ͳ�ת��set
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
	 * ���ؽ��(��ͼ����)
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
					if (result.toString().equals(rs.name())) {// ����ж�
						// �����ж�
						if (rs.type() == ResultType.Dispatcher) {// ת��
							request.getRequestDispatcher("/" + rs.view()).forward(request, response);
							return;
						} else if (rs.type() == ResultType.Redirect) {// ��ת
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
						} else if (rs.type() == ResultType.Stream) {// ���ݣ�����
							try {
								DownloadFile df = (DownloadFile) ReflectUtil.invokeGet(obj, rs.view());
								// ��������ʽ�����ļ�
								byte[] buffer = new byte[df.getInputstream().available()];
								df.getInputstream().read(buffer);
								df.getInputstream().close();
								// ���response
								response.reset();
								// ����response��Header
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
						} else if (rs.type() == ResultType.PlainText) {// �ı�
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
