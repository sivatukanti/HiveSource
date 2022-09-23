// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import java.util.Arrays;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;

public class RequestIdentifier
{
    private final DatastoreClass table;
    private final int[] memberNumbers;
    private final RequestType type;
    private final int hashCode;
    private final String className;
    
    public RequestIdentifier(final DatastoreClass table, final AbstractMemberMetaData[] mmds, final RequestType type, final String className) {
        this.table = table;
        this.type = type;
        if (mmds == null) {
            this.memberNumbers = null;
        }
        else {
            this.memberNumbers = new int[mmds.length];
            for (int i = 0; i < this.memberNumbers.length; ++i) {
                this.memberNumbers[i] = mmds[i].getAbsoluteFieldNumber();
            }
            Arrays.sort(this.memberNumbers);
        }
        this.className = className;
        int h = table.hashCode() ^ type.hashCode() ^ className.hashCode();
        if (this.memberNumbers != null) {
            for (int j = 0; j < this.memberNumbers.length; ++j) {
                h ^= this.memberNumbers[j];
            }
        }
        this.hashCode = h;
    }
    
    public DatastoreClass getTable() {
        return this.table;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RequestIdentifier)) {
            return false;
        }
        final RequestIdentifier ri = (RequestIdentifier)o;
        return this.hashCode == ri.hashCode && this.table.equals(ri.table) && this.type.equals(ri.type) && Arrays.equals(this.memberNumbers, ri.memberNumbers) && this.className.equals(ri.className);
    }
}
