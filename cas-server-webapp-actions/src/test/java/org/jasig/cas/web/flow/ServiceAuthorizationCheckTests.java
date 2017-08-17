package org.jasig.cas.web.flow;

import java.util.ArrayList;
import java.util.List;

import org.jasig.cas.authentication.principal.WebApplicationService;
import org.jasig.cas.services.RegisteredService;
import org.jasig.cas.services.RegisteredServiceImpl;
import org.jasig.cas.services.ServicesManager;
import org.jasig.cas.services.UnauthorizedServiceException;
import org.jasig.cas.services.DefaultRegisteredServiceAccessStrategy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.test.MockRequestContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Mockito based tests for @{link ServiceAuthorizationCheck}
 *
 * @author Dmitriy Kopylenko
 * @since 3.5.0
 */
public class ServiceAuthorizationCheckTests {

    private ServiceAuthorizationCheck serviceAuthorizationCheck;

    private final WebApplicationService authorizedService = mock(WebApplicationService.class);

    private final WebApplicationService unauthorizedService = mock(WebApplicationService.class);

    private final WebApplicationService undefinedService = mock(WebApplicationService.class);

    private final ServicesManager servicesManager = mock(ServicesManager.class);


    @Before
    public void setUpMocks() {
        final RegisteredServiceImpl authorizedRegisteredService = new RegisteredServiceImpl();
        final RegisteredServiceImpl unauthorizedRegisteredService = new RegisteredServiceImpl();
        unauthorizedRegisteredService.setAccessStrategy(
                new DefaultRegisteredServiceAccessStrategy(false, false));

        final List<RegisteredService> list = new ArrayList<>();
        list.add(authorizedRegisteredService);
        list.add(unauthorizedRegisteredService);
        
        when(this.servicesManager.findServiceBy(this.authorizedService)).thenReturn(authorizedRegisteredService);
        when(this.servicesManager.findServiceBy(this.unauthorizedService)).thenReturn(unauthorizedRegisteredService);
        when(this.servicesManager.findServiceBy(this.undefinedService)).thenReturn(null);
        
        when(this.servicesManager.getAllServices()).thenReturn(list);
        
        this.serviceAuthorizationCheck = new ServiceAuthorizationCheck(this.servicesManager);
    }

    @Test
    public void noServiceProvided() throws Exception {
        final MockRequestContext mockRequestContext = new MockRequestContext();
        final Event event = this.serviceAuthorizationCheck.doExecute(mockRequestContext);
        assertEquals("success", event.getId());

    }

    @Test
    public void authorizedServiceProvided() throws Exception {
        final MockRequestContext mockRequestContext = new MockRequestContext();
        mockRequestContext.getFlowScope().put("service", this.authorizedService);
        final Event event = this.serviceAuthorizationCheck.doExecute(mockRequestContext);
        assertEquals("success", event.getId());
    }

    @Test(expected=UnauthorizedServiceException.class)
    public void unauthorizedServiceProvided() throws Exception {
        final MockRequestContext mockRequestContext = new MockRequestContext();
        mockRequestContext.getFlowScope().put("service", this.unauthorizedService);

        this.serviceAuthorizationCheck.doExecute(mockRequestContext);
        fail("Should have thrown UnauthorizedServiceException");
    }

    @Test(expected=UnauthorizedServiceException.class)
    public void serviceThatIsNotRegisteredProvided() throws Exception {
        final MockRequestContext mockRequestContext = new MockRequestContext();
        mockRequestContext.getFlowScope().put("service", this.undefinedService);
        this.serviceAuthorizationCheck.doExecute(mockRequestContext);
        fail("Should have thrown UnauthorizedServiceException");
    }
}
