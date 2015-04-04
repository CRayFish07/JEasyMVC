package net.roseboy.test;

import java.util.Date;
import java.util.List;

import net.roseboy.jeasy.annotation.ActionMethod;
import net.roseboy.jeasy.annotation.ActionName;
import net.roseboy.jeasy.annotation.ActionResult;
import net.roseboy.jeasy.annotation.Result;
import net.roseboy.jeasy.core.Action;
import net.roseboy.jeasy.upload.DownloadFile;

@ActionName("user")
public class test {//extends Action{
	private List<?> lll;
	private Action act;
	private String a[];
	private String b;
	private Integer c;
	private float d;
	private Date e;
	private Student f;
	private DownloadFile df;

	public DownloadFile getDf() {
		return df;
	}

	public void setDf(DownloadFile df) {
		this.df = df;
	}

	public List<?> getLll() {
		return lll;
	}

	public void setLll(List<?> lll) {
		this.lll = lll;
	}

	public Action getAct() {
		return act;
	}

	public void setAct(Action act) {
		this.act = act;
	}

	public String[] getA() {
		return a;
	}

	public void setA(String[] a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public Integer getC() {
		return c;
	}

	public void setC(Integer c) {
		this.c = c;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
	}

	public Date getE() {
		return e;
	}

	public void setE(Date e) {
		this.e = e;
	}

	public Student getF() {
		return f;
	}

	public void setF(Student f) {
		this.f = f;
	}

	@ActionMethod("index")
	@ActionResult({ 
		@Result(name = "111", view = "test111.jsp"),
		@Result(name = "222", view = "test222.jsp"), 
		@Result(name = "333", view = "test333.jsp"), 
		@Result(name = "444", view = "test444.jsp") 
		})
	public String ok() {
		System.out.println("======================");
		System.out.println("a:" + this.getA());
		System.out.println("b:" + this.getB());
		System.out.println("c:" + this.getC());
		System.out.println("d:" + this.getD());
		System.out.println("e:" + this.getE());
		// System.out.println("Student.age:" + this.getF().getAge());
		// System.out.println("Student.name:" + this.getF().getName());
		// System.out.println("Student.array:" + this.getF().getOKKKK().length);
		System.out.println("======================");

		//df.setFilename("‰∏ãËΩΩÊñá‰ª∂.exe");
		//df.setInputstream("C:\\Users\\roseboy\\Desktop\\Note.exe");

		return act.getPara(1);
	}

	//rest get ≤‚ ‘
	@ActionMethod("2")
	public void ok2() {
		System.out.println("======================");
		System.out.println(act.getPara(0));
		System.out.println(act.getPara(1));
		System.out.println(act.getPara(2));
		System.out.println(act.getPara(3));
		System.out.println(act.getPara(4));
		System.out.println("======================");
		
	}

}
