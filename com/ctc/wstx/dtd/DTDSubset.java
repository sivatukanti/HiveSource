// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import javax.xml.stream.events.NotationDeclaration;
import java.util.List;
import com.ctc.wstx.ent.EntityDecl;
import java.util.HashMap;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.ValidationContext;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.sr.InputProblemReporter;
import org.codehaus.stax2.validation.DTDValidationSchema;

public abstract class DTDSubset implements DTDValidationSchema
{
    protected DTDSubset() {
    }
    
    public abstract DTDSubset combineWithExternalSubset(final InputProblemReporter p0, final DTDSubset p1) throws XMLStreamException;
    
    @Override
    public abstract XMLValidator createValidator(final ValidationContext p0) throws XMLStreamException;
    
    @Override
    public String getSchemaType() {
        return "http://www.w3.org/XML/1998/namespace";
    }
    
    @Override
    public abstract int getEntityCount();
    
    @Override
    public abstract int getNotationCount();
    
    public abstract boolean isCachable();
    
    public abstract boolean isReusableWith(final DTDSubset p0);
    
    public abstract HashMap<String, EntityDecl> getGeneralEntityMap();
    
    public abstract List<EntityDecl> getGeneralEntityList();
    
    public abstract HashMap<String, EntityDecl> getParameterEntityMap();
    
    public abstract HashMap<String, NotationDeclaration> getNotationMap();
    
    public abstract List<NotationDeclaration> getNotationList();
    
    public abstract HashMap<PrefixedName, DTDElement> getElementMap();
}
