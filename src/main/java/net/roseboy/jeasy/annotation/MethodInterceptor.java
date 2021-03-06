package net.roseboy.jeasy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
//��Ƿ���������
public @interface MethodInterceptor {
	Class<?> cls();
	String method() default "Interceptor";
}
