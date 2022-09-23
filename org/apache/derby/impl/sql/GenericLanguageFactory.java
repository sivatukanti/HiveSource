// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql;

import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertyFactory;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.services.property.PropertySetCallback;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.sql.conn.LanguageConnectionFactory;
import java.util.Properties;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.sql.LanguageFactory;

public class GenericLanguageFactory implements LanguageFactory, ModuleControl
{
    private GenericParameterValueSet emptySet;
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        final PropertyFactory propertyFactory = ((LanguageConnectionFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.sql.conn.LanguageConnectionFactory")).getPropertyFactory();
        if (propertyFactory != null) {
            propertyFactory.addPropertySetNotification(new LanguageDbPropertySetter());
        }
        this.emptySet = new GenericParameterValueSet(null, 0, false);
    }
    
    public void stop() {
    }
    
    public ParameterValueSet newParameterValueSet(final ClassInspector classInspector, final int n, final boolean b) {
        if (n == 0) {
            return this.emptySet;
        }
        return new GenericParameterValueSet(classInspector, n, b);
    }
    
    public ResultDescription getResultDescription(final ResultDescription resultDescription, final int[] array) {
        return new GenericResultDescription(resultDescription, array);
    }
    
    public ResultDescription getResultDescription(final ResultColumnDescriptor[] array, final String s) {
        return new GenericResultDescription(array, s);
    }
}
