package skullabs.pdf;

import java.io.IOException;
import java.util.Collection;

import javax.jms.JMSException;

import kikaha.hazelcast.Source;
import lombok.extern.java.Log;
import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.api.EndpointConnection;
import uworkers.api.Worker;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

@Log
@Singleton
public class PresentationRepository {

	static final Boolean PROCESSING = true;
	static final Boolean PROCESSED = false;

	@Provided
	@Source( "presentation-images" )
	MultiMap<Long, String> imagesName;

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
			store( presentation );
			final PDFProcessJob job = new PDFProcessJob( presentation.getIdentifier(), fileName );
			pdfConverter.send( job );
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

	public void finishProcessingPDF( final long pdfIdentifier ) {
		presentations.executeOnKey( pdfIdentifier, new Updater<Presentation>(
			presentation -> presentation.setProcessing( PROCESSED )
		) );
	}

	public void storeImageForPDF( final long pdfIdentifier, final String imageName ) {
		imagesName.put( pdfIdentifier, imageName );
	}

	public Collection<String> retrieveStoredImagesForPDF( final Presentation presentation ) {
		return retrieveStoredImagesForPDF( presentation.getIdentifier() );
	}

	public Collection<String> retrieveStoredImagesForPDF( final long pdfIdentifier ) {
		return imagesName.get( pdfIdentifier );
	}
}