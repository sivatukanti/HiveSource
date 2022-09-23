// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.net.URL;

public interface FileLocationStrategy
{
    URL locate(final FileSystem p0, final FileLocator p1);
}
