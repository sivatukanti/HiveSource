// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class DefaultToolErrorHandler implements ToolErrorHandler
{
    private final Tool antlrTool;
    CharFormatter javaCharFormatter;
    
    DefaultToolErrorHandler(final Tool antlrTool) {
        this.javaCharFormatter = new JavaCharFormatter();
        this.antlrTool = antlrTool;
    }
    
    private void dumpSets(final String[] array, int n, final Grammar grammar, final boolean b, final int n2, final Lookahead[] array2) {
        final StringBuffer sb = new StringBuffer(100);
        for (int i = 1; i <= n2; ++i) {
            sb.append("k==").append(i).append(':');
            if (b) {
                final String stringWithRanges = array2[i].fset.toStringWithRanges(",", this.javaCharFormatter);
                if (array2[i].containsEpsilon()) {
                    sb.append("<end-of-token>");
                    if (stringWithRanges.length() > 0) {
                        sb.append(',');
                    }
                }
                sb.append(stringWithRanges);
            }
            else {
                sb.append(array2[i].fset.toString(",", grammar.tokenManager.getVocabulary()));
            }
            array[n++] = sb.toString();
            sb.setLength(0);
        }
    }
    
    public void warnAltAmbiguity(final Grammar grammar, final AlternativeBlock alternativeBlock, final boolean b, final int n, final Lookahead[] array, final int n2, final int n3) {
        final StringBuffer sb = new StringBuffer(100);
        if (alternativeBlock instanceof RuleBlock && ((RuleBlock)alternativeBlock).isLexerAutoGenRule()) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(n2);
            final Alternative alternative2 = alternativeBlock.getAlternativeAt(n3);
            final RuleRefElement ruleRefElement = (RuleRefElement)alternative.head;
            final RuleRefElement ruleRefElement2 = (RuleRefElement)alternative2.head;
            final String reverseLexerRuleName = CodeGenerator.reverseLexerRuleName(ruleRefElement.targetRule);
            final String reverseLexerRuleName2 = CodeGenerator.reverseLexerRuleName(ruleRefElement2.targetRule);
            sb.append("lexical nondeterminism between rules ");
            sb.append(reverseLexerRuleName).append(" and ").append(reverseLexerRuleName2).append(" upon");
        }
        else {
            if (b) {
                sb.append("lexical ");
            }
            sb.append("nondeterminism between alts ");
            sb.append(n2 + 1).append(" and ");
            sb.append(n3 + 1).append(" of block upon");
        }
        final String[] array2 = new String[n + 1];
        array2[0] = sb.toString();
        this.dumpSets(array2, 1, grammar, b, n, array);
        this.antlrTool.warning(array2, grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
    }
    
    public void warnAltExitAmbiguity(final Grammar grammar, final BlockWithImpliedExitPath blockWithImpliedExitPath, final boolean b, final int n, final Lookahead[] array, final int n2) {
        final String[] array2 = new String[n + 2];
        array2[0] = (b ? "lexical " : "") + "nondeterminism upon";
        this.dumpSets(array2, 1, grammar, b, n, array);
        array2[n + 1] = "between alt " + (n2 + 1) + " and exit branch of block";
        this.antlrTool.warning(array2, grammar.getFilename(), blockWithImpliedExitPath.getLine(), blockWithImpliedExitPath.getColumn());
    }
}
