// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import java.util.Collections;
import java.util.Iterator;
import org.eclipse.jetty.xml.XmlParser;
import java.util.ArrayList;
import org.eclipse.jetty.util.resource.Resource;
import java.util.List;

public class FragmentDescriptor extends WebDescriptor
{
    public static final String NAMELESS = "@@-NAMELESS-@@";
    protected static int _counter;
    protected OtherType _otherType;
    protected List<String> _befores;
    protected List<String> _afters;
    protected String _name;
    
    public FragmentDescriptor(final Resource xml) throws Exception {
        super(xml);
        this._otherType = OtherType.None;
        this._befores = new ArrayList<String>();
        this._afters = new ArrayList<String>();
    }
    
    public String getName() {
        return this._name;
    }
    
    @Override
    public void parse() throws Exception {
        super.parse();
        this.processName();
    }
    
    public void processName() {
        final XmlParser.Node root = this.getRoot();
        final XmlParser.Node nameNode = root.get("name");
        this._name = "@@-NAMELESS-@@" + FragmentDescriptor._counter++;
        if (nameNode != null) {
            final String tmp = nameNode.toString(false, true);
            if (tmp != null && tmp.length() > 0) {
                this._name = tmp;
            }
        }
    }
    
    @Override
    public void processOrdering() {
        final XmlParser.Node root = this.getRoot();
        final XmlParser.Node ordering = root.get("ordering");
        if (ordering == null) {
            return;
        }
        this._isOrdered = true;
        this.processBefores(ordering);
        this.processAfters(ordering);
    }
    
    public void processBefores(final XmlParser.Node ordering) {
        final XmlParser.Node before = ordering.get("before");
        if (before == null) {
            return;
        }
        final Iterator<?> iter = before.iterator();
        XmlParser.Node node = null;
        while (iter.hasNext()) {
            final Object o = iter.next();
            if (!(o instanceof XmlParser.Node)) {
                continue;
            }
            node = (XmlParser.Node)o;
            if (node.getTag().equalsIgnoreCase("others")) {
                if (this._otherType != OtherType.None) {
                    throw new IllegalStateException("Duplicate <other> clause detected in " + this._xml.getURI());
                }
                this._otherType = OtherType.Before;
            }
            else {
                if (!node.getTag().equalsIgnoreCase("name")) {
                    continue;
                }
                this._befores.add(node.toString(false, true));
            }
        }
    }
    
    public void processAfters(final XmlParser.Node ordering) {
        final XmlParser.Node after = ordering.get("after");
        if (after == null) {
            return;
        }
        final Iterator<?> iter = after.iterator();
        XmlParser.Node node = null;
        while (iter.hasNext()) {
            final Object o = iter.next();
            if (!(o instanceof XmlParser.Node)) {
                continue;
            }
            node = (XmlParser.Node)o;
            if (node.getTag().equalsIgnoreCase("others")) {
                if (this._otherType != OtherType.None) {
                    throw new IllegalStateException("Duplicate <other> clause detected in " + this._xml.getURI());
                }
                this._otherType = OtherType.After;
            }
            else {
                if (!node.getTag().equalsIgnoreCase("name")) {
                    continue;
                }
                this._afters.add(node.toString(false, true));
            }
        }
    }
    
    public List<String> getBefores() {
        return Collections.unmodifiableList((List<? extends String>)this._befores);
    }
    
    public List<String> getAfters() {
        return Collections.unmodifiableList((List<? extends String>)this._afters);
    }
    
    public OtherType getOtherType() {
        return this._otherType;
    }
    
    @Override
    public List<String> getOrdering() {
        return null;
    }
    
    static {
        FragmentDescriptor._counter = 0;
    }
    
    public enum OtherType
    {
        None, 
        Before, 
        After;
    }
}
