// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.loader.Java5ClassInspector;
import org.apache.derby.iapi.services.loader.ClassInspector;

public class Java5ClassFactory extends ReflectClassesJava2
{
    @Override
    protected ClassInspector makeClassInspector(final DatabaseClasses databaseClasses) {
        return new Java5ClassInspector(databaseClasses);
    }
}
