// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.sql.compile.OptimizableList;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Properties;
import org.apache.derby.iapi.sql.compile.JoinStrategy;
import org.apache.derby.iapi.sql.compile.OptimizerFactory;
import org.apache.derby.iapi.services.monitor.ModuleControl;

public class OptimizerFactoryImpl implements ModuleControl, OptimizerFactory
{
    protected String optimizerId;
    protected boolean ruleBasedOptimization;
    protected boolean noTimeout;
    protected boolean useStatistics;
    protected int maxMemoryPerTable;
    protected JoinStrategy[] joinStrategySet;
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.ruleBasedOptimization = Boolean.valueOf(PropertyUtil.getSystemProperty("derby.optimizer.ruleBasedOptimization"));
        this.noTimeout = Boolean.valueOf(PropertyUtil.getSystemProperty("derby.optimizer.noTimeout"));
        final String systemProperty = PropertyUtil.getSystemProperty("derby.language.maxMemoryPerTable");
        if (systemProperty != null) {
            final int int1 = Integer.parseInt(systemProperty);
            if (int1 >= 0) {
                this.maxMemoryPerTable = int1 * 1024;
            }
        }
        final String systemProperty2 = PropertyUtil.getSystemProperty("derby.language.useStatistics");
        if (systemProperty2 != null) {
            this.useStatistics = Boolean.valueOf(systemProperty2);
        }
    }
    
    public void stop() {
    }
    
    public Optimizer getOptimizer(final OptimizableList list, final OptimizablePredicateList list2, final DataDictionary dataDictionary, final RequiredRowOrdering requiredRowOrdering, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        if (this.joinStrategySet == null) {
            this.joinStrategySet = new JoinStrategy[] { new NestedLoopJoinStrategy(), new HashJoinStrategy() };
        }
        return this.getOptimizerImpl(list, list2, dataDictionary, requiredRowOrdering, n, languageConnectionContext);
    }
    
    public CostEstimate getCostEstimate() throws StandardException {
        return new CostEstimateImpl();
    }
    
    public boolean supportsOptimizerTrace() {
        return false;
    }
    
    public OptimizerFactoryImpl() {
        this.optimizerId = null;
        this.ruleBasedOptimization = false;
        this.noTimeout = false;
        this.useStatistics = true;
        this.maxMemoryPerTable = 1048576;
    }
    
    protected Optimizer getOptimizerImpl(final OptimizableList list, final OptimizablePredicateList list2, final DataDictionary dataDictionary, final RequiredRowOrdering requiredRowOrdering, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        return new OptimizerImpl(list, list2, dataDictionary, this.ruleBasedOptimization, this.noTimeout, this.useStatistics, this.maxMemoryPerTable, this.joinStrategySet, languageConnectionContext.getLockEscalationThreshold(), requiredRowOrdering, n);
    }
    
    public int getMaxMemoryPerTable() {
        return this.maxMemoryPerTable;
    }
}
