package skullabs.kernel.template;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.OutputStream;

import kikaha.urouting.UncloseableWriterWrapper;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.Serializer;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton( name = Mimes.HTML, exposedAs = Serializer.class )
public class HtmlMustacheSerializer implements Serializer {

	@Provided
	MustacheSerializer serializer;

	@Override
	public <T> void serialize( final T object, final HttpServerExchange exchange ) throws IOException {
		final OutputStream outputStream = exchange.getOutputStream();
		final MustacheTemplate template = (MustacheTemplate)object;
		final HtmlResponse response = new HtmlResponse( exchange, template.paramObject() );
		template.paramObject( response );
		serializer.serialize( template, UncloseableWriterWrapper.wrap( outputStream ) );
		outputStream.flush();
	}
}