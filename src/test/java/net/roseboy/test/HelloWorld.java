package net.roseboy.test;

import net.roseboy.jeasy.annotation.ActionMethod;
import net.roseboy.jeasy.annotation.ActionName;
import net.roseboy.jeasy.annotation.ActionResult;
import net.roseboy.jeasy.annotation.Result;

@ActionName("hello")//标记当前类的访问路由
public class HelloWorld {
	private String username;
	private String password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	@ActionMethod("index")//标记当前方法的访问路由
	@ActionResult({ //根据结果渲染视图
		@Result(name = "yes", view = "yes.jsp"),//返回yes，调用yes.jsp
		@Result(name = "no", view = "no.jsp")//返回no,调用哪no.jsp
		})
	public String  method1(){
		if("123".equals(this.password)){
			return "yes";
		}else{
			return "no";
		}
		
	}
}
