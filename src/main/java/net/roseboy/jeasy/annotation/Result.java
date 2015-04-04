package net.roseboy.jeasy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.roseboy.jeasy.emum.ResultType;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Result {
	String name() default "SUCCESS";// action�з����ķ���ֵ����Ӧһ����ͼ

	String view() default "";// ��ͼ

	ResultType type() default ResultType.Dispatcher;// ��ͼ������net.roseboy.jeasy.emum.ResultType����

	// public Result(){

	// }
}
