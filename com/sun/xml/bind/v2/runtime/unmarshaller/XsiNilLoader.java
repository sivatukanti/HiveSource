// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import javax.xml.namespace.QName;
import java.util.Collection;
import org.xml.sax.SAXException;
import javax.xml.bind.JAXBElement;
import com.sun.xml.bind.DatatypeConverterImpl;

public class XsiNilLoader extends ProxyLoader
{
    private final Loader defaultLoader;
    
    public XsiNilLoader(final Loader defaultLoader) {
        this.defaultLoader = defaultLoader;
        assert defaultLoader != null;
    }
    
    @Override
    protected Loader selectLoader(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final int idx = ea.atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "nil");
        if (idx != -1) {
            final Boolean b = DatatypeConverterImpl._parseBoolean(ea.atts.getValue(idx));
            if (b != null && b) {
                this.onNil(state);
                final boolean hasOtherAttributes = ea.atts.getLength() - 1 > 0;
                if (!hasOtherAttributes || !(state.prev.target instanceof JAXBElement)) {
                    return Discarder.INSTANCE;
                }
            }
        }
        return this.defaultLoader;
    }
    
    @Override
    public Collection<QName> getExpectedChildElements() {
        return this.defaultLoader.getExpectedChildElements();
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        return this.defaultLoader.getExpectedAttributes();
    }
    
    protected void onNil(final UnmarshallingContext.State state) throws SAXException {
    }
    
    public static final class Single extends XsiNilLoader
    {
        private final Accessor acc;
        
        public Single(final Loader l, final Accessor acc) {
            super(l);
            this.acc = acc;
        }
        
        @Override
        protected void onNil(final UnmarshallingContext.State state) throws SAXException {
            try {
                this.acc.set(state.prev.target, null);
                state.prev.nil = true;
            }
            catch (AccessorException e) {
                Loader.handleGenericException(e, true);
            }
        }
    }
    
    public static final class Array extends XsiNilLoader
    {
        public Array(final Loader core) {
            super(core);
        }
        
        @Override
        protected void onNil(final UnmarshallingContext.State state) {
            state.target = null;
        }
    }
}
