// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.heap;

import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.access.ColumnOrdering;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import java.util.Properties;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.access.conglomerate.ConglomerateFactory;

public class HeapConglomerateFactory implements ConglomerateFactory, ModuleControl, ModuleSupportable
{
    private static final String IMPLEMENTATIONID = "heap";
    private static final String FORMATUUIDSTRING = "D2976090-D9F5-11d0-B54D-00A024BF8878";
    private UUID formatUUID;
    
    public Properties defaultProperties() {
        return new Properties();
    }
    
    public boolean supportsImplementation(final String s) {
        return s.equals("heap");
    }
    
    public String primaryImplementationType() {
        return "heap";
    }
    
    public boolean supportsFormat(final UUID uuid) {
        return uuid.equals(this.formatUUID);
    }
    
    public UUID primaryFormat() {
        return this.formatUUID;
    }
    
    public int getConglomerateFactoryId() {
        return 0;
    }
    
    public Conglomerate createConglomerate(final TransactionManager transactionManager, final int n, final long n2, final DataValueDescriptor[] array, final ColumnOrdering[] array2, final int[] array3, final Properties properties, final int n3) throws StandardException {
        Heap heap;
        if ((n3 & 0x1) != 0x0 && transactionManager.getAccessManager().isReadOnly()) {
            heap = new Heap();
        }
        else if (transactionManager.checkVersion(10, 3, null)) {
            heap = new Heap();
        }
        else {
            heap = new Heap_v10_2();
        }
        heap.create(transactionManager.getRawStoreXact(), n, n2, array, array2, array3, properties, heap.getTypeFormatId(), n3);
        return heap;
    }
    
    public Conglomerate readConglomerate(final TransactionManager transactionManager, final ContainerKey containerKey) throws StandardException {
        ContainerHandle openContainer = null;
        Page page = null;
        final DataValueDescriptor[] array = { null };
        try {
            openContainer = transactionManager.getRawStoreXact().openContainer(containerKey, null, 0);
            if (openContainer == null) {
                throw StandardException.newException("XSAI2.S", new Long(containerKey.getContainerId()));
            }
            array[0] = new Heap();
            page = openContainer.getPage(1L);
            page.fetchFromSlot(null, 0, array, null, true);
        }
        finally {
            if (page != null) {
                page.unlatch();
            }
            if (openContainer != null) {
                openContainer.close();
            }
        }
        return (Conglomerate)array[0];
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("derby.access.Conglomerate.type");
        return property != null && this.supportsImplementation(property);
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.formatUUID = Monitor.getMonitor().getUUIDFactory().recreateUUID("D2976090-D9F5-11d0-B54D-00A024BF8878");
    }
    
    public void stop() {
    }
}
