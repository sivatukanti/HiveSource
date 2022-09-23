// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.PageTimeStamp;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.services.daemon.Serviceable;

public final class ReclaimSpace implements Serviceable
{
    private boolean serviceASAP;
    private ContainerKey containerId;
    private PageKey pageId;
    private RecordHandle headRowHandle;
    private int columnId;
    private long columnPageId;
    private int columnRecordId;
    private PageTimeStamp timeStamp;
    private int attempts;
    private DataFactory processor;
    private int reclaim;
    public static final int CONTAINER = 1;
    public static final int PAGE = 2;
    public static final int ROW_RESERVE = 3;
    public static final int COLUMN_CHAIN = 4;
    
    private void initContainerInfo(final ContainerKey containerId, final int reclaim, final DataFactory processor, final boolean serviceASAP) {
        this.containerId = containerId;
        this.reclaim = reclaim;
        this.attempts = 0;
        this.processor = processor;
        this.serviceASAP = serviceASAP;
    }
    
    public ReclaimSpace(final int n, final ContainerKey containerKey, final DataFactory dataFactory, final boolean b) {
        this.initContainerInfo(containerKey, n, dataFactory, b);
    }
    
    public ReclaimSpace(final int n, final PageKey pageId, final DataFactory dataFactory, final boolean b) {
        this.initContainerInfo(pageId.getContainerId(), n, dataFactory, b);
        this.pageId = pageId;
    }
    
    public ReclaimSpace(final int n, final RecordHandle headRowHandle, final DataFactory dataFactory, final boolean b) {
        this.initContainerInfo(headRowHandle.getContainerId(), n, dataFactory, b);
        this.headRowHandle = headRowHandle;
    }
    
    public ReclaimSpace(final int n, final RecordHandle headRowHandle, final int columnId, final long columnPageId, final int columnRecordId, final PageTimeStamp timeStamp, final DataFactory dataFactory, final boolean b) {
        this.initContainerInfo(headRowHandle.getContainerId(), n, dataFactory, b);
        this.headRowHandle = headRowHandle;
        this.columnId = columnId;
        this.columnPageId = columnPageId;
        this.columnRecordId = columnRecordId;
        this.timeStamp = timeStamp;
    }
    
    public boolean serviceASAP() {
        return this.serviceASAP;
    }
    
    public int performWork(final ContextManager contextManager) throws StandardException {
        return this.processor.reclaimSpace(this, contextManager);
    }
    
    public boolean serviceImmediately() {
        return true;
    }
    
    public final ContainerKey getContainerId() {
        return this.containerId;
    }
    
    public final PageKey getPageId() {
        return this.pageId;
    }
    
    public final RecordHandle getHeadRowHandle() {
        return this.headRowHandle;
    }
    
    public final int getColumnId() {
        return this.columnId;
    }
    
    public final long getColumnPageId() {
        return this.columnPageId;
    }
    
    public final int getColumnRecordId() {
        return this.columnRecordId;
    }
    
    public final PageTimeStamp getPageTimeStamp() {
        return this.timeStamp;
    }
    
    public final int reclaimWhat() {
        return this.reclaim;
    }
    
    public final int incrAttempts() {
        return ++this.attempts;
    }
    
    public String toString() {
        return null;
    }
}
