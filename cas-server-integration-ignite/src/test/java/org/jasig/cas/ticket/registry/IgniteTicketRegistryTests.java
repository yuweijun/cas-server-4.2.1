package org.jasig.cas.ticket.registry;

import org.jasig.cas.authentication.TestUtils;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.support.NeverExpiresExpirationPolicy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Unit test for {@link IgniteTicketRegistry}.
 *
 * @author Scott Battaglia
 * @author tduehr
 * @since 3.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/ticketRegistry.xml")
public final class IgniteTicketRegistryTests {

    private static final int TICKETS_IN_REGISTRY = 10;

    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    private TicketRegistry ticketRegistry;

    @Before
    public void setUp() throws Exception {
        this.ticketRegistry = this.applicationContext.getBean("igniteTicketRegistry", TicketRegistry.class);
        initTicketRegistry();
    }

    /**
     * Method to add a TicketGrantingTicket to the ticket cache. This should add
     * the ticket and return. Failure upon any exception.
     */
    @Test
    public void verifyAddTicketToCache() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyGetNullTicket() {
        try {
            this.ticketRegistry.getTicket(null, TicketGrantingTicket.class);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Exception caught.  None expected.");
        }
    }

    @Test
    public void verifyGetNonExistingTicket() {
        try {
            this.ticketRegistry.getTicket("FALALALALALAL", TicketGrantingTicket.class);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Exception caught.  None expected.");
        }
    }

    @Test
    public void verifyGetExistingTicketWithProperClass() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
            this.ticketRegistry.getTicket("TEST", TicketGrantingTicket.class);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyGetExistingTicketWithInproperClass() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
            this.ticketRegistry.getTicket("TEST", ServiceTicket.class);
        } catch (final ClassCastException e) {
            return;
        }
        fail("ClassCastException expected.");
    }

    @Test
    public void verifyGetNullTicketWithoutClass() {
        try {
            this.ticketRegistry.getTicket(null);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Exception caught.  None expected.");
        }
    }

    @Test
    public void verifyGetNonExistingTicketWithoutClass() {
        try {
            this.ticketRegistry.getTicket("FALALALALALAL");
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Exception caught.  None expected.");
        }
    }

    @Test
    public void verifyGetExistingTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
            this.ticketRegistry.getTicket("TEST");
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyDeleteExistingTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
            assertTrue("Ticket was not deleted.", this.ticketRegistry.deleteTicket("TEST"));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyDeleteNonExistingTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
            assertFalse("Ticket was deleted.", this.ticketRegistry.deleteTicket("TEST1"));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyDeleteNullTicket() {
        try {
            this.ticketRegistry.addTicket(new TicketGrantingTicketImpl("TEST", TestUtils.getAuthentication(),
                    new NeverExpiresExpirationPolicy()));
            assertFalse("Ticket was deleted.", this.ticketRegistry.deleteTicket(null));
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyGetTicketsIsZero() {
        try {
            final int size = this.ticketRegistry.getTickets().size();
            assertEquals("The size of the empty registry is not zero.", size, 0);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyGetTicketsFromRegistryEqualToTicketsAdded() {
        final Collection<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < TICKETS_IN_REGISTRY; i++) {
            final TicketGrantingTicket ticketGrantingTicket = new TicketGrantingTicketImpl("TEST" + i,
                    TestUtils.getAuthentication(), new NeverExpiresExpirationPolicy());
            final ServiceTicket st = ticketGrantingTicket.grantServiceTicket("tests" + i,
                    org.jasig.cas.services.TestUtils.getService(),
                    new NeverExpiresExpirationPolicy(), false, true);
            tickets.add(ticketGrantingTicket);
            tickets.add(st);
            this.ticketRegistry.addTicket(ticketGrantingTicket);
            this.ticketRegistry.addTicket(st);
        }

        try {
            final Collection<Ticket> ticketRegistryTickets = this.ticketRegistry.getTickets();
            assertEquals("The size of the registry is not the same as the collection.", ticketRegistryTickets.size(),
                    tickets.size());

            for (final Ticket ticket : tickets) {
                if (!ticketRegistryTickets.contains(ticket)) {
                    fail("Ticket was added to registry but was not found in retrieval of collection of all tickets.");
                }
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            fail("Caught an exception. But no exception should have been thrown.");
        }
    }

    @Test
    public void verifyDeleteTicketWithChildren() {
        this.ticketRegistry.addTicket(new TicketGrantingTicketImpl(
                "TGT", TestUtils.getAuthentication(), new NeverExpiresExpirationPolicy()));
        final TicketGrantingTicket tgt = this.ticketRegistry.getTicket(
                "TGT", TicketGrantingTicket.class);

        final Service service = org.jasig.cas.services.TestUtils.getService("TGT_DELETE_TEST");

        final ServiceTicket st1 = tgt.grantServiceTicket(
                "ST1", service, new NeverExpiresExpirationPolicy(), true, false);
        final ServiceTicket st2 = tgt.grantServiceTicket(
                "ST2", service, new NeverExpiresExpirationPolicy(), true, false);
        final ServiceTicket st3 = tgt.grantServiceTicket(
                "ST3", service, new NeverExpiresExpirationPolicy(), true, false);

        this.ticketRegistry.addTicket(st1);
        this.ticketRegistry.addTicket(st2);
        this.ticketRegistry.addTicket(st3);

        assertNotNull(this.ticketRegistry.getTicket("TGT", TicketGrantingTicket.class));
        assertNotNull(this.ticketRegistry.getTicket("ST1", ServiceTicket.class));
        assertNotNull(this.ticketRegistry.getTicket("ST2", ServiceTicket.class));
        assertNotNull(this.ticketRegistry.getTicket("ST3", ServiceTicket.class));

        this.ticketRegistry.deleteTicket(tgt.getId());

        assertNull(this.ticketRegistry.getTicket("TGT", TicketGrantingTicket.class));
        assertNull(this.ticketRegistry.getTicket("ST1", ServiceTicket.class));
        assertNull(this.ticketRegistry.getTicket("ST2", ServiceTicket.class));
        assertNull(this.ticketRegistry.getTicket("ST3", ServiceTicket.class));
    }


    /**
     * Cleaning ticket registry to start afresh, after newing up the instance.
     * Leftover items from the cache interfere with the correctness of tests.
     * Resetting the registry instance back to its default empty state allows each
     * test to run an isolated mode independent of the previous state of either cache.
     */
    private void initTicketRegistry() {
        final Iterator<Ticket> it = this.ticketRegistry.getTickets().iterator();

        while (it.hasNext()) {
            this.ticketRegistry.deleteTicket(it.next().getId());
        }
    }
}
