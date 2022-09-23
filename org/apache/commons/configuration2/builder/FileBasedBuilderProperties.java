// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystem;
import java.net.URL;
import java.io.File;

public interface FileBasedBuilderProperties<T>
{
    T setReloadingRefreshDelay(final Long p0);
    
    T setReloadingDetectorFactory(final ReloadingDetectorFactory p0);
    
    T setFile(final File p0);
    
    T setURL(final URL p0);
    
    T setPath(final String p0);
    
    T setFileName(final String p0);
    
    T setBasePath(final String p0);
    
    T setFileSystem(final FileSystem p0);
    
    T setLocationStrategy(final FileLocationStrategy p0);
    
    T setEncoding(final String p0);
}
