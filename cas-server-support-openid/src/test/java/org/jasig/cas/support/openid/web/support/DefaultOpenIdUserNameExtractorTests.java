package org.jasig.cas.support.openid.web.support;

import static org.junit.Assert.*;

import org.jasig.cas.support.openid.AbstractOpenIdTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Scott Battaglia
 * @since 3.1

 */
public class DefaultOpenIdUserNameExtractorTests extends AbstractOpenIdTests {

    @Autowired
    private DefaultOpenIdUserNameExtractor extractor;

    @Test
    public void verifyExtractionSuccessful() {
        assertEquals("scootman28", this.extractor
                .extractLocalUsernameFromUri("http://test.com/scootman28"));
    }

    @Test
    public void verifyExtractionFailed() {
        assertNull(this.extractor
                .extractLocalUsernameFromUri("test.com"));
    }

    @Test
    public void verifyNull() {
        assertNull(this.extractor
                .extractLocalUsernameFromUri(null));
    }
}
