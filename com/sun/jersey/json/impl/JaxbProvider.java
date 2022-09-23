// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

public interface JaxbProvider
{
    String getJaxbContextClassName();
    
    Class<? extends JaxbXmlDocumentStructure> getDocumentStructureClass();
}
