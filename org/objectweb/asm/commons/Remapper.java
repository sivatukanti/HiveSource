// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.Type;

public abstract class Remapper
{
    public String mapDesc(final String s) {
        final Type type = Type.getType(s);
        switch (type.getSort()) {
            case 9: {
                String str = this.mapDesc(type.getElementType().getDescriptor());
                for (int i = 0; i < type.getDimensions(); ++i) {
                    str = '[' + str;
                }
                return str;
            }
            case 10: {
                final String map = this.map(type.getInternalName());
                if (map != null) {
                    return 'L' + map + ';';
                }
                break;
            }
        }
        return s;
    }
    
    private Type mapType(final Type type) {
        switch (type.getSort()) {
            case 9: {
                String str = this.mapDesc(type.getElementType().getDescriptor());
                for (int i = 0; i < type.getDimensions(); ++i) {
                    str = '[' + str;
                }
                return Type.getType(str);
            }
            case 10: {
                final String map = this.map(type.getInternalName());
                if (map != null) {
                    return Type.getObjectType(map);
                }
                break;
            }
        }
        return type;
    }
    
    public String mapType(final String s) {
        if (s == null) {
            return null;
        }
        return this.mapType(Type.getObjectType(s)).getInternalName();
    }
    
    public String[] mapTypes(final String[] array) {
        String[] array2 = null;
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            final String map = this.map(s);
            if (map != null && array2 == null) {
                array2 = new String[array.length];
                if (i > 0) {
                    System.arraycopy(array, 0, array2, 0, i);
                }
                b = true;
            }
            if (b) {
                array2[i] = ((map == null) ? s : map);
            }
        }
        return b ? array2 : array;
    }
    
    public String mapMethodDesc(final String anObject) {
        if ("()V".equals(anObject)) {
            return anObject;
        }
        final Type[] argumentTypes = Type.getArgumentTypes(anObject);
        String string = "(";
        for (int i = 0; i < argumentTypes.length; ++i) {
            string += this.mapDesc(argumentTypes[i].getDescriptor());
        }
        final Type returnType = Type.getReturnType(anObject);
        if (returnType == Type.VOID_TYPE) {
            return string + ")V";
        }
        return string + ')' + this.mapDesc(returnType.getDescriptor());
    }
    
    public Object mapValue(final Object o) {
        return (o instanceof Type) ? this.mapType((Type)o) : o;
    }
    
    public String mapSignature(final String s, final boolean b) {
        if (s == null) {
            return null;
        }
        final SignatureReader signatureReader = new SignatureReader(s);
        final SignatureWriter signatureWriter = new SignatureWriter();
        final SignatureVisitor remappingSignatureAdapter = this.createRemappingSignatureAdapter(signatureWriter);
        if (b) {
            signatureReader.acceptType(remappingSignatureAdapter);
        }
        else {
            signatureReader.accept(remappingSignatureAdapter);
        }
        return signatureWriter.toString();
    }
    
    protected SignatureVisitor createRemappingSignatureAdapter(final SignatureVisitor signatureVisitor) {
        return (SignatureVisitor)new RemappingSignatureAdapter(signatureVisitor, this);
    }
    
    public String mapMethodName(final String s, final String s2, final String s3) {
        return s2;
    }
    
    public String mapFieldName(final String s, final String s2, final String s3) {
        return s2;
    }
    
    public String map(final String s) {
        return s;
    }
}
