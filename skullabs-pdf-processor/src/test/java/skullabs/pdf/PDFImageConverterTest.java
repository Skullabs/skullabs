package skullabs.pdf;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import uworkers.api.EndpointConnection;
import uworkers.api.Worker;
import uworkers.api.WorkerService;

import com.hazelcast.core.HazelcastInstance;
import com.typesafe.config.Config;

public class PDFImageConverterTest {

	final Executor executor = Executors.newSingleThreadExecutor();

	@Provided
	@Worker( name = "presentation-images" )
	EndpointConnection converter;

	@Provided
	PDFImageRepository repository;

	@Provided
	HazelcastInstance instance;

	@Test
	@SneakyThrows
	public void ensureThatHaveGenerateImagesFromAlreadyKnownPDF() {
		converter = spy( converter );
		converter.send( new PDF( 1l, "src/test/resources/bacon-garbage.pdf" ) );
		Thread.sleep( 5000l );
		final Collection<String> convertedPagesFileNames = repository.retrieveStoredImagesForPDF( 1l );
		assertTrue( convertedPagesFileNames.size() >= 1 );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		final WorkerService workerService = WorkerService.newInstance();
		final ServiceProvider provider = workerService.provider();
		final Config config = workerService.workerConfiguration().getMergeableConfig();
		provider.providerFor( Config.class, config );
		provider.provideOn( this );
		workerService.start();
	}
}