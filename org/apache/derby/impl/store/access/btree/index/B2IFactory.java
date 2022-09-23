// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.btree.index;

import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.impl.store.access.btree.ControlRow;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import java.util.Properties;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.access.conglomerate.ConglomerateFactory;

public class B2IFactory implements ConglomerateFactory, ModuleControl
{
    private static final String IMPLEMENTATIONID = "BTREE";
    private static final String FORMATUUIDSTRING = "C6CEEEF0-DAD3-11d0-BB01-0060973F0942";
    private UUID formatUUID;
    
    public Properties defaultProperties() {
        return new Properties();
    }
    
    public boolean supportsImplementation(final String s) {
        return s.equals("BTREE");
    }
    
    public String primaryImplementationType() {
        return "BTREE";
    }
    
    public boolean supportsFormat(final UUID uuid) {
        return uuid.equals(this.formatUUID);
    }
    
    public UUID primaryFormat() {
        return this.formatUUID;
    }
    
    public int getConglomerateFactoryId() {
        return 1;
    }
    
    public Conglomerate createConglomerate(final TransactionManager transactionManager, final int n, final long n2, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int n3) throws StandardException {
        B2I b2I;
        if ((n3 & 0x1) != 0x0 && transactionManager.getAccessManager().isReadOnly()) {
            b2I = new B2I();
        }
        else if (transactionManager.checkVersion(10, 4, null)) {
            b2I = new B2I();
        }
        else if (transactionManager.checkVersion(10, 3, null)) {
            b2I = new B2I_10_3();
        }
        else {
            b2I = new B2I_v10_2();
        }
        b2I.create(transactionManager, n, n2, array, array2, array3, properties, n3);
        return b2I;
    }
    
    public Conglomerate readConglomerate(final TransactionManager transactionManager, final ContainerKey containerKey) throws StandardException {
        Conglomerate conglom = null;
        ContainerHandle openContainer = null;
        ControlRow value = null;
        try {
            openContainer = transactionManager.getRawStoreXact().openContainer(containerKey, null, 8);
            if (openContainer == null) {
                throw StandardException.newException("XSAI2.S", new Long(containerKey.getContainerId()));
            }
            value = ControlRow.get(openContainer, 1L);
            conglom = value.getConglom(470);
        }
        finally {
            if (value != null) {
                value.release();
            }
            if (openContainer != null) {
                openContainer.close();
            }
        }
        return conglom;
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("derby.access.Conglomerate.type");
        return property != null && this.supportsImplementation(property);
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.formatUUID = Monitor.getMonitor().getUUIDFactory().recreateUUID("C6CEEEF0-DAD3-11d0-BB01-0060973F0942");
    }
    
    public void stop() {
    }
}
