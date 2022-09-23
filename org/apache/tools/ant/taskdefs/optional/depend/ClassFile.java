// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend;

import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPoolEntry;
import java.util.Vector;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ClassCPInfo;
import java.io.DataInputStream;
import java.io.InputStream;
import org.apache.tools.ant.taskdefs.optional.depend.constantpool.ConstantPool;

public class ClassFile
{
    private static final int CLASS_MAGIC = -889275714;
    private ConstantPool constantPool;
    private String className;
    
    public void read(final InputStream stream) throws IOException, ClassFormatError {
        final DataInputStream classStream = new DataInputStream(stream);
        if (classStream.readInt() != -889275714) {
            throw new ClassFormatError("No Magic Code Found - probably not a Java class file.");
        }
        classStream.readUnsignedShort();
        classStream.readUnsignedShort();
        (this.constantPool = new ConstantPool()).read(classStream);
        this.constantPool.resolve();
        classStream.readUnsignedShort();
        final int thisClassIndex = classStream.readUnsignedShort();
        classStream.readUnsignedShort();
        final ClassCPInfo classInfo = (ClassCPInfo)this.constantPool.getEntry(thisClassIndex);
        this.className = classInfo.getClassName();
    }
    
    public Vector<String> getClassRefs() {
        final Vector<String> classRefs = new Vector<String>();
        for (int size = this.constantPool.size(), i = 0; i < size; ++i) {
            final ConstantPoolEntry entry = this.constantPool.getEntry(i);
            if (entry != null && entry.getTag() == 7) {
                final ClassCPInfo classEntry = (ClassCPInfo)entry;
                if (!classEntry.getClassName().equals(this.className)) {
                    classRefs.add(ClassFileUtils.convertSlashName(classEntry.getClassName()));
                }
            }
        }
        return classRefs;
    }
    
    public String getFullClassName() {
        return ClassFileUtils.convertSlashName(this.className);
    }
}
