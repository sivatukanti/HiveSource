// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.io.File;

public class FileMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return File.class;
    }
}
