// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.uuid;

import org.apache.derby.catalog.UUID;

public interface UUIDFactory
{
    UUID createUUID();
    
    UUID recreateUUID(final String p0);
}
