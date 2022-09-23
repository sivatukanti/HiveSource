// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import javax.xml.parsers.DocumentBuilderFactory;

public class DOMLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.newDocumentBuilder();
        }
        catch (Exception e) {
            DOMLeakPreventer.LOG.warn(e);
        }
    }
}
