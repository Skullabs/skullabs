package skullabs.kernel.template;

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
	public Response renderWelcomeFile() {
		return MustacheResponse.ok()
			.templateName( "index.mustache" )
			.paramObject( params );
	}

	@GET
	@Path( "{templatePath}.do" )
	public Response renderTemplate(
		@PathParam( "templatePath" ) final String templatePath ) {
		return MustacheResponse.ok()
			.templateName( templatePath + ".mustache" )
			.paramObject( params );
	}
}