// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.ClassNameConstants;
import java.awt.image.BufferedImage;

public class BufferedImageMapping extends SingleFieldMapping
{
    @Override
    public Class getJavaType() {
        return BufferedImage.class;
    }
    
    @Override
    public String getJavaTypeForDatastoreMapping(final int index) {
        return ClassNameConstants.JAVA_IO_SERIALIZABLE;
    }
}
