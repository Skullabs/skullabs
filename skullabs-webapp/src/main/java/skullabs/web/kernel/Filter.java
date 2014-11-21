package skullabs.web.kernel;

public class Filter {

	public static <T> T first( Iterable<T> iterable ) {
		for ( T t : iterable )
			return t;
		return null;
	}
}
