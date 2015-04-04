package net.roseboy.test;

import net.roseboy.jeasy.annotation.ActionMethod;
import net.roseboy.jeasy.annotation.ActionName;
import net.roseboy.jeasy.annotation.ActionResult;
import net.roseboy.jeasy.annotation.Result;

@ActionName("hello")//��ǵ�ǰ��ķ���·��
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
	
	
	@ActionMethod("index")//��ǵ�ǰ�����ķ���·��
	@ActionResult({ //���ݽ����Ⱦ��ͼ
		@Result(name = "yes", view = "yes.jsp"),//����yes������yes.jsp
		@Result(name = "no", view = "no.jsp")//����no,������no.jsp
		})
	public String  method1(){
		if("123".equals(this.password)){
			return "yes";
		}else{
			return "no";
		}
		
	}
}
