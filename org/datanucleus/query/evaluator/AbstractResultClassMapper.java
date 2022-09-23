// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.query.QueryUtils;
import java.security.AccessController;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import org.datanucleus.query.expression.Expression;
import java.util.Collection;
import org.datanucleus.util.Localiser;

public class AbstractResultClassMapper
{
    protected static final Localiser LOCALISER;
    protected Class resultClass;
    
    public AbstractResultClassMapper(final Class resultClass) {
        this.resultClass = resultClass;
    }
    
    public Collection map(final Collection inputResults, final Expression[] resultNames) {
        return AccessController.doPrivileged((PrivilegedAction<Collection>)new PrivilegedAction() {
            @Override
            public Object run() {
                final String[] fieldNames = new String[resultNames.length];
                final Field[] fields = new Field[fieldNames.length];
                for (int i = 0; i < fieldNames.length; ++i) {
                    if (resultNames[i] instanceof PrimaryExpression) {
                        fieldNames[i] = ((PrimaryExpression)resultNames[i]).getId();
                        if (fieldNames[i].indexOf(46) > 0) {
                            final int pos = fieldNames[i].lastIndexOf(46);
                            fieldNames[i] = fieldNames[i].substring(pos + 1);
                        }
                        fields[i] = AbstractResultClassMapper.this.getFieldForFieldNameInResultClass(AbstractResultClassMapper.this.resultClass, fieldNames[i]);
                    }
                    else if (resultNames[i] instanceof ParameterExpression) {
                        fieldNames[i] = ((ParameterExpression)resultNames[i]).getId();
                        fields[i] = AbstractResultClassMapper.this.getFieldForFieldNameInResultClass(AbstractResultClassMapper.this.resultClass, fieldNames[i]);
                    }
                    else {
                        fieldNames[i] = resultNames[i].getAlias();
                        fields[i] = null;
                    }
                }
                final List outputResults = new ArrayList();
                for (final Object inputResult : inputResults) {
                    final Object row = AbstractResultClassMapper.this.getResultForResultSetRow(inputResult, fieldNames, fields);
                    outputResults.add(row);
                }
                return outputResults;
            }
        });
    }
    
    Object getResultForResultSetRow(final Object inputResult, final String[] fieldNames, final Field[] fields) {
        if (this.resultClass == Object[].class) {
            return inputResult;
        }
        if (QueryUtils.resultClassIsSimple(this.resultClass.getName())) {
            if (fieldNames.length == 1) {
                if (inputResult == null || this.resultClass.isAssignableFrom(inputResult.getClass())) {
                    return inputResult;
                }
                final String msg = AbstractResultClassMapper.LOCALISER.msg("021202", this.resultClass.getName(), inputResult.getClass().getName());
                NucleusLogger.QUERY.error(msg);
                throw new NucleusUserException(msg);
            }
            else {
                if (fieldNames.length > 1) {
                    final String msg = AbstractResultClassMapper.LOCALISER.msg("021201", this.resultClass.getName());
                    NucleusLogger.QUERY.error(msg);
                    throw new NucleusUserException(msg);
                }
                return null;
            }
        }
        else {
            if (fieldNames.length == 1 && this.resultClass.isAssignableFrom(inputResult.getClass())) {
                return inputResult;
            }
            Object[] fieldValues = null;
            if (inputResult instanceof Object[]) {
                fieldValues = (Object[])inputResult;
            }
            else {
                fieldValues = new Object[] { inputResult };
            }
            final Object obj = QueryUtils.createResultObjectUsingArgumentedConstructor(this.resultClass, fieldValues, null);
            if (obj != null) {
                return obj;
            }
            if (NucleusLogger.QUERY.isDebugEnabled()) {
                final Class[] ctr_arg_types = new Class[fieldNames.length];
                for (int i = 0; i < fieldNames.length; ++i) {
                    if (fieldValues[i] != null) {
                        ctr_arg_types[i] = fieldValues[i].getClass();
                    }
                    else {
                        ctr_arg_types[i] = null;
                    }
                }
                NucleusLogger.QUERY.debug(AbstractResultClassMapper.LOCALISER.msg("021206", this.resultClass.getName(), StringUtils.objectArrayToString(ctr_arg_types)));
            }
            return QueryUtils.createResultObjectUsingDefaultConstructorAndSetters(this.resultClass, fieldNames, fields, fieldValues);
        }
    }
    
    Field getFieldForFieldNameInResultClass(final Class cls, final String fieldName) {
        try {
            return cls.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException nsfe) {
            if (cls.getSuperclass() != null) {
                return this.getFieldForFieldNameInResultClass(cls.getSuperclass(), fieldName);
            }
            return null;
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
