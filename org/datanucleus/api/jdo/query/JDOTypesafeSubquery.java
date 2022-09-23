// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.typesafe.CollectionExpression;
import org.datanucleus.query.typesafe.CharacterExpression;
import org.datanucleus.query.typesafe.TimeExpression;
import org.datanucleus.query.typesafe.DateTimeExpression;
import org.datanucleus.query.typesafe.DateExpression;
import org.datanucleus.query.typesafe.StringExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.Expression;
import org.datanucleus.query.typesafe.BooleanExpression;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.jdo.JDOException;
import org.datanucleus.query.typesafe.PersistableExpression;
import javax.jdo.PersistenceManager;
import org.datanucleus.query.typesafe.TypesafeSubquery;

public class JDOTypesafeSubquery<T> extends AbstractTypesafeQuery<T> implements TypesafeSubquery<T>
{
    public JDOTypesafeSubquery(final PersistenceManager pm, final Class<T> candidateClass, final String candidateAlias, final JDOTypesafeQuery parentQuery) {
        super(pm, candidateClass, candidateAlias);
    }
    
    public String getAlias() {
        return "VAR_" + this.candidateAlias.toUpperCase();
    }
    
    public PersistableExpression candidate() {
        final String candName = this.candidateCls.getName();
        final int pos = candName.lastIndexOf(46);
        final String qName = candName.substring(0, pos + 1) + JDOTypesafeQuery.getQueryClassNameForClassName(candName.substring(pos + 1));
        try {
            final Class qClass = this.ec.getClassLoaderResolver().classForName(qName);
            final Constructor ctr = qClass.getConstructor(PersistableExpression.class, String.class);
            final Object candObj = ctr.newInstance(null, this.candidateAlias);
            if (candObj == null || !(candObj instanceof PersistableExpression)) {
                throw new JDOException("Class " + this.candidateCls.getName() + " has a Query class but the candidate is invalid");
            }
            return (PersistableExpression)candObj;
        }
        catch (NoSuchMethodException nsfe) {
            throw new JDOException("Class " + this.candidateCls.getName() + " has a Query class but the candidate is invalid");
        }
        catch (InvocationTargetException ite) {
            throw new JDOException("Class " + this.candidateCls.getName() + " has a Query class but the candidate is invalid");
        }
        catch (InstantiationException ie) {
            throw new JDOException("Class " + this.candidateCls.getName() + " has a Query class but the candidate is invalid");
        }
        catch (IllegalAccessException iae) {
            throw new JDOException("Class " + this.candidateCls.getName() + " has a Query class but the candidate is invalid");
        }
    }
    
    public TypesafeSubquery filter(final BooleanExpression expr) {
        this.discardCompiled();
        this.filter = (BooleanExpressionImpl)expr;
        return this;
    }
    
    public TypesafeSubquery groupBy(final Expression... exprs) {
        this.discardCompiled();
        if (exprs != null && exprs.length > 0) {
            this.grouping = new ArrayList<ExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.grouping.add((ExpressionImpl)exprs[i]);
            }
        }
        return this;
    }
    
    public TypesafeSubquery having(final Expression expr) {
        this.discardCompiled();
        this.having = (ExpressionImpl)expr;
        return this;
    }
    
    public <S> NumericExpression<S> selectUnique(final NumericExpression<S> expr) {
        return (NumericExpression<S>)this.internalSelect(expr, NumericExpressionImpl.class);
    }
    
    public StringExpression selectUnique(final StringExpression expr) {
        return (StringExpression)this.internalSelect(expr, StringExpressionImpl.class);
    }
    
    public <S> DateExpression<S> selectUnique(final DateExpression<S> expr) {
        return (DateExpression<S>)this.internalSelect(expr, DateExpressionImpl.class);
    }
    
    public <S> DateTimeExpression<S> selectUnique(final DateTimeExpression<S> expr) {
        return (DateTimeExpression<S>)this.internalSelect(expr, DateTimeExpressionImpl.class);
    }
    
    public <S> TimeExpression<S> selectUnique(final TimeExpression<S> expr) {
        return (TimeExpression<S>)this.internalSelect(expr, TimeExpressionImpl.class);
    }
    
    public CharacterExpression selectUnique(final CharacterExpression expr) {
        return (CharacterExpression)this.internalSelect(expr, CharacterExpressionImpl.class);
    }
    
    public CollectionExpression select(final CollectionExpression expr) {
        return (CollectionExpression)this.internalSelect(expr, CollectionExpressionImpl.class);
    }
    
    protected Expression internalSelect(final Expression expr, final Class implClass) {
        (this.result = new ArrayList<ExpressionImpl>()).add((ExpressionImpl)expr);
        final VariableExpression varExpr = new VariableExpression(this.getAlias());
        try {
            final Constructor ctr = implClass.getConstructor(org.datanucleus.query.expression.Expression.class);
            return ctr.newInstance(varExpr);
        }
        catch (NoSuchMethodException nsme) {
            throw new JDOException("Unable to create expression of type " + expr.getClass().getName() + " since required constructor doesnt exist");
        }
        catch (InvocationTargetException ite) {
            throw new JDOException("Unable to create expression of type " + expr.getClass().getName() + " due to error in constructor");
        }
        catch (IllegalAccessException iae) {
            throw new JDOException("Unable to create expression of type " + expr.getClass().getName() + " due to error in constructor");
        }
        catch (InstantiationException ie) {
            throw new JDOException("Unable to create expression of type " + expr.getClass().getName() + " due to error in constructor");
        }
    }
}
