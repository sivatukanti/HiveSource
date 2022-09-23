// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.persistence.EntityManagerFactory;

public interface JDOEntityManagerFactory extends EntityManagerFactory, PersistenceManagerFactory
{
    JDOEntityManager getPersistenceManager();
    
    JDOEntityManager getPersistenceManagerProxy();
    
    JDOEntityManager getPersistenceManager(final String p0, final String p1);
}
