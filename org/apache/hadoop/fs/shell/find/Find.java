// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell.find;

import org.apache.hadoop.fs.shell.PathData;
import java.io.IOException;
import java.util.Deque;
import org.apache.hadoop.fs.shell.CommandFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import org.apache.hadoop.conf.Configuration;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.hadoop.fs.shell.Command;
import org.apache.hadoop.fs.shell.CommandFactory;
import org.apache.hadoop.fs.Path;
import java.util.HashSet;
import java.util.Set;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.shell.FsCommand;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class Find extends FsCommand
{
    public static final String NAME = "find";
    public static final String USAGE = "<path> ... <expression> ...";
    public static final String DESCRIPTION;
    private static String[] HELP;
    private static final String OPTION_FOLLOW_LINK = "L";
    private static final String OPTION_FOLLOW_ARG_LINK = "H";
    private static final Set<Class<? extends Expression>> EXPRESSIONS;
    private FindOptions options;
    private Expression rootExpression;
    private HashSet<Path> stopPaths;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Find.class, "-find");
    }
    
    private static void addExpression(final Class<?> clazz) {
        Find.EXPRESSIONS.add(clazz.asSubclass(Expression.class));
    }
    
    private static void registerExpressions(final ExpressionFactory factory) {
        for (final Class<? extends Expression> exprClass : Find.EXPRESSIONS) {
            factory.registerExpression(exprClass);
        }
    }
    
    private static String buildDescription(final ExpressionFactory factory) {
        final ArrayList<Expression> operators = new ArrayList<Expression>();
        final ArrayList<Expression> primaries = new ArrayList<Expression>();
        for (final Class<? extends Expression> exprClass : Find.EXPRESSIONS) {
            final Expression expr = factory.createExpression(exprClass, null);
            if (expr.isOperator()) {
                operators.add(expr);
            }
            else {
                primaries.add(expr);
            }
        }
        Collections.sort(operators, new Comparator<Expression>() {
            @Override
            public int compare(final Expression arg0, final Expression arg1) {
                return arg0.getClass().getName().compareTo(arg1.getClass().getName());
            }
        });
        Collections.sort(primaries, new Comparator<Expression>() {
            @Override
            public int compare(final Expression arg0, final Expression arg1) {
                return arg0.getClass().getName().compareTo(arg1.getClass().getName());
            }
        });
        final StringBuilder sb = new StringBuilder();
        for (final String line : Find.HELP) {
            sb.append(line).append("\n");
        }
        sb.append("\n");
        sb.append("The following primary expressions are recognised:\n");
        final Iterator<Expression> iterator2 = primaries.iterator();
        while (iterator2.hasNext()) {
            final Expression expr = iterator2.next();
            for (final String line2 : expr.getUsage()) {
                sb.append("  ").append(line2).append("\n");
            }
            for (final String line2 : expr.getHelp()) {
                sb.append("    ").append(line2).append("\n");
            }
            sb.append("\n");
        }
        sb.append("The following operators are recognised:\n");
        final Iterator<Expression> iterator3 = operators.iterator();
        while (iterator3.hasNext()) {
            final Expression expr = iterator3.next();
            for (final String line2 : expr.getUsage()) {
                sb.append("  ").append(line2).append("\n");
            }
            for (final String line2 : expr.getHelp()) {
                sb.append("    ").append(line2).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public Find() {
        this.stopPaths = new HashSet<Path>();
        this.setRecursive(true);
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) throws IOException {
        final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "L", "H", null });
        cf.parse(args);
        if (cf.getOpt("L")) {
            this.getOptions().setFollowLink(true);
        }
        else if (cf.getOpt("H")) {
            this.getOptions().setFollowArgLink(true);
        }
        final LinkedList<String> expressionArgs = new LinkedList<String>();
        final Iterator<String> it = args.iterator();
        boolean isPath = true;
        while (it.hasNext()) {
            final String arg = it.next();
            if (isPath && arg.startsWith("-")) {
                isPath = false;
            }
            if (!isPath) {
                expressionArgs.add(arg);
                it.remove();
            }
        }
        if (args.isEmpty()) {
            args.add(".");
        }
        Expression expression = this.parseExpression(expressionArgs);
        if (!expression.isAction()) {
            final Expression and = this.getExpression(And.class);
            final Deque<Expression> children = new LinkedList<Expression>();
            children.add(this.getExpression(Print.class));
            children.add(expression);
            and.addChildren(children);
            expression = and;
        }
        this.setRootExpression(expression);
    }
    
    @InterfaceAudience.Private
    void setRootExpression(final Expression expression) {
        this.rootExpression = expression;
    }
    
    @InterfaceAudience.Private
    Expression getRootExpression() {
        return this.rootExpression;
    }
    
    @InterfaceAudience.Private
    FindOptions getOptions() {
        if (this.options == null) {
            this.options = this.createOptions();
        }
        return this.options;
    }
    
    private FindOptions createOptions() {
        final FindOptions options = new FindOptions();
        options.setOut(this.out);
        options.setErr(this.err);
        options.setIn(System.in);
        options.setCommandFactory(this.getCommandFactory());
        options.setConfiguration(this.getConf());
        return options;
    }
    
    private void addStop(final PathData item) {
        this.stopPaths.add(item.path);
    }
    
    private boolean isStop(final PathData item) {
        return this.stopPaths.contains(item.path);
    }
    
    private Expression parseExpression(final Deque<String> args) throws IOException {
        final Deque<Expression> primaries = new LinkedList<Expression>();
        final Deque<Expression> operators = new LinkedList<Expression>();
        Expression prevExpr = this.getExpression(And.class);
        while (!args.isEmpty()) {
            final String arg = args.pop();
            if ("(".equals(arg)) {
                final Expression expr = this.parseExpression(args);
                primaries.add(expr);
                prevExpr = new BaseExpression() {
                    @Override
                    public Result apply(final PathData item, final int depth) throws IOException {
                        return Result.PASS;
                    }
                };
            }
            else {
                if (")".equals(arg)) {
                    break;
                }
                if (!this.isExpression(arg)) {
                    throw new IOException("Unexpected argument: " + arg);
                }
                final Expression expr = this.getExpression(arg);
                expr.addArguments(args);
                if (expr.isOperator()) {
                    while (!operators.isEmpty() && operators.peek().getPrecedence() >= expr.getPrecedence()) {
                        final Expression op = operators.pop();
                        op.addChildren(primaries);
                        primaries.push(op);
                    }
                    operators.push(expr);
                }
                else {
                    if (!prevExpr.isOperator()) {
                        final Expression and = this.getExpression(And.class);
                        while (!operators.isEmpty() && operators.peek().getPrecedence() >= and.getPrecedence()) {
                            final Expression op2 = operators.pop();
                            op2.addChildren(primaries);
                            primaries.push(op2);
                        }
                        operators.push(and);
                    }
                    primaries.push(expr);
                }
                prevExpr = expr;
            }
        }
        while (!operators.isEmpty()) {
            final Expression operator = operators.pop();
            operator.addChildren(primaries);
            primaries.push(operator);
        }
        return primaries.isEmpty() ? this.getExpression(Print.class) : primaries.pop();
    }
    
    private boolean isAncestor(final PathData source, final PathData target) {
        for (Path parent = source.path; parent != null && !parent.isRoot(); parent = parent.getParent()) {
            if (parent.equals(target.path)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void recursePath(PathData item) throws IOException {
        if (this.isStop(item)) {
            return;
        }
        if (this.getDepth() >= this.getOptions().getMaxDepth()) {
            return;
        }
        if (item.stat.isSymlink() && this.getOptions().isFollowLink()) {
            final PathData linkedItem = new PathData(item.stat.getSymlink().toString(), this.getConf());
            if (this.isAncestor(item, linkedItem)) {
                this.getOptions().getErr().println("Infinite loop ignored: " + item.toString() + " -> " + linkedItem.toString());
                return;
            }
            if (linkedItem.exists) {
                item = linkedItem;
            }
        }
        if (item.stat.isDirectory()) {
            super.recursePath(item);
        }
    }
    
    @Override
    protected boolean isPathRecursable(final PathData item) throws IOException {
        if (item.stat.isDirectory()) {
            return true;
        }
        if (item.stat.isSymlink()) {
            final PathData linkedItem = new PathData(item.fs.resolvePath(item.stat.getSymlink()).toString(), this.getConf());
            if (linkedItem.stat.isDirectory()) {
                if (this.getOptions().isFollowLink()) {
                    return true;
                }
                if (this.getOptions().isFollowArgLink() && this.getDepth() == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    protected void processPath(final PathData item) throws IOException {
        if (this.getOptions().isDepthFirst()) {
            return;
        }
        this.applyItem(item);
    }
    
    @Override
    protected void postProcessPath(final PathData item) throws IOException {
        if (!this.getOptions().isDepthFirst()) {
            return;
        }
        this.applyItem(item);
    }
    
    private void applyItem(final PathData item) throws IOException {
        if (this.getDepth() >= this.getOptions().getMinDepth()) {
            final Result result = this.getRootExpression().apply(item, this.getDepth());
            if (Result.STOP.equals(result)) {
                this.addStop(item);
            }
        }
    }
    
    @Override
    protected void processArguments(final LinkedList<PathData> args) throws IOException {
        final Expression expr = this.getRootExpression();
        expr.setOptions(this.getOptions());
        expr.prepare();
        super.processArguments(args);
        expr.finish();
    }
    
    private Expression getExpression(final String expressionName) {
        return ExpressionFactory.getExpressionFactory().getExpression(expressionName, this.getConf());
    }
    
    private Expression getExpression(final Class<? extends Expression> expressionClass) {
        return ExpressionFactory.getExpressionFactory().createExpression(expressionClass, this.getConf());
    }
    
    private boolean isExpression(final String expressionName) {
        return ExpressionFactory.getExpressionFactory().isExpression(expressionName);
    }
    
    static {
        Find.HELP = new String[] { "Finds all files that match the specified expression and", "applies selected actions to them. If no <path> is specified", "then defaults to the current working directory. If no", "expression is specified then defaults to -print." };
        EXPRESSIONS = new HashSet<Class<? extends Expression>>();
        addExpression(And.class);
        addExpression(Print.class);
        addExpression(Name.class);
        DESCRIPTION = buildDescription(ExpressionFactory.getExpressionFactory());
        registerExpressions(ExpressionFactory.getExpressionFactory());
    }
}
