// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.unmarshaller;

import java.util.Collection;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.bind.v2.runtime.ClassBeanInfoImpl;
import javax.xml.namespace.NamespaceContext;
import com.sun.xml.bind.DatatypeConverterImpl;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;

public final class LeafPropertyXsiLoader extends Loader
{
    private final Loader defaultLoader;
    private final TransducedAccessor xacc;
    private final Accessor acc;
    
    public LeafPropertyXsiLoader(final Loader defaultLoader, final TransducedAccessor xacc, final Accessor acc) {
        this.defaultLoader = defaultLoader;
        this.expectText = true;
        this.xacc = xacc;
        this.acc = acc;
    }
    
    @Override
    public void startElement(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        (state.loader = this.selectLoader(state, ea)).startElement(state, ea);
    }
    
    protected Loader selectLoader(final UnmarshallingContext.State state, final TagName ea) throws SAXException {
        final UnmarshallingContext context = state.getContext();
        JaxBeanInfo beanInfo = null;
        final Attributes atts = ea.atts;
        final int idx = atts.getIndex("http://www.w3.org/2001/XMLSchema-instance", "type");
        if (idx < 0) {
            return this.defaultLoader;
        }
        final String value = atts.getValue(idx);
        final QName type = DatatypeConverterImpl._parseQName(value, context);
        if (type == null) {
            return this.defaultLoader;
        }
        beanInfo = context.getJAXBContext().getGlobalType(type);
        if (beanInfo == null) {
            return this.defaultLoader;
        }
        ClassBeanInfoImpl cbii;
        try {
            cbii = (ClassBeanInfoImpl)beanInfo;
        }
        catch (ClassCastException cce) {
            return this.defaultLoader;
        }
        return new LeafPropertyLoader(new TransducedAccessor.CompositeTransducedAccessorImpl(state.getContext().getJAXBContext(), cbii.getTransducer(), this.acc));
    }
    
    @Override
    public Collection<QName> getExpectedChildElements() {
        return this.defaultLoader.getExpectedChildElements();
    }
    
    @Override
    public Collection<QName> getExpectedAttributes() {
        return this.defaultLoader.getExpectedAttributes();
    }
}
