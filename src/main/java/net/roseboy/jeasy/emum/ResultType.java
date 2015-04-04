package net.roseboy.jeasy.emum;

/**
 * 视图结果类型
 * @author roseboy.net
 *
 */
public enum ResultType {
	JSP("dispacter"), //jsp视图
	FreeMarker("dispacter"), 
	Velocity("dispacter"), 
	Dispatcher("dispacter"), //Dispatcher
	Chain("chain"),// 相当于chain.doFilter(request, response);
	//HttpHeader("header"), 
	Redirect("redirect"), 
	//RedirectAction("redirectaction"), 
	Stream("stream"), //下载
	PlainText("text");//文本
	
	private String value;
	private ResultType(String v){
		this.value=v;
	}
	public String toString(){ //覆盖了父类Enum的toString（）  
		return super.toString()+"("+value+")";  
	} 
}
