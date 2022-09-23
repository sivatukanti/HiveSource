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
import java.util.Properties;

public class Level2OptimizerFactoryImpl extends OptimizerFactoryImpl
{
    public void boot(final boolean b, final Properties properties) throws StandardException {
        super.boot(b, properties);
    }
    
    public boolean supportsOptimizerTrace() {
        return true;
    }
    
    protected Optimizer getOptimizerImpl(final OptimizableList list, final OptimizablePredicateList list2, final DataDictionary dataDictionary, final RequiredRowOrdering requiredRowOrdering, final int n, final LanguageConnectionContext languageConnectionContext) throws StandardException {
        return new Level2OptimizerImpl(list, list2, dataDictionary, this.ruleBasedOptimization, this.noTimeout, this.useStatistics, this.maxMemoryPerTable, this.joinStrategySet, languageConnectionContext.getLockEscalationThreshold(), requiredRowOrdering, n, languageConnectionContext);
    }
    
    public CostEstimate getCostEstimate() throws StandardException {
        return new Level2CostEstimateImpl();
    }
}
