// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import java.net.MalformedURLException;
import java.util.List;
import java.net.URL;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
@Deprecated
public class ApplicationClassLoader extends org.apache.hadoop.util.ApplicationClassLoader
{
    public ApplicationClassLoader(final URL[] urls, final ClassLoader parent, final List<String> systemClasses) {
        super(urls, parent, systemClasses);
    }
    
    public ApplicationClassLoader(final String classpath, final ClassLoader parent, final List<String> systemClasses) throws MalformedURLException {
        super(classpath, parent, systemClasses);
    }
}
