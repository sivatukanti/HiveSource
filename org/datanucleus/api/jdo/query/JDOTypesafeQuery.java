// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.query;

import org.datanucleus.query.JDOQLQueryHelper;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import java.util.Iterator;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.api.jdo.NucleusJDOHelper;
import org.datanucleus.store.query.NoQueryResultsException;
import org.datanucleus.query.compiler.QueryCompilation;
import org.datanucleus.api.jdo.JDOFetchPlan;
import org.datanucleus.query.expression.ParameterExpression;
import java.util.HashSet;
import org.datanucleus.query.typesafe.TypesafeSubquery;
import javax.jdo.JDOUserException;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.typesafe.OrderExpression;
import java.util.ArrayList;
import org.datanucleus.query.typesafe.BooleanExpression;
import java.util.List;
import org.datanucleus.query.typesafe.ListExpression;
import org.datanucleus.query.typesafe.MapExpression;
import org.datanucleus.query.typesafe.CollectionExpression;
import org.datanucleus.query.typesafe.DateTimeExpression;
import org.datanucleus.query.typesafe.TimeExpression;
import org.datanucleus.query.typesafe.DateExpression;
import org.datanucleus.query.typesafe.NumericExpression;
import org.datanucleus.query.typesafe.CharacterExpression;
import org.datanucleus.query.typesafe.StringExpression;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.sql.Date;
import java.sql.Time;
import org.datanucleus.query.typesafe.Expression;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.jdo.JDOException;
import org.datanucleus.query.typesafe.PersistableExpression;
import javax.jdo.PersistenceManager;
import org.datanucleus.store.query.Query;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import javax.jdo.FetchPlan;
import org.datanucleus.query.typesafe.TypesafeQuery;

public class JDOTypesafeQuery<T> extends AbstractTypesafeQuery<T> implements TypesafeQuery<T>
{
    FetchPlan fp;
    boolean ignoreCache;
    boolean subclasses;
    Class resultClass;
    boolean unique;
    protected Collection<T> candidates;
    protected ExpressionImpl rangeLowerExpr;
    protected ExpressionImpl rangeUpperExpr;
    protected Map<String, Object> extensions;
    protected Map<String, ExpressionImpl> parameterExprByName;
    protected Map<String, Object> parameterValuesByName;
    protected transient Set<JDOTypesafeSubquery> subqueries;
    protected transient Set<Query> internalQueries;
    String queryString;
    
    public JDOTypesafeQuery(final PersistenceManager pm, final Class<T> candidateClass) {
        super(pm, candidateClass, "this");
        this.ignoreCache = false;
        this.subclasses = true;
        this.resultClass = null;
        this.unique = false;
        this.candidates = null;
        this.extensions = null;
        this.parameterExprByName = null;
        this.parameterValuesByName = null;
        this.subqueries = null;
        this.internalQueries = null;
        this.queryString = null;
    }
    
