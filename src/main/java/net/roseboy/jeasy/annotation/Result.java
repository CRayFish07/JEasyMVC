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
	String name() default "SUCCESS";// action中方法的返回值，对应一个视图

	String view() default "";// 视图

	ResultType type() default ResultType.Dispatcher;// 视图类型在net.roseboy.jeasy.emum.ResultType定义

	// public Result(){

	// }
}
