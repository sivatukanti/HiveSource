// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;

public interface PageActions
{
    void actionDelete(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final boolean p4, final LogicalUndo p5) throws StandardException;
    
    int actionUpdate(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final Object[] p4, final FormatableBitSet p5, final int p6, final DynamicByteArrayOutputStream p7, final int p8, final RecordHandle p9) throws StandardException;
    
    void actionPurge(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final int[] p4, final boolean p5) throws StandardException;
    
    void actionUpdateField(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final int p4, final Object p5, final LogicalUndo p6) throws StandardException;
    
    int actionInsert(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final Object[] p4, final FormatableBitSet p5, final LogicalUndo p6, final byte p7, final int p8, final boolean p9, final int p10, final DynamicByteArrayOutputStream p11, final int p12, final int p13) throws StandardException;
    
    void actionCopyRows(final RawTransaction p0, final BasePage p1, final BasePage p2, final int p3, final int p4, final int p5, final int[] p6) throws StandardException;
    
    void actionInvalidatePage(final RawTransaction p0, final BasePage p1) throws StandardException;
    
    void actionInitPage(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final long p4) throws StandardException;
    
    void actionShrinkReservedSpace(final RawTransaction p0, final BasePage p1, final int p2, final int p3, final int p4, final int p5) throws StandardException;
}
