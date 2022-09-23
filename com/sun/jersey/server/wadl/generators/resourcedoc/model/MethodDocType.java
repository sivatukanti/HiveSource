// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "methodDoc", propOrder = {})
public class MethodDocType
{
    private String methodName;
    protected String commentText;
    private String returnDoc;
    private String returnTypeExample;
    private RequestDocType requestDoc;
    private ResponseDocType responseDoc;
    @XmlElementWrapper(name = "paramDocs")
    protected List<ParamDocType> paramDoc;
    @XmlAnyElement(lax = true)
    private List<Object> any;
    
    public String getCommentText() {
        return this.commentText;
    }
    
    public void setCommentText(final String value) {
        this.commentText = value;
    }
    
    public List<ParamDocType> getParamDocs() {
        if (this.paramDoc == null) {
            this.paramDoc = new ArrayList<ParamDocType>();
        }
        return this.paramDoc;
    }
    
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
    
    public String getReturnDoc() {
        return this.returnDoc;
    }
    
    public void setReturnDoc(final String returnDoc) {
        this.returnDoc = returnDoc;
    }
    
    public String getReturnTypeExample() {
        return this.returnTypeExample;
    }
    
    public void setReturnTypeExample(final String returnTypeExample) {
        this.returnTypeExample = returnTypeExample;
    }
    
    public RequestDocType getRequestDoc() {
        return this.requestDoc;
    }
    
    public void setRequestDoc(final RequestDocType requestDoc) {
        this.requestDoc = requestDoc;
    }
    
    public ResponseDocType getResponseDoc() {
        return this.responseDoc;
    }
    
    public void setResponseDoc(final ResponseDocType responseDoc) {
        this.responseDoc = responseDoc;
    }
}
