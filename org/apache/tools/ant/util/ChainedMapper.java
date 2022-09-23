// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;

public class ChainedMapper extends ContainerMapper
{
    public String[] mapFileName(final String sourceFileName) {
        final List inputs = new ArrayList();
        final List results = new ArrayList();
        results.add(sourceFileName);
        FileNameMapper mapper = null;
        final Iterator mIter = this.getMappers().iterator();
        while (mIter.hasNext()) {
            mapper = mIter.next();
            if (mapper != null) {
                inputs.clear();
                inputs.addAll(results);
                results.clear();
                final Iterator it = inputs.iterator();
                while (it.hasNext()) {
                    final String[] mapped = mapper.mapFileName(it.next());
                    if (mapped != null) {
                        results.addAll(Arrays.asList(mapped));
                    }
                }
            }
        }
        return (String[])((results.size() == 0) ? null : ((String[])results.toArray(new String[results.size()])));
    }
}
