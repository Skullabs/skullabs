package skullabs.pdf;

import java.util.Collection;

import kikaha.hazelcast.Source;
import trip.spi.Provided;
import trip.spi.Singleton;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;

@Singleton
public class PDFImageRepository {

	static final Boolean PROCESSING = true;

	@Provided
	@Source( "presentation-images" )
	MultiMap<Long, String> imagesName;

	@Provided
	@Source( "presentation-images" )
	IMap<Long, Boolean> imagesProcessingStatus;

	@Provided
	HazelcastInstance hazelcast;

	public void startProcessingPDF( final long pdfIdentifier ) {
		imagesProcessingStatus.put( pdfIdentifier, PROCESSING );
	}

	public void finishProcessingPDF( final long pdfIdentifier ) {
		imagesProcessingStatus.remove( pdfIdentifier, PROCESSING );
	}

	public boolean isProcessingPDF( final long pdfIdentifier ) {
		return imagesProcessingStatus.containsKey( pdfIdentifier );
	}

	public void storeImageForPDF( final long pdfIdentifier, final String imageName ) {
		imagesName.put( pdfIdentifier, imageName );
	}

	public Collection<String> retrieveStoredImagesForPDF( final long pdfIdentifier ) {
		return imagesName.get( pdfIdentifier );
	}
}