// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.db;

import org.apache.derby.iapi.services.context.Context;

public interface DatabaseContext extends Context
{
    public static final String CONTEXT_ID = "Database";
    
    Database getDatabase();
}
