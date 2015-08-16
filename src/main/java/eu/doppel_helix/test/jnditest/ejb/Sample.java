
package eu.doppel_helix.test.jnditest.ejb;

import javax.ejb.Stateless;

@Stateless
public class Sample {
    public String hallo(String name) {
        return "Hallo " + name;
    }
}
