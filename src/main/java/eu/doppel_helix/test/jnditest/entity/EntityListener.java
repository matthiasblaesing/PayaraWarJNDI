package eu.doppel_helix.test.jnditest.entity;

import eu.doppel_helix.test.jnditest.CommonTest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.persistence.PrePersist;

public class EntityListener {
    private static final Logger LOG = Logger.getLogger(EntityListener.class.getName());
    
    @PrePersist
    public void prePersist(Object o) {
        try {
            StringWriter sw = new StringWriter();
            CommonTest.doTest(new PrintWriter(sw));
            LOG.info(sw.toString());
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
