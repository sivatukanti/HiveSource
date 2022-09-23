// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class Accessor
{
    protected Field field;
    protected Method setter;
    protected Method getter;
    protected int index;
    protected Class<?> type;
    protected Type genericType;
    protected String fieldName;
    
    public int getIndex() {
        return this.index;
    }
    
    public boolean isPublic() {
        return this.setter == null;
    }
    
    public boolean isEnum() {
        return this.type.isEnum();
    }
    
    public String getName() {
        return this.fieldName;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public Type getGenericType() {
        return this.genericType;
    }
    
    public boolean isUsable() {
        return this.field != null || this.getter != null || this.setter != null;
    }
    
    public boolean isReadable() {
        return this.field != null || this.getter != null;
    }
    
    public boolean isWritable() {
        return this.field != null || this.getter != null;
    }
    
    public Accessor(final Class<?> c, final Field field, final FieldFilter filter) {
        this.fieldName = field.getName();
        final int m = field.getModifiers();
        if ((m & 0x88) > 0) {
            return;
        }
        if ((m & 0x1) > 0) {
            this.field = field;
        }
        String name = ASMUtil.getSetterName(field.getName());
        try {
            this.setter = c.getDeclaredMethod(name, field.getType());
        }
        catch (Exception ex) {}
        final boolean isBool = field.getType().equals(Boolean.TYPE);
        if (isBool) {
            name = ASMUtil.getIsName(field.getName());
        }
        else {
            name = ASMUtil.getGetterName(field.getName());
        }
        try {
            this.getter = c.getDeclaredMethod(name, (Class<?>[])new Class[0]);
        }
        catch (Exception ex2) {}
        if (this.getter == null && isBool) {
            try {
                this.getter = c.getDeclaredMethod(ASMUtil.getGetterName(field.getName()), (Class<?>[])new Class[0]);
            }
            catch (Exception ex3) {}
        }
        if (this.field == null && this.getter == null && this.setter == null) {
            return;
        }
        if (this.getter != null && !filter.canUse(field, this.getter)) {
            this.getter = null;
        }
        if (this.setter != null && !filter.canUse(field, this.setter)) {
            this.setter = null;
        }
        if (this.getter == null && this.setter == null && this.field == null) {
            return;
        }
        this.type = field.getType();
        this.genericType = field.getGenericType();
    }
}
