// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.parser;

import org.antlr.runtime.ANTLRStringStream;
import com.google.common.collect.Sets;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import java.text.DateFormat;
import java.util.Date;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.Set;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.Warehouse;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hive.metastore.api.MetaException;
import java.util.Stack;

public class ExpressionTree
{
    public static final ExpressionTree EMPTY_TREE;
    private TreeNode root;
    private final Stack<TreeNode> nodeStack;
    
    public ExpressionTree() {
        this.root = null;
        this.nodeStack = new Stack<TreeNode>();
    }
    
    public void accept(final TreeVisitor treeVisitor) throws MetaException {
        if (this.root != null) {
            this.root.accept(treeVisitor);
        }
    }
    
    private static void makeFilterForEquals(final String keyName, final String value, final String paramName, final Map<String, Object> params, final int keyPos, final int keyCount, final boolean isEq, final FilterBuilder fltr) throws MetaException {
        final Map<String, String> partKeyToVal = new HashMap<String, String>();
        partKeyToVal.put(keyName, value);
        final String escapedNameFragment = Warehouse.makePartName(partKeyToVal, false);
        if (keyCount == 1) {
            params.put(paramName, escapedNameFragment);
            fltr.append("partitionName ").append(isEq ? "== " : "!= ").append(paramName);
        }
        else if (keyPos + 1 == keyCount) {
            params.put(paramName, "/" + escapedNameFragment);
            fltr.append(isEq ? "" : "!").append("partitionName.endsWith(").append(paramName).append(")");
        }
        else if (keyPos == 0) {
            params.put(paramName, escapedNameFragment + "/");
            fltr.append(isEq ? "" : "!").append("partitionName.startsWith(").append(paramName).append(")");
        }
        else {
            params.put(paramName, "/" + escapedNameFragment + "/");
            fltr.append("partitionName.indexOf(").append(paramName).append(")").append(isEq ? ">= 0" : "< 0");
        }
    }
    
    public TreeNode getRoot() {
        return this.root;
    }
    
    public void addIntermediateNode(final LogicalOperator andOr) {
        final TreeNode rhs = this.nodeStack.pop();
        final TreeNode lhs = this.nodeStack.pop();
        final TreeNode newNode = new TreeNode(lhs, andOr, rhs);
        this.nodeStack.push(newNode);
        this.root = newNode;
    }
    
    public void addLeafNode(final LeafNode newNode) {
        if (this.root == null) {
            this.root = newNode;
        }
        this.nodeStack.push(newNode);
    }
    
    public void generateJDOFilterFragment(final Configuration conf, final Table table, final Map<String, Object> params, final FilterBuilder filterBuilder) throws MetaException {
        if (this.root == null) {
            return;
        }
        filterBuilder.append(" && ( ");
        this.root.generateJDOFilter(conf, table, params, filterBuilder);
        filterBuilder.append(" )");
    }
    
    static {
        EMPTY_TREE = new ExpressionTree();
    }
    
    public enum LogicalOperator
    {
        AND, 
        OR;
    }
    
    public enum Operator
    {
        EQUALS("=", "==", "="), 
        GREATERTHAN(">"), 
        LESSTHAN("<"), 
        LESSTHANOREQUALTO("<="), 
        GREATERTHANOREQUALTO(">="), 
        LIKE("LIKE", "matches", "like"), 
        NOTEQUALS2("!=", "!=", "<>"), 
        NOTEQUALS("<>", "!=", "<>");
        
        private final String op;
        private final String jdoOp;
        private final String sqlOp;
        
        private Operator(final String op) {
            this.op = op;
            this.jdoOp = op;
            this.sqlOp = op;
        }
        
        private Operator(final String op, final String jdoOp, final String sqlOp) {
            this.op = op;
            this.jdoOp = jdoOp;
            this.sqlOp = sqlOp;
        }
        
        public String getOp() {
            return this.op;
        }
        
        public String getJdoOp() {
            return this.jdoOp;
        }
        
        public String getSqlOp() {
            return this.sqlOp;
        }
        
        public static Operator fromString(final String inputOperator) {
            for (final Operator op : values()) {
                if (op.getOp().equals(inputOperator)) {
                    return op;
                }
            }
            throw new Error("Invalid value " + inputOperator + " for " + Operator.class.getSimpleName());
        }
        
        @Override
        public String toString() {
            return this.op;
        }
    }
    
    public static class TreeVisitor
    {
        private void visit(final TreeNode node) throws MetaException {
            if (this.shouldStop()) {
                return;
            }
            assert node != null && node.getLhs() != null && node.getRhs() != null;
            this.beginTreeNode(node);
            node.lhs.accept(this);
            this.midTreeNode(node);
            node.rhs.accept(this);
            this.endTreeNode(node);
        }
        
        protected void beginTreeNode(final TreeNode node) throws MetaException {
        }
        
        protected void midTreeNode(final TreeNode node) throws MetaException {
        }
        
        protected void endTreeNode(final TreeNode node) throws MetaException {
        }
        
        protected void visit(final LeafNode node) throws MetaException {
        }
        
