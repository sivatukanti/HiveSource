// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.ClassConstants;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.StringUtils;
import java.lang.reflect.Array;
import org.datanucleus.query.expression.ArrayExpression;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import org.datanucleus.query.expression.CaseExpression;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.InvokeExpression;
import java.util.List;
import org.datanucleus.query.expression.ExpressionEvaluator;
import java.util.ArrayList;
import org.datanucleus.query.expression.CreatorExpression;
import org.datanucleus.query.expression.Literal;
import java.math.BigInteger;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.DyadicExpression;
import java.math.BigDecimal;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.QueryUtils;
import org.datanucleus.query.expression.Expression;
import java.util.HashMap;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.Imports;
import java.util.Map;
import java.util.Stack;
import org.datanucleus.util.Localiser;
import org.datanucleus.query.evaluator.AbstractExpressionEvaluator;

public class InMemoryExpressionEvaluator extends AbstractExpressionEvaluator
{
    protected static final Localiser LOCALISER;
    String queryLanguage;
    Stack stack;
    Map parameterValues;
    Map<String, Object> variableValues;
    Map<String, Object> state;
    Imports imports;
    ExecutionContext ec;
    ClassLoaderResolver clr;
    QueryManager queryMgr;
    final String candidateAlias;
    
    public InMemoryExpressionEvaluator(final ExecutionContext ec, final Map params, final Map<String, Object> state, final Imports imports, final ClassLoaderResolver clr, final String candidateAlias, final String queryLang) {
        this.queryLanguage = null;
        this.stack = new Stack();
        this.ec = ec;
        this.queryMgr = ec.getStoreManager().getQueryManager();
        this.parameterValues = ((params != null) ? params : new HashMap());
        this.state = state;
        this.imports = imports;
        this.clr = clr;
        this.candidateAlias = candidateAlias;
        this.queryLanguage = queryLang;
    }
    
    public Map getParameterValues() {
        return this.parameterValues;
    }
    
    public String getQueryLanguage() {
        return this.queryLanguage;
    }
    
    @Override
    protected Object processAndExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        this.stack.push((left == Boolean.TRUE && right == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE);
        return this.stack.peek();
    }
    
