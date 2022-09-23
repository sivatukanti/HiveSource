// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.sort;

import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Properties;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.store.access.BackingStoreHashtable;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.ScanInfo;
import org.apache.derby.iapi.store.access.conglomerate.ScanManager;

public abstract class Scan implements ScanManager, ScanInfo
{
    public void didNotQualify() throws StandardException {
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public int fetchNextGroup(final DataValueDescriptor[][] array, final RowLocation[] array2, final RowLocation[] array3) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void fetchSet(final long n, final int[] array, final BackingStoreHashtable backingStoreHashtable) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public boolean doesCurrentPositionQualify() throws StandardException {
        return true;
    }
    
    public void fetchLocation(final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public ScanInfo getScanInfo() throws StandardException {
        return this;
    }
    
    public long getEstimatedRowCount() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void setEstimatedRowCount(final long n) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public boolean isCurrentPositionDeleted() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public boolean isKeyed() {
        return false;
    }
    
    public boolean isTableLocked() {
        return true;
    }
    
    public boolean delete() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void reopenScan(final DataValueDescriptor[] array, final int n, final Qualifier[][] array2, final DataValueDescriptor[] array3, final int n2) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public void reopenScanByRowLocation(final RowLocation rowLocation, final Qualifier[][] array) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public boolean replace(final DataValueDescriptor[] array, final FormatableBitSet set) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public boolean positionAtRowLocation(final RowLocation rowLocation) throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
    
    public Properties getAllScanInfo(Properties properties) throws StandardException {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put(MessageService.getTextMessage("XSAJ0.U"), MessageService.getTextMessage("XSAJH.U"));
        return properties;
    }
    
    public boolean isHeldAfterCommit() throws StandardException {
        throw StandardException.newException("XSAS0.S");
    }
}