    public PersistableExpression candidate() {
        final String candName = this.candidateCls.getName();
        final int pos = candName.lastIndexOf(46);
        final String qName = candName.substring(0, pos + 1) + getQueryClassNameForClassName(candName.substring(pos + 1));
        try {
            final Class qClass = this.ec.getClassLoaderResolver().classForName(qName);
            final Method method = qClass.getMethod("candidate", (Class[])new Class[0]);
            final Object candObj = method.invoke(null, (Object[])null);
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
        catch (IllegalAccessException iae) {
            throw new JDOException("Class " + this.candidateCls.getName() + " has a Query class but the candidate is invalid");
        }
    }
    
    public Expression parameter(final String name, final Class type) {
        this.discardCompiled();
        ExpressionImpl paramExpr = null;
        if (type == Boolean.class || type == Boolean.TYPE) {
            paramExpr = new BooleanExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Byte.class || type == Byte.TYPE) {
            paramExpr = new ByteExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Character.class || type == Character.TYPE) {
            paramExpr = new CharacterExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Double.class || type == Double.TYPE) {
            paramExpr = new NumericExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Float.class || type == Float.TYPE) {
            paramExpr = new NumericExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Integer.class || type == Integer.TYPE) {
            paramExpr = new NumericExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Long.class || type == Long.TYPE) {
            paramExpr = new NumericExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == Short.class || type == Short.TYPE) {
            paramExpr = new NumericExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (type == String.class) {
            paramExpr = new StringExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (Time.class.isAssignableFrom(type)) {
            paramExpr = new TimeExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (Date.class.isAssignableFrom(type)) {
            paramExpr = new DateExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (java.util.Date.class.isAssignableFrom(type)) {
            paramExpr = new DateTimeExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        else if (this.ec.getApiAdapter().isPersistable(type)) {
            final String typeName = type.getName();
            final int pos = typeName.lastIndexOf(46);
            final String qName = typeName.substring(0, pos + 1) + getQueryClassNameForClassName(typeName.substring(pos + 1));
            try {
                final Class qClass = this.ec.getClassLoaderResolver().classForName(qName);
                final Constructor ctr = qClass.getConstructor(Class.class, String.class, ExpressionType.class);
                final Object candObj = ctr.newInstance(type, name, ExpressionType.PARAMETER);
                paramExpr = (ExpressionImpl)candObj;
            }
            catch (NoSuchMethodException nsme) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for parameters");
            }
            catch (IllegalAccessException iae) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for parameters");
            }
            catch (InvocationTargetException ite) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for parameters");
            }
            catch (InstantiationException ie) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for parameters");
            }
        }
        else {
            paramExpr = new ObjectExpressionImpl(type, name, ExpressionType.PARAMETER);
        }
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public StringExpression stringParameter(final String name) {
        final StringExpressionImpl paramExpr = new StringExpressionImpl(String.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public CharacterExpression characterParameter(final String name) {
        final CharacterExpressionImpl paramExpr = new CharacterExpressionImpl(Character.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public NumericExpression<Long> longParameter(final String name) {
        final NumericExpressionImpl<Long> paramExpr = new NumericExpressionImpl<Long>((Class<Number>)Long.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public NumericExpression<Integer> integerParameter(final String name) {
        final NumericExpressionImpl<Integer> paramExpr = new NumericExpressionImpl<Integer>((Class<Number>)Integer.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public NumericExpression<Short> shortParameter(final String name) {
        final NumericExpressionImpl<Short> paramExpr = new NumericExpressionImpl<Short>((Class<Number>)Short.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public NumericExpression<Double> doubleParameter(final String name) {
        final NumericExpressionImpl<Double> paramExpr = new NumericExpressionImpl<Double>((Class<Number>)Double.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public NumericExpression<Float> floatParameter(final String name) {
        final NumericExpressionImpl<Float> paramExpr = new NumericExpressionImpl<Float>((Class<Number>)Float.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public DateExpression<Date> dateParameter(final String name) {
        final DateExpressionImpl paramExpr = new DateExpressionImpl((Class<java.util.Date>)Date.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return (DateExpression<Date>)paramExpr;
    }
    
    public TimeExpression<Time> timeParameter(final String name) {
        final TimeExpressionImpl paramExpr = new TimeExpressionImpl((Class<java.util.Date>)Date.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return (TimeExpression<Time>)paramExpr;
    }
    
    public DateTimeExpression<java.util.Date> datetimeParameter(final String name) {
        final DateTimeExpressionImpl paramExpr = new DateTimeExpressionImpl(java.util.Date.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public CollectionExpression collectionParameter(final String name) {
        final CollectionExpressionImpl paramExpr = new CollectionExpressionImpl((Class<T>)Collection.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public MapExpression mapParameter(final String name) {
        final MapExpressionImpl paramExpr = new MapExpressionImpl((Class<T>)Map.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public ListExpression listParameter(final String name) {
        final ListExpressionImpl paramExpr = new ListExpressionImpl((Class<T>)List.class, name, ExpressionType.PARAMETER);
        if (this.parameterExprByName == null) {
            this.parameterExprByName = new HashMap<String, ExpressionImpl>();
        }
        this.parameterExprByName.put(name, paramExpr);
        return paramExpr;
    }
    
    public Expression variable(final String name, final Class type) {
        this.discardCompiled();
        Expression varExpr = null;
        if (this.ec.getApiAdapter().isPersistable(type)) {
            final String typeName = type.getName();
            final int pos = typeName.lastIndexOf(46);
            final String qName = typeName.substring(0, pos + 1) + getQueryClassNameForClassName(typeName.substring(pos + 1));
            try {
                final Class qClass = this.ec.getClassLoaderResolver().classForName(qName);
                final Constructor ctr = qClass.getConstructor(Class.class, String.class, ExpressionType.class);
                final Object candObj = ctr.newInstance(type, name, ExpressionType.VARIABLE);
                varExpr = (Expression)candObj;
            }
            catch (NoSuchMethodException nsme) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for variables");
            }
            catch (IllegalAccessException iae) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for variables");
            }
            catch (InvocationTargetException ite) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for variables");
            }
            catch (InstantiationException ie) {
                throw new JDOException("Class " + typeName + " has a Query class but has no constructor for variables");
            }
        }
        else if (type == Boolean.class || type == Boolean.TYPE) {
            varExpr = new BooleanExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Byte.class || type == Byte.TYPE) {
            varExpr = new ByteExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Character.class || type == Character.TYPE) {
            varExpr = new CharacterExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Double.class || type == Double.TYPE) {
            varExpr = new NumericExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Float.class || type == Float.TYPE) {
            varExpr = new NumericExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Integer.class || type == Integer.TYPE) {
            varExpr = new NumericExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Long.class || type == Long.TYPE) {
            varExpr = new NumericExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == Short.class || type == Short.TYPE) {
            varExpr = new NumericExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (type == String.class) {
            varExpr = new StringExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (Time.class.isAssignableFrom(type)) {
            varExpr = new TimeExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (Date.class.isAssignableFrom(type)) {
            varExpr = new DateExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else if (java.util.Date.class.isAssignableFrom(type)) {
            varExpr = new DateTimeExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        else {
            varExpr = new ObjectExpressionImpl(type, name, ExpressionType.VARIABLE);
        }
        return varExpr;
    }
    
    public TypesafeQuery<T> excludeSubclasses() {
        this.subclasses = false;
        return this;
    }
    
    public TypesafeQuery<T> includeSubclasses() {
        this.subclasses = true;
        return this;
    }
    
    public TypesafeQuery<T> filter(final BooleanExpression expr) {
        this.discardCompiled();
        this.filter = (BooleanExpressionImpl)expr;
        return this;
    }
    
    public TypesafeQuery<T> groupBy(final Expression... exprs) {
        this.discardCompiled();
        if (exprs != null && exprs.length > 0) {
            this.grouping = new ArrayList<ExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.grouping.add((ExpressionImpl)exprs[i]);
            }
        }
        return this;
    }
    
    public TypesafeQuery<T> having(final Expression expr) {
        this.discardCompiled();
        this.having = (ExpressionImpl)expr;
        return this;
    }
    
    public TypesafeQuery<T> orderBy(final OrderExpression... exprs) {
        this.discardCompiled();
        if (exprs != null && exprs.length > 0) {
            this.ordering = new ArrayList<OrderExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.ordering.add((OrderExpressionImpl)exprs[i]);
            }
        }
        return this;
    }
    
    public TypesafeQuery<T> range(final long lowerIncl, final long upperExcl) {
        this.discardCompiled();
        this.rangeLowerExpr = new NumericExpressionImpl(new Literal(lowerIncl));
        this.rangeUpperExpr = new NumericExpressionImpl(new Literal(upperExcl));
        return this;
    }
    
    public TypesafeQuery<T> range(final NumericExpression lowerInclExpr, final NumericExpression upperExclExpr) {
        this.discardCompiled();
        this.rangeLowerExpr = (ExpressionImpl)lowerInclExpr;
        this.rangeUpperExpr = (ExpressionImpl)upperExclExpr;
        return this;
    }
    
    public TypesafeQuery<T> range(final Expression paramLowerInclExpr, final Expression paramUpperExclExpr) {
        this.discardCompiled();
        if (!((ExpressionImpl)paramLowerInclExpr).isParameter()) {
            throw new JDOUserException("lower inclusive expression should be a parameter");
        }
        if (!((ExpressionImpl)paramUpperExclExpr).isParameter()) {
            throw new JDOUserException("upper exclusive expression should be a parameter");
        }
        this.rangeLowerExpr = (ExpressionImpl)paramLowerInclExpr;
        this.rangeUpperExpr = (ExpressionImpl)paramUpperExclExpr;
        return this;
    }
    
    public <S> TypesafeSubquery<S> subquery(final Class<S> candidateClass, final String candidateAlias) {
        final JDOTypesafeSubquery<S> subquery = new JDOTypesafeSubquery<S>(this.pm, candidateClass, candidateAlias, this);
        if (this.subqueries == null) {
            this.subqueries = new HashSet<JDOTypesafeSubquery>();
        }
        this.subqueries.add(subquery);
        return subquery;
    }
    
    public TypesafeSubquery<T> subquery(final String candidateAlias) {
        final JDOTypesafeSubquery<T> subquery = new JDOTypesafeSubquery<T>(this.pm, this.candidateCls, candidateAlias, this);
        if (this.subqueries == null) {
            this.subqueries = new HashSet<JDOTypesafeSubquery>();
        }
        this.subqueries.add(subquery);
        return subquery;
    }
    
    public TypesafeQuery<T> setParameter(final Expression paramExpr, final Object value) {
        this.discardCompiled();
        final ParameterExpression internalParamExpr = (ParameterExpression)((ExpressionImpl)paramExpr).getQueryExpression();
        if (this.parameterExprByName == null || (this.parameterExprByName != null && !this.parameterExprByName.containsKey(internalParamExpr.getAlias()))) {
            throw new JDOUserException("Parameter with name " + internalParamExpr.getAlias() + " doesnt exist for this query");
        }
        if (this.parameterValuesByName == null) {
            this.parameterValuesByName = new HashMap<String, Object>();
        }
        this.parameterValuesByName.put(internalParamExpr.getAlias(), value);
        return this;
    }
    
    public TypesafeQuery<T> setParameter(final String paramName, final Object value) {
        this.discardCompiled();
        if (this.parameterExprByName == null || (this.parameterExprByName != null && !this.parameterExprByName.containsKey(paramName))) {
            throw new JDOUserException("Parameter with name " + paramName + " doesnt exist for this query");
        }
        if (this.parameterValuesByName == null) {
            this.parameterValuesByName = new HashMap<String, Object>();
        }
        this.parameterValuesByName.put(paramName, value);
        return this;
    }
    
    public TypesafeQuery<T> setCandidates(final Collection<T> candidates) {
        if (candidates != null) {
            this.candidates = new ArrayList<T>((Collection<? extends T>)candidates);
        }
        else {
            this.candidates = null;
        }
        return null;
    }
    
    public <T> List<T> executeList() {
        if (this.result != null || this.resultDistinct != null || this.resultClass != null) {
            this.discardCompiled();
            this.result = null;
            this.resultClass = null;
            this.resultDistinct = null;
        }
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.unique = false;
        return (List<T>)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public <T> T executeUnique() {
        if (this.result != null || this.resultDistinct != null || this.resultClass != null) {
            this.discardCompiled();
            this.result = null;
            this.resultClass = null;
            this.resultDistinct = null;
        }
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.unique = true;
        return (T)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public List<Object[]> executeResultList(final boolean distinct, final Expression... exprs) {
        this.discardCompiled();
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.result = null;
        if (exprs != null && exprs.length > 0) {
            this.result = new ArrayList<ExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.result.add((ExpressionImpl)exprs[i]);
            }
        }
        this.resultClass = null;
        this.resultDistinct = distinct;
        return (List<Object[]>)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public List<Object> executeResultList(final boolean distinct, final Expression expr) {
        this.discardCompiled();
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.result = null;
        if (expr != null) {
            (this.result = new ArrayList<ExpressionImpl>()).add((ExpressionImpl)expr);
        }
        this.resultClass = null;
        this.resultDistinct = distinct;
        return (List<Object>)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public <R> List<R> executeResultList(final Class<R> resultCls, final boolean distinct, final Expression... exprs) {
        this.discardCompiled();
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.result = null;
        if (exprs != null && exprs.length > 0) {
            this.result = new ArrayList<ExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.result.add((ExpressionImpl)exprs[i]);
            }
        }
        this.resultClass = resultCls;
        this.resultDistinct = distinct;
        this.unique = false;
        return (List<R>)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public Object[] executeResultUnique(final boolean distinct, final Expression... exprs) {
        this.discardCompiled();
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.result = null;
        if (exprs != null && exprs.length > 0) {
            this.result = new ArrayList<ExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.result.add((ExpressionImpl)exprs[i]);
            }
        }
        this.resultClass = null;
        this.resultDistinct = distinct;
        this.unique = true;
        return (Object[])this.executeInternalQuery(this.getInternalQuery());
    }
    
    public Object executeResultUnique(final boolean distinct, final Expression expr) {
        this.discardCompiled();
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.result = null;
        if (expr != null) {
            (this.result = new ArrayList<ExpressionImpl>()).add((ExpressionImpl)expr);
        }
        this.resultClass = null;
        this.resultDistinct = distinct;
        this.unique = true;
        return this.executeInternalQuery(this.getInternalQuery());
    }
    
    public <R> R executeResultUnique(final Class<R> resultCls, final boolean distinct, final Expression... exprs) {
        this.discardCompiled();
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.result = null;
        if (exprs != null && exprs.length > 0) {
            this.result = new ArrayList<ExpressionImpl>();
            for (int i = 0; i < exprs.length; ++i) {
                this.result.add((ExpressionImpl)exprs[i]);
            }
        }
        this.resultClass = resultCls;
        this.resultDistinct = distinct;
        this.unique = true;
        return (R)this.executeInternalQuery(this.getInternalQuery());
    }
    
    protected Query getInternalQuery() {
        final Query internalQuery = this.ec.getStoreManager().getQueryManager().newQuery("JDOQL", this.ec, this.toString());
        internalQuery.setIgnoreCache(this.ignoreCache);
        if (!this.subclasses) {
            internalQuery.setSubclasses(false);
        }
        if (this.type == QueryType.SELECT) {
            if (this.resultDistinct != null) {
                internalQuery.setResultDistinct(this.resultDistinct);
            }
            internalQuery.setResultClass(this.resultClass);
            internalQuery.setUnique(this.unique);
        }
        if (this.extensions != null) {
            internalQuery.setExtensions(this.extensions);
        }
        if (this.fp != null) {
            internalQuery.setFetchPlan(((JDOFetchPlan)this.fp).getInternalFetchPlan());
        }
        if (this.type == QueryType.SELECT && this.candidates != null) {
            internalQuery.setCandidates(this.candidates);
        }
        final QueryCompilation compilation = this.getCompilation();
        internalQuery.setCompilation(compilation);
        return internalQuery;
    }
    
    protected Object executeInternalQuery(final Query internalQuery) {
        if (this.internalQueries == null) {
            this.internalQueries = new HashSet<Query>();
        }
        this.internalQueries.add(internalQuery);
        try {
            if (this.parameterValuesByName != null || this.parameterExprByName != null) {
                this.validateParameters();
                return internalQuery.executeWithMap(this.parameterValuesByName);
            }
            return internalQuery.execute();
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public long deletePersistentAll() {
        if (this.result != null || this.resultClass != null) {
            this.discardCompiled();
            this.result = null;
            this.resultClass = null;
        }
        this.type = QueryType.SELECT;
        this.updateExprs = null;
        this.updateVals = null;
        this.unique = false;
        try {
            final Query internalQuery = this.getInternalQuery();
            if (this.parameterValuesByName != null || this.parameterExprByName != null) {
                this.validateParameters();
                return internalQuery.deletePersistentAll(this.parameterValuesByName);
            }
            return internalQuery.deletePersistentAll();
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    private void validateParameters() {
        final int numParams = (this.parameterExprByName != null) ? this.parameterExprByName.size() : 0;
        final int numValues = (this.parameterValuesByName != null) ? this.parameterValuesByName.size() : 0;
        if (numParams != numValues) {
            throw new JDOUserException("Query has " + numParams + " but " + numValues + " values have been provided");
        }
        for (final String paramName : this.parameterExprByName.keySet()) {
            if (!this.parameterValuesByName.containsKey(paramName)) {
                throw new JDOUserException("Query has a parameter " + paramName + " defined but no value supplied");
            }
        }
    }
    
    public TypesafeQuery<T> set(final Expression expr, final Object val) {
        this.type = QueryType.BULK_UPDATE;
        if (this.updateExprs == null) {
            this.updateExprs = new ArrayList<ExpressionImpl>();
            this.updateVals = new ArrayList<ExpressionImpl>();
        }
        ExpressionImpl valExpr = null;
        final org.datanucleus.query.expression.Expression literalExpr = new Literal(val);
        if (val instanceof String) {
            valExpr = new StringExpressionImpl(literalExpr);
        }
        else if (val instanceof Number) {
            valExpr = new NumericExpressionImpl(literalExpr);
        }
        else if (val instanceof Time) {
            valExpr = new TimeExpressionImpl(literalExpr);
        }
        else if (val instanceof Date) {
            valExpr = new DateExpressionImpl(literalExpr);
        }
        else if (val instanceof java.util.Date) {
            valExpr = new DateTimeExpressionImpl(literalExpr);
        }
        else if (val instanceof Boolean) {
            valExpr = new BooleanExpressionImpl(literalExpr);
        }
        else if (val instanceof Byte) {
            valExpr = new ByteExpressionImpl(literalExpr);
        }
        else if (val instanceof Enum) {
            valExpr = new EnumExpressionImpl(literalExpr);
        }
        this.updateExprs.add((ExpressionImpl)expr);
        this.updateVals.add(valExpr);
        return this;
    }
    
    public long update() {
        this.type = QueryType.BULK_UPDATE;
        if (this.updateExprs == null || this.updateExprs.size() == 0) {
            throw new JDOUserException("No update expressions defined. Use set() method");
        }
        return (long)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public long delete() {
        this.type = QueryType.BULK_DELETE;
        this.updateExprs = null;
        this.updateVals = null;
        return (long)this.executeInternalQuery(this.getInternalQuery());
    }
    
    public FetchPlan getFetchPlan() {
        if (this.fp == null) {
            this.fp = new JDOFetchPlan(this.ec.getFetchPlan().getCopy());
        }
        return this.fp;
    }
    
    public PersistenceManager getPersistenceManager() {
        return this.pm;
    }
    
    public TypesafeQuery setIgnoreCache(final boolean ignore) {
        this.ignoreCache = ignore;
        return this;
    }
    
    public TypesafeQuery addExtension(final String key, final Object value) {
        if (this.extensions == null) {
            this.extensions = new HashMap<String, Object>();
        }
        this.extensions.put(key, value);
        return this;
    }
    
    public TypesafeQuery setExtensions(final Map<String, Object> extensions) {
        this.extensions = new HashMap<String, Object>(extensions);
        return this;
    }
    
    public void close(final Object result) {
        if (this.internalQueries != null) {
            for (final Query query : this.internalQueries) {
                query.close(result);
            }
        }
    }
    
    public void closeAll() {
        if (this.internalQueries != null) {
            for (final Query query : this.internalQueries) {
                query.closeAll();
            }
            this.internalQueries.clear();
            this.internalQueries = null;
        }
    }
    
    public QueryCompilation compile(final MetaDataManager mmgr, final ClassLoaderResolver clr) {
        final QueryCompilation compilation = super.compile(mmgr, clr);
        if (this.subqueries != null && !this.subqueries.isEmpty()) {
            for (final JDOTypesafeSubquery subquery : this.subqueries) {
                final QueryCompilation subqueryCompilation = subquery.getCompilation();
                compilation.addSubqueryCompilation(subquery.getAlias(), subqueryCompilation);
            }
        }
        return compilation;
    }
    
    @Override
    public String toString() {
        if (this.queryString == null) {
            StringBuffer str = null;
            if (this.type == QueryType.BULK_UPDATE) {
                str = new StringBuffer("UPDATE");
            }
            else if (this.type == QueryType.BULK_DELETE) {
                str = new StringBuffer("DELETE");
            }
            else {
                str = new StringBuffer("SELECT");
            }
            if (this.type == QueryType.SELECT) {
                if (this.unique) {
                    str.append(" UNIQUE");
                }
                if (this.result != null && !this.result.isEmpty()) {
                    if (this.resultDistinct != null && this.resultDistinct) {
                        str.append(" DISTINCT");
                    }
                    str.append(" ");
                    final Iterator<ExpressionImpl> iter = (Iterator<ExpressionImpl>)this.result.iterator();
                    while (iter.hasNext()) {
                        final ExpressionImpl resultExpr = iter.next();
                        str.append(JDOQLQueryHelper.getJDOQLForExpression(resultExpr.getQueryExpression()));
                        if (iter.hasNext()) {
                            str.append(",");
                        }
                    }
                }
                if (this.resultClass != null) {
                    str.append(" INTO ").append(this.resultClass.getName());
                }
            }
            if (this.type == QueryType.SELECT || this.type == QueryType.BULK_DELETE) {
                str.append(" FROM ").append(this.candidateCls.getName());
            }
            else {
                str.append(" " + this.candidateCls.getName());
            }
            if (!this.subclasses) {
                str.append(" EXCLUDE SUBCLASSES");
            }
            if (this.type == QueryType.BULK_UPDATE) {
                str.append(" SET");
                final Iterator<ExpressionImpl> exprIter = (Iterator<ExpressionImpl>)this.updateExprs.iterator();
                final Iterator<ExpressionImpl> valIter = (Iterator<ExpressionImpl>)this.updateVals.iterator();
                while (exprIter.hasNext()) {
                    final ExpressionImpl expr = exprIter.next();
                    final ExpressionImpl val = valIter.next();
                    str.append(" ").append(JDOQLQueryHelper.getJDOQLForExpression(expr.getQueryExpression()));
                    str.append(" = ").append(JDOQLQueryHelper.getJDOQLForExpression(val.getQueryExpression()));
                    if (exprIter.hasNext()) {
                        str.append(",");
                    }
                }
            }
            if (this.filter != null) {
                str.append(" WHERE ");
                str.append(JDOQLQueryHelper.getJDOQLForExpression(this.filter.getQueryExpression()));
            }
            if (this.type == QueryType.SELECT) {
                if (this.grouping != null && !this.grouping.isEmpty()) {
                    str.append(" GROUP BY ");
                    final Iterator<ExpressionImpl> iter = (Iterator<ExpressionImpl>)this.grouping.iterator();
                    while (iter.hasNext()) {
                        final ExpressionImpl groupExpr = iter.next();
                        str.append(JDOQLQueryHelper.getJDOQLForExpression(groupExpr.getQueryExpression()));
                        if (iter.hasNext()) {
                            str.append(",");
                        }
                    }
                }
                if (this.having != null) {
                    str.append(" HAVING ");
                    str.append(JDOQLQueryHelper.getJDOQLForExpression(this.having.getQueryExpression()));
                }
                if (this.ordering != null && !this.ordering.isEmpty()) {
                    str.append(" ORDER BY ");
                    final Iterator<OrderExpressionImpl> iter2 = (Iterator<OrderExpressionImpl>)this.ordering.iterator();
                    while (iter2.hasNext()) {
                        final OrderExpressionImpl orderExpr = iter2.next();
                        str.append(JDOQLQueryHelper.getJDOQLForExpression(((ExpressionImpl)orderExpr.getExpression()).getQueryExpression()));
                        str.append(" " + ((orderExpr.getDirection() == OrderExpression.OrderDirection.ASC) ? "ASCENDING" : "DESCENDING"));
                        if (iter2.hasNext()) {
                            str.append(",");
                        }
                    }
                }
                if (this.rangeLowerExpr != null && this.rangeUpperExpr != null) {
                    str.append(" RANGE ");
                    str.append(JDOQLQueryHelper.getJDOQLForExpression(this.rangeLowerExpr.getQueryExpression()));
                    str.append(",");
                    str.append(JDOQLQueryHelper.getJDOQLForExpression(this.rangeUpperExpr.getQueryExpression()));
                }
            }
            this.queryString = str.toString();
        }
        return this.queryString;
    }
    
    @Override
    protected void discardCompiled() {
        super.discardCompiled();
        this.queryString = null;
    }
    
    public static String getQueryClassNameForClassName(final String name) {
        return "Q" + name;
    }
}
