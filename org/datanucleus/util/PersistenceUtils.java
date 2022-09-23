// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import org.datanucleus.ClassConstants;
import java.io.InputStream;
import org.datanucleus.PersistenceConfiguration;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.datanucleus.exceptions.NucleusUserException;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;

public class PersistenceUtils
{
    protected static final Localiser LOCALISER;
    
    public static synchronized Properties setPropertiesUsingFile(final String filename) {
        if (filename == null) {
            return null;
        }
        final Properties props = new Properties();
        final File file = new File(filename);
        if (file.exists()) {
            try {
                final InputStream is = new FileInputStream(file);
                props.load(is);
                is.close();
                return props;
            }
            catch (FileNotFoundException e) {
                throw new NucleusUserException(PersistenceUtils.LOCALISER.msg("008014", filename), e).setFatal();
            }
            catch (IOException e2) {
                throw new NucleusUserException(PersistenceUtils.LOCALISER.msg("008014", filename), e2).setFatal();
            }
        }
        try {
            final InputStream is = PersistenceConfiguration.class.getClassLoader().getResourceAsStream(filename);
            props.load(is);
            is.close();
        }
        catch (Exception e3) {
            throw new NucleusUserException(PersistenceUtils.LOCALISER.msg("008014", filename), e3).setFatal();
        }
        return props;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
