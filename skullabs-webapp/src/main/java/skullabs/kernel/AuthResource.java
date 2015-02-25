package skullabs.kernel;

import java.util.Collection;

import kikaha.core.api.conf.Configuration;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.FormParam;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.POST;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.Produces;
import kikaha.urouting.api.Response;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

@Path( "auth" )
@Singleton
public class AuthResource {

	@Provided
	UserService userService;

	@Provided
	Configuration configuration;

	@POST
	@Path( "form" )
	public Response persistUserThroughForm(
		@FormParam( "name" ) final String fullName,
		@FormParam( "username" ) final String username,
		@FormParam( "password" ) final String password )
	{
		val user = createUser( fullName, username, password );
		userService.persistUser( user );
		val loginPage = configuration.authentication().formAuth().loginPage();
		return DefaultResponse.seeOther( loginPage );
	}

	User createUser( final String fullName, final String username, final String password ) {
		val user = new User();
		user.setName( fullName );
		user.setUsername( username );
		user.setPassword( password );
		return user;
	}

	@GET
	@Path( "users" )
	@Produces( Mimes.JSON )
	public Collection<User> retrieveUsers() {
		return userService.retrieveAllUsers();
	}
}