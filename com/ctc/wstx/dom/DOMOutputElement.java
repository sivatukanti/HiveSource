// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dom;

import javax.xml.namespace.NamespaceContext;
import com.ctc.wstx.util.BijectiveNsMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.ctc.wstx.sw.OutputElementBase;

public final class DOMOutputElement extends OutputElementBase
{
    private DOMOutputElement mParent;
    private final Node mRootNode;
    private Element mElement;
    private boolean mDefaultNsSet;
    
    private DOMOutputElement(final Node rootNode) {
        this.mRootNode = rootNode;
        this.mParent = null;
        this.mElement = null;
        this.mNsMapping = null;
        this.mNsMapShared = false;
        this.mDefaultNsURI = "";
        this.mRootNsContext = null;
        this.mDefaultNsSet = false;
    }
    
    private DOMOutputElement(final DOMOutputElement parent, final Element element, final BijectiveNsMap ns) {
        super(parent, ns);
        this.mRootNode = null;
        this.mParent = parent;
        this.mElement = element;
        this.mNsMapping = ns;
        this.mNsMapShared = (ns != null);
        this.mDefaultNsURI = parent.mDefaultNsURI;
        this.mRootNsContext = parent.mRootNsContext;
        this.mDefaultNsSet = false;
    }
    
    private void relink(final DOMOutputElement parent, final Element element) {
        super.relink(parent);
        (this.mParent = parent).appendNode(this.mElement = element);
        this.mDefaultNsSet = false;
    }
    
    public static DOMOutputElement createRoot(final Node rootNode) {
        return new DOMOutputElement(rootNode);
    }
    
    protected DOMOutputElement createAndAttachChild(final Element element) {
        if (this.mRootNode != null) {
            this.mRootNode.appendChild(element);
        }
        else {
            this.mElement.appendChild(element);
        }
        return this.createChild(element);
    }
    
    protected DOMOutputElement createChild(final Element element) {
        return new DOMOutputElement(this, element, this.mNsMapping);
    }
    
    protected DOMOutputElement reuseAsChild(final DOMOutputElement parent, final Element element) {
        final DOMOutputElement poolHead = this.mParent;
        this.relink(parent, element);
        return poolHead;
    }
    
    protected void addToPool(final DOMOutputElement poolHead) {
        this.mParent = poolHead;
    }
    
    public DOMOutputElement getParent() {
        return this.mParent;
    }
    
    @Override
    public boolean isRoot() {
        return this.mParent == null;
    }
    
    @Override
    public String getNameDesc() {
        if (this.mElement != null) {
            return this.mElement.getLocalName();
        }
        return "#error";
    }
    
    @Override
    public void setDefaultNsUri(final String uri) {
        this.mDefaultNsURI = uri;
        this.mDefaultNsSet = true;
    }
    
    @Override
    protected void setRootNsContext(final NamespaceContext ctxt) {
        this.mRootNsContext = ctxt;
        if (!this.mDefaultNsSet) {
            final String defURI = ctxt.getNamespaceURI("");
            if (defURI != null && defURI.length() > 0) {
                this.mDefaultNsURI = defURI;
            }
        }
    }
    
    protected void appendNode(final Node n) {
        if (this.mRootNode != null) {
            this.mRootNode.appendChild(n);
        }
        else {
            this.mElement.appendChild(n);
        }
    }
    
    protected void addAttribute(final String pname, final String value) {
        this.mElement.setAttribute(pname, value);
    }
    
    protected void addAttribute(final String uri, final String qname, final String value) {
        this.mElement.setAttributeNS(uri, qname, value);
    }
    
    public void appendChild(final Node n) {
        this.mElement.appendChild(n);
    }
}
