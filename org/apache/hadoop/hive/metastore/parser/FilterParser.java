// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.parser;

import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.Token;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.Parser;

public class FilterParser extends Parser
{
    public static final String[] tokenNames;
    public static final int EOF = -1;
    public static final int BETWEEN = 4;
    public static final int DateLiteral = 5;
    public static final int DateString = 6;
    public static final int Digit = 7;
    public static final int EQUAL = 8;
    public static final int GREATERTHAN = 9;
    public static final int GREATERTHANOREQUALTO = 10;
    public static final int Identifier = 11;
    public static final int IntegralLiteral = 12;
    public static final int KW_AND = 13;
    public static final int KW_DATE = 14;
    public static final int KW_LIKE = 15;
    public static final int KW_NOT = 16;
    public static final int KW_OR = 17;
    public static final int LESSTHAN = 18;
    public static final int LESSTHANOREQUALTO = 19;
    public static final int LPAREN = 20;
    public static final int Letter = 21;
    public static final int NOTEQUAL = 22;
    public static final int RPAREN = 23;
    public static final int StringLiteral = 24;
    public static final int WS = 25;
    public ExpressionTree tree;
    public static final BitSet FOLLOW_orExpression_in_filter84;
    public static final BitSet FOLLOW_andExpression_in_orExpression106;
    public static final BitSet FOLLOW_KW_OR_in_orExpression109;
    public static final BitSet FOLLOW_andExpression_in_orExpression111;
    public static final BitSet FOLLOW_expression_in_andExpression137;
    public static final BitSet FOLLOW_KW_AND_in_andExpression140;
    public static final BitSet FOLLOW_expression_in_andExpression142;
    public static final BitSet FOLLOW_LPAREN_in_expression169;
    public static final BitSet FOLLOW_orExpression_in_expression171;
    public static final BitSet FOLLOW_RPAREN_in_expression173;
    public static final BitSet FOLLOW_operatorExpression_in_expression185;
    public static final BitSet FOLLOW_betweenExpression_in_operatorExpression206;
    public static final BitSet FOLLOW_binOpExpression_in_operatorExpression218;
    public static final BitSet FOLLOW_Identifier_in_binOpExpression269;
    public static final BitSet FOLLOW_operator_in_binOpExpression275;
    public static final BitSet FOLLOW_DateLiteral_in_binOpExpression282;
    public static final BitSet FOLLOW_DateLiteral_in_binOpExpression310;
    public static final BitSet FOLLOW_operator_in_binOpExpression317;
    public static final BitSet FOLLOW_Identifier_in_binOpExpression323;
    public static final BitSet FOLLOW_Identifier_in_binOpExpression371;
    public static final BitSet FOLLOW_operator_in_binOpExpression377;
    public static final BitSet FOLLOW_StringLiteral_in_binOpExpression384;
    public static final BitSet FOLLOW_StringLiteral_in_binOpExpression412;
    public static final BitSet FOLLOW_operator_in_binOpExpression419;
    public static final BitSet FOLLOW_Identifier_in_binOpExpression425;
    public static final BitSet FOLLOW_Identifier_in_binOpExpression473;
    public static final BitSet FOLLOW_operator_in_binOpExpression479;
    public static final BitSet FOLLOW_IntegralLiteral_in_binOpExpression485;
    public static final BitSet FOLLOW_IntegralLiteral_in_binOpExpression513;
    public static final BitSet FOLLOW_operator_in_binOpExpression519;
    public static final BitSet FOLLOW_Identifier_in_binOpExpression525;
    public static final BitSet FOLLOW_set_in_operator573;
    public static final BitSet FOLLOW_Identifier_in_betweenExpression638;
    public static final BitSet FOLLOW_KW_NOT_in_betweenExpression641;
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression648;
    public static final BitSet FOLLOW_DateLiteral_in_betweenExpression673;
    public static final BitSet FOLLOW_KW_AND_in_betweenExpression675;
    public static final BitSet FOLLOW_DateLiteral_in_betweenExpression681;
    public static final BitSet FOLLOW_StringLiteral_in_betweenExpression711;
    public static final BitSet FOLLOW_KW_AND_in_betweenExpression713;
    public static final BitSet FOLLOW_StringLiteral_in_betweenExpression719;
    public static final BitSet FOLLOW_IntegralLiteral_in_betweenExpression749;
    public static final BitSet FOLLOW_KW_AND_in_betweenExpression751;
    public static final BitSet FOLLOW_IntegralLiteral_in_betweenExpression757;
    
