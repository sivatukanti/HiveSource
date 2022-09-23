// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.evaluator.memory;

import org.datanucleus.exceptions.NucleusException;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import org.datanucleus.query.expression.ExpressionEvaluator;
import org.datanucleus.query.expression.Expression;
import java.util.Collection;
import java.util.Iterator;

public class SetExpression
{
    String alias;
    Iterator itemIterator;
    
    public SetExpression(final Collection items, final String alias) {
        this.alias = "this";
        this.itemIterator = ((items != null) ? items.iterator() : null);
        this.alias = alias;
    }
    
    public Object count(final Expression expr, final ExpressionEvaluator eval) {
        if (this.itemIterator == null) {
            return 0;
        }
        int i = 0;
        while (this.itemIterator.hasNext()) {
            this.itemIterator.next();
            ++i;
        }
        return i;
    }
    
    public Object min(final Expression paramExpr, final ExpressionEvaluator eval, final Map<String, Object> state) {
        if (this.itemIterator == null) {
            return 0;
        }
        int i = 0;
        Object val = null;
        while (this.itemIterator.hasNext()) {
            state.put(this.alias, this.itemIterator.next());
            final Object result = paramExpr.evaluate(eval);
            AggregateExpression memexpr = null;
            if (i == 0) {
                val = result;
            }
            if (result instanceof Float) {
                memexpr = new FloatAggregateExpression((Float)result);
            }
            else if (result instanceof Double) {
                memexpr = new DoubleAggregateExpression((Double)result);
            }
            else if (result instanceof Long) {
                memexpr = new LongAggregateExpression((Long)result);
            }
            else if (result instanceof Integer) {
                memexpr = new IntegerAggregateExpression((Integer)result);
            }
            else if (result instanceof Short) {
                memexpr = new ShortAggregateExpression((Short)result);
            }
            else if (result instanceof BigInteger) {
                memexpr = new BigIntegerAggregateExpression((BigInteger)result);
            }
            else if (result instanceof BigDecimal) {
                memexpr = new BigDecimalAggregateExpression((BigDecimal)result);
            }
            else if (result instanceof Date) {
                memexpr = new DateAggregateExpression((Date)result);
            }
            else {
                if (!(result instanceof String)) {
                    throw new NucleusException("Evaluation of min() on object of type " + result.getClass().getName() + " - not supported");
                }
                memexpr = new StringAggregateExpression((String)result);
            }
            if (Boolean.TRUE.equals(memexpr.lt(val))) {
                val = result;
            }
            ++i;
        }
        return val;
    }
    
    public Object max(final Expression paramExpr, final ExpressionEvaluator eval, final Map<String, Object> state) {
        if (this.itemIterator == null) {
            return 0;
        }
        int i = 0;
        Object val = null;
        while (this.itemIterator.hasNext()) {
            state.put(this.alias, this.itemIterator.next());
            final Object result = paramExpr.evaluate(eval);
            AggregateExpression memexpr = null;
            if (i == 0) {
                val = result;
            }
            if (result instanceof Float) {
                memexpr = new FloatAggregateExpression((Float)result);
            }
            else if (result instanceof Double) {
                memexpr = new DoubleAggregateExpression((Double)result);
            }
            else if (result instanceof Long) {
                memexpr = new LongAggregateExpression((Long)result);
            }
            else if (result instanceof Integer) {
                memexpr = new IntegerAggregateExpression((Integer)result);
            }
            else if (result instanceof Short) {
                memexpr = new ShortAggregateExpression((Short)result);
            }
            else if (result instanceof BigInteger) {
                memexpr = new BigIntegerAggregateExpression((BigInteger)result);
            }
            else if (result instanceof BigDecimal) {
                memexpr = new BigDecimalAggregateExpression((BigDecimal)result);
            }
            else if (result instanceof Date) {
                memexpr = new DateAggregateExpression((Date)result);
            }
            else {
                if (!(result instanceof String)) {
                    throw new NucleusException("Evaluation of max() on object of type " + result.getClass().getName() + " - not supported");
                }
                memexpr = new StringAggregateExpression((String)result);
            }
            if (Boolean.TRUE.equals(memexpr.gt(val))) {
                val = result;
            }
            ++i;
        }
        return val;
    }
    
