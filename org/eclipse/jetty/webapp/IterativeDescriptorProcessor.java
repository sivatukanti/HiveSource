// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.util.Iterator;
import org.eclipse.jetty.xml.XmlParser;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class IterativeDescriptorProcessor implements DescriptorProcessor
{
    public static final Class<?>[] __signature;
    protected Map<String, Method> _visitors;
    
    public IterativeDescriptorProcessor() {
        this._visitors = new HashMap<String, Method>();
    }
    
    public abstract void start(final WebAppContext p0, final Descriptor p1);
    
    public abstract void end(final WebAppContext p0, final Descriptor p1);
    
    public void registerVisitor(final String nodeName, final Method m) {
        this._visitors.put(nodeName, m);
    }
    
    @Override
    public void process(final WebAppContext context, final Descriptor descriptor) throws Exception {
        if (descriptor == null) {
            return;
        }
        this.start(context, descriptor);
        final XmlParser.Node root = descriptor.getRoot();
        final Iterator<?> iter = root.iterator();
        XmlParser.Node node = null;
        while (iter.hasNext()) {
            final Object o = iter.next();
            if (!(o instanceof XmlParser.Node)) {
                continue;
            }
            node = (XmlParser.Node)o;
            this.visit(context, descriptor, node);
        }
        this.end(context, descriptor);
    }
    
    protected void visit(final WebAppContext context, final Descriptor descriptor, final XmlParser.Node node) throws Exception {
        final String name = node.getTag();
        final Method m = this._visitors.get(name);
        if (m != null) {
            m.invoke(this, context, descriptor, node);
        }
    }
    
    static {
        __signature = new Class[] { WebAppContext.class, Descriptor.class, XmlParser.Node.class };
    }
}
