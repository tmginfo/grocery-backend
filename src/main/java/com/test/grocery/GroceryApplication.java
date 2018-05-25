package com.test.grocery;

import org.glassfish.jersey.server.ServerProperties;

import com.test.grocery.business.service.GroceryBusinessService;
import com.test.grocery.rest.service.GroceryRestService;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class GroceryApplication extends Application<GroceryConfiguration> {

    @Override
    public void initialize(Bootstrap<GroceryConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
            )
        );
    }
    
	@Override
	public void run(GroceryConfiguration configuration, Environment environment) throws Exception {
		environment.jersey().property(ServerProperties.OUTBOUND_CONTENT_LENGTH_BUFFER,0);
		GroceryBusinessService groceryBusinessService = new GroceryBusinessService();
		environment.jersey().register(new GroceryRestService(groceryBusinessService));
	}

}
