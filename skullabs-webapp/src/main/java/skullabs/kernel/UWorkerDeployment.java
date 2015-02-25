package skullabs.kernel;

import kikaha.core.api.DeploymentContext;
import kikaha.core.api.DeploymentHook;
import trip.spi.Provided;
import trip.spi.ServiceProvider;
import trip.spi.ServiceProviderException;
import trip.spi.Singleton;
import uworkers.api.UWorkerException;
import uworkers.api.UWorkerService;
import uworkers.core.config.UWorkerConfiguration;

@Singleton( exposedAs = DeploymentHook.class )
public class UWorkerDeployment implements DeploymentHook {

	@Provided
	ServiceProvider provider;

	@Provided
	UWorkerConfiguration workerConfiguration;

	UWorkerService uWorkerService;

	@Override
	public void onDeploy( final DeploymentContext context ) {
		try {
			uWorkerService = new UWorkerService( workerConfiguration, provider );
			uWorkerService.start();
		} catch ( ServiceProviderException | UWorkerException e ) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUndeploy( final DeploymentContext context ) {
		uWorkerService.stop();
	}
}