package de.greenrobot.dao.wrapper.exception;

public class NotSupportedOperationSQLCypherException extends RuntimeException {

	private static final long serialVersionUID = -8708290373742559496L;

	public NotSupportedOperationSQLCypherException() {
		super("SQLCypher doesn't provide any implementation of this method");
	}

	public NotSupportedOperationSQLCypherException(String detailMessage, Throwable throwable) {
		super("SQLCypher doesn't provide any implementation of this method : " + detailMessage, throwable);
	}

	public NotSupportedOperationSQLCypherException(String detailMessage) {
		super("SQLCypher doesn't provide any implementation of this method : " + detailMessage);
	}

	public NotSupportedOperationSQLCypherException(Throwable throwable) {
		super(throwable);
	}

}
