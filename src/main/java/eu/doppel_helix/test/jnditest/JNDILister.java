/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.doppel_helix.test.jnditest;

import eu.doppel_helix.test.jnditest.ejb.Sample;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Matthias Bläsing
 */
@WebServlet(name = "JNDILister", urlPatterns = {"/*"})
public class JNDILister extends HttpServlet {

    private static final String indent = "    ";
    
    @EJB
    private Sample sample;
    

    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        try {
            String[] contexte = new String[] {"java:comp", "java:global", "java:app", "java:module"};
            response.setContentType("text/plain;charset=UTF-8");
            InitialContext ic = new InitialContext();
            try (PrintWriter out = response.getWriter()) {
                out.print("Sample woks: " + sample.hallo("Welt"));
                out.print("\n\n");
                for (String context : contexte) {
                    out.append("========= ");
                    out.append(context);
                    out.append(" =========\n");
                    Context c = (Context) ic.lookup(context);
                    listContext("", out, c, context);
                }
            }
        }   catch (NamingException ex) {
            Logger.getLogger(JNDILister.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void listContext(String indent, PrintWriter out, Context ctx, String ctxName) throws NamingException {
        NamingEnumeration<Binding> ne = ctx.listBindings("");
        while (ne.hasMore()) {
            Binding ncp = ne.next();
            String name = ncp.getName();
            if(name.startsWith(ctxName)) {
                name = name.substring(ctxName.length() + 1);
            }
            out.println(indent + name + " => " + ncp.getClassName());
            if(ncp.getObject() instanceof Context) {
                listContext(indent + JNDILister.indent, out, (Context) ncp.getObject(), ncp.getName());
            }
        }
    }

}
