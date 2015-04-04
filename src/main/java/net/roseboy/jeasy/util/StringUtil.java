package net.roseboy.jeasy.util;

/**
 * 字符串工具处理类
 * @author roseboy.net
 *
 */
public class StringUtil {
	public static int search(String a,char b){
		int c=0;
		char[] t=a.toCharArray();
		for(int i=0;i<t.length;i++){
			if(t[i]==b){
				c+=1;
			}
		}
		return c;
	}
}
