package skullabs.web.kernel.template;

import java.io.OutputStream;

import kikaha.urouting.api.RoutingException;
import kikaha.urouting.api.Serializer;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( name = "text/javascript", exposedAs = Serializer.class )
public class JavaScriptMustacheSerializer implements Serializer {

	@Provided
	MustacheSerializer serializer;

	@Override
	public <T> void serialize( final T object, final OutputStream output ) throws RoutingException
	{
		serializer.serialize( object, output );
	}
}
