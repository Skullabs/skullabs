package skullabs.pdf;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

import java.util.Collection;

import kikaha.core.impl.conf.DefaultConfiguration;
import kikaha.hazelcast.Source;
import lombok.SneakyThrows;

import org.junit.Before;
import org.junit.Test;

import trip.spi.Provided;
import trip.spi.ServiceProvider;
import uworkers.api.EndpointConnection;
import uworkers.api.Worker;

import com.hazelcast.core.MultiMap;
import com.typesafe.config.Config;

public class PDFImageConverterTest {

	@Provided
	@Worker( name = "presentation-images" )
	EndpointConnection converter;

	@Provided
	@Source( "presentation-images" )
	MultiMap<Long, String> presentationImages;

	@Test
	@SneakyThrows
	public void ensureThatHaveGenerateImagesFromAlreadyKnownPDF() {
		converter = spy( converter );
		converter.send( new PDF( 1l, "src/test/resources/bacon-garbage.pdf" ) );
		final Collection<String> convertedPagesFileNames = presentationImages.get( 1l );
		assertThat( convertedPagesFileNames.size(), is( 19 ) );
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		final ServiceProvider provider = new ServiceProvider();
		provider.providerFor( Config.class, DefaultConfiguration.loadDefaultConfig() );
		provider.provideOn( this );
	}
}