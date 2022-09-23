// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

public class JRecord extends JCompType
{
    private String mFQName;
    private String mName;
    private String mModule;
    private ArrayList<JField> mFields;
    static HashMap<String, String> vectorStructs;
    
    public JRecord(final String name, final ArrayList<JField> flist) {
        super("struct " + name.substring(name.lastIndexOf(46) + 1), name.replaceAll("\\.", "::"), getCsharpFQName(name), name, "Record", name, getCsharpFQName("IRecord"));
        this.mFQName = name;
        final int idx = name.lastIndexOf(46);
        this.mName = name.substring(idx + 1);
        this.mModule = name.substring(0, idx);
        this.mFields = flist;
    }
    
    public String getName() {
        return this.mName;
    }
    
    public String getCsharpName() {
        return "Id".equals(this.mName) ? "ZKId" : this.mName;
    }
    
    public String getJavaFQName() {
        return this.mFQName;
    }
    
    public String getCppFQName() {
        return this.mFQName.replaceAll("\\.", "::");
    }
    
    public String getJavaPackage() {
        return this.mModule;
    }
    
    public String getCppNameSpace() {
        return this.mModule.replaceAll("\\.", "::");
    }
    
    public String getCsharpNameSpace() {
        final String[] parts = this.mModule.split("\\.");
        final StringBuffer namespace = new StringBuffer();
        for (int i = 0; i < parts.length; ++i) {
            final String capitalized = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase();
            namespace.append(capitalized);
            if (i != parts.length - 1) {
                namespace.append(".");
            }
        }
        return namespace.toString();
    }
    
    public ArrayList<JField> getFields() {
        return this.mFields;
    }
    
