
package eu.doppel_helix.test.jnditest;

import eu.doppel_helix.test.jnditest.ejb.Sample;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;


@WebServlet(name = "JNDILister", urlPatterns = {"/lister"})
public class JNDILister extends HttpServlet {

    private static final String indent = "    ";


    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        try {

            response.setContentType("text/plain;charset=UTF-8");

            try (PrintWriter out = response.getWriter()) {
                doTest(out);
            }
        } catch (NamingException ex) {
            Logger.getLogger(JNDILister.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void listContext(String indent, PrintWriter out, Context ctx, String ctxName) throws NamingException {
        NamingEnumeration<Binding> ne = ctx.listBindings("");
        while (ne.hasMore()) {
            Binding ncp = ne.next();
            String name = ncp.getName();
            if (name.startsWith(ctxName)) {
                name = name.substring(ctxName.length() + 1);
            }
            out.println(indent + name + " => " + ncp.getClassName());
            if (ncp.getObject() instanceof Context) {
                listContext(indent + JNDILister.indent, out, (Context) ncp.getObject(), ncp.getName());
            }
        }
    }

    public static void doTest(PrintWriter out) throws NamingException {
        String[] contexte = new String[]{"java:comp", "java:global", "java:app", "java:module"};

        InitialContext ic = new InitialContext();
        Sample sampleFromLookup = (Sample) ic.lookup("java:module/Sample");
        out.println("JDNI Lookup works (java:module/Sample): "
                + sampleFromLookup.hallo("Lookup"));
        out.print("\n");
        for (String context : contexte) {
            out.append("========= ");
            out.append(context);
            out.append(" =========\n");
            Context c = (Context) ic.lookup(context);
            listContext("", out, c, context);

        }
        // also look up the bean as expected in the local namespaces
        out.append("=============  Direct look up of bean using the JNDI names ==================\n");
        out.append("java:app/jnditest-1.0-SNAPSHOT/Sample = "
                + ic.lookup("java:app/jnditest-1.0-SNAPSHOT/Sample").toString()
                + "\n");
        out.append("java:app/jnditest-1.0-SNAPSHOT/Sample!eu.doppel_helix.test.jnditest.ejb.Sample = "
                + ic.lookup("java:app/jnditest-1.0-SNAPSHOT/Sample!eu.doppel_helix.test.jnditest.ejb.Sample").toString()
                + "\n");
        out.append("java:module/Sample = "
                + ic.lookup("java:module/Sample").toString() + "\n");
        out.append("java:module/Sample = "
                + ic.lookup("java:module/Sample!eu.doppel_helix.test.jnditest.ejb.Sample").toString()
                + "\n");
    }

}
