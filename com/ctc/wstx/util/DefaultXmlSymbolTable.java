// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

public final class DefaultXmlSymbolTable
{
    static final SymbolTable sInstance;
    static final String mNsPrefixXml;
    static final String mNsPrefixXmlns;
    
    public static SymbolTable getInstance() {
        return DefaultXmlSymbolTable.sInstance.makeChild();
    }
    
    public static String getXmlSymbol() {
        return DefaultXmlSymbolTable.mNsPrefixXml;
    }
    
    public static String getXmlnsSymbol() {
        return DefaultXmlSymbolTable.mNsPrefixXmlns;
    }
    
    static {
        sInstance = new SymbolTable(true, 128);
        mNsPrefixXml = DefaultXmlSymbolTable.sInstance.findSymbol("xml");
        mNsPrefixXmlns = DefaultXmlSymbolTable.sInstance.findSymbol("xmlns");
        DefaultXmlSymbolTable.sInstance.findSymbol("id");
        DefaultXmlSymbolTable.sInstance.findSymbol("name");
        DefaultXmlSymbolTable.sInstance.findSymbol("xsd");
        DefaultXmlSymbolTable.sInstance.findSymbol("xsi");
        DefaultXmlSymbolTable.sInstance.findSymbol("type");
        DefaultXmlSymbolTable.sInstance.findSymbol("soap");
        DefaultXmlSymbolTable.sInstance.findSymbol("SOAP-ENC");
        DefaultXmlSymbolTable.sInstance.findSymbol("SOAP-ENV");
        DefaultXmlSymbolTable.sInstance.findSymbol("Body");
        DefaultXmlSymbolTable.sInstance.findSymbol("Envelope");
    }
}
