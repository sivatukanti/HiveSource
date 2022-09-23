// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.query;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class StatementNewObjectMapping
{
    Class cls;
    Map<Integer, Object> ctrArgMappings;
    
    public StatementNewObjectMapping(final Class cls) {
        this.cls = null;
        this.ctrArgMappings = null;
        this.cls = cls;
    }
    
    public Class getObjectClass() {
        return this.cls;
    }
    
    public Object getConstructorArgMapping(final int position) {
        if (this.ctrArgMappings == null) {
            return null;
        }
        return this.ctrArgMappings.get(position);
    }
    
    public void addConstructorArgMapping(final int ctrPos, final Object argMapping) {
        if (this.ctrArgMappings == null) {
            this.ctrArgMappings = new HashMap<Integer, Object>();
        }
        this.ctrArgMappings.put(ctrPos, argMapping);
    }
    
    public boolean isEmpty() {
        return this.getNumberOfConstructorArgMappings() == 0;
    }
    
    public int getNumberOfConstructorArgMappings() {
        return (this.ctrArgMappings != null) ? this.ctrArgMappings.size() : 0;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("StatementNewObject: " + this.cls.getName() + "(");
        if (this.ctrArgMappings != null) {
            final Iterator<Integer> keyIter = this.ctrArgMappings.keySet().iterator();
            while (keyIter.hasNext()) {
                final Integer position = keyIter.next();
                str.append(this.ctrArgMappings.get(position));
                if (keyIter.hasNext()) {
                    str.append(",");
                }
            }
        }
        str.append(")");
        return str.toString();
    }
}
