// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.mappers;

import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import java.util.Vector;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import java.io.StringReader;
import org.apache.tools.ant.UnsupportedAttributeException;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.types.FilterChain;

public class FilterMapper extends FilterChain implements FileNameMapper
{
    private static final int BUFFER_SIZE = 8192;
    
    public void setFrom(final String from) {
        throw new UnsupportedAttributeException("filtermapper doesn't support the \"from\" attribute.", "from");
    }
    
    public void setTo(final String to) {
        throw new UnsupportedAttributeException("filtermapper doesn't support the \"to\" attribute.", "to");
    }
    
    public String[] mapFileName(final String sourceFileName) {
        try {
            final Reader stringReader = new StringReader(sourceFileName);
            final ChainReaderHelper helper = new ChainReaderHelper();
            helper.setBufferSize(8192);
            helper.setPrimaryReader(stringReader);
            helper.setProject(this.getProject());
            final Vector<FilterChain> filterChains = new Vector<FilterChain>();
            filterChains.add(this);
            helper.setFilterChains(filterChains);
            final String result = FileUtils.safeReadFully(helper.getAssembledReader());
            if (result.length() == 0) {
                return null;
            }
            return new String[] { result };
        }
        catch (BuildException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new BuildException(ex2);
        }
    }
}
