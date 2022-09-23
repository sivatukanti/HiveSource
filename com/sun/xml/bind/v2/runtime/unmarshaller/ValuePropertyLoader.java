// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;

public class ValuePropertyLoader extends Loader
{
    private final TransducedAccessor xacc;
    
    public ValuePropertyLoader(final TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }
    
    @Override
    public void text(final UnmarshallingContext.State state, final CharSequence text) throws SAXException {
        try {
            this.xacc.parse(state.target, text);
        }
        catch (AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (RuntimeException e2) {
            if (state.prev != null) {
                if (!(state.prev.target instanceof JAXBElement)) {
                    Loader.handleParseConversionException(state, e2);
                }
            }
            else {
                Loader.handleParseConversionException(state, e2);
            }
        }
    }
}
