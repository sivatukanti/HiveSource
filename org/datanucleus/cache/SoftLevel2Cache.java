// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import org.datanucleus.util.SoftValueMap;
import java.util.HashMap;
import org.datanucleus.NucleusContext;

public class SoftLevel2Cache extends WeakLevel2Cache
{
    public SoftLevel2Cache(final NucleusContext nucleusCtx) {
        this.apiAdapter = nucleusCtx.getApiAdapter();
        this.pinnedCache = new HashMap();
        this.unpinnedCache = new SoftValueMap();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.unpinnedCache = new SoftValueMap();
    }
}
