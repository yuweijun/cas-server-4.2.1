package org.jasig.cas;

import org.jasig.cas.authentication.AuthenticationHandler;
import org.jasig.cas.authentication.BasicCredentialMetaData;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.DefaultHandlerResult;
import org.jasig.cas.authentication.HandlerResult;
import org.jasig.cas.authentication.OneTimePasswordCredential;
import org.jasig.cas.authentication.PreventedException;
import org.jasig.cas.authentication.principal.DefaultPrincipalFactory;
import org.springframework.util.StringUtils;

import javax.security.auth.login.FailedLoginException;
import javax.validation.constraints.NotNull;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Test one-time password authentication handler.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
public class TestOneTimePasswordAuthenticationHandler implements AuthenticationHandler {

    @NotNull
    private final Map<String, String> credentialMap;

    /** Handler name. */
    private String name;


    /**
     * Creates a new instance with a map that defines the one-time passwords that can be authenticated.
     *
     * @param credentialMap Non-null map of one-time password identifiers to password values.
     */
    public TestOneTimePasswordAuthenticationHandler(final Map<String, String> credentialMap) {
        this.credentialMap = credentialMap;
    }

    @Override
    public HandlerResult authenticate(final Credential credential)
            throws GeneralSecurityException, PreventedException {
        final OneTimePasswordCredential otp = (OneTimePasswordCredential) credential;
        final String valueOnRecord = credentialMap.get(otp.getId());
        if (otp.getPassword().equals(valueOnRecord)) {
            return new DefaultHandlerResult(this, new BasicCredentialMetaData(otp),
                    new DefaultPrincipalFactory().createPrincipal(otp.getId()));
        }
        throw new FailedLoginException();
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof OneTimePasswordCredential;
    }

    @Override
    public String getName() {
        if (StringUtils.hasText(this.name)) {
            return this.name;
        } else {
            return getClass().getSimpleName();
        }
    }

    public void setName(final String name) {
        this.name = name;
    }
}