    public Parser[] getDelegates() {
        return new Parser[0];
    }
    
    public FilterParser(final TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    
    public FilterParser(final TokenStream input, final RecognizerSharedState state) {
        super(input, state);
        this.tree = new ExpressionTree();
    }
    
    @Override
    public String[] getTokenNames() {
        return FilterParser.tokenNames;
    }
    
    @Override
    public String getGrammarFileName() {
        return "org/apache/hadoop/hive/metastore/parser/Filter.g";
    }
    
    public static String TrimQuotes(final String input) {
        if (input.length() > 1 && ((input.charAt(0) == '\"' && input.charAt(input.length() - 1) == '\"') || (input.charAt(0) == '\'' && input.charAt(input.length() - 1) == '\''))) {
            return input.substring(1, input.length() - 1);
        }
        return input;
    }
    
    public final void filter() throws RecognitionException {
        try {
            this.pushFollow(FilterParser.FOLLOW_orExpression_in_filter84);
            this.orExpression();
            final RecognizerSharedState state = this.state;
            --state._fsp;
        }
        catch (RecognitionException e) {
            throw e;
        }
    }
    
    public final void orExpression() throws RecognitionException {
        Label_0142: {
            try {
                this.pushFollow(FilterParser.FOLLOW_andExpression_in_orExpression106);
                this.andExpression();
                final RecognizerSharedState state = this.state;
                --state._fsp;
                while (true) {
                    int alt1 = 2;
                    switch (this.input.LA(1)) {
                        case 17: {
                            alt1 = 1;
                            break;
                        }
                    }
                    switch (alt1) {
                        case 1: {
                            this.match(this.input, 17, FilterParser.FOLLOW_KW_OR_in_orExpression109);
                            this.pushFollow(FilterParser.FOLLOW_andExpression_in_orExpression111);
                            this.andExpression();
                            final RecognizerSharedState state2 = this.state;
                            --state2._fsp;
                            this.tree.addIntermediateNode(ExpressionTree.LogicalOperator.OR);
                            continue;
                        }
                        default: {
                            break Label_0142;
                        }
                    }
                }
            }
            catch (RecognitionException e) {
                throw e;
            }
        }
    }
    
    public final void andExpression() throws RecognitionException {
        Label_0142: {
            try {
                this.pushFollow(FilterParser.FOLLOW_expression_in_andExpression137);
                this.expression();
                final RecognizerSharedState state = this.state;
                --state._fsp;
                while (true) {
                    int alt2 = 2;
                    switch (this.input.LA(1)) {
                        case 13: {
                            alt2 = 1;
                            break;
                        }
                    }
                    switch (alt2) {
                        case 1: {
                            this.match(this.input, 13, FilterParser.FOLLOW_KW_AND_in_andExpression140);
                            this.pushFollow(FilterParser.FOLLOW_expression_in_andExpression142);
                            this.expression();
                            final RecognizerSharedState state2 = this.state;
                            --state2._fsp;
                            this.tree.addIntermediateNode(ExpressionTree.LogicalOperator.AND);
                            continue;
                        }
                        default: {
                            break Label_0142;
                        }
                    }
                }
            }
            catch (RecognitionException e) {
                throw e;
            }
        }
    }
    
    public final void expression() throws RecognitionException {
        try {
            int alt3 = 2;
            switch (this.input.LA(1)) {
                case 20: {
                    alt3 = 1;
                    break;
                }
                case 5:
                case 11:
                case 12:
                case 24: {
                    alt3 = 2;
                    break;
                }
                default: {
                    final NoViableAltException nvae = new NoViableAltException("", 3, 0, this.input);
                    throw nvae;
                }
            }
            switch (alt3) {
                case 1: {
                    this.match(this.input, 20, FilterParser.FOLLOW_LPAREN_in_expression169);
                    this.pushFollow(FilterParser.FOLLOW_orExpression_in_expression171);
                    this.orExpression();
                    final RecognizerSharedState state = this.state;
                    --state._fsp;
                    this.match(this.input, 23, FilterParser.FOLLOW_RPAREN_in_expression173);
                    break;
                }
                case 2: {
                    this.pushFollow(FilterParser.FOLLOW_operatorExpression_in_expression185);
                    this.operatorExpression();
                    final RecognizerSharedState state2 = this.state;
                    --state2._fsp;
                    break;
                }
            }
        }
        catch (RecognitionException e) {
            throw e;
        }
    }
    
    public final void operatorExpression() throws RecognitionException {
        try {
            int alt4 = 2;
            switch (this.input.LA(1)) {
                case 11: {
                    switch (this.input.LA(2)) {
                        case 4:
                        case 16: {
                            alt4 = 1;
                            break;
                        }
                        case 8:
                        case 9:
                        case 10:
                        case 15:
                        case 18:
                        case 19:
                        case 22: {
                            alt4 = 2;
                            break;
                        }
                        default: {
                            final NoViableAltException nvae = new NoViableAltException("", 4, 1, this.input);
                            throw nvae;
                        }
                    }
                    break;
                }
                case 5:
                case 12:
                case 24: {
                    alt4 = 2;
                    break;
                }
                default: {
                    final NoViableAltException nvae = new NoViableAltException("", 4, 0, this.input);
                    throw nvae;
                }
            }
            switch (alt4) {
                case 1: {
                    this.pushFollow(FilterParser.FOLLOW_betweenExpression_in_operatorExpression206);
                    this.betweenExpression();
                    final RecognizerSharedState state = this.state;
                    --state._fsp;
                    break;
                }
                case 2: {
                    this.pushFollow(FilterParser.FOLLOW_binOpExpression_in_operatorExpression218);
                    this.binOpExpression();
                    final RecognizerSharedState state2 = this.state;
                    --state2._fsp;
                    break;
                }
            }
        }
        catch (RecognitionException e) {
            throw e;
        }
    }
    
    public final void binOpExpression() throws RecognitionException {
        Token key = null;
        Token value = null;
        ExpressionTree.Operator op = null;
        boolean isReverseOrder = false;
        Object val = null;
        try {
            int alt8 = 3;
            Label_0301: {
                switch (this.input.LA(1)) {
                    case 11: {
                        switch (this.input.LA(2)) {
                            case 8:
                            case 9:
                            case 10:
                            case 15:
                            case 18:
                            case 19:
                            case 22: {
                                switch (this.input.LA(3)) {
                                    case 5: {
                                        alt8 = 1;
                                        break;
                                    }
                                    case 24: {
                                        alt8 = 2;
                                        break;
                                    }
                                    case 12: {
                                        alt8 = 3;
                                        break;
                                    }
                                    default: {
                                        final NoViableAltException nvae = new NoViableAltException("", 8, 5, this.input);
                                        throw nvae;
                                    }
                                }
                                break Label_0301;
                            }
                            default: {
                                final NoViableAltException nvae = new NoViableAltException("", 8, 1, this.input);
                                throw nvae;
                            }
                        }
                        break;
                    }
                    case 5: {
                        alt8 = 1;
                        break;
                    }
                    case 24: {
                        alt8 = 2;
                        break;
                    }
                    case 12: {
                        alt8 = 3;
                        break;
                    }
                    default: {
                        final NoViableAltException nvae = new NoViableAltException("", 8, 0, this.input);
                        throw nvae;
                    }
                }
            }
            switch (alt8) {
                case 1: {
                    int alt9 = 2;
                    switch (this.input.LA(1)) {
                        case 11: {
                            alt9 = 1;
                            break;
                        }
                        case 5: {
                            alt9 = 2;
                            break;
                        }
                        default: {
                            final NoViableAltException nvae2 = new NoViableAltException("", 5, 0, this.input);
                            throw nvae2;
                        }
                    }
                    switch (alt9) {
                        case 1: {
                            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_binOpExpression269);
                            this.pushFollow(FilterParser.FOLLOW_operator_in_binOpExpression275);
                            op = this.operator();
                            final RecognizerSharedState state = this.state;
                            --state._fsp;
                            value = (Token)this.match(this.input, 5, FilterParser.FOLLOW_DateLiteral_in_binOpExpression282);
                            break;
                        }
                        case 2: {
                            value = (Token)this.match(this.input, 5, FilterParser.FOLLOW_DateLiteral_in_binOpExpression310);
                            this.pushFollow(FilterParser.FOLLOW_operator_in_binOpExpression317);
                            op = this.operator();
                            final RecognizerSharedState state2 = this.state;
                            --state2._fsp;
                            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_binOpExpression323);
                            isReverseOrder = true;
                            break;
                        }
                    }
                    val = FilterLexer.ExtractDate(value.getText());
                    break;
                }
                case 2: {
                    int alt10 = 2;
                    switch (this.input.LA(1)) {
                        case 11: {
                            alt10 = 1;
                            break;
                        }
                        case 24: {
                            alt10 = 2;
                            break;
                        }
                        default: {
                            final NoViableAltException nvae2 = new NoViableAltException("", 6, 0, this.input);
                            throw nvae2;
                        }
                    }
                    switch (alt10) {
                        case 1: {
                            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_binOpExpression371);
                            this.pushFollow(FilterParser.FOLLOW_operator_in_binOpExpression377);
                            op = this.operator();
                            final RecognizerSharedState state3 = this.state;
                            --state3._fsp;
                            value = (Token)this.match(this.input, 24, FilterParser.FOLLOW_StringLiteral_in_binOpExpression384);
                            break;
                        }
                        case 2: {
                            value = (Token)this.match(this.input, 24, FilterParser.FOLLOW_StringLiteral_in_binOpExpression412);
                            this.pushFollow(FilterParser.FOLLOW_operator_in_binOpExpression419);
                            op = this.operator();
                            final RecognizerSharedState state4 = this.state;
                            --state4._fsp;
                            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_binOpExpression425);
                            isReverseOrder = true;
                            break;
                        }
                    }
                    val = TrimQuotes(value.getText());
                    break;
                }
                case 3: {
                    int alt11 = 2;
                    switch (this.input.LA(1)) {
                        case 11: {
                            alt11 = 1;
                            break;
                        }
                        case 12: {
                            alt11 = 2;
                            break;
                        }
                        default: {
                            final NoViableAltException nvae2 = new NoViableAltException("", 7, 0, this.input);
                            throw nvae2;
                        }
                    }
                    switch (alt11) {
                        case 1: {
                            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_binOpExpression473);
                            this.pushFollow(FilterParser.FOLLOW_operator_in_binOpExpression479);
                            op = this.operator();
                            final RecognizerSharedState state5 = this.state;
                            --state5._fsp;
                            value = (Token)this.match(this.input, 12, FilterParser.FOLLOW_IntegralLiteral_in_binOpExpression485);
                            break;
                        }
                        case 2: {
                            value = (Token)this.match(this.input, 12, FilterParser.FOLLOW_IntegralLiteral_in_binOpExpression513);
                            this.pushFollow(FilterParser.FOLLOW_operator_in_binOpExpression519);
                            op = this.operator();
                            final RecognizerSharedState state6 = this.state;
                            --state6._fsp;
                            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_binOpExpression525);
                            isReverseOrder = true;
                            break;
                        }
                    }
                    val = Long.parseLong(value.getText());
                    break;
                }
            }
            final ExpressionTree.LeafNode node = new ExpressionTree.LeafNode();
            node.keyName = key.getText();
            node.value = val;
            node.operator = op;
            node.isReverseOrder = isReverseOrder;
            this.tree.addLeafNode(node);
        }
        catch (RecognitionException e) {
            throw e;
        }
    }
    
    public final ExpressionTree.Operator operator() throws RecognitionException {
        ExpressionTree.Operator op = null;
        Token t = null;
        try {
            t = this.input.LT(1);
            if ((this.input.LA(1) < 8 || this.input.LA(1) > 10) && this.input.LA(1) != 15 && (this.input.LA(1) < 18 || this.input.LA(1) > 19) && this.input.LA(1) != 22) {
                final MismatchedSetException mse = new MismatchedSetException(null, this.input);
                throw mse;
            }
            this.input.consume();
            this.state.errorRecovery = false;
            op = ExpressionTree.Operator.fromString(t.getText().toUpperCase());
        }
        catch (RecognitionException e) {
            throw e;
        }
        return op;
    }
    
    public final void betweenExpression() throws RecognitionException {
        Token key = null;
        Token left = null;
        Token right = null;
        Object leftV = null;
        Object rightV = null;
        boolean isPositive = true;
        try {
            key = (Token)this.match(this.input, 11, FilterParser.FOLLOW_Identifier_in_betweenExpression638);
            int alt9 = 2;
            switch (this.input.LA(1)) {
                case 16: {
                    alt9 = 1;
                    break;
                }
            }
            switch (alt9) {
                case 1: {
                    this.match(this.input, 16, FilterParser.FOLLOW_KW_NOT_in_betweenExpression641);
                    isPositive = false;
                    break;
                }
            }
            this.match(this.input, 4, FilterParser.FOLLOW_BETWEEN_in_betweenExpression648);
            int alt10 = 3;
            switch (this.input.LA(1)) {
                case 5: {
                    alt10 = 1;
                    break;
                }
                case 24: {
                    alt10 = 2;
                    break;
                }
                case 12: {
                    alt10 = 3;
                    break;
                }
                default: {
                    final NoViableAltException nvae = new NoViableAltException("", 10, 0, this.input);
                    throw nvae;
                }
            }
            switch (alt10) {
                case 1: {
                    left = (Token)this.match(this.input, 5, FilterParser.FOLLOW_DateLiteral_in_betweenExpression673);
                    this.match(this.input, 13, FilterParser.FOLLOW_KW_AND_in_betweenExpression675);
                    right = (Token)this.match(this.input, 5, FilterParser.FOLLOW_DateLiteral_in_betweenExpression681);
                    leftV = FilterLexer.ExtractDate(left.getText());
                    rightV = FilterLexer.ExtractDate(right.getText());
                    break;
                }
                case 2: {
                    left = (Token)this.match(this.input, 24, FilterParser.FOLLOW_StringLiteral_in_betweenExpression711);
                    this.match(this.input, 13, FilterParser.FOLLOW_KW_AND_in_betweenExpression713);
                    right = (Token)this.match(this.input, 24, FilterParser.FOLLOW_StringLiteral_in_betweenExpression719);
                    leftV = TrimQuotes(left.getText());
                    rightV = TrimQuotes(right.getText());
                    break;
                }
                case 3: {
                    left = (Token)this.match(this.input, 12, FilterParser.FOLLOW_IntegralLiteral_in_betweenExpression749);
                    this.match(this.input, 13, FilterParser.FOLLOW_KW_AND_in_betweenExpression751);
                    right = (Token)this.match(this.input, 12, FilterParser.FOLLOW_IntegralLiteral_in_betweenExpression757);
                    leftV = Long.parseLong(left.getText());
                    rightV = Long.parseLong(right.getText());
                    break;
                }
            }
            final ExpressionTree.LeafNode leftNode = new ExpressionTree.LeafNode();
            final ExpressionTree.LeafNode rightNode = new ExpressionTree.LeafNode();
            final ExpressionTree.LeafNode leafNode = leftNode;
            final ExpressionTree.LeafNode leafNode2 = rightNode;
            final String text = key.getText();
            leafNode2.keyName = text;
            leafNode.keyName = text;
            leftNode.value = leftV;
            rightNode.value = rightV;
            leftNode.operator = (isPositive ? ExpressionTree.Operator.GREATERTHANOREQUALTO : ExpressionTree.Operator.LESSTHAN);
            rightNode.operator = (isPositive ? ExpressionTree.Operator.LESSTHANOREQUALTO : ExpressionTree.Operator.GREATERTHAN);
            this.tree.addLeafNode(leftNode);
            this.tree.addLeafNode(rightNode);
            this.tree.addIntermediateNode(isPositive ? ExpressionTree.LogicalOperator.AND : ExpressionTree.LogicalOperator.OR);
        }
        catch (RecognitionException e) {
            throw e;
        }
    }
    
    static {
        tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>", "<UP>", "BETWEEN", "DateLiteral", "DateString", "Digit", "EQUAL", "GREATERTHAN", "GREATERTHANOREQUALTO", "Identifier", "IntegralLiteral", "KW_AND", "KW_DATE", "KW_LIKE", "KW_NOT", "KW_OR", "LESSTHAN", "LESSTHANOREQUALTO", "LPAREN", "Letter", "NOTEQUAL", "RPAREN", "StringLiteral", "WS" };
        FOLLOW_orExpression_in_filter84 = new BitSet(new long[] { 2L });
        FOLLOW_andExpression_in_orExpression106 = new BitSet(new long[] { 131074L });
        FOLLOW_KW_OR_in_orExpression109 = new BitSet(new long[] { 17831968L });
        FOLLOW_andExpression_in_orExpression111 = new BitSet(new long[] { 131074L });
        FOLLOW_expression_in_andExpression137 = new BitSet(new long[] { 8194L });
        FOLLOW_KW_AND_in_andExpression140 = new BitSet(new long[] { 17831968L });
        FOLLOW_expression_in_andExpression142 = new BitSet(new long[] { 8194L });
        FOLLOW_LPAREN_in_expression169 = new BitSet(new long[] { 17831968L });
        FOLLOW_orExpression_in_expression171 = new BitSet(new long[] { 8388608L });
        FOLLOW_RPAREN_in_expression173 = new BitSet(new long[] { 2L });
        FOLLOW_operatorExpression_in_expression185 = new BitSet(new long[] { 2L });
        FOLLOW_betweenExpression_in_operatorExpression206 = new BitSet(new long[] { 2L });
        FOLLOW_binOpExpression_in_operatorExpression218 = new BitSet(new long[] { 2L });
        FOLLOW_Identifier_in_binOpExpression269 = new BitSet(new long[] { 5015296L });
        FOLLOW_operator_in_binOpExpression275 = new BitSet(new long[] { 32L });
        FOLLOW_DateLiteral_in_binOpExpression282 = new BitSet(new long[] { 2L });
        FOLLOW_DateLiteral_in_binOpExpression310 = new BitSet(new long[] { 5015296L });
        FOLLOW_operator_in_binOpExpression317 = new BitSet(new long[] { 2048L });
        FOLLOW_Identifier_in_binOpExpression323 = new BitSet(new long[] { 2L });
        FOLLOW_Identifier_in_binOpExpression371 = new BitSet(new long[] { 5015296L });
        FOLLOW_operator_in_binOpExpression377 = new BitSet(new long[] { 16777216L });
        FOLLOW_StringLiteral_in_binOpExpression384 = new BitSet(new long[] { 2L });
        FOLLOW_StringLiteral_in_binOpExpression412 = new BitSet(new long[] { 5015296L });
        FOLLOW_operator_in_binOpExpression419 = new BitSet(new long[] { 2048L });
        FOLLOW_Identifier_in_binOpExpression425 = new BitSet(new long[] { 2L });
        FOLLOW_Identifier_in_binOpExpression473 = new BitSet(new long[] { 5015296L });
        FOLLOW_operator_in_binOpExpression479 = new BitSet(new long[] { 4096L });
        FOLLOW_IntegralLiteral_in_binOpExpression485 = new BitSet(new long[] { 2L });
        FOLLOW_IntegralLiteral_in_binOpExpression513 = new BitSet(new long[] { 5015296L });
        FOLLOW_operator_in_binOpExpression519 = new BitSet(new long[] { 2048L });
        FOLLOW_Identifier_in_binOpExpression525 = new BitSet(new long[] { 2L });
        FOLLOW_set_in_operator573 = new BitSet(new long[] { 2L });
        FOLLOW_Identifier_in_betweenExpression638 = new BitSet(new long[] { 65552L });
        FOLLOW_KW_NOT_in_betweenExpression641 = new BitSet(new long[] { 16L });
        FOLLOW_BETWEEN_in_betweenExpression648 = new BitSet(new long[] { 16781344L });
        FOLLOW_DateLiteral_in_betweenExpression673 = new BitSet(new long[] { 8192L });
        FOLLOW_KW_AND_in_betweenExpression675 = new BitSet(new long[] { 32L });
        FOLLOW_DateLiteral_in_betweenExpression681 = new BitSet(new long[] { 2L });
        FOLLOW_StringLiteral_in_betweenExpression711 = new BitSet(new long[] { 8192L });
        FOLLOW_KW_AND_in_betweenExpression713 = new BitSet(new long[] { 16777216L });
        FOLLOW_StringLiteral_in_betweenExpression719 = new BitSet(new long[] { 2L });
        FOLLOW_IntegralLiteral_in_betweenExpression749 = new BitSet(new long[] { 8192L });
        FOLLOW_KW_AND_in_betweenExpression751 = new BitSet(new long[] { 4096L });
        FOLLOW_IntegralLiteral_in_betweenExpression757 = new BitSet(new long[] { 2L });
    }
}
