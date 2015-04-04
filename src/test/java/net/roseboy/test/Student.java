package net.roseboy.test;

import java.util.Arrays;

import net.roseboy.jeasy.util.Log;

public class Student {
	private String name;
	private int age;
	private String[] OKKKK;
	
	public String[] getOKKKK() {
		return OKKKK;
	}
	public void setOKKKK(String[] oKKKK) {
		OKKKK = oKKKK;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	public void print(){
		Log.print(this.name+"==="+this.age);
	}
	@Override
	public String toString() {
		return "Student [name=" + name + ", age=" + age + ", OKKKK=" + Arrays.toString(OKKKK) + "]";
	}
	
	
}
