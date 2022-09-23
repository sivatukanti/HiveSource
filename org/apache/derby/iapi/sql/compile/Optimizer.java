// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.error.StandardException;

public interface Optimizer
{
    public static final String MODULE = "org.apache.derby.iapi.sql.compile.Optimizer";
    public static final String JOIN_ORDER_OPTIMIZATION = "derby.optimizer.optimizeJoinOrder";
    public static final String RULE_BASED_OPTIMIZATION = "derby.optimizer.ruleBasedOptimization";
    public static final String NO_TIMEOUT = "derby.optimizer.noTimeout";
    public static final String MAX_MEMORY_PER_TABLE = "derby.language.maxMemoryPerTable";
    public static final int MAX_DYNAMIC_MATERIALIZED_ROWS = 512;
    public static final String USE_STATISTICS = "derby.language.useStatistics";
    public static final int NORMAL_PLAN = 1;
    public static final int SORT_AVOIDANCE_PLAN = 2;
    public static final int STARTED = 1;
    public static final int TIME_EXCEEDED = 2;
    public static final int NO_TABLES = 3;
    public static final int COMPLETE_JOIN_ORDER = 4;
    public static final int COST_OF_SORTING = 5;
    public static final int NO_BEST_PLAN = 6;
    public static final int MODIFYING_ACCESS_PATHS = 7;
    public static final int SHORT_CIRCUITING = 8;
    public static final int SKIPPING_JOIN_ORDER = 9;
    public static final int ILLEGAL_USER_JOIN_ORDER = 10;
    public static final int USER_JOIN_ORDER_OPTIMIZED = 11;
    public static final int CONSIDERING_JOIN_ORDER = 12;
    public static final int TOTAL_COST_NON_SA_PLAN = 13;
    public static final int TOTAL_COST_SA_PLAN = 14;
    public static final int TOTAL_COST_WITH_SORTING = 15;
    public static final int CURRENT_PLAN_IS_SA_PLAN = 16;
    public static final int CHEAPEST_PLAN_SO_FAR = 17;
    public static final int PLAN_TYPE = 18;
    public static final int COST_OF_CHEAPEST_PLAN_SO_FAR = 19;
    public static final int SORT_NEEDED_FOR_ORDERING = 20;
    public static final int REMEMBERING_BEST_JOIN_ORDER = 21;
    public static final int SKIPPING_DUE_TO_EXCESS_MEMORY = 22;
    public static final int COST_OF_N_SCANS = 23;
    public static final int HJ_SKIP_NOT_MATERIALIZABLE = 24;
    public static final int HJ_SKIP_NO_JOIN_COLUMNS = 25;
    public static final int HJ_HASH_KEY_COLUMNS = 26;
    public static final int CALLING_ON_JOIN_NODE = 27;
    public static final int CONSIDERING_JOIN_STRATEGY = 28;
    public static final int REMEMBERING_BEST_ACCESS_PATH = 29;
    public static final int NO_MORE_CONGLOMERATES = 30;
    public static final int CONSIDERING_CONGLOMERATE = 31;
    public static final int SCANNING_HEAP_FULL_MATCH_ON_UNIQUE_KEY = 32;
    public static final int ADDING_UNORDERED_OPTIMIZABLE = 33;
    public static final int CHANGING_ACCESS_PATH_FOR_TABLE = 34;
    public static final int TABLE_LOCK_NO_START_STOP = 35;
    public static final int NON_COVERING_INDEX_COST = 36;
    public static final int ROW_LOCK_ALL_CONSTANT_START_STOP = 37;
    public static final int ESTIMATING_COST_OF_CONGLOMERATE = 38;
    public static final int LOOKING_FOR_SPECIFIED_INDEX = 39;
    public static final int MATCH_SINGLE_ROW_COST = 40;
    public static final int COST_INCLUDING_EXTRA_1ST_COL_SELECTIVITY = 41;
    public static final int CALLING_NEXT_ACCESS_PATH = 42;
    public static final int TABLE_LOCK_OVER_THRESHOLD = 43;
    public static final int ROW_LOCK_UNDER_THRESHOLD = 44;
    public static final int COST_INCLUDING_EXTRA_START_STOP = 45;
    public static final int COST_INCLUDING_EXTRA_QUALIFIER_SELECTIVITY = 46;
    public static final int COST_INCLUDING_EXTRA_NONQUALIFIER_SELECTIVITY = 47;
    public static final int COST_OF_NONCOVERING_INDEX = 48;
    public static final int REMEMBERING_JOIN_STRATEGY = 49;
    public static final int REMEMBERING_BEST_ACCESS_PATH_SUBSTRING = 50;
    public static final int REMEMBERING_BEST_SORT_AVOIDANCE_ACCESS_PATH_SUBSTRING = 51;
    public static final int REMEMBERING_BEST_UNKNOWN_ACCESS_PATH_SUBSTRING = 52;
    public static final int COST_OF_CONGLOMERATE_SCAN1 = 53;
    public static final int COST_OF_CONGLOMERATE_SCAN2 = 54;
    public static final int COST_OF_CONGLOMERATE_SCAN3 = 55;
    public static final int COST_OF_CONGLOMERATE_SCAN4 = 56;
    public static final int COST_OF_CONGLOMERATE_SCAN5 = 57;
    public static final int COST_OF_CONGLOMERATE_SCAN6 = 58;
    public static final int COST_OF_CONGLOMERATE_SCAN7 = 59;
    public static final int COST_INCLUDING_COMPOSITE_SEL_FROM_STATS = 60;
    public static final int COMPOSITE_SEL_FROM_STATS = 61;
    public static final int COST_INCLUDING_STATS_FOR_INDEX = 62;
    
    boolean getNextPermutation() throws StandardException;
    
    boolean getNextDecoratedPermutation() throws StandardException;
    
    void costPermutation() throws StandardException;
    
    void costOptimizable(final Optimizable p0, final TableDescriptor p1, final ConglomerateDescriptor p2, final OptimizablePredicateList p3, final CostEstimate p4) throws StandardException;
    
    void considerCost(final Optimizable p0, final OptimizablePredicateList p1, final CostEstimate p2, final CostEstimate p3) throws StandardException;
    
    DataDictionary getDataDictionary();
    
    void modifyAccessPaths() throws StandardException;
    
    CostEstimate newCostEstimate();
    
    CostEstimate getOptimizedCost();
    
    CostEstimate getFinalCost();
    
    void prepForNextRound();
    
    void setOuterRows(final double p0);
    
    int getNumberOfJoinStrategies();
    
    int tableLockThreshold();
    
    JoinStrategy getJoinStrategy(final int p0);
    
    JoinStrategy getJoinStrategy(final String p0);
    
    void trace(final int p0, final int p1, final int p2, final double p3, final Object p4);
    
    int getLevel();
    
    double uniqueJoinWithOuterTable(final OptimizablePredicateList p0) throws StandardException;
    
    boolean useStatistics();
    
    int getMaxMemoryPerTable();
}
