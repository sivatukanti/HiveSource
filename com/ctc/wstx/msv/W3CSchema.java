// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.msv;

import javax.xml.stream.XMLStreamException;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.verifier.regexp.xmlschema.XSREDocDecl;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.ValidationContext;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import org.codehaus.stax2.validation.XMLValidationSchema;

public class W3CSchema implements XMLValidationSchema
{
    protected final XMLSchemaGrammar mGrammar;
    
    public W3CSchema(final XMLSchemaGrammar grammar) {
        this.mGrammar = grammar;
    }
    
    @Override
    public String getSchemaType() {
        return "http://www.w3.org/2001/XMLSchema";
    }
    
    @Override
    public XMLValidator createValidator(final ValidationContext ctxt) throws XMLStreamException {
        final XSREDocDecl dd = new XSREDocDecl(this.mGrammar);
        return new GenericMsvValidator(this, ctxt, (DocumentDeclaration)dd);
    }
}
