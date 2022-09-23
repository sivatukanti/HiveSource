// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.IOException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.InputStream;

public interface InputStreamSupport
{
    void read(final InputStream p0) throws ConfigurationException, IOException;
}
