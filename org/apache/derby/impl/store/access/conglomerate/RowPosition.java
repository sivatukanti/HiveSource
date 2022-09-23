// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.Page;

public class RowPosition
{
    public Page current_page;
    public RecordHandle current_rh;
    public int current_slot;
    public boolean current_rh_qualified;
    public long current_pageno;
    
    public void init() {
        this.current_page = null;
        this.current_rh = null;
        this.current_slot = -1;
        this.current_rh_qualified = false;
        this.current_pageno = -1L;
    }
    
    public final void positionAtNextSlot() {
        ++this.current_slot;
        this.current_rh = null;
    }
    
    public final void positionAtPrevSlot() {
        --this.current_slot;
        this.current_rh = null;
    }
    
    public void unlatch() {
        if (this.current_page != null) {
            this.current_page.unlatch();
            this.current_page = null;
        }
        this.current_slot = -1;
    }
    
    public String toString() {
        return null;
    }
}
