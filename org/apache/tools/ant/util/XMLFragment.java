// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.DynamicConfiguratorNS;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Document;
import org.apache.tools.ant.DynamicElementNS;
import org.apache.tools.ant.ProjectComponent;

public class XMLFragment extends ProjectComponent implements DynamicElementNS
{
    private Document doc;
    private DocumentFragment fragment;
    
    public XMLFragment() {
        this.doc = JAXPUtils.getDocumentBuilder().newDocument();
        this.fragment = this.doc.createDocumentFragment();
    }
    
    public DocumentFragment getFragment() {
        return this.fragment;
    }
    
    public void addText(final String s) {
        this.addText(this.fragment, s);
    }
    
    public Object createDynamicElement(final String uri, final String name, final String qName) {
        Element e = null;
        if (uri.equals("")) {
            e = this.doc.createElement(name);
        }
        else {
            e = this.doc.createElementNS(uri, qName);
        }
        this.fragment.appendChild(e);
        return new Child(e);
    }
    
    private void addText(final Node n, String s) {
        s = this.getProject().replaceProperties(s);
        if (s != null && !s.trim().equals("")) {
            final Text t = this.doc.createTextNode(s.trim());
            n.appendChild(t);
        }
    }
    
    public class Child implements DynamicConfiguratorNS
    {
        private Element e;
        
        Child(final Element e) {
            this.e = e;
        }
        
        public void addText(final String s) {
            XMLFragment.this.addText(this.e, s);
        }
        
        public void setDynamicAttribute(final String uri, final String name, final String qName, final String value) {
            if (uri.equals("")) {
                this.e.setAttribute(name, value);
            }
            else {
                this.e.setAttributeNS(uri, qName, value);
            }
        }
        
        public Object createDynamicElement(final String uri, final String name, final String qName) {
            Element e2 = null;
            if (uri.equals("")) {
                e2 = XMLFragment.this.doc.createElement(name);
            }
            else {
                e2 = XMLFragment.this.doc.createElementNS(uri, qName);
            }
            this.e.appendChild(e2);
            return new Child(e2);
        }
    }
}
