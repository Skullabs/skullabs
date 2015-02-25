package skullabs.presentation;

import java.io.IOException;
import java.util.function.BiConsumer;

import javax.jms.JMSException;

import kikaha.hazelcast.Source;
import lombok.val;
import lombok.extern.java.Log;
import skullabs.commons.Executor;
import skullabs.commons.UpdatedEntryListener;
import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.api.EndpointConnection;
import uworkers.api.Worker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Log
@Singleton
public class PresentationRepository {

	static final Boolean PROCESSING = true;
	static final Boolean PROCESSED = false;

	@Provided
	@Source( "presentation-processing" )
	IMap<Long, Presentation> presentations;

	@Provided
	@Worker( name = "pdf-pages-processor" )
	EndpointConnection pdfConverter;

	@Provided
	HazelcastInstance hazelcast;

	public void create( final Presentation presentation, final String fileName ) {
		try {
			val shouldProcess = fileName != null;
			presentation.setProcessing( shouldProcess );
			store( presentation );
			if ( shouldProcess ) {
				final PDFProcessJob job = new PDFProcessJob( presentation.getIdentifier(), fileName );
				pdfConverter.send( job );
			}
		} catch ( JMSException | IOException e ) {
			log.severe( e.getMessage() );
			throw new RuntimeException( e );
		}
	}

	public void store( final Presentation presentation ) {
		presentations.put( presentation.getIdentifier(), presentation );
	}

	public Presentation retrieve( final long pdfIdentifier ) {
		return presentations.get( pdfIdentifier );
	}

	public void finishProcessingPDF( final long pdfIdentifier, final int numberOfProcessedSlides ) {
		presentations.submitToKey( pdfIdentifier, new Executor<Presentation>(
			presentation -> {
				presentation.setProcessing( PROCESSED );
				presentation.setNumberOfSlides( numberOfProcessedSlides );
			}
		) );
	}

	/**
	 * @param presentation
	 * @param callback
	 */
	public void notifyWhenFinishProcessing( final Presentation presentation, final BiConsumer<Long, Presentation> callback ) {
		val identifier = presentation.getIdentifier();
		notifyWhenFinishProcessing( identifier, callback );
	}

	/**
	 * @param presentationIdentifier
	 * @param callback
	 */
	public void notifyWhenFinishProcessing( final Long presentationIdentifier, final BiConsumer<Long, Presentation> callback ) {
		val presentation = retrieve( presentationIdentifier );
		if ( presentation != null && !presentation.isProcessing() ) {
			callback.accept( presentationIdentifier, presentation );
			return;
		}

		UpdatedEntryListener.listenOnce( presentations )
			.forKey( presentationIdentifier )
			.includeValue( true )
			.then( callback );
	}
}