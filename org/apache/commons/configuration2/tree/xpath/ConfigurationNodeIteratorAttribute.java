// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.QName;
import java.util.List;

class ConfigurationNodeIteratorAttribute<T> extends ConfigurationNodeIteratorBase<T>
{
    private static final String WILDCARD = "*";
    private ConfigurationNodePointer<T> parentPointer;
    private List<String> attributeNames;
    
    public ConfigurationNodeIteratorAttribute(final ConfigurationNodePointer<T> parent, final QName name) {
        super(parent, false);
        this.parentPointer = parent;
        this.attributeNames = this.createAttributeDataList(parent, name);
    }
    
    @Override
    protected NodePointer createNodePointer(final int position) {
        return new ConfigurationAttributePointer<Object>(this.parentPointer, this.attributeNames.get(position));
    }
    
    @Override
    protected int size() {
        return this.attributeNames.size();
    }
    
    private List<String> createAttributeDataList(final ConfigurationNodePointer<T> parent, final QName name) {
        final List<String> result = new ArrayList<String>();
        if (!"*".equals(name.getName())) {
            this.addAttributeData(parent, result, ConfigurationNodeIteratorBase.qualifiedName(name));
        }
        else {
            final Set<String> names = new LinkedHashSet<String>(parent.getNodeHandler().getAttributes(parent.getConfigurationNode()));
            final String prefix = (name.getPrefix() != null) ? ConfigurationNodeIteratorBase.prefixName(name.getPrefix(), null) : null;
            for (final String n : names) {
                if (prefix == null || StringUtils.startsWith(n, prefix)) {
                    this.addAttributeData(parent, result, n);
                }
            }
        }
        return result;
    }
    
    private void addAttributeData(final ConfigurationNodePointer<T> parent, final List<String> result, final String name) {
        if (parent.getNodeHandler().getAttributeValue(parent.getConfigurationNode(), name) != null) {
            result.add(name);
        }
    }
}
