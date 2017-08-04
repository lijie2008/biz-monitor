package com.huntkey.rx.sceo.monitor.commom.exception;

/**
 * 
 * ClassName: ApplicationException 应用异常
 * Date: 2017年8月4日 下午4:46:16
 * @author lijie
 * @version
 */
public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = -4494334310812861506L;

	private int code;
	
    public ApplicationException(String message,
					            Throwable cause,
					            boolean enableSuppression,
					            boolean writableStackTrace){
		super(message,cause,enableSuppression,writableStackTrace);
	}
    
    public static void throwException(int code,
									  String message,
							          Throwable cause,
							          boolean enableSuppression,
							          boolean writableStackTrace){
    	ApplicationException e = new ApplicationException(message, cause, enableSuppression, writableStackTrace);
		e.code = code;
    	throw e;
    }
    
    public static void throwCodeMesg(int code,String message){
    	ApplicationException e = new ApplicationException(message, null, true, true);
    	e.code = code;
    	throw e;
	}
    
    public static void throwMesg(String message){
    	throw new ApplicationException(message, null, true, true);
	}
    
    public static void throwCodeCause(int code,Throwable cause){
    	ApplicationException e = new ApplicationException(cause.getMessage(), cause, true, true);
    	e.code = code;
    	throw e;
	}
    
    public static void throwCause(Throwable cause){
    	throw new ApplicationException(cause.getMessage(), cause, true, true);
	}
    
    public static void throwMesgCause(String message,Throwable cause){
    	throw new ApplicationException(message, cause, true, true);
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
}
