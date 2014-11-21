package skullabs.web.kernel;

public class InternalErrorException extends RuntimeException {

	private static final long serialVersionUID = 2123036630262807049L;

	public InternalErrorException( final String message ) {
		super( message );
	}
}
