// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.runtime.unmarshaller.Receiver;
import com.sun.xml.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.bind.v2.runtime.unmarshaller.ValuePropertyLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.bind.v2.util.QNameMap;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.bind.api.AccessorException;
import org.xml.sax.SAXException;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;

public final class ValueProperty<BeanT> extends PropertyImpl<BeanT>
{
    private final TransducedAccessor<BeanT> xacc;
    private final Accessor<BeanT, ?> acc;
    
    public ValueProperty(final JAXBContextImpl context, final RuntimeValuePropertyInfo prop) {
        super(context, prop);
        this.xacc = TransducedAccessor.get(context, prop);
        this.acc = (Accessor<BeanT, ?>)prop.getAccessor();
    }
    
    @Override
    public final void serializeBody(final BeanT o, final XMLSerializer w, final Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        if (this.xacc.hasValue(o)) {
            this.xacc.writeText(w, o, this.fieldName);
        }
    }
    
    @Override
    public void serializeURIs(final BeanT o, final XMLSerializer w) throws SAXException, AccessorException {
        this.xacc.declareNamespace(o, w);
    }
    
    @Override
    public boolean hasSerializeURIAction() {
        return this.xacc.useNamespace();
    }
    
    public void buildChildElementUnmarshallers(final UnmarshallerChain chainElem, final QNameMap<ChildLoader> handlers) {
        handlers.put(StructureLoaderBuilder.TEXT_HANDLER, new ChildLoader(new ValuePropertyLoader(this.xacc), null));
    }
    
    public PropertyKind getKind() {
        return PropertyKind.VALUE;
    }
    
    public void reset(final BeanT o) throws AccessorException {
        this.acc.set(o, null);
    }
    
    public String getIdValue(final BeanT bean) throws AccessorException, SAXException {
        return this.xacc.print(bean).toString();
    }
}
