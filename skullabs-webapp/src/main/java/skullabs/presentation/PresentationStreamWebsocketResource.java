package skullabs.presentation;

import io.undertow.websockets.core.CloseMessage;

import java.io.IOException;
import java.util.Collection;

import kikaha.core.websocket.WebSocketSession;
import kikaha.hazelcast.Source;
import kikaha.urouting.api.OnClose;
import kikaha.urouting.api.OnError;
import kikaha.urouting.api.OnMessage;
import kikaha.urouting.api.OnOpen;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.WebSocket;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.MultiMap;

@Singleton
@WebSocket( "presention/stream/{id}" )
public class PresentationStreamWebsocketResource {

	@Provided
	@Source( "app-messages" )
	MultiMap<Long, String> messages;

	@OnOpen
	public void onOpen(
		@PathParam( "id" ) final Long id,
		final WebSocketSession session )
	{
		final String loggedUsername = session.userPrincipal().getName();
		final Collection<String> messageList = retrievePresentationMessageList( id );
		for ( final String message : messageList )
			session.send( message ).to( session.channel() );
		session.broadcast( loggedUsername + " has logged in." );
	}

	@OnMessage
	public void onMessage(
		@PathParam( "id" ) final Long id,
		final WebSocketSession session, final String message ) throws IOException {
		final String loggedUsername = session.userPrincipal().getName();
		final String messageToSend = loggedUsername + ": " + message;
		persistMessage( id, messageToSend );
		session.broadcast( messageToSend );
	}

	void persistMessage( final Long presentationId, final String message ) {
		messages.put( presentationId, message );
	}

	Collection<String> retrievePresentationMessageList( final Long presentationId ) {
		return messages.get( presentationId );
	}

	@OnClose
	public void onClose( final WebSocketSession session, final CloseMessage cm ) {
		final String loggedUsername = session.userPrincipal().getName();
		final String messageToSend = loggedUsername + ": has leave the room.";
		session.broadcast( messageToSend );
	}

	@OnError
	public void onError( final Throwable cause ) {
		cause.printStackTrace();
	}
}