// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import java.security.BasicPermission;

public final class JDOPermission extends BasicPermission
{
    public static final JDOPermission GET_METADATA;
    public static final JDOPermission MANAGE_METADATA;
    public static final JDOPermission SET_STATE_MANAGER;
    public static final JDOPermission CLOSE_PERSISTENCE_MANAGER_FACTORY;
    
    public JDOPermission(final String name) {
        super(name);
    }
    
    public JDOPermission(final String name, final String actions) {
        super(name, actions);
    }
    
    static {
        GET_METADATA = new JDOPermission("getMetadata");
        MANAGE_METADATA = new JDOPermission("manageMetadata");
        SET_STATE_MANAGER = new JDOPermission("setStateManager");
        CLOSE_PERSISTENCE_MANAGER_FACTORY = new JDOPermission("closePersistenceManagerFactory");
    }
}
