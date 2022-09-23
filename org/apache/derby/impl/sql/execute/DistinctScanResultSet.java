// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import java.util.Enumeration;

class DistinctScanResultSet extends HashScanResultSet
{
    Enumeration element;
    
    DistinctScanResultSet(final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final Activation activation, final int n2, final int n3, final int n4, final String s, final String s2, final String s3, final boolean b, final int n5, final int n6, final boolean b2, final int n7, final double n8, final double n9) throws StandardException {
        super(n, staticCompiledOpenConglomInfo, activation, n2, n3, null, 0, null, 0, false, null, null, -1, -1.0f, -1, n4, s, s2, s3, b, false, n5, n6, b2, n7, false, n8, n9);
        this.element = null;
        this.eliminateDuplicates = true;
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow compactRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            if (this.firstNext) {
                this.element = this.hashtable.elements();
                this.firstNext = false;
            }
            if (this.element.hasMoreElements()) {
                this.setCompatRow(this.compactRow, this.element.nextElement());
                ++this.rowsSeen;
                compactRow = this.compactRow;
            }
        }
        this.setCurrentRow(compactRow);
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return compactRow;
    }
}
