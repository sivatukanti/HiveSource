// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.reflect.opt;

import org.xml.sax.SAXException;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.DatatypeConverterImpl;
import com.sun.xml.bind.v2.runtime.reflect.DefaultTransducedAccessor;

public final class TransducedAccessor_field_Float extends DefaultTransducedAccessor
{
    @Override
    public String print(final Object o) {
        return DatatypeConverterImpl._printFloat(((Bean)o).f_float);
    }
    
    @Override
    public void parse(final Object o, final CharSequence lexical) {
        ((Bean)o).f_float = DatatypeConverterImpl._parseFloat(lexical);
    }
    
    @Override
    public boolean hasValue(final Object o) {
        return true;
    }
}