    public Object sum(final Expression paramExpr, final ExpressionEvaluator eval, final Map<String, Object> state) {
        if (this.itemIterator == null) {
            return 0;
        }
        Object val = null;
        while (this.itemIterator.hasNext()) {
            state.put(this.alias, this.itemIterator.next());
            final Object result = paramExpr.evaluate(eval);
            AggregateExpression memexpr = null;
            if (result instanceof Float) {
                if (val == null) {
                    val = 0.0;
                }
                memexpr = new DoubleAggregateExpression((double)result);
            }
            else if (result instanceof Double) {
                if (val == null) {
                    val = 0.0;
                }
                memexpr = new DoubleAggregateExpression((Double)result);
            }
            else if (result instanceof Long) {
                if (val == null) {
                    val = 0L;
                }
                memexpr = new LongAggregateExpression((Long)result);
            }
            else if (result instanceof Integer) {
                if (val == null) {
                    val = 0L;
                }
                memexpr = new LongAggregateExpression((long)result);
            }
            else if (result instanceof Short) {
                if (val == null) {
                    val = 0L;
                }
                memexpr = new LongAggregateExpression((long)result);
            }
            else if (result instanceof BigInteger) {
                if (val == null) {
                    val = BigInteger.ZERO;
                }
                memexpr = new BigIntegerAggregateExpression((BigInteger)result);
            }
            else if (result instanceof Date) {
                if (val == null) {
                    val = new Date(0L);
                }
                memexpr = new DateAggregateExpression((Date)result);
            }
            else {
                if (!(result instanceof BigDecimal)) {
                    throw new NucleusException("Evaluation of sum() on object of type " + result.getClass().getName() + " - not supported");
                }
                if (val == null) {
                    val = BigDecimal.ZERO;
                }
                memexpr = new BigDecimalAggregateExpression((BigDecimal)result);
            }
            val = memexpr.add(val);
        }
        return val;
    }
    
    public Object avg(final Expression paramExpr, final ExpressionEvaluator eval, final Map<String, Object> state) {
        if (this.itemIterator == null) {
            return 0.0;
        }
        int i = 0;
        Object val = null;
        AggregateExpression memexpr = null;
        while (this.itemIterator.hasNext()) {
            state.put(this.alias, this.itemIterator.next());
            final Object result = paramExpr.evaluate(eval);
            if (result instanceof Float) {
                if (val == null) {
                    val = new Double(0.0);
                }
                memexpr = new DoubleAggregateExpression(new Double((double)result));
            }
            else if (result instanceof Double) {
                if (val == null) {
                    val = new Double(0.0);
                }
                memexpr = new DoubleAggregateExpression((Double)result);
            }
            else if (result instanceof Long) {
                if (val == null) {
                    val = 0.0;
                }
                memexpr = new DoubleAggregateExpression(new Double((double)result));
            }
            else if (result instanceof Integer) {
                if (val == null) {
                    val = 0.0;
                }
                memexpr = new DoubleAggregateExpression(new Double((double)result));
            }
            else if (result instanceof Short) {
                if (val == null) {
                    val = 0.0;
                }
                memexpr = new DoubleAggregateExpression(new Double((double)result));
            }
            else if (result instanceof BigInteger) {
                if (val == null) {
                    val = BigDecimal.ZERO;
                }
                memexpr = new BigDecimalAggregateExpression(new BigDecimal((BigInteger)result));
            }
            else {
                if (!(result instanceof BigDecimal)) {
                    throw new NucleusException("Evaluation of avg() on object of type " + result.getClass().getName() + " - not supported");
                }
                if (val == null) {
                    val = BigDecimal.ZERO;
                }
                memexpr = new BigDecimalAggregateExpression((BigDecimal)result);
            }
            val = memexpr.add(val);
            ++i;
        }
        Object divisor = null;
        if (val instanceof Float) {
            memexpr = new FloatAggregateExpression((Float)val);
            divisor = new Float((float)i);
        }
        else if (val instanceof Double) {
            memexpr = new DoubleAggregateExpression((Double)val);
            divisor = new Double(i);
        }
        else if (val instanceof Long) {
            memexpr = new LongAggregateExpression((Long)val);
            divisor = i;
        }
        else if (val instanceof Integer) {
            memexpr = new IntegerAggregateExpression((Integer)val);
            divisor = i;
        }
        else if (val instanceof Short) {
            memexpr = new ShortAggregateExpression((Short)val);
            divisor = (short)i;
        }
        else if (val instanceof BigInteger) {
            memexpr = new BigIntegerAggregateExpression((BigInteger)val);
            divisor = BigInteger.valueOf(i);
        }
        else if (val instanceof BigDecimal) {
            memexpr = new BigDecimalAggregateExpression((BigDecimal)val);
            divisor = BigDecimal.valueOf(i);
        }
        return memexpr.div(divisor);
    }
}
