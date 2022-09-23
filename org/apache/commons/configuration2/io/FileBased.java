// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.io.Writer;
import java.io.IOException;
import org.apache.commons.configuration2.ex.ConfigurationException;
import java.io.Reader;

public interface FileBased
{
    void read(final Reader p0) throws ConfigurationException, IOException;
    
    void write(final Writer p0) throws ConfigurationException, IOException;
}
