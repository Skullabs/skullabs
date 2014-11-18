package skullabs.pdf;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kikaha.hazelcast.Source;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import uworkers.api.UWorkerService;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.typesafe.config.Config;

public class PDFImageConverterTest {

	final Executor executor = Executors.newSingleThreadExecutor();

	@Provided
	PresentationRepository repository;

	@Provided
	HazelcastInstance instance;

	@Provided
	@Source( "presentation-processing" )
	IMap<Long, Presentation> presentations;

	@Test
	@SneakyThrows
	public void ensureThatHaveGenerateImagesFromAlreadyKnownPDF() {
		final CountDownLatch counter = new CountDownLatch( 1 );
		final EntryListener<Long, Presentation> listener = new CountUpdatedEntriesListener( counter );
		presentations.addEntryListener( listener, true );

		final String fileName = "src/test/resources/bacon-garbage.pdf";
		final Presentation presentation = new Presentation( "Sample" );
		repository.create( presentation, fileName );

		counter.await();

		final Collection<String> convertedPagesFileNames = repository.retrieveStoredImagesForPDF( presentation );
		assertTrue( convertedPagesFileNames.size() == 19 );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		final UWorkerService workerService = UWorkerService.newInstance();
		final ServiceProvider provider = workerService.provider();
		final Config config = workerService.workerConfiguration().getMergeableConfig();
		provider.providerFor( Config.class, config );
		provider.provideOn( this );
		workerService.start();
	}
}

@RequiredArgsConstructor
class CountUpdatedEntriesListener implements EntryListener<Long, Presentation> {

	final CountDownLatch counter;

	@Override
	public void entryAdded( final EntryEvent<Long, Presentation> event ) {
	}

	@Override
	public void entryRemoved( final EntryEvent<Long, Presentation> event ) {
	}

	@Override
	public void entryUpdated( final EntryEvent<Long, Presentation> event ) {
		counter.countDown();
	}

	@Override
	public void entryEvicted( final EntryEvent<Long, Presentation> event ) {
	}
}