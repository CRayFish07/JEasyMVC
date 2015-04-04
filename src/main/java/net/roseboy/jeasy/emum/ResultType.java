package net.roseboy.jeasy.emum;

/**
 * ��ͼ�������
 * @author roseboy.net
 *
 */
public enum ResultType {
	JSP("dispacter"), //jsp��ͼ
	FreeMarker("dispacter"), 
	Velocity("dispacter"), 
	Dispatcher("dispacter"), //Dispatcher
	Chain("chain"),// �൱��chain.doFilter(request, response);
	//HttpHeader("header"), 
	Redirect("redirect"), 
	//RedirectAction("redirectaction"), 
	Stream("stream"), //����
	PlainText("text");//�ı�
	
	private String value;
	private ResultType(String v){
		this.value=v;
	}
	public String toString(){ //�����˸���Enum��toString����  
		return super.toString()+"("+value+")";  
	} 
}
