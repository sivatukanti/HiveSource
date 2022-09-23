// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

import org.datanucleus.store.types.SCO;

public class UnsetOwnerFieldManager extends AbstractFieldManager
{
    @Override
    public void storeObjectField(final int fieldNumber, final Object value) {
        if (value instanceof SCO) {
            ((SCO)value).unsetOwner();
        }
    }
}
