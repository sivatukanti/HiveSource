// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.depend;

import java.util.Hashtable;

public class ProviderList extends Hashtable
{
    public void addProvider(final Provider value) {
        this.put(value.getObjectID(), value);
    }
}
