package org.jasig.cas.adaptors.ldap;

import org.apache.commons.io.IOUtils;
import org.jasig.cas.util.ldap.uboundid.InMemoryTestLdapDirectoryServer;
import org.junit.AfterClass;
import org.ldaptive.LdapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Base class for LDAP tests that provision and deprovision DIRECTORY data as part of test setup/teardown.
 * @author Marvin S. Addison
 * @author Misagh Moayyed
 * @since 4.1.0
 */
public abstract class AbstractLdapTests implements ApplicationContextAware {
    private static InMemoryTestLdapDirectoryServer DIRECTORY;

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext context;

    public static void initDirectoryServer(final InputStream ldifFile) throws IOException {
        final ClassPathResource properties = new ClassPathResource("ldap.properties");
        final ClassPathResource schema = new ClassPathResource("schema/standard-ldap.schema");

        DIRECTORY = new InMemoryTestLdapDirectoryServer(properties.getInputStream(),
                ldifFile,
                schema.getInputStream());
    }

    public static void initDirectoryServer() throws IOException {
        initDirectoryServer(new ClassPathResource("ldif/ldap-base.ldif").getInputStream());
    }

    @AfterClass
    public static void tearDown() {
        IOUtils.closeQuietly(DIRECTORY);
    }

    protected static InMemoryTestLdapDirectoryServer getDirectory() {
        return DIRECTORY;
    }

    protected Collection<LdapEntry> getEntries() {
        return DIRECTORY.getLdapEntries();
    }

    protected String getUsername(final LdapEntry entry) {
        final String unameAttr = this.context.getBean("usernameAttribute", String.class);
        return entry.getAttribute(unameAttr).getStringValue();
    }

    protected <T> T getBean(final String id, final Class<T> clazz) {
        return this.context.getBean(id, clazz);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
