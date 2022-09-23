// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.access.SpaceInfo;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.RowLocation;

abstract class GenericController
{
    protected OpenConglomerate open_conglom;
    
    protected void getRowPositionFromRowLocation(final RowLocation rowLocation, final RowPosition rowPosition) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    protected void queueDeletePostCommitWork(final RowPosition rowPosition) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public void init(final OpenConglomerate open_conglom) throws StandardException {
        this.open_conglom = open_conglom;
    }
    
    public OpenConglomerate getOpenConglom() {
        return this.open_conglom;
    }
    
    public void checkConsistency() throws StandardException {
        this.open_conglom.checkConsistency();
    }
    
    public void debugConglomerate() throws StandardException {
        this.open_conglom.debugConglomerate();
    }
    
    public void getTableProperties(final Properties properties) throws StandardException {
        this.open_conglom.getTableProperties(properties);
    }
    
    public Properties getInternalTablePropertySet(final Properties properties) throws StandardException {
        return this.open_conglom.getInternalTablePropertySet(properties);
    }
    
    public SpaceInfo getSpaceInfo() throws StandardException {
        return this.open_conglom.getSpaceInfo();
    }
    
    public void close() throws StandardException {
        if (this.open_conglom != null) {
            this.open_conglom.close();
        }
    }
    
    public boolean isKeyed() {
        return this.open_conglom.isKeyed();
    }
    
    public RowLocation newRowLocationTemplate() throws StandardException {
        if (this.open_conglom.isClosed()) {
            this.open_conglom.reopen();
        }
        return this.open_conglom.newRowLocationTemplate();
    }
    
    public boolean isTableLocked() {
        return this.open_conglom.isTableLocked();
    }
    
    public long getEstimatedRowCount() throws StandardException {
        if (this.open_conglom.isClosed()) {
            this.open_conglom.reopen();
        }
        final long estimatedRowCount = this.open_conglom.getContainer().getEstimatedRowCount(0);
        return (estimatedRowCount == 0L) ? 1L : estimatedRowCount;
    }
    
    public void setEstimatedRowCount(final long n) throws StandardException {
        if (this.open_conglom.getContainer() == null) {
            this.open_conglom.reopen();
        }
        final ContainerHandle container = this.open_conglom.getContainer();
        if (container != null) {
            container.setEstimatedRowCount(n, 0);
        }
    }
}
