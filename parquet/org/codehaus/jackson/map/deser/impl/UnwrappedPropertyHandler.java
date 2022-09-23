// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.impl;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.util.TokenBuffer;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.deser.SettableBeanProperty;
import java.util.ArrayList;

public class UnwrappedPropertyHandler
{
    protected final ArrayList<SettableBeanProperty> _properties;
    
    public UnwrappedPropertyHandler() {
        this._properties = new ArrayList<SettableBeanProperty>();
    }
    
    public void addProperty(final SettableBeanProperty property) {
        this._properties.add(property);
    }
    
    public Object processUnwrapped(final JsonParser originalParser, final DeserializationContext ctxt, final Object bean, final TokenBuffer buffered) throws IOException, JsonProcessingException {
        for (int i = 0, len = this._properties.size(); i < len; ++i) {
            final SettableBeanProperty prop = this._properties.get(i);
            final JsonParser jp = buffered.asParser();
            jp.nextToken();
            prop.deserializeAndSet(jp, ctxt, bean);
        }
        return bean;
    }
}
