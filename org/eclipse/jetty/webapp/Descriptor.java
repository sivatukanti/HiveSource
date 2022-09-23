// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.xml.XmlParser;
import org.eclipse.jetty.util.resource.Resource;

public abstract class Descriptor
{
    protected Resource _xml;
    protected XmlParser.Node _root;
    protected XmlParser _parser;
    protected boolean _validating;
    
    public Descriptor(final Resource xml) {
        this._xml = xml;
    }
    
    public abstract void ensureParser() throws ClassNotFoundException;
    
    public void setValidating(final boolean validating) {
        this._validating = validating;
    }
    
    public void parse() throws Exception {
        if (this._parser == null) {
            this.ensureParser();
        }
        if (this._root == null) {
            try {
                this._root = this._parser.parse(this._xml.getInputStream());
            }
            finally {
                this._xml.close();
            }
        }
    }
    
    public Resource getResource() {
        return this._xml;
    }
    
    public XmlParser.Node getRoot() {
        return this._root;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this._xml + ")";
    }
}
