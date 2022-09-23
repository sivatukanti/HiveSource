// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Collection;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import org.apache.commons.configuration2.tree.DefaultConfigurationKey;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;

abstract class HierarchicalConfigurationConverter
{
    public void process(final Configuration config) {
        if (config != null) {
            final DefaultExpressionEngine exprEngine = DefaultExpressionEngine.INSTANCE;
            DefaultConfigurationKey keyLast;
            final DefaultConfigurationKey keyEmpty = keyLast = new DefaultConfigurationKey(exprEngine);
            final Set<String> keySet = new HashSet<String>();
            final Iterator<String> it = config.getKeys();
            while (it.hasNext()) {
                final String key = it.next();
                if (keySet.contains(key)) {
                    continue;
                }
                final DefaultConfigurationKey keyAct = new DefaultConfigurationKey(exprEngine, key);
                this.closeElements(keyLast, keyAct);
                final String elem = this.openElements(keyLast, keyAct, config, keySet);
                this.fireValue(elem, config.getProperty(key));
                keyLast = keyAct;
            }
            this.closeElements(keyLast, keyEmpty);
        }
    }
    
    protected abstract void elementStart(final String p0, final Object p1);
    
    protected abstract void elementEnd(final String p0);
    
    protected void closeElements(final DefaultConfigurationKey keyLast, final DefaultConfigurationKey keyAct) {
        final DefaultConfigurationKey keyDiff = keyAct.differenceKey(keyLast);
        final Iterator<String> it = this.reverseIterator(keyDiff);
        if (it.hasNext()) {
            it.next();
        }
        while (it.hasNext()) {
            this.elementEnd(it.next());
        }
    }
    
    protected Iterator<String> reverseIterator(final DefaultConfigurationKey key) {
        final List<String> list = new ArrayList<String>();
        final DefaultConfigurationKey.KeyIterator it = key.iterator();
        while (it.hasNext()) {
            list.add(it.nextKey());
        }
        Collections.reverse(list);
        return list.iterator();
    }
    
    protected String openElements(final DefaultConfigurationKey keyLast, final DefaultConfigurationKey keyAct, final Configuration config, final Set<String> keySet) {
        final DefaultConfigurationKey.KeyIterator it = keyLast.differenceKey(keyAct).iterator();
        final DefaultConfigurationKey k = keyLast.commonKey(keyAct);
        it.nextKey();
        while (it.hasNext()) {
            k.append(it.currentKey(true));
            this.elementStart(it.currentKey(true), config.getProperty(k.toString()));
            keySet.add(k.toString());
            it.nextKey();
        }
        return it.currentKey(true);
    }
    
    protected void fireValue(final String name, final Object value) {
        if (value instanceof Collection) {
            final Collection<?> valueCol = (Collection<?>)value;
            for (final Object v : valueCol) {
                this.fireValue(name, v);
            }
        }
        else {
            this.elementStart(name, value);
            this.elementEnd(name);
        }
    }
}
