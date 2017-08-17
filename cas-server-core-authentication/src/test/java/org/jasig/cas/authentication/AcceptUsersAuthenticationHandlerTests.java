package org.jasig.cas.authentication;

import org.junit.Test;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Scott Battaglia
 * @since 3.0.0
 */
public class AcceptUsersAuthenticationHandlerTests  {

    private final Map<String, String> users;

    private final AcceptUsersAuthenticationHandler authenticationHandler;

    public AcceptUsersAuthenticationHandlerTests() throws Exception {
        this.users = new HashMap<>();

        this.users.put("scott", "rutgers");
        this.users.put("dima", "javarules");
        this.users.put("bill", "thisisAwesoME");
        this.users.put("brian", "t�st");

        this.authenticationHandler = new AcceptUsersAuthenticationHandler();

        this.authenticationHandler.setUsers(this.users);
    }

    @Test
    public void verifySupportsSpecialCharacters() throws Exception {
        final UsernamePasswordCredential c = new UsernamePasswordCredential();
        c.setUsername("brian");
        c.setPassword("t�st");
        assertEquals("brian", this.authenticationHandler.authenticate(c).getPrincipal().getId());

    }

    @Test
    public void verifySupportsProperUserCredentials() throws Exception {
        final UsernamePasswordCredential c = new UsernamePasswordCredential();

        c.setUsername("scott");
        c.setPassword("rutgers");
       assertTrue(this.authenticationHandler.supports(c));
    }

    @Test
    public void verifyDoesntSupportBadUserCredentials() {
        try {
            assertFalse(this.authenticationHandler
                    .supports(new HttpBasedServiceCredential(new URL(
                            "http://www.rutgers.edu"), TestUtils.getRegisteredService("https://some.app.edu"))));
        } catch (final MalformedURLException e) {
            fail("Could not resolve URL.");
        }
    }

    @Test
    public void verifyAuthenticatesUserInMap() throws Exception {
        final UsernamePasswordCredential c = new UsernamePasswordCredential();

        c.setUsername("scott");
        c.setPassword("rutgers");

        try {
            assertEquals("scott", this.authenticationHandler.authenticate(c).getPrincipal().getId());
        } catch (final GeneralSecurityException e) {
            fail("Authentication exception caught but it should not have been thrown.");
        }
    }

    @Test(expected = AccountNotFoundException.class)
    public void verifyFailsUserNotInMap() throws Exception {
        final UsernamePasswordCredential c = new UsernamePasswordCredential();

        c.setUsername("fds");
        c.setPassword("rutgers");

        this.authenticationHandler.authenticate(c);
    }

    @Test(expected = AccountNotFoundException.class)
    public void verifyFailsNullUserName() throws Exception {
        final UsernamePasswordCredential c = new UsernamePasswordCredential();

        c.setUsername(null);
        c.setPassword("user");

        this.authenticationHandler.authenticate(c);
    }

    @Test(expected = AccountNotFoundException.class)
    public void verifyFailsNullUserNameAndPassword() throws Exception {
        final UsernamePasswordCredential c = new UsernamePasswordCredential();

        c.setUsername(null);
        c.setPassword(null);

        this.authenticationHandler.authenticate(c);
    }

    @Test(expected = FailedLoginException.class)
    public void verifyFailsNullPassword() throws Exception{
        final UsernamePasswordCredential c = new UsernamePasswordCredential();

        c.setUsername("scott");
        c.setPassword(null);

        this.authenticationHandler.authenticate(c);
    }
}
