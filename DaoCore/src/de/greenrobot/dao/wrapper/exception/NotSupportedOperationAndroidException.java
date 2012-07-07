package de.greenrobot.dao.wrapper.exception;

public class NotSupportedOperationAndroidException extends RuntimeException {

	private static final long serialVersionUID = -8708290373742559496L;

	public NotSupportedOperationAndroidException() {
		super("Android doesn't provide any implementation of this method");
	}

	public NotSupportedOperationAndroidException(String detailMessage, Throwable throwable) {
		super("Android doesn't provide any implementation of this method : " + detailMessage, throwable);
	}

	public NotSupportedOperationAndroidException(String detailMessage) {
		super("Android doesn't provide any implementation of this method : " + detailMessage);
	}

	public NotSupportedOperationAndroidException(Throwable throwable) {
		super(throwable);
	}

}
