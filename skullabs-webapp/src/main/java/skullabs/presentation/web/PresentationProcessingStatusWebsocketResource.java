package skullabs.presentation.web;

import kikaha.core.websocket.WebSocketSession;
import kikaha.urouting.api.OnOpen;
import kikaha.urouting.api.PathParam;
import kikaha.urouting.api.WebSocket;
import skullabs.presentation.PresentationRepository;
import trip.spi.Provided;
import trip.spi.Singleton;

@Singleton
@WebSocket( "presention/upload/status/{id}" )
public class PresentationProcessingStatusWebsocketResource {

	@Provided
	PresentationRepository repository;

	@OnOpen
	public void onOpen(
		@PathParam( "id" ) final Long id,
		final WebSocketSession session )
	{
		repository.notifyWhenFinishProcessing( id, ( i, p ) -> {
			try {
				session.send( "done" ).to( session.channel() );
			} catch ( final Throwable cause ) {
				cause.printStackTrace();
			}
		});
	}
}