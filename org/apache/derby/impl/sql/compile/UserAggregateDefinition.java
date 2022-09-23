// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.JSQLType;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.sql.compile.NodeFactory;
import org.apache.derby.iapi.sql.compile.TypeCompilerFactory;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.catalog.types.AggregateAliasInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;

public class UserAggregateDefinition implements AggregateDefinition
{
    private static final int INPUT_TYPE = 0;
    private static final int RETURN_TYPE = 1;
    private static final int AGGREGATOR_TYPE = 2;
    private static final int AGGREGATOR_PARAM_COUNT = 3;
    private static final String DERBY_BYTE_ARRAY_NAME = "byte[]";
    private AliasDescriptor _alias;
    
    public UserAggregateDefinition(final AliasDescriptor alias) {
        this._alias = alias;
    }
    
    public AliasDescriptor getAliasDescriptor() {
        return this._alias;
    }
    
    public final DataTypeDescriptor getAggregator(final DataTypeDescriptor dataTypeDescriptor, final StringBuffer sb) throws StandardException {
        try {
            final CompilerContext compilerContext = (CompilerContext)ContextService.getContext("CompilerContext");
            final ClassFactory classFactory = compilerContext.getClassFactory();
            final TypeCompilerFactory typeCompilerFactory = compilerContext.getTypeCompilerFactory();
            final Class loadApplicationClass = classFactory.loadApplicationClass("org.apache.derby.agg.Aggregator");
            final Class loadApplicationClass2 = classFactory.loadApplicationClass(this._alias.getJavaClassName());
            final Class[][] typeBounds = classFactory.getClassInspector().getTypeBounds(loadApplicationClass, loadApplicationClass2);
            if (typeBounds == null || typeBounds.length != 3 || typeBounds[0] == null || typeBounds[1] == null) {
                throw StandardException.newException("42ZC4", this._alias.getSchemaName(), this._alias.getName(), loadApplicationClass2.getName());
            }
            Class[] genericParameterTypes = classFactory.getClassInspector().getGenericParameterTypes(loadApplicationClass, loadApplicationClass2);
            if (genericParameterTypes == null) {
                genericParameterTypes = new Class[3];
            }
            final AggregateAliasInfo aggregateAliasInfo = (AggregateAliasInfo)this._alias.getAliasInfo();
            final DataTypeDescriptor type = DataTypeDescriptor.getType(aggregateAliasInfo.getForType());
            final DataTypeDescriptor type2 = DataTypeDescriptor.getType(aggregateAliasInfo.getReturnType());
            final Class javaClass = this.getJavaClass(classFactory, type);
            final Class javaClass2 = this.getJavaClass(classFactory, type2);
            if (!typeCompilerFactory.getTypeCompiler(type.getTypeId()).storable(dataTypeDescriptor.getTypeId(), classFactory)) {
                return null;
            }
            final Class[] array = typeBounds[0];
            for (int i = 0; i < array.length; ++i) {
                this.vetCompatibility(array[i], javaClass, "42ZC6");
            }
            if (genericParameterTypes[0] != null) {
                this.vetCompatibility(genericParameterTypes[0], javaClass, "42ZC6");
            }
            final Class[] array2 = typeBounds[1];
            for (int j = 0; j < array2.length; ++j) {
                this.vetCompatibility(array2[j], javaClass2, "42ZC7");
            }
            if (genericParameterTypes[1] != null) {
                this.vetCompatibility(genericParameterTypes[1], javaClass2, "42ZC7");
            }
            sb.append("org.apache.derby.impl.sql.execute.UserDefinedAggregator");
            return type2;
        }
        catch (ClassNotFoundException ex) {
            throw this.aggregatorInstantiation(ex);
        }
    }
    
    private void vetCompatibility(final Class clazz, final Class clazz2, final String s) throws StandardException {
        if (!clazz.isAssignableFrom(clazz2)) {
            throw StandardException.newException(s, this._alias.getSchemaName(), this._alias.getName(), clazz2.toString(), clazz.toString());
        }
    }
    
    public final ValueNode castInputValue(final ValueNode valueNode, final NodeFactory nodeFactory, final ContextManager contextManager) throws StandardException {
        final DataTypeDescriptor type = DataTypeDescriptor.getType(((AggregateAliasInfo)this._alias.getAliasInfo()).getForType());
        if (type.isExactTypeAndLengthMatch(valueNode.getTypeServices())) {
            return null;
        }
        return StaticMethodCallNode.makeCast(valueNode, type, nodeFactory, contextManager);
    }
    
    private Class getJavaClass(final ClassFactory classFactory, final DataTypeDescriptor dataTypeDescriptor) throws StandardException, ClassNotFoundException {
        String anObject = MethodCallNode.getObjectTypeName(new JSQLType(dataTypeDescriptor), null);
        if ("byte[]".equals(anObject)) {
            anObject = byte[].class.getName();
        }
        return classFactory.loadApplicationClass(anObject);
    }
    
    private StandardException aggregatorInstantiation(final Throwable t) {
        return StandardException.newException("42ZC8", t, this._alias.getJavaClassName(), this._alias.getSchemaName(), this._alias.getName(), t.getMessage());
    }
}
