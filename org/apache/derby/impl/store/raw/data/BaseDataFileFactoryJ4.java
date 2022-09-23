// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.cache.Cacheable;

public class BaseDataFileFactoryJ4 extends BaseDataFileFactory
{
    protected Cacheable newRAFContainer(final BaseDataFileFactory baseDataFileFactory) {
        return new RAFContainer4(baseDataFileFactory);
    }
}
