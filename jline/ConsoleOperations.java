// 
// Decompiled by Procyon v0.5.36
// 

package jline;

public interface ConsoleOperations
{
    public static final String CR = System.getProperty("line.separator");
    public static final char BACKSPACE = '\b';
    public static final char RESET_LINE = '\r';
    public static final char KEYBOARD_BELL = '\u0007';
    public static final char CTRL_A = '\u0001';
    public static final char CTRL_B = '\u0002';
    public static final char CTRL_C = '\u0003';
    public static final char CTRL_D = '\u0004';
    public static final char CTRL_E = '\u0005';
    public static final char CTRL_F = '\u0006';
    public static final char CTRL_K = '\u000b';
    public static final char CTRL_L = '\f';
    public static final char CTRL_N = '\u000e';
    public static final char CTRL_P = '\u0010';
    public static final char CTRL_OB = '\u001b';
    public static final char DELETE = '\u007f';
    public static final char CTRL_QM = '\u007f';
    public static final short UNKNOWN = -99;
    public static final short MOVE_TO_BEG = -1;
    public static final short MOVE_TO_END = -3;
    public static final short PREV_CHAR = -4;
    public static final short NEWLINE = -6;
    public static final short KILL_LINE = -7;
    public static final short CLEAR_SCREEN = -8;
    public static final short NEXT_HISTORY = -9;
    public static final short PREV_HISTORY = -11;
    public static final short REDISPLAY = -13;
    public static final short KILL_LINE_PREV = -15;
    public static final short DELETE_PREV_WORD = -16;
    public static final short NEXT_CHAR = -19;
    public static final short REPEAT_PREV_CHAR = -20;
    public static final short SEARCH_PREV = -21;
    public static final short REPEAT_NEXT_CHAR = -24;
    public static final short SEARCH_NEXT = -25;
    public static final short PREV_SPACE_WORD = -27;
    public static final short TO_END_WORD = -29;
    public static final short REPEAT_SEARCH_PREV = -34;
    public static final short PASTE_PREV = -36;
    public static final short REPLACE_MODE = -37;
    public static final short SUBSTITUTE_LINE = -38;
    public static final short TO_PREV_CHAR = -39;
    public static final short NEXT_SPACE_WORD = -40;
    public static final short DELETE_PREV_CHAR = -41;
    public static final short ADD = -42;
    public static final short PREV_WORD = -43;
    public static final short CHANGE_META = -44;
    public static final short DELETE_META = -45;
    public static final short END_WORD = -46;
    public static final short INSERT = -48;
    public static final short REPEAT_SEARCH_NEXT = -49;
    public static final short PASTE_NEXT = -50;
    public static final short REPLACE_CHAR = -51;
    public static final short SUBSTITUTE_CHAR = -52;
    public static final short TO_NEXT_CHAR = -53;
    public static final short UNDO = -54;
    public static final short NEXT_WORD = -55;
    public static final short DELETE_NEXT_CHAR = -56;
    public static final short CHANGE_CASE = -57;
    public static final short COMPLETE = -58;
    public static final short EXIT = -59;
    public static final short PASTE = -60;
    public static final short START_OF_HISTORY = -61;
    public static final short END_OF_HISTORY = -62;
    public static final short CLEAR_LINE = -63;
}
