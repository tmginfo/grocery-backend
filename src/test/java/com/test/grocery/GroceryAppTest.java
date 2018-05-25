package com.test.grocery;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.ClassRule;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class GroceryAppTest {
	@ClassRule
    public static final DropwizardAppRule<GroceryConfiguration> RULE =
            new DropwizardAppRule<>(GroceryApplication.class, ResourceHelpers.resourceFilePath("test-config.yml"));
	
	private final String APP_URL = String.format("http://localhost:%s", RULE.getLocalPort());
	
    @AfterClass
    public static void tearDown() throws Exception {
        RULE.getTestSupport().after();
    }
    
    
    protected HttpResponse<String> get(String path) throws Exception {
        return Unirest.get(APP_URL + path).asString();
    }
    
    protected HttpResponse<String> post(String path) throws Exception {
        return Unirest.post(APP_URL + path)
                .header("Content-Type", MediaType.APPLICATION_JSON)     
                .asString();
    }
}
