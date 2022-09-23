// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.util.Iterator;
import org.apache.derby.catalog.UUID;
import java.util.ArrayList;

public class GenericDescriptorList extends ArrayList
{
    private boolean scanned;
    
    public void setScanned(final boolean scanned) {
        this.scanned = scanned;
    }
    
    public boolean getScanned() {
        return this.scanned;
    }
    
    public UniqueTupleDescriptor getUniqueTupleDescriptor(final UUID obj) {
        for (final UniqueTupleDescriptor uniqueTupleDescriptor : this) {
            if (uniqueTupleDescriptor.getUUID().equals(obj)) {
                return uniqueTupleDescriptor;
            }
        }
        return null;
    }
}