    @Override
    protected Object processEqExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        final Boolean result = QueryUtils.compareExpressionValues(left, right, expr.getOperator()) ? Boolean.TRUE : Boolean.FALSE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processLikeExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        if (!(left instanceof String)) {
            throw new NucleusUserException("LIKE expression can only be used on a String expression, but found on " + left.getClass().getName());
        }
        if (right instanceof String) {
            final Boolean result = ((String)left).matches((String)right) ? Boolean.TRUE : Boolean.FALSE;
            this.stack.push(result);
            return result;
        }
        throw new NucleusUserException("Dont currently support expression on right of LIKE to be other than String but was " + right.getClass().getName());
    }
    
    @Override
    protected Object processNoteqExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        final Boolean result = QueryUtils.compareExpressionValues(left, right, expr.getOperator()) ? Boolean.TRUE : Boolean.FALSE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processOrExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        this.stack.push((left == Boolean.TRUE || right == Boolean.TRUE) ? Boolean.TRUE : Boolean.FALSE);
        return this.stack.peek();
    }
    
    @Override
    protected Object processGteqExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        final Boolean result = QueryUtils.compareExpressionValues(left, right, expr.getOperator()) ? Boolean.TRUE : Boolean.FALSE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processGtExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        final Boolean result = QueryUtils.compareExpressionValues(left, right, expr.getOperator()) ? Boolean.TRUE : Boolean.FALSE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processIsExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        if (!(right instanceof Class)) {
            throw new NucleusException("Attempt to invoke instanceof with argument of type " + right.getClass().getName() + " has to be Class");
        }
        try {
            final Boolean result = ((Class)right).isAssignableFrom(left.getClass()) ? Boolean.TRUE : Boolean.FALSE;
            this.stack.push(result);
            return result;
        }
        catch (ClassNotResolvedException cnre) {
            throw new NucleusException("Attempt to invoke instanceof with " + right + " yet class was not found!");
        }
    }
    
    @Override
    protected Object processIsnotExpression(final Expression expr) {
        this.processIsExpression(expr);
        Boolean val = this.stack.pop();
        val = !val;
        this.stack.push(val);
        return val;
    }
    
    @Override
    protected Object processCastExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        throw new NucleusException("CAST not yet supported in in-memory evaluator");
    }
    
    @Override
    protected Object processLteqExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        final Boolean result = QueryUtils.compareExpressionValues(left, right, expr.getOperator()) ? Boolean.TRUE : Boolean.FALSE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processLtExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        if (left instanceof InMemoryFailure || right instanceof InMemoryFailure) {
            this.stack.push(Boolean.FALSE);
            return this.stack.peek();
        }
        final Boolean result = QueryUtils.compareExpressionValues(left, right, expr.getOperator()) ? Boolean.TRUE : Boolean.FALSE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processAddExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        Object value = null;
        if (right instanceof String && left instanceof String) {
            value = "" + left + right;
        }
        else if (right instanceof Number && left instanceof Number) {
            value = new BigDecimal(left.toString()).add(new BigDecimal(right.toString()));
        }
        else {
            if (!(left instanceof String)) {
                throw new NucleusException("Performing ADD operation on " + left + " and " + right + " is not supported");
            }
            value = "" + left + right;
        }
        this.stack.push(value);
        return this.stack.peek();
    }
    
    @Override
    protected Object processSubExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        final Object value = new BigDecimal(left.toString()).subtract(new BigDecimal(right.toString()));
        this.stack.push(value);
        return this.stack.peek();
    }
    
    @Override
    protected Object processDivExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        final double firstValue = new BigDecimal(left.toString()).doubleValue();
        final double secondValue = new BigDecimal(right.toString()).doubleValue();
        final BigDecimal value = new BigDecimal(firstValue / secondValue);
        this.stack.push(value);
        return this.stack.peek();
    }
    
    @Override
    protected Object processModExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        final BigDecimal firstValue = new BigDecimal(left.toString());
        final BigDecimal divisor = new BigDecimal(right.toString());
        final Object value = firstValue.subtract(firstValue.divideToIntegralValue(divisor).multiply(divisor));
        this.stack.push(value);
        return this.stack.peek();
    }
    
    @Override
    protected Object processMulExpression(final Expression expr) {
        final Object right = this.stack.pop();
        final Object left = this.stack.pop();
        final Object value = new BigDecimal(left.toString()).multiply(new BigDecimal(right.toString()));
        this.stack.push(value);
        return this.stack.peek();
    }
    
    @Override
    protected Object processNegExpression(final Expression expr) {
        Number val = null;
        if (expr instanceof DyadicExpression) {
            final DyadicExpression dyExpr = (DyadicExpression)expr;
            if (dyExpr.getLeft() instanceof PrimaryExpression) {
                val = (Number)this.getValueForPrimaryExpression((PrimaryExpression)expr.getLeft());
            }
            else {
                if (!(dyExpr.getLeft() instanceof ParameterExpression)) {
                    throw new NucleusException("No current support for negation of dyadic expression on type " + dyExpr.getLeft().getClass().getName());
                }
                val = (Number)QueryUtils.getValueForParameterExpression(this.parameterValues, (ParameterExpression)expr.getLeft());
            }
            if (val instanceof Integer) {
                this.stack.push(-val.intValue());
                return this.stack.peek();
            }
            if (val instanceof Long) {
                this.stack.push(-val.longValue());
                return this.stack.peek();
            }
            if (val instanceof Short) {
                this.stack.push((short)(-val.shortValue()));
                return this.stack.peek();
            }
            if (val instanceof BigInteger) {
                this.stack.push(BigInteger.valueOf(-val.longValue()));
                return this.stack.peek();
            }
            if (val instanceof Double) {
                this.stack.push(-val.doubleValue());
                return this.stack.peek();
            }
            if (val instanceof Float) {
                this.stack.push(-val.floatValue());
                return this.stack.peek();
            }
            if (val instanceof BigDecimal) {
                this.stack.push(new BigDecimal(-val.doubleValue()));
                return this.stack.peek();
            }
            throw new NucleusException("Attempt to negate value of type " + val + " not supported");
        }
        else {
            if (expr instanceof Literal) {
                throw new NucleusException("No current support for negation of expression of type Literal");
            }
            throw new NucleusException("No current support for negation of expression of type " + expr.getClass().getName());
        }
    }
    
    @Override
    protected Object processComExpression(final Expression expr) {
        final PrimaryExpression primExpr = (PrimaryExpression)expr.getLeft();
        final Object primVal = this.getValueForPrimaryExpression(primExpr);
        int val = -1;
        if (primVal instanceof Number) {
            val = ((Number)primVal).intValue();
        }
        final Integer result = ~val;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processNotExpression(final Expression expr) {
        final Boolean left = this.stack.pop();
        final Boolean result = ((boolean)left) ? Boolean.FALSE : Boolean.TRUE;
        this.stack.push(result);
        return this.stack.peek();
    }
    
    @Override
    protected Object processCreatorExpression(final CreatorExpression expr) {
        final List params = new ArrayList();
        for (int i = 0; i < expr.getArguments().size(); ++i) {
            params.add(expr.getArguments().get(i).evaluate(this));
        }
        final Class cls = this.imports.resolveClassDeclaration(expr.getId(), this.clr, null);
        final Object value = QueryUtils.createResultObjectUsingArgumentedConstructor(cls, params.toArray(), null);
        this.stack.push(value);
        return value;
    }
    
    @Override
    protected Object processInvokeExpression(final InvokeExpression expr) {
        final Object result = this.getValueForInvokeExpression(expr);
        this.stack.push(result);
        return result;
    }
    
    @Override
    protected Object processLiteral(final Literal expr) {
        final Object value = expr.getLiteral();
        this.stack.push(value);
        return value;
    }
    
    @Override
    protected Object processVariableExpression(final VariableExpression expr) {
        if (expr.getLeft() == null && this.state.containsKey(expr.getId())) {
            Object value = this.state.get(expr.getId());
            if (value == null) {
                NucleusLogger.QUERY.warn("Variable expression " + expr.getId() + " doesnt have its value set yet. Unsupported query structure");
                value = new InMemoryFailure();
            }
            this.stack.push(value);
            return value;
        }
        return super.processVariableExpression(expr);
    }
    
    @Override
    protected Object processParameterExpression(final ParameterExpression expr) {
        final Object value = QueryUtils.getValueForParameterExpression(this.parameterValues, expr);
        this.stack.push(value);
        return value;
    }
    
    @Override
    protected Object processPrimaryExpression(final PrimaryExpression expr) {
        final Object paramValue = (this.parameterValues != null) ? this.parameterValues.get(expr.getId()) : null;
        if (expr.getLeft() == null && paramValue != null) {
            this.stack.push(paramValue);
            return paramValue;
        }
        final Object value = this.getValueForPrimaryExpression(expr);
        this.stack.push(value);
        return value;
    }
    
    @Override
    protected Object processCaseExpression(final CaseExpression expr) {
        final Map<Expression, Expression> exprs = expr.getConditions();
        for (final Map.Entry<Expression, Expression> entry : exprs.entrySet()) {
            final Expression keyExpr = entry.getKey();
            final Expression valExpr = entry.getValue();
            final Object keyResult = keyExpr.evaluate(this);
            if (!(keyResult instanceof Boolean)) {
                NucleusLogger.QUERY.error("Case expression " + expr + " clause " + keyExpr + " did not return boolean");
                final Object value = new InMemoryFailure();
                this.stack.push(value);
                return value;
            }
            if (keyResult) {
                final Object value = valExpr.evaluate(this);
                this.stack.push(value);
                return value;
            }
        }
        final Object value2 = expr.getElseExpression().evaluate(this);
        this.stack.push(value2);
        return value2;
    }
    
    public Object getValueForInvokeExpression(final InvokeExpression invokeExpr) {
        final String method = invokeExpr.getOperation();
        if (invokeExpr.getLeft() == null) {
            if (method.toLowerCase().equals("count")) {
                Collection coll = this.state.get("DATANUCLEUS_RESULTS_SET");
                final SetExpression setexpr = new SetExpression(coll, this.candidateAlias);
                final Expression paramExpr = invokeExpr.getArguments().get(0);
                if (paramExpr.getOperator() == Expression.OP_DISTINCT) {
                    final Collection processable = coll = new HashSet(coll);
                }
                return setexpr.count(paramExpr, this);
            }
            if (method.toLowerCase().equals("sum")) {
                Collection coll = this.state.get("DATANUCLEUS_RESULTS_SET");
                final SetExpression setexpr = new SetExpression(coll, this.candidateAlias);
                final Expression paramExpr = invokeExpr.getArguments().get(0);
                if (paramExpr.getOperator() == Expression.OP_DISTINCT) {
                    final Collection processable = coll = new HashSet(coll);
                }
                return setexpr.sum(paramExpr, this, this.state);
            }
            if (method.toLowerCase().equals("avg")) {
                Collection coll = this.state.get("DATANUCLEUS_RESULTS_SET");
                final SetExpression setexpr = new SetExpression(coll, this.candidateAlias);
                final Expression paramExpr = invokeExpr.getArguments().get(0);
                if (paramExpr.getOperator() == Expression.OP_DISTINCT) {
                    final Collection processable = coll = new HashSet(coll);
                }
                return setexpr.avg(paramExpr, this, this.state);
            }
            if (method.toLowerCase().equals("min")) {
                Collection coll = this.state.get("DATANUCLEUS_RESULTS_SET");
                final SetExpression setexpr = new SetExpression(coll, this.candidateAlias);
                final Expression paramExpr = invokeExpr.getArguments().get(0);
                if (paramExpr.getOperator() == Expression.OP_DISTINCT) {
                    final Collection processable = coll = new HashSet(coll);
                }
                return setexpr.min(paramExpr, this, this.state);
            }
            if (method.toLowerCase().equals("max")) {
                Collection coll = this.state.get("DATANUCLEUS_RESULTS_SET");
                final SetExpression setexpr = new SetExpression(coll, this.candidateAlias);
                final Expression paramExpr = invokeExpr.getArguments().get(0);
                if (paramExpr.getOperator() == Expression.OP_DISTINCT) {
                    final Collection processable = coll = new HashSet(coll);
                }
                return setexpr.max(paramExpr, this, this.state);
            }
            final InvocationEvaluator methodEval = this.queryMgr.getInMemoryEvaluatorForMethod(null, method);
            if (methodEval != null) {
                return methodEval.evaluate(invokeExpr, null, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to static method " + method + " yet no support is available for in-memory evaluation of this");
            return new InMemoryFailure();
        }
        else if (invokeExpr.getLeft() instanceof ParameterExpression) {
            final Object invokedValue = QueryUtils.getValueForParameterExpression(this.parameterValues, (ParameterExpression)invokeExpr.getLeft());
            final Class invokedType = (invokedValue != null) ? invokedValue.getClass() : invokeExpr.getLeft().getSymbol().getValueType();
            final InvocationEvaluator methodEval2 = this.queryMgr.getInMemoryEvaluatorForMethod(invokedType, method);
            if (methodEval2 != null) {
                return methodEval2.evaluate(invokeExpr, invokedValue, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to method " + invokedValue.getClass().getName() + "." + method + " yet no support is available for this");
            return new InMemoryFailure();
        }
        else if (invokeExpr.getLeft() instanceof PrimaryExpression) {
            final Object invokedValue = this.getValueForPrimaryExpression((PrimaryExpression)invokeExpr.getLeft());
            if (invokedValue instanceof InMemoryFailure) {
                return invokedValue;
            }
            final Class invokedType = (invokedValue != null) ? invokedValue.getClass() : invokeExpr.getLeft().getSymbol().getValueType();
            final InvocationEvaluator methodEval2 = this.queryMgr.getInMemoryEvaluatorForMethod(invokedType, method);
            if (methodEval2 != null) {
                return methodEval2.evaluate(invokeExpr, invokedValue, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to method " + invokedType.getName() + "." + method + " yet no support is available for this");
            return new InMemoryFailure();
        }
        else if (invokeExpr.getLeft() instanceof InvokeExpression) {
            final Object invokedValue = this.getValueForInvokeExpression((InvokeExpression)invokeExpr.getLeft());
            final Class invokedType = (invokedValue != null) ? invokedValue.getClass() : ((invokeExpr.getLeft().getSymbol() != null) ? invokeExpr.getLeft().getSymbol().getValueType() : null);
            if (invokedType == null) {
                return new InMemoryFailure();
            }
            final InvocationEvaluator methodEval2 = this.queryMgr.getInMemoryEvaluatorForMethod(invokedType, method);
            if (methodEval2 != null) {
                return methodEval2.evaluate(invokeExpr, invokedValue, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to method " + invokedType.getName() + "." + method + " yet no support is available for this");
            return new InMemoryFailure();
        }
        else if (invokeExpr.getLeft() instanceof VariableExpression) {
            final Object invokedValue = this.getValueForVariableExpression((VariableExpression)invokeExpr.getLeft());
            final Class invokedType = (invokedValue != null) ? invokedValue.getClass() : invokeExpr.getLeft().getSymbol().getValueType();
            final InvocationEvaluator methodEval2 = this.queryMgr.getInMemoryEvaluatorForMethod(invokedType, method);
            if (methodEval2 != null) {
                return methodEval2.evaluate(invokeExpr, invokedValue, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to method " + invokedType.getName() + "." + method + " yet no support is available for this");
            return new InMemoryFailure();
        }
        else if (invokeExpr.getLeft() instanceof Literal) {
            final Object invokedValue = ((Literal)invokeExpr.getLeft()).getLiteral();
            final Class invokedType = (invokedValue != null) ? invokedValue.getClass() : invokeExpr.getLeft().getSymbol().getValueType();
            final InvocationEvaluator methodEval2 = this.queryMgr.getInMemoryEvaluatorForMethod(invokedType, method);
            if (methodEval2 != null) {
                return methodEval2.evaluate(invokeExpr, invokedValue, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to method " + invokedType.getName() + "." + method + " yet no support is available for this");
            return new InMemoryFailure();
        }
        else {
            if (!(invokeExpr.getLeft() instanceof ArrayExpression)) {
                NucleusLogger.QUERY.warn("No support is available for in-memory evaluation of methods invoked on expressions of type " + invokeExpr.getLeft().getClass().getName());
                return new InMemoryFailure();
            }
            final Object invokedValue = this.getValueForArrayExpression((ArrayExpression)invokeExpr.getLeft());
            final Class invokedType = (invokedValue != null) ? invokedValue.getClass() : invokeExpr.getLeft().getSymbol().getValueType();
            final InvocationEvaluator methodEval2 = this.queryMgr.getInMemoryEvaluatorForMethod(invokedType, method);
            if (methodEval2 != null) {
                return methodEval2.evaluate(invokeExpr, invokedValue, this);
            }
            NucleusLogger.QUERY.warn("Query contains call to method " + invokedType.getName() + "." + method + " yet no support is available for this");
            return new InMemoryFailure();
        }
    }
    
    private Object getValueForArrayExpression(final ArrayExpression arrayExpr) {
        final Object value = new Object[arrayExpr.getArraySize()];
        for (int i = 0; i < Array.getLength(value); ++i) {
            final Expression elem = arrayExpr.getElement(i);
            if (elem instanceof Literal) {
                Array.set(value, i, ((Literal)elem).getLiteral());
            }
            else if (elem instanceof PrimaryExpression) {
                Array.set(value, i, this.getValueForPrimaryExpression((PrimaryExpression)elem));
            }
            else {
                if (!(elem instanceof ParameterExpression)) {
                    NucleusLogger.QUERY.warn("No support is available for array expression with element of type " + elem.getClass().getName());
                    return new InMemoryFailure();
                }
                Array.set(value, i, QueryUtils.getValueForParameterExpression(this.parameterValues, (ParameterExpression)elem));
            }
        }
        return value;
    }
    
    public int getIntegerForLiteral(final Literal lit) {
        final Object val = lit.getLiteral();
        if (val instanceof BigDecimal) {
            return ((BigDecimal)val).intValue();
        }
        if (val instanceof BigInteger) {
            return ((BigInteger)val).intValue();
        }
        if (val instanceof Long) {
            return ((Long)val).intValue();
        }
        if (val instanceof Integer) {
            return (int)val;
        }
        if (val instanceof Short) {
            return (int)val;
        }
        throw new NucleusException("Attempt to convert literal with value " + val + " (" + val.getClass().getName() + ") into an int failed");
    }
    
    public Object getValueForPrimaryExpression(final PrimaryExpression primExpr) {
        Object value = null;
        if (primExpr.getLeft() != null) {
            if (primExpr.getLeft() instanceof DyadicExpression) {
                final DyadicExpression dyExpr = (DyadicExpression)primExpr.getLeft();
                if (dyExpr.getOperator() != Expression.OP_CAST) {
                    NucleusLogger.QUERY.error("Dont currently support PrimaryExpression starting with DyadicExpression of " + dyExpr);
                    return new InMemoryFailure();
                }
                final Expression castLeftExpr = dyExpr.getLeft();
                if (castLeftExpr instanceof PrimaryExpression) {
                    value = this.getValueForPrimaryExpression((PrimaryExpression)castLeftExpr);
                    final String castClassName = (String)((Literal)dyExpr.getRight()).getLiteral();
                    if (value != null) {
                        final Class castClass = this.imports.resolveClassDeclaration(castClassName, this.clr, null);
                        if (!castClass.isAssignableFrom(value.getClass())) {
                            NucleusLogger.QUERY.warn("Candidate for query results in attempt to cast " + StringUtils.toJVMIDString(value) + " to " + castClass.getName() + " which is impossible!");
                            return new InMemoryFailure();
                        }
                    }
                }
                else {
                    if (!(castLeftExpr instanceof VariableExpression)) {
                        NucleusLogger.QUERY.warn("Dont currently support CastExpression of " + castLeftExpr);
                        return new InMemoryFailure();
                    }
                    value = this.getValueForVariableExpression((VariableExpression)castLeftExpr);
                    final String castClassName = (String)((Literal)dyExpr.getRight()).getLiteral();
                    if (value != null) {
                        final Class castClass = this.imports.resolveClassDeclaration(castClassName, this.clr, null);
                        if (!castClass.isAssignableFrom(value.getClass())) {
                            NucleusLogger.QUERY.warn("Candidate for query results in attempt to cast " + StringUtils.toJVMIDString(value) + " to " + castClass.getName() + " which is impossible!");
                            return new InMemoryFailure();
                        }
                    }
                }
            }
            else if (primExpr.getLeft() instanceof ParameterExpression) {
                value = QueryUtils.getValueForParameterExpression(this.parameterValues, (ParameterExpression)primExpr.getLeft());
            }
            else {
                if (!(primExpr.getLeft() instanceof VariableExpression)) {
                    NucleusLogger.QUERY.warn("Dont currently support PrimaryExpression with left-side of " + primExpr.getLeft());
                    return new InMemoryFailure();
                }
                final VariableExpression varExpr = (VariableExpression)primExpr.getLeft();
                try {
                    value = this.getValueForVariableExpression(varExpr);
                }
                catch (VariableNotSetException vnse) {
                    NucleusLogger.QUERY.error("Attempt to access variable " + varExpr.getId() + " as part of primaryExpression " + primExpr);
                    return new InMemoryFailure();
                }
            }
        }
        int firstTupleToProcess = 0;
        if (value == null) {
            if (this.state.containsKey(primExpr.getTuples().get(0))) {
                value = this.state.get(primExpr.getTuples().get(0));
                firstTupleToProcess = 1;
            }
            else if (this.state.containsKey(this.candidateAlias)) {
                value = this.state.get(this.candidateAlias);
            }
        }
        for (int i = firstTupleToProcess; i < primExpr.getTuples().size(); ++i) {
            final String fieldName = primExpr.getTuples().get(i);
            if (!fieldName.equals(this.candidateAlias)) {
                boolean getValueByReflection = true;
                if (this.ec.getApiAdapter().isPersistent(value)) {
                    final ObjectProvider valueSM = this.ec.findObjectProvider(value);
                    if (valueSM != null) {
                        final AbstractMemberMetaData mmd = valueSM.getClassMetaData().getMetaDataForMember(fieldName);
                        if (mmd == null) {
                            NucleusLogger.QUERY.error("Cannot find " + fieldName + " member of " + valueSM.getClassMetaData().getFullClassName());
                            return new InMemoryFailure();
                        }
                        if (mmd.getAbsoluteFieldNumber() >= 0) {
                            valueSM.isLoaded(mmd.getAbsoluteFieldNumber());
                            value = valueSM.provideField(mmd.getAbsoluteFieldNumber());
                            getValueByReflection = false;
                        }
                    }
                }
                if (getValueByReflection) {
                    value = ClassUtils.getValueOfFieldByReflection(value, fieldName);
                }
            }
        }
        return value;
    }
    
    public void setVariableValue(final String id, final Object value) {
        if (this.variableValues == null) {
            this.variableValues = new HashMap<String, Object>();
        }
        this.variableValues.put(id, value);
    }
    
    public void removeVariableValue(final String id) {
        this.variableValues.remove(id);
    }
    
    public Object getValueForVariableExpression(final VariableExpression varExpr) {
        if (this.variableValues == null || !this.variableValues.containsKey(varExpr.getId())) {
            throw new VariableNotSetException(varExpr);
        }
        return this.variableValues.get(varExpr.getId());
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
