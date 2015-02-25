package skullabs.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.CountDownLatch;

import kikaha.core.api.conf.Configuration;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import skullabs.presentation.Presentation;
import skullabs.presentation.PresentationRepository;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import uworkers.api.UWorkerService;

import com.hazelcast.core.HazelcastInstance;
import com.typesafe.config.Config;

@RunWith( MockitoJUnitRunner.class )
public class PDFImageConverterTest {

	@Provided
	PresentationRepository repository;

	@Provided
	HazelcastInstance instance;

	@Mock
	Configuration configuration;

	@Test
	@SneakyThrows
	public void ensureThatHaveGenerateImagesFromAlreadyKnownPDF() {
		final CountDownLatch counter = new CountDownLatch( 1 );
		final String fileName = "src/test/resources/bacon-garbage.pdf";
		final Presentation presentation = new Presentation( "Sample", "Sample Description" );

		repository.notifyWhenFinishProcessing( presentation, ( id, p ) -> counter.countDown() );
		repository.create( presentation, fileName );
		counter.await();

		final Presentation processedPresentation = repository.retrieve( presentation.getIdentifier() );
		assertThat( processedPresentation.getNumberOfSlides(), is( 19 ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		final UWorkerService workerService = UWorkerService.newInstance();
		final ServiceProvider provider = workerService.provider();
		final Config config = workerService.workerConfiguration().getMergeableConfig();
		provider.providerFor( Config.class, config );
		provider.providerFor( Configuration.class, configuration );
		provider.provideOn( this );
		workerService.start();
	}
}