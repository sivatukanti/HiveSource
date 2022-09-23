// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.msv;

import javax.xml.stream.XMLStreamException;
import com.sun.msv.verifier.DocumentDeclaration;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.ValidationContext;
import com.sun.msv.grammar.trex.TREXGrammar;
import org.codehaus.stax2.validation.XMLValidationSchema;

public class RelaxNGSchema implements XMLValidationSchema
{
    protected final TREXGrammar mGrammar;
    
    public RelaxNGSchema(final TREXGrammar grammar) {
        this.mGrammar = grammar;
    }
    
    @Override
    public String getSchemaType() {
        return "http://relaxng.org/ns/structure/0.9";
    }
    
    @Override
    public XMLValidator createValidator(final ValidationContext ctxt) throws XMLStreamException {
        final REDocumentDeclaration dd = new REDocumentDeclaration((Grammar)this.mGrammar);
        return new GenericMsvValidator(this, ctxt, (DocumentDeclaration)dd);
    }
}
