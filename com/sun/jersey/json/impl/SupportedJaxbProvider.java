// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

public enum SupportedJaxbProvider implements JaxbProvider
{
    JAXB_RI("com.sun.xml.bind.v2.runtime.JAXBContextImpl", (Class<? extends DefaultJaxbXmlDocumentStructure>)JaxbRiXmlStructure.class), 
    MOXY("org.eclipse.persistence.jaxb.JAXBContext", (Class<? extends DefaultJaxbXmlDocumentStructure>)MoxyXmlStructure.class), 
    JAXB_JDK("com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl", (Class<? extends DefaultJaxbXmlDocumentStructure>)JaxbJdkXmlStructure.class);
    
    private final String jaxbContextClassName;
    private final Class<? extends DefaultJaxbXmlDocumentStructure> documentStructureClass;
    
    private SupportedJaxbProvider(final String jaxbContextClassName, final Class<? extends DefaultJaxbXmlDocumentStructure> documentStructureClass) {
        this.jaxbContextClassName = jaxbContextClassName;
        this.documentStructureClass = documentStructureClass;
    }
    
    @Override
    public Class<? extends DefaultJaxbXmlDocumentStructure> getDocumentStructureClass() {
        return this.documentStructureClass;
    }
    
    @Override
    public String getJaxbContextClassName() {
        return this.jaxbContextClassName;
    }
}
