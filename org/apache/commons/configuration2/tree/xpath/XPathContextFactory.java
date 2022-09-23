// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.tree.xpath;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.configuration2.tree.NodeHandler;

class XPathContextFactory
{
    public <T> JXPathContext createContext(final T root, final NodeHandler<T> handler) {
        final JXPathContext context = JXPathContext.newContext(ConfigurationNodePointerFactory.wrapNode(root, handler));
        context.setLenient(true);
        return context;
    }
}
