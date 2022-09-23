// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

abstract class JCompType extends JType
{
    JCompType(final String cType, final String cppType, final String csharpType, final String javaType, final String suffix, final String wrapper, final String csharpWrapper) {
        super(cType, cppType, csharpType, javaType, suffix, wrapper, csharpWrapper, null);
    }
    
    @Override
    String genCppGetSet(final String fname, final int fIdx) {
        String cgetFunc = "  virtual const " + this.getCppType() + "& get" + fname + "() const {\n";
        cgetFunc = cgetFunc + "    return m" + fname + ";\n";
        cgetFunc += "  }\n";
        String getFunc = "  virtual " + this.getCppType() + "& get" + fname + "() {\n";
        getFunc = getFunc + "    bs_.set(" + fIdx + ");return m" + fname + ";\n";
        getFunc += "  }\n";
        return cgetFunc + getFunc;
    }
    
    @Override
    String genJavaCompareTo(final String fname) {
        return "    ret = " + fname + ".compareTo(peer." + fname + ");\n";
    }
    
    @Override
    String genJavaEquals(final String fname, final String peer) {
        return "    ret = " + fname + ".equals(" + peer + ");\n";
    }
    
    @Override
    String genJavaHashCode(final String fname) {
        return "    ret = " + fname + ".hashCode();\n";
    }
    
    @Override
    String genCsharpHashCode(final String fname) {
        return "    ret = " + JType.capitalize(fname) + ".GetHashCode();\n";
    }
    
    @Override
    String genCsharpEquals(final String name, final String peer) {
        final String[] peerSplit = peer.split("\\.");
        return "    ret = " + JType.capitalize(name) + ".Equals(" + peerSplit[0] + "." + JType.capitalize(peerSplit[1]) + ");\n";
    }
    
    @Override
    String genCsharpCompareTo(final String name) {
        return "    ret = " + JType.capitalize(name) + ".CompareTo(peer." + JType.capitalize(name) + ");\n";
    }
}
