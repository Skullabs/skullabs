package skullabs.kernel.template;

import io.undertow.server.HttpServerExchange;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import kikaha.urouting.api.Context;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Response;
import trip.spi.Singleton;

@Singleton
public class MustacheResource {

	final Object params = new Object();

	@GET
	@Path( "/" )
	public Response renderWelcomeFile()
	{
		return MustacheResponse.ok()
			.templateName( "index.mustache" )
			.paramObject( params );
	}

	@GET
	@Path( "{templatePath}.do" )
	public Response renderTemplate(
		@PathParam( "templatePath" ) final String templatePath )
	{
		return MustacheResponse.ok()
			.templateName( templatePath + ".mustache" )
			.paramObject( params );
	}

	@GET
	@Path( "{templatePath}.jss" )
	public Response renderJavaScript(
		@Context final HttpServerExchange exchange,
		@PathParam( "templatePath" ) final String templatePath )
	{
		final Map<String, String> params = convertQueryParametersToFlatMap( exchange );
		return MustacheResponse.ok()
			.templateName( templatePath + ".mustache" )
			.paramObject( params );
	}

	Map<String, String> convertQueryParametersToFlatMap( final HttpServerExchange exchange )
	{
		final Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
		final Map<String, String> parameters = new HashMap<>();
		for ( final String key : queryParameters.keySet() )
			parameters.put( key, queryParameters.get( key ).poll() );
		return parameters;
	}
}