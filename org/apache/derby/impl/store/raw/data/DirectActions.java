// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.OutputStream;
import java.io.ObjectInput;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;

public class DirectActions implements PageActions
{
    protected DynamicByteArrayOutputStream outBytes;
    protected ArrayInputStream limitIn;
    
    public DirectActions() {
        this.outBytes = new DynamicByteArrayOutputStream();
        this.limitIn = new ArrayInputStream();
    }
    
    public void actionDelete(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final boolean b, final LogicalUndo logicalUndo) throws StandardException {
        try {
            basePage.setDeleteStatus(null, n, b);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public int actionUpdate(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final Object[] array, final FormatableBitSet set, final int n3, final DynamicByteArrayOutputStream outBytes, final int n4, final RecordHandle recordHandle) throws StandardException {
        if (outBytes == null) {
            this.outBytes.reset();
        }
        else {
            this.outBytes = outBytes;
        }
        try {
            final int logRow = basePage.logRow(n, false, n2, array, set, this.outBytes, 0, (byte)8, n3, n4, 100);
            this.limitIn.setData(this.outBytes.getByteArray());
            this.limitIn.setPosition(this.outBytes.getBeginPosition());
            this.limitIn.setLimit(this.outBytes.getPosition() - this.outBytes.getBeginPosition());
            basePage.storeRecord(null, n, false, this.limitIn);
            return logRow;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public void actionPurge(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final int[] array, final boolean b) throws StandardException {
        try {
            for (int i = n2 - 1; i >= 0; --i) {
                basePage.purgeRecord(null, n + i, array[i]);
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public void actionUpdateField(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final int n3, final Object o, final LogicalUndo logicalUndo) throws StandardException {
        this.outBytes.reset();
        try {
            basePage.logColumn(n, n3, o, this.outBytes, 100);
            this.limitIn.setData(this.outBytes.getByteArray());
            this.limitIn.setPosition(this.outBytes.getBeginPosition());
            this.limitIn.setLimit(this.outBytes.getPosition() - this.outBytes.getBeginPosition());
            basePage.storeField(null, n, n3, this.limitIn);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public int actionInsert(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final Object[] array, final FormatableBitSet set, final LogicalUndo logicalUndo, final byte b, int n3, final boolean b2, final int n4, final DynamicByteArrayOutputStream outBytes, final int n5, final int n6) throws StandardException {
        if (outBytes == null) {
            this.outBytes.reset();
        }
        else {
            this.outBytes = outBytes;
        }
        try {
            if (b2) {
                n3 = basePage.logLongColumn(n, n2, array[0], this.outBytes);
            }
            else {
                n3 = basePage.logRow(n, true, n2, array, set, this.outBytes, n3, b, n4, n5, n6);
            }
            this.limitIn.setData(this.outBytes.getByteArray());
            this.limitIn.setPosition(this.outBytes.getBeginPosition());
            this.limitIn.setLimit(this.outBytes.getPosition() - this.outBytes.getBeginPosition());
            basePage.storeRecord(null, n, true, this.limitIn);
            return n3;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public void actionCopyRows(final RawTransaction rawTransaction, final BasePage basePage, final BasePage basePage2, final int n, final int n2, final int n3, final int[] array) throws StandardException {
        try {
            final int[] array2 = new int[n2];
            for (int i = 0; i < n2; ++i) {
                this.outBytes.reset();
                basePage2.logRecord(n3 + i, 0, array[i], null, this.outBytes, null);
                array2[i] = this.outBytes.getUsed();
            }
            if (!basePage.spaceForCopy(n2, array2)) {
                throw StandardException.newException("XSDA3.S");
            }
            for (int j = 0; j < n2; ++j) {
                this.outBytes.reset();
                basePage2.logRecord(n3 + j, 0, array[j], null, this.outBytes, null);
                this.limitIn.setData(this.outBytes.getByteArray());
                this.limitIn.setPosition(this.outBytes.getBeginPosition());
                this.limitIn.setLimit(this.outBytes.getPosition() - this.outBytes.getBeginPosition());
                basePage.storeRecord(null, n + j, true, this.limitIn);
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public void actionInvalidatePage(final RawTransaction rawTransaction, final BasePage basePage) throws StandardException {
        basePage.setPageStatus(null, (byte)2);
    }
    
    public void actionInitPage(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final long n3) throws StandardException {
        basePage.initPage(null, (byte)1, ((n & 0x4) == 0x0) ? basePage.newRecordId() : 6, (n & 0x2) != 0x0, (n & 0x1) != 0x0);
    }
    
    public void actionShrinkReservedSpace(final RawTransaction rawTransaction, final BasePage basePage, final int n, final int n2, final int n3, final int n4) throws StandardException {
        try {
            basePage.setReservedSpace(null, n, n3);
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
}
