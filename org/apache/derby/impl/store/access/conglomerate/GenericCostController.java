// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.store.access.StoreCostController;

public abstract class GenericCostController extends GenericController implements StoreCostController
{
    public double getFetchFromFullKeyCost(final FormatableBitSet set, final int n) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
}
