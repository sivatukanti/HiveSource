// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import java.sql.SQLException;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.sql.dictionary.OptionalTool;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.compile.CompilerContext;

public class Java5SystemProcedures
{
    private static final int TOOL_NAME = 0;
    private static final int TOOL_CLASS_NAME = 1;
    private static final String CUSTOM_TOOL_CLASS_NAME = "customTool";
    private static final String[][] OPTIONAL_TOOLS;
    
    public static void SYSCS_REGISTER_TOOL(final String anObject, final boolean b, String... stripCustomClassName) throws SQLException {
        try {
            final ClassFactory classFactory = ((CompilerContext)ContextService.getContext("CompilerContext")).getClassFactory();
            final String toolClassName = findToolClassName(anObject, stripCustomClassName);
            OptionalTool optionalTool;
            try {
                optionalTool = classFactory.loadApplicationClass(toolClassName).newInstance();
            }
            catch (ClassNotFoundException ex) {
                throw wrap(ex);
            }
            catch (InstantiationException ex2) {
                throw wrap(ex2);
            }
            catch (IllegalAccessException ex3) {
                throw wrap(ex3);
            }
            if ("customTool".equals(anObject)) {
                stripCustomClassName = stripCustomClassName(stripCustomClassName);
            }
            if (b) {
                optionalTool.loadTool(stripCustomClassName);
            }
            else {
                optionalTool.unloadTool(stripCustomClassName);
            }
        }
        catch (StandardException ex4) {
            throw PublicAPI.wrapStandardException(ex4);
        }
    }
    
    private static String findToolClassName(final String s, final String... array) throws StandardException {
        if (!"customTool".equals(s)) {
            for (final String[] array2 : Java5SystemProcedures.OPTIONAL_TOOLS) {
                if (array2[0].equals(s)) {
                    return array2[1];
                }
            }
            throw badTool(s);
        }
        if (array == null || array.length == 0) {
            throw badTool("null");
        }
        return array[0];
    }
    
    private static StandardException badTool(final String s) {
        return StandardException.newException("X0Y88.S", s);
    }
    
    private static String[] stripCustomClassName(final String... array) {
        final int n = array.length - 1;
        final String[] array2 = new String[n];
        for (int i = 0; i < n; ++i) {
            array2[i] = array[i + 1];
        }
        return array2;
    }
    
    private static StandardException wrap(final Throwable t) {
        return StandardException.plainWrapException(t);
    }
    
    static {
        OPTIONAL_TOOLS = new String[][] { { "databaseMetaData", "org.apache.derby.impl.tools.optional.DBMDWrapper" }, { "foreignViews", "org.apache.derby.impl.tools.optional.ForeignDBViews" }, { "optimizerTracing", "org.apache.derby.impl.tools.optional.OptimizerTracer" } };
    }
}
