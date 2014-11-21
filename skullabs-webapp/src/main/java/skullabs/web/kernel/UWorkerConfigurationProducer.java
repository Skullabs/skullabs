package skullabs.web.kernel;

import lombok.Getter;
import trip.spi.Producer;
import trip.spi.Provided;
import trip.spi.Singleton;
import uworkers.core.config.Configuration;
import uworkers.core.config.DefaultConfiguration;
import uworkers.core.config.UWorkerConfiguration;

import com.typesafe.config.Config;

@Singleton
public class UWorkerConfigurationProducer {

	@Provided
	Config config;

	@Getter( lazy = true )
	private final UWorkerConfiguration workerConfiguration = createUWorkerConfiguration();

	UWorkerConfiguration createUWorkerConfiguration() {
		final Configuration configuration = new DefaultConfiguration( config );
		return new UWorkerConfiguration( configuration );
	}

	@Producer
	public UWorkerConfiguration produceUWorkerConfiguration() {
		return getWorkerConfiguration();
	}
}