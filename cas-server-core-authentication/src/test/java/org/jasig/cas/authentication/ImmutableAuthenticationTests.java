package org.jasig.cas.authentication;

import org.jasig.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.DefaultPrincipalFactory;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @author Marvin S. Addison
 * @since 3.0.0
 */
public class ImmutableAuthenticationTests {
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void verifyImmutable() {
        final AuthenticationHandler authenticationHandler = new SimpleTestUsernamePasswordAuthenticationHandler();
        final CredentialMetaData credential1 = new BasicCredentialMetaData(new UsernamePasswordCredential());
        final CredentialMetaData credential2 = new BasicCredentialMetaData(new UsernamePasswordCredential());
        final List<CredentialMetaData> credentials = new ArrayList<>();
        credentials.add(credential1);
        credentials.add(credential2);
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("authenticationMethod", "password");
        final Map<String, HandlerResult> successes = new HashMap<>();
        successes.put("handler1", new DefaultHandlerResult(authenticationHandler, credential1));
        final Map<String, Class<? extends Exception>> failures = new HashMap<>();
        failures.put("handler2", FailedLoginException.class);
        final ImmutableAuthentication auth = new ImmutableAuthentication(
                new DateTime(),
                credentials,
                new DefaultPrincipalFactory().createPrincipal("test"),
                attributes,
                successes,
                failures);

        try {
            auth.getCredentials().add(new BasicCredentialMetaData(new UsernamePasswordCredential()));
            fail("Should have failed");
        } catch (final RuntimeException e) {
            logger.debug("Adding authentication credential metadata failed correctly");
        }
        try {
            auth.getSuccesses().put("test", new DefaultHandlerResult(authenticationHandler, credential1));
            fail("Should have failed");
        } catch (final RuntimeException e) {
            logger.debug("Adding authentication success event failed correctly");
        }
        try {
            auth.getFailures().put("test", FailedLoginException.class);
            fail("Should have failed");
        } catch (final RuntimeException e) {
            logger.debug("Adding authentication failure event failed correctly");
        }
    }
}
