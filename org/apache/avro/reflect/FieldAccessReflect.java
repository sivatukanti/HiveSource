// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import org.apache.avro.io.Encoder;
import org.apache.avro.io.Decoder;
import java.io.IOException;
import java.lang.annotation.Annotation;
import org.apache.avro.AvroRuntimeException;
import java.lang.reflect.Field;

class FieldAccessReflect extends FieldAccess
{
    @Override
    protected FieldAccessor getAccessor(final Field field) {
        final AvroEncode enc = field.getAnnotation(AvroEncode.class);
        if (enc != null) {
            try {
                return new ReflectionBasesAccessorCustomEncoded(field, (CustomEncoding<?>)enc.using().newInstance());
            }
            catch (Exception e) {
                throw new AvroRuntimeException("Could not instantiate custom Encoding");
            }
        }
        return new ReflectionBasedAccessor(field);
    }
    
    private class ReflectionBasedAccessor extends FieldAccessor
    {
        protected final Field field;
        private boolean isStringable;
        private boolean isCustomEncoded;
        
        public ReflectionBasedAccessor(final Field field) {
            (this.field = field).setAccessible(true);
            this.isStringable = field.isAnnotationPresent(Stringable.class);
            this.isCustomEncoded = field.isAnnotationPresent(AvroEncode.class);
        }
        
        @Override
        public String toString() {
            return this.field.getName();
        }
        
        public Object get(final Object object) throws IllegalAccessException {
            return this.field.get(object);
        }
        
        public void set(final Object object, final Object value) throws IllegalAccessException, IOException {
            this.field.set(object, value);
        }
        
        @Override
        protected Field getField() {
            return this.field;
        }
        
        @Override
        protected boolean isStringable() {
            return this.isStringable;
        }
        
        @Override
        protected boolean isCustomEncoded() {
            return this.isCustomEncoded;
        }
    }
    
    private final class ReflectionBasesAccessorCustomEncoded extends ReflectionBasedAccessor
    {
        private CustomEncoding<?> encoding;
        
        public ReflectionBasesAccessorCustomEncoded(final Field f, final CustomEncoding<?> encoding) {
            super(f);
            this.encoding = encoding;
        }
        
        @Override
        protected void read(final Object object, final Decoder in) throws IOException {
            try {
                this.field.set(object, this.encoding.read(in));
            }
            catch (IllegalAccessException e) {
                throw new AvroRuntimeException(e);
            }
        }
        
        @Override
        protected void write(final Object object, final Encoder out) throws IOException {
            try {
                this.encoding.write(this.field.get(object), out);
            }
            catch (IllegalAccessException e) {
                throw new AvroRuntimeException(e);
            }
        }
        
        @Override
        protected boolean isCustomEncoded() {
            return true;
        }
        
        @Override
        protected boolean supportsIO() {
            return true;
        }
    }
}