        protected boolean shouldStop() {
            return false;
        }
    }
    
    public static class FilterBuilder
    {
        private final StringBuilder result;
        private String errorMessage;
        private boolean expectNoErrors;
        
        public FilterBuilder(final boolean expectNoErrors) {
            this.result = new StringBuilder();
            this.errorMessage = null;
            this.expectNoErrors = false;
            this.expectNoErrors = expectNoErrors;
        }
        
        public String getFilter() throws MetaException {
            assert this.errorMessage == null;
            if (this.errorMessage != null) {
                throw new MetaException("Trying to get result after error: " + this.errorMessage);
            }
            return this.result.toString();
        }
        
        @Override
        public String toString() {
            try {
                return this.getFilter();
            }
            catch (MetaException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        public String getErrorMessage() {
            return this.errorMessage;
        }
        
        public boolean hasError() {
            return this.errorMessage != null;
        }
        
        public FilterBuilder append(final String filterPart) {
            this.result.append(filterPart);
            return this;
        }
        
        public void setError(final String errorMessage) throws MetaException {
            this.errorMessage = errorMessage;
            if (this.expectNoErrors) {
                throw new MetaException(errorMessage);
            }
        }
    }
    
    public static class TreeNode
    {
        private TreeNode lhs;
        private LogicalOperator andOr;
        private TreeNode rhs;
        
        public TreeNode() {
        }
        
        public TreeNode(final TreeNode lhs, final LogicalOperator andOr, final TreeNode rhs) {
            this.lhs = lhs;
            this.andOr = andOr;
            this.rhs = rhs;
        }
        
        public TreeNode getLhs() {
            return this.lhs;
        }
        
        public LogicalOperator getAndOr() {
            return this.andOr;
        }
        
        public TreeNode getRhs() {
            return this.rhs;
        }
        
        protected void accept(final TreeVisitor visitor) throws MetaException {
            visitor.visit(this);
        }
        
        public void generateJDOFilter(final Configuration conf, final Table table, final Map<String, Object> params, final FilterBuilder filterBuffer) throws MetaException {
            if (filterBuffer.hasError()) {
                return;
            }
            if (this.lhs != null) {
                filterBuffer.append(" (");
                this.lhs.generateJDOFilter(conf, table, params, filterBuffer);
                if (this.rhs != null) {
                    if (this.andOr == LogicalOperator.AND) {
                        filterBuffer.append(" && ");
                    }
                    else {
                        filterBuffer.append(" || ");
                    }
                    this.rhs.generateJDOFilter(conf, table, params, filterBuffer);
                }
                filterBuffer.append(") ");
            }
        }
    }
    
    public static class LeafNode extends TreeNode
    {
        public String keyName;
        public Operator operator;
        public Object value;
        public boolean isReverseOrder;
        private static final String PARAM_PREFIX = "hive_filter_param_";
        private static final Set<Operator> TABLE_FILTER_OPS;
        
        public LeafNode() {
            this.isReverseOrder = false;
        }
        
        @Override
        protected void accept(final TreeVisitor visitor) throws MetaException {
            visitor.visit(this);
        }
        
        @Override
        public void generateJDOFilter(final Configuration conf, final Table table, final Map<String, Object> params, final FilterBuilder filterBuilder) throws MetaException {
            if (table != null) {
                this.generateJDOFilterOverPartitions(conf, table, params, filterBuilder);
            }
            else {
                this.generateJDOFilterOverTables(params, filterBuilder);
            }
        }
        
        private void generateJDOFilterOverTables(final Map<String, Object> params, final FilterBuilder filterBuilder) throws MetaException {
            if (this.keyName.equals("hive_filter_field_owner__")) {
                this.keyName = "this.owner";
            }
            else if (this.keyName.equals("hive_filter_field_last_access__")) {
                if (this.operator == Operator.LIKE) {
                    filterBuilder.setError("Like is not supported for HIVE_FILTER_FIELD_LAST_ACCESS");
                    return;
                }
                this.keyName = "this.lastAccessTime";
            }
            else {
                if (!this.keyName.startsWith("hive_filter_field_params__")) {
                    filterBuilder.setError("Invalid key name in filter.  Use constants from org.apache.hadoop.hive.metastore.api");
                    return;
                }
                if (!LeafNode.TABLE_FILTER_OPS.contains(this.operator)) {
                    filterBuilder.setError("Only " + LeafNode.TABLE_FILTER_OPS + " are supported " + "operators for HIVE_FILTER_FIELD_PARAMS");
                    return;
                }
                final String paramKeyName = this.keyName.substring("hive_filter_field_params__".length());
                this.keyName = "this.parameters.get(\"" + paramKeyName + "\")";
                this.value = this.value.toString();
            }
            this.generateJDOFilterGeneral(params, filterBuilder);
        }
        
        private void generateJDOFilterGeneral(final Map<String, Object> params, final FilterBuilder filterBuilder) throws MetaException {
            final String paramName = "hive_filter_param_" + params.size();
            params.put(paramName, this.value);
            if (this.isReverseOrder) {
                if (this.operator == Operator.LIKE) {
                    filterBuilder.setError("Value should be on the RHS for LIKE operator : Key <" + this.keyName + ">");
                }
                else {
                    filterBuilder.append(paramName + " " + this.operator.getJdoOp() + " " + this.keyName);
                }
            }
            else if (this.operator == Operator.LIKE) {
                filterBuilder.append(" " + this.keyName + "." + this.operator.getJdoOp() + "(" + paramName + ") ");
            }
            else {
                filterBuilder.append(" " + this.keyName + " " + this.operator.getJdoOp() + " " + paramName);
            }
        }
        
        private void generateJDOFilterOverPartitions(final Configuration conf, final Table table, final Map<String, Object> params, final FilterBuilder filterBuilder) throws MetaException {
            final int partitionColumnCount = table.getPartitionKeys().size();
            final int partitionColumnIndex = this.getPartColIndexForFilter(table, filterBuilder);
            if (filterBuilder.hasError()) {
                return;
            }
            final boolean canPushDownIntegral = HiveConf.getBoolVar(conf, HiveConf.ConfVars.METASTORE_INTEGER_JDO_PUSHDOWN);
            final String valueAsString = this.getJdoFilterPushdownParam(table, partitionColumnIndex, filterBuilder, canPushDownIntegral);
            if (filterBuilder.hasError()) {
                return;
            }
            final String paramName = "hive_filter_param_" + params.size();
            params.put(paramName, valueAsString);
            final boolean isOpEquals = this.operator == Operator.EQUALS;
            if (isOpEquals || this.operator == Operator.NOTEQUALS || this.operator == Operator.NOTEQUALS2) {
                makeFilterForEquals(this.keyName, valueAsString, paramName, params, partitionColumnIndex, partitionColumnCount, isOpEquals, filterBuilder);
                return;
            }
            final String valString = "values.get(" + partitionColumnIndex + ")";
            if (this.operator == Operator.LIKE) {
                if (this.isReverseOrder) {
                    filterBuilder.setError("Value should be on the RHS for LIKE operator : Key <" + this.keyName + ">");
                }
                filterBuilder.append(" " + valString + "." + this.operator.getJdoOp() + "(" + paramName + ") ");
            }
            else {
                filterBuilder.append(this.isReverseOrder ? (paramName + " " + this.operator.getJdoOp() + " " + valString) : (" " + valString + " " + this.operator.getJdoOp() + " " + paramName));
            }
        }
        
        public boolean canJdoUseStringsWithIntegral() {
            return this.operator == Operator.EQUALS || this.operator == Operator.NOTEQUALS || this.operator == Operator.NOTEQUALS2;
        }
        
        public int getPartColIndexForFilter(final Table table, final FilterBuilder filterBuilder) throws MetaException {
            assert table.getPartitionKeys().size() > 0;
            int partitionColumnIndex;
            for (partitionColumnIndex = 0; partitionColumnIndex < table.getPartitionKeys().size() && !table.getPartitionKeys().get(partitionColumnIndex).getName().equalsIgnoreCase(this.keyName); ++partitionColumnIndex) {}
            if (partitionColumnIndex == table.getPartitionKeys().size()) {
                filterBuilder.setError("Specified key <" + this.keyName + "> is not a partitioning key for the table");
                return -1;
            }
            return partitionColumnIndex;
        }
        
        private String getJdoFilterPushdownParam(final Table table, final int partColIndex, final FilterBuilder filterBuilder, final boolean canPushDownIntegral) throws MetaException {
            final boolean isIntegralSupported = canPushDownIntegral && this.canJdoUseStringsWithIntegral();
            final String colType = table.getPartitionKeys().get(partColIndex).getType();
            if (!colType.equals("string") && (!isIntegralSupported || !serdeConstants.IntegralTypes.contains(colType))) {
                filterBuilder.setError("Filtering is supported only on partition keys of type string" + (isIntegralSupported ? ", or integral types" : ""));
                return null;
            }
            Object val = this.value;
            if (this.value instanceof Date) {
                val = HiveMetaStore.PARTITION_DATE_FORMAT.get().format((Date)this.value);
            }
            final boolean isStringValue = val instanceof String;
            if (!isStringValue && (!isIntegralSupported || !(val instanceof Long))) {
                filterBuilder.setError("Filtering is supported only on partition keys of type string" + (isIntegralSupported ? ", or integral types" : ""));
                return null;
            }
            return (String)(isStringValue ? val : Long.toString((long)val));
        }
        
        static {
            TABLE_FILTER_OPS = Sets.newHashSet(Operator.EQUALS, Operator.NOTEQUALS, Operator.NOTEQUALS2);
        }
    }
    
    public static class ANTLRNoCaseStringStream extends ANTLRStringStream
    {
        public ANTLRNoCaseStringStream(final String input) {
            super(input);
        }
        
        @Override
        public int LA(final int i) {
            final int returnChar = super.LA(i);
            if (returnChar == -1) {
                return returnChar;
            }
            if (returnChar == 0) {
                return returnChar;
            }
            return Character.toUpperCase((char)returnChar);
        }
    }
}
