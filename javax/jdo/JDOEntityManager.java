// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import javax.persistence.EntityManager;

public interface JDOEntityManager extends EntityManager, PersistenceManager
{
    JDOEntityManagerFactory getPersistenceManagerFactory();
}
