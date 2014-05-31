package at.ac.tuwien.inso.tl.client.exception;

public class ServiceException extends Exception {
	private static final long serialVersionUID = 3400531267454846055L;

	public ServiceException(){
		super();
	}
	
	public ServiceException(String message){
		super(message);
	}
	
	public ServiceException(Throwable throwable){
		super(throwable);
	}
	
	public ServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
