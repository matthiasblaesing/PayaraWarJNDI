package eu.doppel_helix.test.jnditest;

import eu.doppel_helix.test.jnditest.entity.Test;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(name = "JNDIEntityListener", urlPatterns = {"/entity"})
public class JNDIEntityListener extends HttpServlet {

    @PersistenceUnit(unitName = "X")
    private EntityManagerFactory emf;
    
    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("Output should be in Log!");
        }

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Test t = new Test();
            t.setTitle("Test");
            em.persist(t);
            em.getTransaction().commit();
        } finally {
            if(em != null) {
                em.close();
            }
        }
        
    }

}
