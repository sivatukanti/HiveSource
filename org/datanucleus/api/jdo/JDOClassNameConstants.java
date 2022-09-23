// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.spi.PersistenceCapable;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.identity.ShortIdentity;
import javax.jdo.identity.ObjectIdentity;
import javax.jdo.identity.ByteIdentity;
import javax.jdo.identity.CharIdentity;
import javax.jdo.identity.StringIdentity;
import javax.jdo.identity.IntIdentity;
import javax.jdo.identity.LongIdentity;

public class JDOClassNameConstants
{
    public static final String JAVAX_JDO_IDENTITY_LONG_IDENTITY;
    public static final String JAVAX_JDO_IDENTITY_INT_IDENTITY;
    public static final String JAVAX_JDO_IDENTITY_STRING_IDENTITY;
    public static final String JAVAX_JDO_IDENTITY_CHAR_IDENTITY;
    public static final String JAVAX_JDO_IDENTITY_BYTE_IDENTITY;
    public static final String JAVAX_JDO_IDENTITY_OBJECT_IDENTITY;
    public static final String JAVAX_JDO_IDENTITY_SHORT_IDENTITY;
    public static final String JAVAX_JDO_PersistenceManagerFactory;
    public static final String JAVAX_JDO_SPI_PERSISTENCE_CAPABLE;
    public static final String JDOPersistenceManagerFactory;
    
    static {
        JAVAX_JDO_IDENTITY_LONG_IDENTITY = LongIdentity.class.getName();
        JAVAX_JDO_IDENTITY_INT_IDENTITY = IntIdentity.class.getName();
        JAVAX_JDO_IDENTITY_STRING_IDENTITY = StringIdentity.class.getName();
        JAVAX_JDO_IDENTITY_CHAR_IDENTITY = CharIdentity.class.getName();
        JAVAX_JDO_IDENTITY_BYTE_IDENTITY = ByteIdentity.class.getName();
        JAVAX_JDO_IDENTITY_OBJECT_IDENTITY = ObjectIdentity.class.getName();
        JAVAX_JDO_IDENTITY_SHORT_IDENTITY = ShortIdentity.class.getName();
        JAVAX_JDO_PersistenceManagerFactory = PersistenceManagerFactory.class.getName();
        JAVAX_JDO_SPI_PERSISTENCE_CAPABLE = PersistenceCapable.class.getName();
        JDOPersistenceManagerFactory = JDOPersistenceManagerFactory.class.getName();
    }
}
