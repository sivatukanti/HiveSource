// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Iterator;
import java.util.Map;
import java.io.IOException;

class LazyField
{
    private final MessageLite defaultInstance;
    private final ExtensionRegistryLite extensionRegistry;
    private ByteString bytes;
    private volatile MessageLite value;
    private volatile boolean isDirty;
    
    public LazyField(final MessageLite defaultInstance, final ExtensionRegistryLite extensionRegistry, final ByteString bytes) {
        this.isDirty = false;
        this.defaultInstance = defaultInstance;
        this.extensionRegistry = extensionRegistry;
        this.bytes = bytes;
    }
    
    public MessageLite getValue() {
        this.ensureInitialized();
        return this.value;
    }
    
    public MessageLite setValue(final MessageLite value) {
        final MessageLite originalValue = this.value;
        this.value = value;
        this.bytes = null;
        this.isDirty = true;
        return originalValue;
    }
    
    public int getSerializedSize() {
        if (this.isDirty) {
            return this.value.getSerializedSize();
        }
        return this.bytes.size();
    }
    
    public ByteString toByteString() {
        if (!this.isDirty) {
            return this.bytes;
        }
        synchronized (this) {
            if (!this.isDirty) {
                return this.bytes;
            }
            this.bytes = this.value.toByteString();
            this.isDirty = false;
            return this.bytes;
        }
    }
    
    @Override
    public int hashCode() {
        this.ensureInitialized();
        return this.value.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        this.ensureInitialized();
        return this.value.equals(obj);
    }
    
    @Override
    public String toString() {
        this.ensureInitialized();
        return this.value.toString();
    }
    
    private void ensureInitialized() {
        if (this.value != null) {
            return;
        }
        synchronized (this) {
            if (this.value != null) {
                return;
            }
            try {
                if (this.bytes != null) {
                    this.value = (MessageLite)this.defaultInstance.getParserForType().parseFrom(this.bytes, this.extensionRegistry);
                }
            }
            catch (IOException ex) {}
        }
    }
    
    static class LazyEntry<K> implements Map.Entry<K, Object>
    {
        private Map.Entry<K, LazyField> entry;
        
        private LazyEntry(final Map.Entry<K, LazyField> entry) {
            this.entry = entry;
        }
        
        public K getKey() {
            return this.entry.getKey();
        }
        
        public Object getValue() {
            final LazyField field = this.entry.getValue();
            if (field == null) {
                return null;
            }
            return field.getValue();
        }
        
        public LazyField getField() {
            return this.entry.getValue();
        }
        
        public Object setValue(final Object value) {
            if (!(value instanceof MessageLite)) {
                throw new IllegalArgumentException("LazyField now only used for MessageSet, and the value of MessageSet must be an instance of MessageLite");
            }
            return this.entry.getValue().setValue((MessageLite)value);
        }
    }
    
    static class LazyIterator<K> implements Iterator<Map.Entry<K, Object>>
    {
        private Iterator<Map.Entry<K, Object>> iterator;
        
        public LazyIterator(final Iterator<Map.Entry<K, Object>> iterator) {
            this.iterator = iterator;
        }
        
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        public Map.Entry<K, Object> next() {
            final Map.Entry<K, ?> entry = this.iterator.next();
            if (entry.getValue() instanceof LazyField) {
                return new LazyEntry<K>((Map.Entry)entry);
            }
            return (Map.Entry<K, Object>)entry;
        }
        
        public void remove() {
            this.iterator.remove();
        }
    }
}
