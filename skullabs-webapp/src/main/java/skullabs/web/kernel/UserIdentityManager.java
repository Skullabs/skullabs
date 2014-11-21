package skullabs.web.kernel;

import io.undertow.security.idm.Account;
import kikaha.core.auth.AbstractPasswordBasedIdentityManager;
import kikaha.core.auth.FixedUsernameAndRolesAccount;
import lombok.val;
import lombok.extern.java.Log;
import trip.spi.Provided;

@Log
public class UserIdentityManager
	extends AbstractPasswordBasedIdentityManager {

	@Provided
	UserService userService;

	@Override
	public Account retrieveAccountFor( final String id, final String password ) {
		val user = userService.retrieveUserByUsernameAndPassword( id, password );
		if ( user != null ) {
			log.info( "Authentication succeed!" );
			return new FixedUsernameAndRolesAccount( user.getUsername(), null );
		}
		log.warning( "Authentication failed for " + id );
		return null;
	}
}