    public String getSignature() {
        final StringBuilder sb = new StringBuilder();
        sb.append("L").append(this.mName).append("(");
        final Iterator<JField> i = this.mFields.iterator();
        while (i.hasNext()) {
            final String s = i.next().getSignature();
            sb.append(s);
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String genCppDecl(final String fname) {
        return "  " + this.getCppNameSpace() + "::" + this.mName + " m" + fname + ";\n";
    }
    
    public String genJavaReadMethod(final String fname, final String tag) {
        return this.genJavaReadWrapper(fname, tag, false);
    }
    
    public String genJavaReadWrapper(final String fname, final String tag, final boolean decl) {
        final StringBuilder ret = new StringBuilder("");
        if (decl) {
            ret.append("    " + this.getJavaFQName() + " " + fname + ";\n");
        }
        ret.append("    " + fname + "= new " + this.getJavaFQName() + "();\n");
        ret.append("    a_.readRecord(" + fname + ",\"" + tag + "\");\n");
        return ret.toString();
    }
    
    public String genJavaWriteWrapper(final String fname, final String tag) {
        return "    a_.writeRecord(" + fname + ",\"" + tag + "\");\n";
    }
    
    @Override
    String genCsharpReadMethod(final String fname, final String tag) {
        return this.genCsharpReadWrapper(JType.capitalize(fname), tag, false);
    }
    
    public String genCsharpReadWrapper(final String fname, final String tag, final boolean decl) {
        final StringBuilder ret = new StringBuilder("");
        if (decl) {
            ret.append("    " + getCsharpFQName(this.mFQName) + " " + fname + ";\n");
        }
        ret.append("    " + fname + "= new " + getCsharpFQName(this.mFQName) + "();\n");
        ret.append("    a_.ReadRecord(" + fname + ",\"" + tag + "\");\n");
        return ret.toString();
    }
    
    public String genCsharpWriteWrapper(final String fname, final String tag) {
        return "    a_.WriteRecord(" + fname + ",\"" + tag + "\");\n";
    }
    
    public void genCCode(final FileWriter h, final FileWriter c) throws IOException {
        for (final JField f : this.mFields) {
            if (f.getType() instanceof JVector) {
                final JVector jv = (JVector)f.getType();
                final JType jvType = jv.getElementType();
                final String struct_name = JVector.extractVectorName(jvType);
                if (JRecord.vectorStructs.get(struct_name) != null) {
                    continue;
                }
                JRecord.vectorStructs.put(struct_name, struct_name);
                h.write("struct " + struct_name + " {\n    int32_t count;\n" + jv.getElementType().genCDecl("*data") + "\n};\n");
                h.write("int serialize_" + struct_name + "(struct oarchive *out, const char *tag, struct " + struct_name + " *v);\n");
                h.write("int deserialize_" + struct_name + "(struct iarchive *in, const char *tag, struct " + struct_name + " *v);\n");
                h.write("int allocate_" + struct_name + "(struct " + struct_name + " *v, int32_t len);\n");
                h.write("int deallocate_" + struct_name + "(struct " + struct_name + " *v);\n");
                c.write("int allocate_" + struct_name + "(struct " + struct_name + " *v, int32_t len) {\n");
                c.write("    if (!len) {\n");
                c.write("        v->count = 0;\n");
                c.write("        v->data = 0;\n");
                c.write("    } else {\n");
                c.write("        v->count = len;\n");
                c.write("        v->data = calloc(sizeof(*v->data), len);\n");
                c.write("    }\n");
                c.write("    return 0;\n");
                c.write("}\n");
                c.write("int deallocate_" + struct_name + "(struct " + struct_name + " *v) {\n");
                c.write("    if (v->data) {\n");
                c.write("        int32_t i;\n");
                c.write("        for(i=0;i<v->count; i++) {\n");
                c.write("            deallocate_" + extractMethodSuffix(jvType) + "(&v->data[i]);\n");
                c.write("        }\n");
                c.write("        free(v->data);\n");
                c.write("        v->data = 0;\n");
                c.write("    }\n");
                c.write("    return 0;\n");
                c.write("}\n");
                c.write("int serialize_" + struct_name + "(struct oarchive *out, const char *tag, struct " + struct_name + " *v)\n");
                c.write("{\n");
                c.write("    int32_t count = v->count;\n");
                c.write("    int rc = 0;\n");
                c.write("    int32_t i;\n");
                c.write("    rc = out->start_vector(out, tag, &count);\n");
                c.write("    for(i=0;i<v->count;i++) {\n");
                this.genSerialize(c, jvType, "data", "data[i]");
                c.write("    }\n");
                c.write("    rc = rc ? rc : out->end_vector(out, tag);\n");
                c.write("    return rc;\n");
                c.write("}\n");
                c.write("int deserialize_" + struct_name + "(struct iarchive *in, const char *tag, struct " + struct_name + " *v)\n");
                c.write("{\n");
                c.write("    int rc = 0;\n");
                c.write("    int32_t i;\n");
                c.write("    rc = in->start_vector(in, tag, &v->count);\n");
                c.write("    v->data = calloc(v->count, sizeof(*v->data));\n");
                c.write("    for(i=0;i<v->count;i++) {\n");
                this.genDeserialize(c, jvType, "value", "data[i]");
                c.write("    }\n");
                c.write("    rc = in->end_vector(in, tag);\n");
                c.write("    return rc;\n");
                c.write("}\n");
            }
        }
        final String rec_name = this.getName();
        h.write("struct " + rec_name + " {\n");
        for (final JField f2 : this.mFields) {
            h.write(f2.genCDecl());
        }
        h.write("};\n");
        h.write("int serialize_" + rec_name + "(struct oarchive *out, const char *tag, struct " + rec_name + " *v);\n");
        h.write("int deserialize_" + rec_name + "(struct iarchive *in, const char *tag, struct " + rec_name + "*v);\n");
        h.write("void deallocate_" + rec_name + "(struct " + rec_name + "*);\n");
        c.write("int serialize_" + rec_name + "(struct oarchive *out, const char *tag, struct " + rec_name + " *v)");
        c.write("{\n");
        c.write("    int rc;\n");
        c.write("    rc = out->start_record(out, tag);\n");
        for (final JField f2 : this.mFields) {
            this.genSerialize(c, f2.getType(), f2.getTag(), f2.getName());
        }
        c.write("    rc = rc ? rc : out->end_record(out, tag);\n");
        c.write("    return rc;\n");
        c.write("}\n");
        c.write("int deserialize_" + rec_name + "(struct iarchive *in, const char *tag, struct " + rec_name + "*v)");
        c.write("{\n");
        c.write("    int rc;\n");
        c.write("    rc = in->start_record(in, tag);\n");
        for (final JField f2 : this.mFields) {
            this.genDeserialize(c, f2.getType(), f2.getTag(), f2.getName());
        }
        c.write("    rc = rc ? rc : in->end_record(in, tag);\n");
        c.write("    return rc;\n");
        c.write("}\n");
        c.write("void deallocate_" + rec_name + "(struct " + rec_name + "*v)");
        c.write("{\n");
        for (final JField f2 : this.mFields) {
            if (f2.getType() instanceof JRecord) {
                c.write("    deallocate_" + extractStructName(f2.getType()) + "(&v->" + f2.getName() + ");\n");
            }
            else if (f2.getType() instanceof JVector) {
                final JVector vt = (JVector)f2.getType();
                c.write("    deallocate_" + JVector.extractVectorName(vt.getElementType()) + "(&v->" + f2.getName() + ");\n");
            }
            else {
                if (!(f2.getType() instanceof JCompType)) {
                    continue;
                }
                c.write("    deallocate_" + extractMethodSuffix(f2.getType()) + "(&v->" + f2.getName() + ");\n");
            }
        }
        c.write("}\n");
    }
    
    private void genSerialize(final FileWriter c, final JType type, final String tag, final String name) throws IOException {
        if (type instanceof JRecord) {
            c.write("    rc = rc ? rc : serialize_" + extractStructName(type) + "(out, \"" + tag + "\", &v->" + name + ");\n");
        }
        else if (type instanceof JVector) {
            c.write("    rc = rc ? rc : serialize_" + JVector.extractVectorName(((JVector)type).getElementType()) + "(out, \"" + tag + "\", &v->" + name + ");\n");
        }
        else {
            c.write("    rc = rc ? rc : out->serialize_" + extractMethodSuffix(type) + "(out, \"" + tag + "\", &v->" + name + ");\n");
        }
    }
    
    private void genDeserialize(final FileWriter c, final JType type, final String tag, final String name) throws IOException {
        if (type instanceof JRecord) {
            c.write("    rc = rc ? rc : deserialize_" + extractStructName(type) + "(in, \"" + tag + "\", &v->" + name + ");\n");
        }
        else if (type instanceof JVector) {
            c.write("    rc = rc ? rc : deserialize_" + JVector.extractVectorName(((JVector)type).getElementType()) + "(in, \"" + tag + "\", &v->" + name + ");\n");
        }
        else {
            c.write("    rc = rc ? rc : in->deserialize_" + extractMethodSuffix(type) + "(in, \"" + tag + "\", &v->" + name + ");\n");
        }
    }
    
    static String extractMethodSuffix(final JType t) {
        if (t instanceof JRecord) {
            return extractStructName(t);
        }
        return t.getMethodSuffix();
    }
    
    private static String extractStructName(final JType t) {
        final String type = t.getCType();
        if (!type.startsWith("struct ")) {
            return type;
        }
        return type.substring("struct ".length());
    }
    
    public void genCppCode(final FileWriter hh, final FileWriter cc) throws IOException {
        final String[] ns = this.getCppNameSpace().split("::");
        for (int i = 0; i < ns.length; ++i) {
            hh.write("namespace " + ns[i] + " {\n");
        }
        hh.write("class " + this.getName() + " : public ::hadoop::Record {\n");
        hh.write("private:\n");
        for (final JField jf : this.mFields) {
            hh.write(jf.genCppDecl());
        }
        hh.write("  mutable std::bitset<" + this.mFields.size() + "> bs_;\n");
        hh.write("public:\n");
        hh.write("  virtual void serialize(::hadoop::OArchive& a_, const char* tag) const;\n");
        hh.write("  virtual void deserialize(::hadoop::IArchive& a_, const char* tag);\n");
        hh.write("  virtual const ::std::string& type() const;\n");
        hh.write("  virtual const ::std::string& signature() const;\n");
        hh.write("  virtual bool validate() const;\n");
        hh.write("  virtual bool operator<(const " + this.getName() + "& peer_) const;\n");
        hh.write("  virtual bool operator==(const " + this.getName() + "& peer_) const;\n");
        hh.write("  virtual ~" + this.getName() + "() {};\n");
        int fIdx = 0;
        for (final JField jf2 : this.mFields) {
            hh.write(jf2.genCppGetSet(fIdx));
            ++fIdx;
        }
        hh.write("}; // end record " + this.getName() + "\n");
        for (int l = ns.length - 1; l >= 0; --l) {
            hh.write("} // end namespace " + ns[l] + "\n");
        }
        cc.write("void " + this.getCppFQName() + "::serialize(::hadoop::OArchive& a_, const char* tag) const {\n");
        cc.write("  if (!validate()) throw new ::hadoop::IOException(\"All fields not set.\");\n");
        cc.write("  a_.startRecord(*this,tag);\n");
        fIdx = 0;
        for (final JField jf2 : this.mFields) {
            final String name = jf2.getName();
            if (jf2.getType() instanceof JBuffer) {
                cc.write("  a_.serialize(m" + name + ",m" + name + ".length(),\"" + jf2.getTag() + "\");\n");
            }
            else {
                cc.write("  a_.serialize(m" + name + ",\"" + jf2.getTag() + "\");\n");
            }
            cc.write("  bs_.reset(" + fIdx + ");\n");
            ++fIdx;
        }
        cc.write("  a_.endRecord(*this,tag);\n");
        cc.write("  return;\n");
        cc.write("}\n");
        cc.write("void " + this.getCppFQName() + "::deserialize(::hadoop::IArchive& a_, const char* tag) {\n");
        cc.write("  a_.startRecord(*this,tag);\n");
        fIdx = 0;
        for (final JField jf2 : this.mFields) {
            final String name = jf2.getName();
            if (jf2.getType() instanceof JBuffer) {
                cc.write("  { size_t len=0; a_.deserialize(m" + name + ",len,\"" + jf2.getTag() + "\");}\n");
            }
            else {
                cc.write("  a_.deserialize(m" + name + ",\"" + jf2.getTag() + "\");\n");
            }
            cc.write("  bs_.set(" + fIdx + ");\n");
            ++fIdx;
        }
        cc.write("  a_.endRecord(*this,tag);\n");
        cc.write("  return;\n");
        cc.write("}\n");
        cc.write("bool " + this.getCppFQName() + "::validate() const {\n");
        cc.write("  if (bs_.size() != bs_.count()) return false;\n");
        for (final JField jf2 : this.mFields) {
            final JType type = jf2.getType();
            if (type instanceof JRecord) {
                cc.write("  if (!m" + jf2.getName() + ".validate()) return false;\n");
            }
            ++fIdx;
        }
        cc.write("  return true;\n");
        cc.write("}\n");
        cc.write("bool " + this.getCppFQName() + "::operator< (const " + this.getCppFQName() + "& peer_) const {\n");
        cc.write("  return (1\n");
        for (final JField jf2 : this.mFields) {
            final String name = jf2.getName();
            cc.write("    && (m" + name + " < peer_.m" + name + ")\n");
        }
        cc.write("  );\n");
        cc.write("}\n");
        cc.write("bool " + this.getCppFQName() + "::operator== (const " + this.getCppFQName() + "& peer_) const {\n");
        cc.write("  return (1\n");
        for (final JField jf2 : this.mFields) {
            final String name = jf2.getName();
            cc.write("    && (m" + name + " == peer_.m" + name + ")\n");
        }
        cc.write("  );\n");
        cc.write("}\n");
        cc.write("const ::std::string&" + this.getCppFQName() + "::type() const {\n");
        cc.write("  static const ::std::string type_(\"" + this.mName + "\");\n");
        cc.write("  return type_;\n");
        cc.write("}\n");
        cc.write("const ::std::string&" + this.getCppFQName() + "::signature() const {\n");
        cc.write("  static const ::std::string sig_(\"" + this.getSignature() + "\");\n");
        cc.write("  return sig_;\n");
        cc.write("}\n");
    }
    
    public void genJavaCode(final File outputDirectory) throws IOException {
        final String pkg = this.getJavaPackage();
        final String pkgpath = pkg.replaceAll("\\.", "/");
        final File pkgdir = new File(outputDirectory, pkgpath);
        if (!pkgdir.exists()) {
            if (!pkgdir.mkdirs()) {
                throw new IOException("Cannnot create directory: " + pkgpath);
            }
        }
        else if (!pkgdir.isDirectory()) {
            throw new IOException(pkgpath + " is not a directory.");
        }
        final File jfile = new File(pkgdir, this.getName() + ".java");
        FileWriter jj = null;
        try {
            jj = new FileWriter(jfile);
            jj.write("// File generated by hadoop record compiler. Do not edit.\n");
            jj.write("/**\n");
            jj.write("* Licensed to the Apache Software Foundation (ASF) under one\n");
            jj.write("* or more contributor license agreements.  See the NOTICE file\n");
            jj.write("* distributed with this work for additional information\n");
            jj.write("* regarding copyright ownership.  The ASF licenses this file\n");
            jj.write("* to you under the Apache License, Version 2.0 (the\n");
            jj.write("* \"License\"); you may not use this file except in compliance\n");
            jj.write("* with the License.  You may obtain a copy of the License at\n");
            jj.write("*\n");
            jj.write("*     http://www.apache.org/licenses/LICENSE-2.0\n");
            jj.write("*\n");
            jj.write("* Unless required by applicable law or agreed to in writing, software\n");
            jj.write("* distributed under the License is distributed on an \"AS IS\" BASIS,\n");
            jj.write("* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
            jj.write("* See the License for the specific language governing permissions and\n");
            jj.write("* limitations under the License.\n");
            jj.write("*/\n");
            jj.write("\n");
            jj.write("package " + this.getJavaPackage() + ";\n\n");
            jj.write("import org.apache.jute.*;\n");
            jj.write("import org.apache.yetus.audience.InterfaceAudience;\n");
            jj.write("@InterfaceAudience.Public\n");
            jj.write("public class " + this.getName() + " implements Record {\n");
            for (final JField jf : this.mFields) {
                jj.write(jf.genJavaDecl());
            }
            jj.write("  public " + this.getName() + "() {\n");
            jj.write("  }\n");
            jj.write("  public " + this.getName() + "(\n");
            int fIdx = 0;
            final int fLen = this.mFields.size();
            for (final JField jf2 : this.mFields) {
                jj.write(jf2.genJavaConstructorParam(jf2.getName()));
                jj.write((fLen - 1 == fIdx) ? "" : ",\n");
                ++fIdx;
            }
            jj.write(") {\n");
            fIdx = 0;
            for (final JField jf2 : this.mFields) {
                jj.write(jf2.genJavaConstructorSet(jf2.getName()));
                ++fIdx;
            }
            jj.write("  }\n");
            fIdx = 0;
            for (final JField jf2 : this.mFields) {
                jj.write(jf2.genJavaGetSet(fIdx));
                ++fIdx;
            }
            jj.write("  public void serialize(OutputArchive a_, String tag) throws java.io.IOException {\n");
            jj.write("    a_.startRecord(this,tag);\n");
            fIdx = 0;
            for (final JField jf2 : this.mFields) {
                jj.write(jf2.genJavaWriteMethodName());
                ++fIdx;
            }
            jj.write("    a_.endRecord(this,tag);\n");
            jj.write("  }\n");
            jj.write("  public void deserialize(InputArchive a_, String tag) throws java.io.IOException {\n");
            jj.write("    a_.startRecord(tag);\n");
            fIdx = 0;
            for (final JField jf2 : this.mFields) {
                jj.write(jf2.genJavaReadMethodName());
                ++fIdx;
            }
            jj.write("    a_.endRecord(tag);\n");
            jj.write("}\n");
            jj.write("  public String toString() {\n");
            jj.write("    try {\n");
            jj.write("      java.io.ByteArrayOutputStream s =\n");
            jj.write("        new java.io.ByteArrayOutputStream();\n");
            jj.write("      CsvOutputArchive a_ = \n");
            jj.write("        new CsvOutputArchive(s);\n");
            jj.write("      a_.startRecord(this,\"\");\n");
            fIdx = 0;
            for (final JField jf2 : this.mFields) {
                jj.write(jf2.genJavaWriteMethodName());
                ++fIdx;
            }
            jj.write("      a_.endRecord(this,\"\");\n");
            jj.write("      return new String(s.toByteArray(), \"UTF-8\");\n");
            jj.write("    } catch (Throwable ex) {\n");
            jj.write("      ex.printStackTrace();\n");
            jj.write("    }\n");
            jj.write("    return \"ERROR\";\n");
            jj.write("  }\n");
            jj.write("  public void write(java.io.DataOutput out) throws java.io.IOException {\n");
            jj.write("    BinaryOutputArchive archive = new BinaryOutputArchive(out);\n");
            jj.write("    serialize(archive, \"\");\n");
            jj.write("  }\n");
            jj.write("  public void readFields(java.io.DataInput in) throws java.io.IOException {\n");
            jj.write("    BinaryInputArchive archive = new BinaryInputArchive(in);\n");
            jj.write("    deserialize(archive, \"\");\n");
            jj.write("  }\n");
            jj.write("  public int compareTo (Object peer_) throws ClassCastException {\n");
            boolean unimplemented = false;
            for (final JField f : this.mFields) {
                if (f.getType() instanceof JMap || f.getType() instanceof JVector) {
                    unimplemented = true;
                }
            }
            if (unimplemented) {
                jj.write("    throw new UnsupportedOperationException(\"comparing " + this.getName() + " is unimplemented\");\n");
            }
            else {
                jj.write("    if (!(peer_ instanceof " + this.getName() + ")) {\n");
                jj.write("      throw new ClassCastException(\"Comparing different types of records.\");\n");
                jj.write("    }\n");
                jj.write("    " + this.getName() + " peer = (" + this.getName() + ") peer_;\n");
                jj.write("    int ret = 0;\n");
                for (final JField jf3 : this.mFields) {
                    jj.write(jf3.genJavaCompareTo());
                    jj.write("    if (ret != 0) return ret;\n");
                    ++fIdx;
                }
                jj.write("     return ret;\n");
            }
            jj.write("  }\n");
            jj.write("  public boolean equals(Object peer_) {\n");
            jj.write("    if (!(peer_ instanceof " + this.getName() + ")) {\n");
            jj.write("      return false;\n");
            jj.write("    }\n");
            jj.write("    if (peer_ == this) {\n");
            jj.write("      return true;\n");
            jj.write("    }\n");
            jj.write("    " + this.getName() + " peer = (" + this.getName() + ") peer_;\n");
            jj.write("    boolean ret = false;\n");
            for (final JField jf3 : this.mFields) {
                jj.write(jf3.genJavaEquals());
                jj.write("    if (!ret) return ret;\n");
                ++fIdx;
            }
            jj.write("     return ret;\n");
            jj.write("  }\n");
            jj.write("  public int hashCode() {\n");
            jj.write("    int result = 17;\n");
            jj.write("    int ret;\n");
            for (final JField jf3 : this.mFields) {
                jj.write(jf3.genJavaHashCode());
                jj.write("    result = 37*result + ret;\n");
                ++fIdx;
            }
            jj.write("    return result;\n");
            jj.write("  }\n");
            jj.write("  public static String signature() {\n");
            jj.write("    return \"" + this.getSignature() + "\";\n");
            jj.write("  }\n");
            jj.write("}\n");
        }
        finally {
            if (jj != null) {
                jj.close();
            }
        }
    }
    
    public void genCsharpCode(final File outputDirectory) throws IOException {
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new IOException("Cannnot create directory: " + outputDirectory);
            }
        }
        else if (!outputDirectory.isDirectory()) {
            throw new IOException(outputDirectory + " is not a directory.");
        }
        final File csharpFile = new File(outputDirectory, this.getName() + ".cs");
        FileWriter cs = null;
        try {
            cs = new FileWriter(csharpFile);
            cs.write("// File generated by hadoop record compiler. Do not edit.\n");
            cs.write("/**\n");
            cs.write("* Licensed to the Apache Software Foundation (ASF) under one\n");
            cs.write("* or more contributor license agreements.  See the NOTICE file\n");
            cs.write("* distributed with this work for additional information\n");
            cs.write("* regarding copyright ownership.  The ASF licenses this file\n");
            cs.write("* to you under the Apache License, Version 2.0 (the\n");
            cs.write("* \"License\"); you may not use this file except in compliance\n");
            cs.write("* with the License.  You may obtain a copy of the License at\n");
            cs.write("*\n");
            cs.write("*     http://www.apache.org/licenses/LICENSE-2.0\n");
            cs.write("*\n");
            cs.write("* Unless required by applicable law or agreed to in writing, software\n");
            cs.write("* distributed under the License is distributed on an \"AS IS\" BASIS,\n");
            cs.write("* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
            cs.write("* See the License for the specific language governing permissions and\n");
            cs.write("* limitations under the License.\n");
            cs.write("*/\n");
            cs.write("\n");
            cs.write("using System;\n");
            cs.write("using Org.Apache.Jute;\n");
            cs.write("\n");
            cs.write("namespace " + this.getCsharpNameSpace() + "\n");
            cs.write("{\n");
            final String className = this.getCsharpName();
            cs.write("public class " + className + " : IRecord, IComparable \n");
            cs.write("{\n");
            cs.write("  public " + className + "() {\n");
            cs.write("  }\n");
            cs.write("  public " + className + "(\n");
            int fIdx = 0;
            final int fLen = this.mFields.size();
            for (final JField jf : this.mFields) {
                cs.write(jf.genCsharpConstructorParam(jf.getCsharpName()));
                cs.write((fLen - 1 == fIdx) ? "" : ",\n");
                ++fIdx;
            }
            cs.write(") {\n");
            fIdx = 0;
            for (final JField jf : this.mFields) {
                cs.write(jf.genCsharpConstructorSet(jf.getCsharpName()));
                ++fIdx;
            }
            cs.write("  }\n");
            fIdx = 0;
            for (final JField jf : this.mFields) {
                cs.write(jf.genCsharpGetSet(fIdx));
                cs.write("\n");
                ++fIdx;
            }
            cs.write("  public void Serialize(IOutputArchive a_, String tag) {\n");
            cs.write("    a_.StartRecord(this,tag);\n");
            fIdx = 0;
            for (final JField jf : this.mFields) {
                cs.write(jf.genCsharpWriteMethodName());
                ++fIdx;
            }
            cs.write("    a_.EndRecord(this,tag);\n");
            cs.write("  }\n");
            cs.write("  public void Deserialize(IInputArchive a_, String tag) {\n");
            cs.write("    a_.StartRecord(tag);\n");
            fIdx = 0;
            for (final JField jf : this.mFields) {
                cs.write(jf.genCsharpReadMethodName());
                ++fIdx;
            }
            cs.write("    a_.EndRecord(tag);\n");
            cs.write("}\n");
            cs.write("  public override String ToString() {\n");
            cs.write("    try {\n");
            cs.write("      System.IO.MemoryStream ms = new System.IO.MemoryStream();\n");
            cs.write("      MiscUtil.IO.EndianBinaryWriter writer =\n");
            cs.write("        new MiscUtil.IO.EndianBinaryWriter(MiscUtil.Conversion.EndianBitConverter.Big, ms, System.Text.Encoding.UTF8);\n");
            cs.write("      BinaryOutputArchive a_ = \n");
            cs.write("        new BinaryOutputArchive(writer);\n");
            cs.write("      a_.StartRecord(this,\"\");\n");
            fIdx = 0;
            for (final JField jf : this.mFields) {
                cs.write(jf.genCsharpWriteMethodName());
                ++fIdx;
            }
            cs.write("      a_.EndRecord(this,\"\");\n");
            cs.write("      ms.Position = 0;\n");
            cs.write("      return System.Text.Encoding.UTF8.GetString(ms.ToArray());\n");
            cs.write("    } catch (Exception ex) {\n");
            cs.write("      Console.WriteLine(ex.StackTrace);\n");
            cs.write("    }\n");
            cs.write("    return \"ERROR\";\n");
            cs.write("  }\n");
            cs.write("  public void Write(MiscUtil.IO.EndianBinaryWriter writer) {\n");
            cs.write("    BinaryOutputArchive archive = new BinaryOutputArchive(writer);\n");
            cs.write("    Serialize(archive, \"\");\n");
            cs.write("  }\n");
            cs.write("  public void ReadFields(MiscUtil.IO.EndianBinaryReader reader) {\n");
            cs.write("    BinaryInputArchive archive = new BinaryInputArchive(reader);\n");
            cs.write("    Deserialize(archive, \"\");\n");
            cs.write("  }\n");
            cs.write("  public int CompareTo (object peer_) {\n");
            boolean unimplemented = false;
            for (final JField f : this.mFields) {
                if (f.getType() instanceof JMap || f.getType() instanceof JVector) {
                    unimplemented = true;
                }
            }
            if (unimplemented) {
                cs.write("    throw new InvalidOperationException(\"comparing " + this.getCsharpName() + " is unimplemented\");\n");
            }
            else {
                cs.write("    if (!(peer_ is " + this.getCsharpName() + ")) {\n");
                cs.write("      throw new InvalidOperationException(\"Comparing different types of records.\");\n");
                cs.write("    }\n");
                cs.write("    " + this.getCsharpName() + " peer = (" + this.getCsharpName() + ") peer_;\n");
                cs.write("    int ret = 0;\n");
                for (final JField jf2 : this.mFields) {
                    cs.write(jf2.genCsharpCompareTo());
                    cs.write("    if (ret != 0) return ret;\n");
                    ++fIdx;
                }
                cs.write("     return ret;\n");
            }
            cs.write("  }\n");
            cs.write("  public override bool Equals(object peer_) {\n");
            cs.write("    if (!(peer_ is " + this.getCsharpName() + ")) {\n");
            cs.write("      return false;\n");
            cs.write("    }\n");
            cs.write("    if (peer_ == this) {\n");
            cs.write("      return true;\n");
            cs.write("    }\n");
            cs.write("    bool ret = false;\n");
            cs.write("    " + this.getCsharpName() + " peer = (" + this.getCsharpName() + ")peer_;\n");
            for (final JField jf2 : this.mFields) {
                cs.write(jf2.genCsharpEquals());
                cs.write("    if (!ret) return ret;\n");
                ++fIdx;
            }
            cs.write("     return ret;\n");
            cs.write("  }\n");
            cs.write("  public override int GetHashCode() {\n");
            cs.write("    int result = 17;\n");
            cs.write("    int ret;\n");
            for (final JField jf2 : this.mFields) {
                cs.write(jf2.genCsharpHashCode());
                cs.write("    result = 37*result + ret;\n");
                ++fIdx;
            }
            cs.write("    return result;\n");
            cs.write("  }\n");
            cs.write("  public static string Signature() {\n");
            cs.write("    return \"" + this.getSignature() + "\";\n");
            cs.write("  }\n");
            cs.write("}\n");
            cs.write("}\n");
        }
        finally {
            if (cs != null) {
                cs.close();
            }
        }
    }
    
    public static String getCsharpFQName(final String name) {
        final String[] packages = name.split("\\.");
        final StringBuffer fQName = new StringBuffer();
        for (int i = 0; i < packages.length; ++i) {
            String pack = packages[i];
            pack = JType.capitalize(pack);
            pack = ("Id".equals(pack) ? "ZKId" : pack);
            fQName.append(JType.capitalize(pack));
            if (i != packages.length - 1) {
                fQName.append(".");
            }
        }
        return fQName.toString();
    }
    
    static {
        JRecord.vectorStructs = new HashMap<String, String>();
    }
}
