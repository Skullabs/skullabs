package skullabs.kernel.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.StringWriter;

import kikaha.core.api.conf.Configuration;
import kikaha.urouting.api.Serializer;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import skullabs.kernel.User;
import skullabs.kernel.template.MustacheResponse;
import skullabs.kernel.template.MustacheSerializer;
import trip.spi.Provided;
import trip.spi.ServiceProvider;

import com.typesafe.config.Config;

@RunWith( MockitoJUnitRunner.class )
public class MustacheSerializerTest {

	@Provided( exposedAs = Serializer.class )
	MustacheSerializer serializer;

	@Mock
	Configuration configuration;

	@Mock
	Config config;

	@Test
	@SneakyThrows
	public void ensureThatCompileATemplateWithoutRetrieveFromCache() {
		doReturn( false ).when( config ).getBoolean( eq( "mustache.cache-templates" ) );
		val response = readSimulatedResponse();
		val output = new StringWriter();
		serializer.serialize( response.entity(), output );
		assertThat( output.toString(), is( "<h1>Hello Poppins</h1>" ) );
		verify( serializer, never() ).getCachedTemplate( eq( "sample.template" ) );
	}

	@Test
	@SneakyThrows
	public void ensureThatCompileATemplateRetrievingFromCache() {
		doReturn( true ).when( config ).getBoolean( eq( "mustache.cache-templates" ) );
		val response = readSimulatedResponse();
		val output = new StringWriter();
		serializer.serialize( response.entity(), output );
		assertThat( output.toString(), is( "<h1>Hello Poppins</h1>" ) );
		verify( serializer ).getCachedTemplate( eq( "sample.template" ) );
	}

	MustacheResponse readSimulatedResponse() {
		val user = new User();
		user.setName( "Poppins" );
		val response = MustacheResponse.ok()
			.templateName( "sample.template" )
			.paramObject( user );
		return response;
	}

	@Before
	@SneakyThrows
	public void provideDependencies() {
		doReturn( "src/test/resources" ).when( configuration ).resourcesPath();
		doReturn( config ).when( configuration ).config();

		final ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.providerFor( Configuration.class, configuration );
		serviceProvider.provideOn( this );
		serializer = spy( serializer );
	}
}
