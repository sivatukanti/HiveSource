// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import org.apache.avro.AvroRuntimeException;

class ReflectionUtil
{
    private static FieldAccess fieldAccess;
    
    private ReflectionUtil() {
    }
    
    static void resetFieldAccess() {
        FieldAccess access = null;
        try {
            if (null == System.getProperty("avro.disable.unsafe")) {
                final FieldAccess unsafeAccess = load("org.apache.avro.reflect.FieldAccessUnsafe", FieldAccess.class);
                if (validate(unsafeAccess)) {
                    access = unsafeAccess;
                }
            }
        }
        catch (Throwable t) {}
        if (access == null) {
            try {
                final FieldAccess reflectAccess = load("org.apache.avro.reflect.FieldAccessReflect", FieldAccess.class);
                if (validate(reflectAccess)) {
                    access = reflectAccess;
                }
            }
            catch (Throwable oops) {
                throw new AvroRuntimeException("Unable to load a functional FieldAccess class!");
            }
        }
        ReflectionUtil.fieldAccess = access;
    }
    
    private static <T> T load(final String name, final Class<T> type) throws Exception {
        return (T)ReflectionUtil.class.getClassLoader().loadClass(name).asSubclass(type).newInstance();
    }
    
    public static FieldAccess getFieldAccess() {
        return ReflectionUtil.fieldAccess;
    }
    
    private static boolean validate(final FieldAccess access) throws Exception {
        return new AccessorTestClass().validate(access);
    }
    
    static {
        resetFieldAccess();
    }
    
    private static final class AccessorTestClass
    {
        private boolean b;
        protected byte by;
        public char c;
        short s;
        int i;
        long l;
        float f;
        double d;
        Object o;
        Integer i2;
        
        private AccessorTestClass() {
            this.b = true;
            this.by = 15;
            this.c = 'c';
            this.s = 123;
            this.i = 999;
            this.l = 12345L;
            this.f = 2.2f;
            this.d = 4.4;
            this.o = "foo";
            this.i2 = 555;
        }
        
        private boolean validate(final FieldAccess access) throws Exception {
            boolean valid = true;
            valid &= this.validField(access, "b", this.b, false);
            valid &= this.validField(access, "by", this.by, -81);
            valid &= this.validField(access, "c", this.c, 'C');
            valid &= this.validField(access, "s", this.s, 321);
            valid &= this.validField(access, "i", this.i, 111);
            valid &= this.validField(access, "l", this.l, 54321L);
            valid &= this.validField(access, "f", this.f, 0.2f);
            valid &= this.validField(access, "d", this.d, 0.4);
            valid &= this.validField(access, "o", this.o, new Object());
            valid &= this.validField(access, "i2", this.i2, -555);
            return valid;
        }
        
        private boolean validField(final FieldAccess access, final String name, final Object original, final Object toSet) throws Exception {
            boolean valid = true;
            final FieldAccessor a = this.accessor(access, name);
            valid &= original.equals(a.get(this));
            a.set(this, toSet);
            valid &= !original.equals(a.get(this));
            return valid;
        }
        
        private FieldAccessor accessor(final FieldAccess access, final String name) throws Exception {
            return access.getAccessor(this.getClass().getDeclaredField(name));
        }
    }
}
