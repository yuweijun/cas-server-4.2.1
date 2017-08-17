package org.jasig.cas.services;

import org.apache.commons.lang3.SerializationUtils;
import org.jasig.cas.authentication.principal.DefaultPrincipalFactory;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.cache.CachingPrincipalAttributesRepository;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.jasig.services.persondir.IPersonAttributes;
import org.jasig.services.persondir.support.StubPersonAttributeDao;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Attribute filtering policy tests.
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class RegisteredServiceAttributeReleasePolicyTests {
    @Test
    public void verifyAttributeFilterMappedAttributes() {
        final ReturnMappedAttributeReleasePolicy policy = new ReturnMappedAttributeReleasePolicy();
        final Map<String, String> mappedAttr = new HashMap<>();
        mappedAttr.put("attr1", "newAttr1");
        
        policy.setAllowedAttributes(mappedAttr);
                
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 1);
        assertTrue(attr.containsKey("newAttr1"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnMappedAttributeReleasePolicy p2 = SerializationUtils.deserialize(data);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }
    
    @Test
    public void verifyServiceAttributeFilterAllowedAttributes() {
        final ReturnAllowedAttributeReleasePolicy policy = new ReturnAllowedAttributeReleasePolicy();
        policy.setAllowedAttributes(Arrays.asList("attr1", "attr3"));
        final Principal p = mock(Principal.class);
        
        final Map<String, Object> map = new HashMap<>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));
        
        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");
        
        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), 2);
        assertTrue(attr.containsKey("attr1"));
        assertTrue(attr.containsKey("attr3"));
        
        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllowedAttributeReleasePolicy p2 = SerializationUtils.deserialize(data);
        assertNotNull(p2);
        assertEquals(p2.getAllowedAttributes(), policy.getAllowedAttributes());
    }

    
    @Test
    public void verifyServiceAttributeFilterAllAttributes() {
        final ReturnAllAttributeReleasePolicy policy = new ReturnAllAttributeReleasePolicy();
        final Principal p = mock(Principal.class);

        final Map<String, Object> map = new HashMap<>();
        map.put("attr1", "value1");
        map.put("attr2", "value2");
        map.put("attr3", Arrays.asList("v3", "v4"));

        when(p.getAttributes()).thenReturn(map);
        when(p.getId()).thenReturn("principalId");

        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), map.size());

        final byte[] data = SerializationUtils.serialize(policy);
        final ReturnAllAttributeReleasePolicy p2 = SerializationUtils.deserialize(data);
        assertNotNull(p2);
    }

    @Test
    public void checkServiceAttributeFilterAllAttributesWithCachingTurnedOn() {
        final ReturnAllAttributeReleasePolicy policy = new ReturnAllAttributeReleasePolicy();

        final Map<String, List<Object>> attributes = new HashMap<>();
        attributes.put("values", Arrays.asList(new Object[]{"v1", "v2", "v3"}));
        attributes.put("cn", Arrays.asList(new Object[]{"commonName"}));
        attributes.put("username", Arrays.asList(new Object[]{"uid"}));

        final IPersonAttributeDao dao = new StubPersonAttributeDao(attributes);
        final IPersonAttributes person = mock(IPersonAttributes.class);
        when(person.getName()).thenReturn("uid");
        when(person.getAttributes()).thenReturn(attributes);

        final CachingPrincipalAttributesRepository repository =
                new CachingPrincipalAttributesRepository(TimeUnit.MILLISECONDS, 100);
        repository.setAttributeRepository(dao);

        final Principal p = new DefaultPrincipalFactory().createPrincipal("uid",
                    Collections.<String, Object>singletonMap("mail", "final@example.com"));

        policy.setPrincipalAttributesRepository(repository);

        final Map<String, Object> attr = policy.getAttributes(p);
        assertEquals(attr.size(), attributes.size());
    }
}
