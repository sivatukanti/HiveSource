// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.InputStream;

interface FilterDecoder extends FilterCoder
{
    int getMemoryUsage();
    
    InputStream getInputStream(final InputStream p0);
}
