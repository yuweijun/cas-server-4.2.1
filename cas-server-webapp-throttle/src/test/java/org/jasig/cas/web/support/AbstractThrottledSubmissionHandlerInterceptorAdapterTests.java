package org.jasig.cas.web.support;

import org.jasig.inspektr.common.web.ClientInfo;
import org.jasig.inspektr.common.web.ClientInfoHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

/**
 * Base class for submission throttle tests.
 *
 * @author Marvin S. Addison
 * @since 3.0.0
 */
public abstract class AbstractThrottledSubmissionHandlerInterceptorAdapterTests {

    protected static final int FAILURE_RANGE = 5;

    protected static final int FAILURE_THRESHOLD = 10;

    protected static final String IP_ADDRESS = "1.2.3.4";

    protected static final ClientInfo CLIENT_INFO = new ClientInfo(IP_ADDRESS, IP_ADDRESS);

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception {
        ClientInfoHolder.setClientInfo(CLIENT_INFO);
    }

    @After
    public void tearDown() throws Exception {
        ClientInfoHolder.setClientInfo(null);
    }

    @Test
    public void verifyThrottle() throws Exception {
        final double rate = (double) FAILURE_THRESHOLD / (double) FAILURE_RANGE;
        getThrottle().setFailureRangeInSeconds(FAILURE_RANGE);
        getThrottle().setFailureThreshold(FAILURE_THRESHOLD);
        getThrottle().afterPropertiesSet();

        // Ensure that repeated logins BELOW threshold rate are allowed
        // Wait 7% more than threshold period
        int wait = (int) (1000.0 * 1.07 / rate);
        failLoop(3, wait, 200);

        // Ensure that repeated logins ABOVE threshold rate are throttled
        // Wait 7% less than threshold period
        wait = (int) (1000.0 * 0.93 / rate);
        failLoop(3, wait, 403);

        // Ensure that slowing down relieves throttle
        // Wait 7% more than threshold period
        wait = (int) (1000.0 * 1.07 / rate);
        Thread.sleep(wait);
        failLoop(3, wait, 200);
    }


    private void failLoop(final int trials, final int period, final int expected) throws Exception {
        // Seed with something to compare against
        loginUnsuccessfully("mog", "1.2.3.4").getStatus();
        for (int i = 0; i < trials; i++) {
            logger.debug("Waiting for {} ms", period);
            Thread.sleep(period);
            assertEquals(expected, loginUnsuccessfully("mog", "1.2.3.4").getStatus());
        }
    }


    protected abstract MockHttpServletResponse loginUnsuccessfully(String username, String fromAddress) throws Exception;

    protected abstract AbstractThrottledSubmissionHandlerInterceptorAdapter getThrottle();
}
