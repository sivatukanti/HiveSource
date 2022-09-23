// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.lang.reflect.Field;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JavaConstantResource extends AbstractClasspathResource
{
    @Override
    protected InputStream openInputStream(final ClassLoader cl) throws IOException {
        final String constant = this.getName();
        final int index1 = constant.lastIndexOf(46);
        if (index1 < 0) {
            throw new IOException("No class name in " + constant);
        }
        final int index2 = index1;
        final String classname = constant.substring(0, index2);
        final String fieldname = constant.substring(index2 + 1, constant.length());
        try {
            final Class<?> clazz = (cl != null) ? Class.forName(classname, true, cl) : Class.forName(classname);
            final Field field = clazz.getField(fieldname);
            final String value = field.get(null).toString();
            return new ByteArrayInputStream(value.getBytes("UTF-8"));
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Class not found:" + classname);
        }
        catch (NoSuchFieldException e2) {
            throw new IOException("Field not found:" + fieldname + " in " + classname);
        }
        catch (IllegalAccessException e3) {
            throw new IOException("Illegal access to :" + fieldname + " in " + classname);
        }
        catch (NullPointerException npe) {
            throw new IOException("Not a static field: " + fieldname + " in " + classname);
        }
    }
}
