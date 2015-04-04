package net.roseboy.jeasy.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.roseboy.jeasy.annotation.ActionName;
import net.roseboy.jeasy.annotation.AppInterceptor;
import net.roseboy.jeasy.util.Log;

/**
 * 项目启动
 * 单例
 * @author roseboy.net
 *
 */
public class Loader {

	private static Loader loader;
	private static Map<String, String> ActionMapping = new HashMap<String, String>();
	private static List<String> ClassList = new ArrayList<String>();
	private static boolean isDevMode = false;
	private static String Encoding = "UTF-8";
	static {
		loader = new Loader();
	}

	public Loader() {
		SetActionList(this.getClass().getResource("/").getPath());

	}

	public final static Loader getInstance() {
		return loader;
	}

	public final Map<String, String> getActionMapping() {
		return ActionMapping;
	}

	public final List<String> getClassList() {
		return ClassList;
	}

	public final boolean getIsDevMode() {
		return isDevMode;
	}

	public final String getEncoding() {
		return Encoding;
	}
	
	public final Loader getLoader() {
		return loader;
	}

	public final void setLoader(Loader loader) {
		Loader.loader = loader;
	}

	public final void setActionMapping(Map<String, String> actionMapping) {
		ActionMapping = actionMapping;
	}

	public final void setClassList(List<String> classList) {
		ClassList = classList;
	}

	public final void setDevMode(boolean isDevMode) {
		Loader.isDevMode = isDevMode;
	}

	public final void setEncoding(String encoding) {
		Encoding = encoding;
	}

	/**
	 * 扫描Action，全包扫描
	 * 将action存到全局Mapping中
	 * @param strPath
	 */
	public final void SetActionList(String strPath) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		String clsname;
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				SetActionList(files[i].getAbsolutePath());
			} else {
				clsname = files[i].getAbsolutePath().replace("/", ".").replace("\\", ".");
				if (clsname.endsWith(".class")) {
					clsname = clsname.substring(clsname.indexOf("WEB-INF.classes.") + "WEB-INF.classes.".length());
					clsname = clsname.substring(0, clsname.length() - 6);
					ClassList.add(clsname);
					Class<?> cls = null;
					try {
						cls = Class.forName(clsname);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if (cls != null && cls.isAnnotationPresent(ActionName.class)) {// 判断类是否是action
						ActionName an = cls.getAnnotation(ActionName.class);
						if (!an.value().contains("/")) {// 不能包含斜杠
							ActionMapping.put(an.value(), clsname);// cls.getName()
							Log.print("ActionMapping:" + an.value() + " ==> " + clsname);
						} else {
							Log.print("ActionMappingError:" + an.value());
						}
					}
					if(cls!=null && cls.isAnnotationPresent(AppInterceptor.class)){//全局拦截器
						
					}
				}
			}
		}
	}
}
