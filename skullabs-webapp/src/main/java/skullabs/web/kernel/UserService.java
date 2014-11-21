package skullabs.web.kernel;

import java.util.Collection;

import kikaha.hazelcast.Source;
import kikaha.urouting.api.Consumes;
import kikaha.urouting.api.DefaultResponse;
import kikaha.urouting.api.GET;
import kikaha.urouting.api.Mimes;
import kikaha.urouting.api.POST;
import kikaha.urouting.api.PUT;
import kikaha.urouting.api.Path;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.Produces;
import kikaha.urouting.api.Response;
import lombok.val;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.IMap;
import com.hazelcast.query.SqlPredicate;

@Singleton
@Produces( Mimes.JSON )
@Consumes( Mimes.JSON )
@Path( "users" )
public class UserService {

	@Provided
	@Source( "users" )
	IMap<Long, User> users;

	@GET
	@Path( "{id}" )
	public User retrieveUserBy(
			@PathParam( "id" ) final Long id ) {
		return users.get( id );
	}

	public User retrieveUserByUsernameAndPassword(
		final String username, final String password ) {
		val query = "username = '" + username + "' AND password = '" + password + "'";
		val values = users.values( new SqlPredicate( query ) );
		return Filter.first( values );
	}

	@GET
	public Collection<User> retrieveAllUsers() {
		return users.values();
	}

	@POST
	@PUT
	public Response persistUser( final User user ) {
		val id = user.getId();
		users.put( id, user );
		return DefaultResponse.created( "/users/" + id );
	}
}