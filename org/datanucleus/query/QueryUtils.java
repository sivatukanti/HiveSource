// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query;

import org.datanucleus.ClassConstants;
import java.util.Arrays;
import org.datanucleus.query.expression.OrderExpression;
import org.datanucleus.query.expression.ExpressionEvaluator;
import org.datanucleus.query.evaluator.memory.InMemoryExpressionEvaluator;
import java.util.Comparator;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.query.compiler.JavaQueryCompiler;
import java.util.HashMap;
import org.datanucleus.util.Imports;
import java.util.Collection;
import org.datanucleus.query.compiler.JPQLCompiler;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import java.util.List;
import org.datanucleus.query.expression.InvokeExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.exceptions.NucleusException;
import java.util.Iterator;
import org.datanucleus.query.expression.ParameterExpression;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.text.StringCharacterIterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import org.datanucleus.util.TypeConversionHelper;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import org.datanucleus.exceptions.NucleusUserException;
import java.lang.reflect.Constructor;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.store.query.Query;
import java.util.StringTokenizer;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.datanucleus.ClassNameConstants;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class QueryUtils
{
    protected static final Localiser LOCALISER;
    static final Class[] classArrayObjectObject;
    
    public static boolean resultClassIsUserType(final String className) {
        return !resultClassIsSimple(className) && !className.equals(Map.class.getName()) && !className.equals(ClassNameConstants.Object);
    }
    
    public static boolean resultClassIsSimple(final String className) {
        return className.equals(ClassNameConstants.JAVA_LANG_BOOLEAN) || className.equals(ClassNameConstants.JAVA_LANG_BYTE) || className.equals(ClassNameConstants.JAVA_LANG_CHARACTER) || className.equals(ClassNameConstants.JAVA_LANG_DOUBLE) || className.equals(ClassNameConstants.JAVA_LANG_FLOAT) || className.equals(ClassNameConstants.JAVA_LANG_INTEGER) || className.equals(ClassNameConstants.JAVA_LANG_LONG) || className.equals(ClassNameConstants.JAVA_LANG_SHORT) || className.equals(ClassNameConstants.JAVA_LANG_STRING) || className.equals(BigDecimal.class.getName()) || className.equals(BigInteger.class.getName()) || className.equals(Date.class.getName()) || className.equals(java.sql.Date.class.getName()) || className.equals(Time.class.getName()) || className.equals(Timestamp.class.getName()) || className.equals(ClassNameConstants.Object);
    }
    
    public static boolean resultHasOnlyAggregates(final String result) {
        if (result == null) {
            return false;
        }
        String resultDefn = result;
        if (resultDefn.toLowerCase().startsWith("distinct")) {
            resultDefn = resultDefn.substring(8);
        }
        final StringTokenizer tokenizer = new StringTokenizer(resultDefn, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim().toLowerCase();
            if (token.startsWith("max") || token.startsWith("min") || token.startsWith("avg") || token.startsWith("sum")) {
                token = token.substring(3).trim();
                if (token.startsWith("(")) {
                    continue;
                }
                return false;
            }
            else {
                if (!token.startsWith("count")) {
                    return false;
                }
                token = token.substring(5).trim();
                if (token.startsWith("(")) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }
    
    public static boolean queryReturnsSingleRow(final Query query) {
        return query.isUnique() || (query.getGrouping() == null && resultHasOnlyAggregates(query.getResult()));
    }
    
    public static Object createResultObjectUsingArgumentedConstructor(final Class resultClass, final Object[] fieldValues, final Class[] fieldTypes) {
        Object obj = null;
        final Class[] ctrTypes = new Class[fieldValues.length];
        for (int i = 0; i < ctrTypes.length; ++i) {
            if (fieldTypes != null && fieldTypes[i] != null) {
                ctrTypes[i] = fieldTypes[i];
            }
            else if (fieldValues[i] != null) {
                ctrTypes[i] = fieldValues[i].getClass();
            }
            else {
                ctrTypes[i] = null;
            }
        }
        final Constructor ctr = ClassUtils.getConstructorWithArguments(resultClass, ctrTypes);
        if (ctr != null) {
            try {
                obj = ctr.newInstance(fieldValues);
                if (NucleusLogger.QUERY.isDebugEnabled()) {
                    final String msg = "ResultObject of type " + resultClass.getName() + " created with following constructor arguments: " + StringUtils.objectArrayToString(fieldValues);
                    NucleusLogger.QUERY.debug(msg);
                }
            }
            catch (Exception ex) {}
        }
        return obj;
    }
    
    public static Object createResultObjectUsingDefaultConstructorAndSetters(final Class resultClass, final String[] resultFieldNames, final Map resultClassFieldNames, final Object[] fieldValues) {
        Object obj = null;
        try {
            obj = resultClass.newInstance();
        }
        catch (Exception e) {
            final String msg = QueryUtils.LOCALISER.msg("021205", resultClass.getName());
            NucleusLogger.QUERY.error(msg);
            throw new NucleusUserException(msg);
        }
        for (int i = 0; i < fieldValues.length; ++i) {
            final Field field = resultClassFieldNames.get(resultFieldNames[i].toUpperCase());
            if (!setFieldForResultObject(obj, resultFieldNames[i], field, fieldValues[i])) {
                String fieldType = "null";
                if (fieldValues[i] != null) {
                    fieldType = fieldValues[i].getClass().getName();
                }
                final String msg2 = QueryUtils.LOCALISER.msg("021204", resultClass.getName(), resultFieldNames[i], fieldType);
                NucleusLogger.QUERY.error(msg2);
                throw new NucleusUserException(msg2);
            }
        }
        return obj;
    }
    
    public static Object createResultObjectUsingDefaultConstructorAndSetters(final Class resultClass, final String[] resultFieldNames, final Field[] resultFields, final Object[] fieldValues) {
        Object obj = null;
        try {
            obj = resultClass.newInstance();
        }
        catch (Exception e) {
            final String msg = QueryUtils.LOCALISER.msg("021205", resultClass.getName());
            NucleusLogger.QUERY.error(msg);
            throw new NucleusUserException(msg);
        }
        for (int i = 0; i < fieldValues.length; ++i) {
            if (!setFieldForResultObject(obj, resultFieldNames[i], resultFields[i], fieldValues[i])) {
                String fieldType = "null";
                if (fieldValues[i] != null) {
                    fieldType = fieldValues[i].getClass().getName();
                }
                final String msg2 = QueryUtils.LOCALISER.msg("021204", resultClass.getName(), resultFieldNames[i], fieldType);
                NucleusLogger.QUERY.error(msg2);
                throw new NucleusUserException(msg2);
            }
        }
        return obj;
    }
    
    private static boolean setFieldForResultObject(final Object obj, final String fieldName, final Field field, final Object value) {
        boolean fieldSet = false;
        if (!fieldSet) {
            String declaredFieldName = fieldName;
            if (field != null) {
                declaredFieldName = field.getName();
            }
            final Field f = ClassUtils.getFieldForClass(obj.getClass(), declaredFieldName);
            if (f != null && Modifier.isPublic(f.getModifiers())) {
                try {
                    f.set(obj, value);
                    fieldSet = true;
                }
                catch (Exception e) {
                    final Object convertedValue = TypeConversionHelper.convertTo(value, f.getType());
                    if (convertedValue != value) {
                        try {
                            f.set(obj, convertedValue);
                            fieldSet = true;
                            if (NucleusLogger.QUERY.isDebugEnabled()) {
                                final String msg = "ResultObject set field=" + fieldName + " using reflection";
                                NucleusLogger.QUERY.debug(msg);
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            if (!fieldSet && NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(QueryUtils.LOCALISER.msg("021209", obj.getClass().getName(), declaredFieldName));
            }
        }
        if (!fieldSet) {
            String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            if (field != null) {
                setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + field.getName().substring(1);
            }
            Class argType = null;
            if (value != null) {
                argType = value.getClass();
            }
            else if (field != null) {
                argType = field.getType();
            }
            final Method m = ClassUtils.getMethodWithArgument(obj.getClass(), setMethodName, argType);
            if (m != null && Modifier.isPublic(m.getModifiers())) {
                try {
                    m.invoke(obj, value);
                    fieldSet = true;
                    if (NucleusLogger.QUERY.isDebugEnabled()) {
                        final String msg2 = "ResultObject set field=" + fieldName + " using public " + setMethodName + "() method";
                        NucleusLogger.QUERY.debug(msg2);
                    }
                }
                catch (Exception e2) {}
            }
            else if (m == null) {
                final Method[] methods = AccessController.doPrivileged((PrivilegedAction<Method[]>)new PrivilegedAction() {
                    @Override
                    public Object run() {
                        return obj.getClass().getDeclaredMethods();
                    }
                });
                for (int i = 0; i < methods.length; ++i) {
                    final Class[] args = methods[i].getParameterTypes();
                    if (methods[i].getName().equals(setMethodName) && Modifier.isPublic(methods[i].getModifiers()) && args != null && args.length == 1) {
                        try {
                            methods[i].invoke(obj, ClassUtils.convertValue(value, args[0]));
                            fieldSet = true;
                            if (NucleusLogger.QUERY.isDebugEnabled()) {
                                final String msg3 = "ResultObject set field=" + fieldName + " using " + setMethodName + "() method";
                                NucleusLogger.QUERY.debug(msg3);
                            }
                            break;
                        }
                        catch (Exception ex2) {}
                    }
                }
            }
            if (!fieldSet && NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(QueryUtils.LOCALISER.msg("021207", obj.getClass().getName(), setMethodName, (argType != null) ? argType.getName() : null));
            }
        }
        if (!fieldSet) {
            final Method j = getPublicPutMethodForResultClass(obj.getClass());
            if (j != null) {
                try {
                    j.invoke(obj, fieldName, value);
                    fieldSet = true;
                    if (NucleusLogger.QUERY.isDebugEnabled()) {
                        final String msg4 = "ResultObject set field=" + fieldName + " using put() method";
                        NucleusLogger.QUERY.debug(msg4);
                    }
                }
                catch (Exception ex3) {}
            }
            if (!fieldSet && NucleusLogger.QUERY.isDebugEnabled()) {
                NucleusLogger.QUERY.debug(QueryUtils.LOCALISER.msg("021208", obj.getClass().getName(), "put"));
            }
        }
        return fieldSet;
    }
    
    public static Method getPublicSetMethodForFieldOfResultClass(final Class resultClass, final String fieldName, final Class fieldType) {
        final String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        final Method m = ClassUtils.getMethodWithArgument(resultClass, setMethodName, fieldType);
        if (m != null && Modifier.isPublic(m.getModifiers())) {
            return m;
        }
        return null;
    }
    
    public static Method getPublicPutMethodForResultClass(final Class resultClass) {
        return AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    return resultClass.getMethod("put", (Class[])QueryUtils.classArrayObjectObject);
                }
                catch (NoSuchMethodException ex) {
                    return null;
                }
            }
        });
    }
    
    public static String[] getExpressionsFromString(final String str) {
        final CharacterIterator ci = new StringCharacterIterator(str);
        int braces = 0;
        String text = "";
        final ArrayList exprList = new ArrayList();
        while (ci.getIndex() != ci.getEndIndex()) {
            final char c = ci.current();
            if (c == ',' && braces == 0) {
                exprList.add(text);
                text = "";
            }
            else if (c == '(') {
                ++braces;
                text += c;
            }
            else if (c == ')') {
                --braces;
                text += c;
            }
            else {
                text += c;
            }
            ci.next();
        }
        exprList.add(text);
        return exprList.toArray(new String[exprList.size()]);
    }
    
    public static Object getValueForParameterExpression(final Map parameterValues, final ParameterExpression paramExpr) {
        if (parameterValues == null) {
            return null;
        }
        for (final Object key : parameterValues.keySet()) {
            if (key instanceof Integer) {
                if ((int)key == paramExpr.getPosition()) {
                    return parameterValues.get(key);
                }
                continue;
            }
            else {
                if (key instanceof String && ((String)key).equals(paramExpr.getId())) {
                    return parameterValues.get(key);
                }
                continue;
            }
        }
        return null;
    }
    
    public static String getStringValue(final Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = (String)obj;
        }
        else if (obj instanceof Character) {
            value = ((Character)obj).toString();
        }
        else if (obj instanceof Number) {
            value = ((Number)obj).toString();
        }
        else if (obj != null) {
            throw new NucleusException("getStringValue(obj) where obj is instanceof " + obj.getClass().getName() + " not supported");
        }
        return value;
    }
    
    public static String getStringValueForExpression(final Expression expr, final Map parameters) {
        String paramValue = null;
        if (expr instanceof ParameterExpression) {
            final ParameterExpression paramExpr = (ParameterExpression)expr;
            final Object obj = getValueForParameterExpression(parameters, paramExpr);
            paramValue = getStringValue(obj);
        }
        else {
            if (!(expr instanceof Literal)) {
                throw new NucleusException("getStringValueForExpression(expr) where expr is instanceof " + expr.getClass().getName() + " not supported");
            }
            final Literal literal = (Literal)expr;
            paramValue = getStringValue(literal.getLiteral());
        }
        return paramValue;
    }
    
    public static boolean compareExpressionValues(final Object left, final Object right, final Expression.Operator op) {
        if (left == null || right == null) {
            if (op == Expression.OP_GT) {
                return (left != null || right != null) && left != null;
            }
            if (op == Expression.OP_LT) {
                return (left != null || right != null) && left == null;
            }
            if (op == Expression.OP_GTEQ) {
                return (left == null && right == null) || left != null;
            }
            if (op == Expression.OP_LTEQ) {
                return (left == null && right == null) || left == null;
            }
            if (op == Expression.OP_EQ) {
                return left == right;
            }
            if (op == Expression.OP_NOTEQ) {
                return left != right;
            }
        }
        else if (left instanceof Float || left instanceof Double || left instanceof BigDecimal || right instanceof Float || right instanceof Double || right instanceof BigDecimal) {
            Double leftVal = null;
            Double rightVal = null;
            if (left instanceof BigDecimal) {
                leftVal = new Double(((BigDecimal)left).doubleValue());
            }
            else if (left instanceof Double) {
                leftVal = (Double)left;
            }
            else if (left instanceof Float) {
                leftVal = new Double((double)left);
            }
            else if (left instanceof BigInteger) {
                leftVal = new Double(((BigInteger)left).doubleValue());
            }
            else if (left instanceof Long) {
                leftVal = new Double((double)left);
            }
            else if (left instanceof Integer) {
                leftVal = new Double((double)left);
            }
            else if (left instanceof Short) {
                leftVal = new Double((double)left);
            }
            else if (left instanceof Enum) {
                leftVal = new Double(((Enum)left).ordinal());
            }
            if (right instanceof BigDecimal) {
                rightVal = new Double(((BigDecimal)right).doubleValue());
            }
            else if (right instanceof Double) {
                rightVal = (Double)right;
            }
            else if (right instanceof Float) {
                rightVal = new Double((double)right);
            }
            else if (right instanceof BigInteger) {
                rightVal = new Double(((BigInteger)right).doubleValue());
            }
            else if (right instanceof Long) {
                rightVal = new Double((double)right);
            }
            else if (right instanceof Integer) {
                rightVal = new Double((double)right);
            }
            else if (right instanceof Short) {
                rightVal = new Double((double)right);
            }
            else if (right instanceof Enum) {
                rightVal = new Double(((Enum)right).ordinal());
            }
            if (leftVal == null || rightVal == null) {
                throw new NucleusException("Attempt to evaluate relational expression between\"" + left + "\" (type=" + left.getClass().getName() + ") and" + "\"" + right + "\" (type=" + right.getClass().getName() + ") not possible due to types");
            }
            final int comparison = leftVal.compareTo(rightVal);
            if (op == Expression.OP_GT) {
                return comparison > 0;
            }
            if (op == Expression.OP_LT) {
                return comparison < 0;
            }
            if (op == Expression.OP_GTEQ) {
                return comparison >= 0;
            }
            if (op == Expression.OP_LTEQ) {
                return comparison <= 0;
            }
            if (op == Expression.OP_EQ) {
                return comparison == 0;
            }
            if (op == Expression.OP_NOTEQ) {
                return comparison != 0;
            }
        }
        else if (left instanceof Short || left instanceof Integer || left instanceof Long || left instanceof BigInteger || left instanceof Character || right instanceof Short || right instanceof Integer || right instanceof Long || right instanceof BigInteger || right instanceof Character) {
            boolean leftUnset = false;
            boolean rightUnset = false;
            long leftVal2 = Long.MAX_VALUE;
            long rightVal2 = Long.MAX_VALUE;
            if (left instanceof BigInteger) {
                leftVal2 = ((BigInteger)left).longValue();
            }
            else if (left instanceof Long) {
                leftVal2 = (long)left;
            }
            else if (left instanceof Integer) {
                leftVal2 = (long)left;
            }
            else if (left instanceof Short) {
                leftVal2 = (long)left;
            }
            else if (left instanceof BigDecimal) {
                leftVal2 = ((BigDecimal)left).longValue();
            }
            else if (left instanceof Double) {
                leftVal2 = ((Double)left).longValue();
            }
            else if (left instanceof Float) {
                leftVal2 = ((Float)left).longValue();
            }
            else if (left instanceof Enum) {
                leftVal2 = ((Enum)left).ordinal();
            }
            else if (left instanceof Byte) {
                leftVal2 = (long)left;
            }
            else if (left instanceof Character) {
                leftVal2 = (char)left;
            }
            else {
                leftUnset = true;
            }
            if (right instanceof BigInteger) {
                rightVal2 = ((BigInteger)right).longValue();
            }
            else if (right instanceof Long) {
                rightVal2 = (long)right;
            }
            else if (right instanceof Integer) {
                rightVal2 = (long)right;
            }
            else if (right instanceof Short) {
                rightVal2 = (long)right;
            }
            else if (right instanceof BigDecimal) {
                rightVal2 = ((BigDecimal)right).longValue();
            }
            else if (right instanceof Double) {
                rightVal2 = ((Double)right).longValue();
            }
            else if (right instanceof Float) {
                rightVal2 = ((Float)right).longValue();
            }
            else if (right instanceof Enum) {
                rightVal2 = ((Enum)right).ordinal();
            }
            else if (right instanceof Byte) {
                rightVal2 = (long)right;
            }
            else if (right instanceof Character) {
                rightVal2 = (char)right;
            }
            else {
                rightUnset = true;
            }
            if (leftUnset || rightUnset) {
                throw new NucleusException("Attempt to evaluate relational expression between\"" + left + "\" (type=" + left.getClass().getName() + ") and" + "\"" + right + "\" (type=" + right.getClass().getName() + ") not possible due to types");
            }
            if (op == Expression.OP_GT) {
                return leftVal2 > rightVal2;
            }
            if (op == Expression.OP_LT) {
                return leftVal2 < rightVal2;
            }
            if (op == Expression.OP_GTEQ) {
                return leftVal2 >= rightVal2;
            }
            if (op == Expression.OP_LTEQ) {
                return leftVal2 <= rightVal2;
            }
            if (op == Expression.OP_EQ) {
                return leftVal2 == rightVal2;
            }
            if (op == Expression.OP_NOTEQ) {
                return leftVal2 != rightVal2;
            }
        }
        else if (left instanceof Enum || right instanceof Enum || left instanceof String || right instanceof String) {
            final String leftStr = left.toString();
            final String rightStr = right.toString();
            if (op == Expression.OP_EQ) {
                return (leftStr != null) ? leftStr.equals(rightStr) : (rightStr == null);
            }
            if (op == Expression.OP_NOTEQ) {
                return (leftStr != null) ? (!leftStr.equals(rightStr)) : (rightStr != null);
            }
            if (op == Expression.OP_GT) {
                return leftStr != null && leftStr.compareTo(rightStr) > 0;
            }
            if (op == Expression.OP_GTEQ) {
                return leftStr != null && leftStr.compareTo(rightStr) >= 0;
            }
            if (op == Expression.OP_LT) {
                return leftStr != null && leftStr.compareTo(rightStr) < 0;
            }
            if (op == Expression.OP_LTEQ) {
                return leftStr != null && leftStr.compareTo(rightStr) <= 0;
            }
            throw new NucleusException("Attempt to evaluate relational expression between\"" + left + "\" (type=" + left.getClass().getName() + ") and" + "\"" + right + "\" (type=" + right.getClass().getName() + ") not possible due to types");
        }
        else if (left instanceof Date || right instanceof Date) {
            long leftVal3 = Long.MAX_VALUE;
            long rightVal3 = Long.MAX_VALUE;
            if (left instanceof Date) {
                leftVal3 = ((Date)left).getTime();
            }
            if (right instanceof Date) {
                rightVal3 = ((Date)right).getTime();
            }
            if (leftVal3 == Long.MAX_VALUE || rightVal3 == Long.MAX_VALUE) {
                throw new NucleusException("Attempt to evaluate relational expression between\"" + left + "\" (type=" + left.getClass().getName() + ") and" + "\"" + right + "\" (type=" + right.getClass().getName() + ") not possible due to types");
            }
            if (op == Expression.OP_GT) {
                return leftVal3 > rightVal3;
            }
            if (op == Expression.OP_LT) {
                return leftVal3 < rightVal3;
            }
            if (op == Expression.OP_GTEQ) {
                return leftVal3 >= rightVal3;
            }
            if (op == Expression.OP_LTEQ) {
                return leftVal3 <= rightVal3;
            }
            if (op == Expression.OP_EQ) {
                return leftVal3 == rightVal3;
            }
            if (op == Expression.OP_NOTEQ) {
                return leftVal3 != rightVal3;
            }
        }
        else {
            if (op == Expression.OP_EQ) {
                return left.equals(right);
            }
            if (op == Expression.OP_NOTEQ) {
                return !left.equals(right);
            }
            if (left instanceof Comparable && right instanceof Comparable) {
                final Comparable leftC = (Comparable)left;
                final Comparable rightC = (Comparable)right;
                if (op == Expression.OP_GT) {
                    return leftC.compareTo(rightC) > 0;
                }
                if (op == Expression.OP_LT) {
                    return leftC.compareTo(rightC) < 0;
                }
                if (op == Expression.OP_LTEQ) {
                    return leftC.compareTo(rightC) < 0 || leftC.compareTo(rightC) == 0;
                }
                if (op == Expression.OP_GTEQ) {
                    return leftC.compareTo(rightC) > 0 || leftC.compareTo(rightC) == 0;
                }
            }
            throw new NucleusException("Attempt to evaluate relational expression between\"" + left + "\" (type=" + left.getClass().getName() + ") and" + "\"" + right + "\" (type=" + right.getClass().getName() + ") not possible due to types");
        }
        throw new NucleusException("Attempt to evaluate relational expression between " + left + " and " + right + " with operation = " + op + " impossible to perform");
    }
    
    public static boolean expressionHasOrOperator(final Expression expr) {
        return (expr instanceof DyadicExpression && expr.getOperator() == Expression.OP_OR) || (expr.getLeft() != null && expressionHasOrOperator(expr.getLeft())) || (expr.getRight() != null && expressionHasOrOperator(expr.getRight()));
    }
    
    public static boolean expressionHasNotOperator(final Expression expr) {
        return (expr instanceof DyadicExpression && expr.getOperator() == Expression.OP_NOT) || (expr.getLeft() != null && expressionHasNotOperator(expr.getLeft())) || (expr.getRight() != null && expressionHasNotOperator(expr.getRight()));
    }
    
    public static ParameterExpression getParameterExpressionForPosition(final Expression rootExpr, final int pos) {
        if (rootExpr instanceof ParameterExpression && ((ParameterExpression)rootExpr).getPosition() == pos) {
            return (ParameterExpression)rootExpr;
        }
        if (rootExpr.getLeft() != null) {
            final ParameterExpression paramExpr = getParameterExpressionForPosition(rootExpr.getLeft(), pos);
            if (paramExpr != null) {
                return paramExpr;
            }
        }
        if (rootExpr.getRight() != null) {
            final ParameterExpression paramExpr = getParameterExpressionForPosition(rootExpr.getRight(), pos);
            if (paramExpr != null) {
                return paramExpr;
            }
        }
        if (rootExpr instanceof InvokeExpression) {
            final InvokeExpression invokeExpr = (InvokeExpression)rootExpr;
            final List<Expression> args = invokeExpr.getArguments();
            if (args != null) {
                final Iterator<Expression> argIter = args.iterator();
                while (argIter.hasNext()) {
                    final ParameterExpression paramExpr2 = getParameterExpressionForPosition(argIter.next(), pos);
                    if (paramExpr2 != null) {
                        return paramExpr2;
                    }
                }
            }
        }
        return null;
    }
    
    public static boolean queryParameterTypesAreCompatible(final Class cls1, final Class cls2) {
        Class first = cls1;
        Class second = cls2;
        if (cls1.isPrimitive()) {
            first = ClassUtils.getWrapperTypeForPrimitiveType(cls1);
        }
        if (cls2.isPrimitive()) {
            second = ClassUtils.getWrapperTypeForPrimitiveType(cls2);
        }
        return first.isAssignableFrom(second) || (Number.class.isAssignableFrom(first) && Number.class.isAssignableFrom(cls2));
    }
    
    public static String getKeyForQueryResultsCache(final Query query, final Map params) {
        if (params != null && params.size() > 0) {
            return query.getLanguage() + ":" + query.toString() + ":" + params.hashCode();
        }
        return query.getLanguage() + ":" + query.toString() + ":";
    }
    
    public static List orderCandidates(final List candidates, final Class type, final String ordering, final ExecutionContext ec, final ClassLoaderResolver clr) {
        return orderCandidates(candidates, type, ordering, ec, clr, "JDOQL");
    }
    
    public static List orderCandidates(final List candidates, final Class type, final String ordering, final ExecutionContext ec, final ClassLoaderResolver clr, final String queryLanguage) {
        if (candidates == null || candidates.isEmpty() || ordering == null || ordering.equals("#PK")) {
            return candidates;
        }
        final JavaQueryCompiler compiler = new JPQLCompiler(ec.getMetaDataManager(), ec.getClassLoaderResolver(), null, type, null, null, null, ordering, null, null, null, null, null);
        final QueryCompilation compilation = compiler.compile(null, null);
        return orderCandidates(candidates, compilation.getExprOrdering(), new HashMap(), "this", ec, clr, null, null, queryLanguage);
    }
    
    public static List orderCandidates(final List candidates, final Expression[] ordering, final Map state, final String candidateAlias, final ExecutionContext ec, final ClassLoaderResolver clr, final Map parameterValues, final Imports imports, final String queryLanguage) {
        if (ordering == null) {
            return candidates;
        }
        final Object[] o = candidates.toArray();
        Arrays.sort(o, new Comparator() {
            @Override
            public int compare(final Object obj1, final Object obj2) {
                int i = 0;
                while (i < ordering.length) {
                    state.put(candidateAlias, obj1);
                    final Object a = ordering[i].evaluate(new InMemoryExpressionEvaluator(ec, parameterValues, state, imports, clr, candidateAlias, queryLanguage));
                    state.put(candidateAlias, obj2);
                    final Object b = ordering[i].evaluate(new InMemoryExpressionEvaluator(ec, parameterValues, state, imports, clr, candidateAlias, queryLanguage));
                    final OrderExpression orderExpr = (OrderExpression)ordering[i];
                    if (a == null && b == null) {
                        return 0;
                    }
                    if (a == null) {
                        if (orderExpr.getNullOrder() != null) {
                            return (orderExpr.getNullOrder() == NullOrderingType.NULLS_FIRST) ? 1 : -1;
                        }
                        return -1;
                    }
                    else if (b == null) {
                        if (orderExpr.getNullOrder() != null) {
                            return (orderExpr.getNullOrder() == NullOrderingType.NULLS_FIRST) ? -1 : 1;
                        }
                        return 1;
                    }
                    else {
                        final int result = ((Comparable)a).compareTo(b);
                        if (result != 0) {
                            if (orderExpr.getSortOrder() == null || orderExpr.getSortOrder().equals("ascending")) {
                                return result;
                            }
                            return -1 * result;
                        }
                        else {
                            ++i;
                        }
                    }
                }
                return 0;
            }
        });
        return Arrays.asList(o);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        classArrayObjectObject = new Class[] { Object.class, Object.class };
    }
}
