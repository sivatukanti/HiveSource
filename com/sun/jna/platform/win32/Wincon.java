// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.IntByReference;

public interface Wincon
{
    public static final int ATTACH_PARENT_PROCESS = -1;
    public static final int CTRL_C_EVENT = 0;
    public static final int CTRL_BREAK_EVENT = 1;
    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;
    public static final int CONSOLE_FULLSCREEN = 1;
    public static final int CONSOLE_FULLSCREEN_HARDWARE = 2;
    public static final int ENABLE_PROCESSED_INPUT = 1;
    public static final int ENABLE_LINE_INPUT = 2;
    public static final int ENABLE_ECHO_INPUT = 4;
    public static final int ENABLE_WINDOW_INPUT = 8;
    public static final int ENABLE_MOUSE_INPUT = 16;
    public static final int ENABLE_INSERT_MODE = 32;
    public static final int ENABLE_QUICK_EDIT_MODE = 64;
    public static final int ENABLE_EXTENDED_FLAGS = 128;
    public static final int ENABLE_PROCESSED_OUTPUT = 1;
    public static final int ENABLE_WRAP_AT_EOL_OUTPUT = 2;
    public static final int MAX_CONSOLE_TITLE_LENGTH = 65536;
    
    boolean AllocConsole();
    
    boolean FreeConsole();
    
    boolean AttachConsole(final int p0);
    
    boolean FlushConsoleInputBuffer(final WinNT.HANDLE p0);
    
    boolean GenerateConsoleCtrlEvent(final int p0, final int p1);
    
    int GetConsoleCP();
    
    boolean SetConsoleCP(final int p0);
    
    int GetConsoleOutputCP();
    
    boolean SetConsoleOutputCP(final int p0);
    
    WinDef.HWND GetConsoleWindow();
    
    boolean GetNumberOfConsoleInputEvents(final WinNT.HANDLE p0, final IntByReference p1);
    
    boolean GetNumberOfConsoleMouseButtons(final IntByReference p0);
    
    WinNT.HANDLE GetStdHandle(final int p0);
    
    boolean SetStdHandle(final int p0, final WinNT.HANDLE p1);
    
    boolean GetConsoleDisplayMode(final IntByReference p0);
    
    boolean GetConsoleMode(final WinNT.HANDLE p0, final IntByReference p1);
    
    boolean SetConsoleMode(final WinNT.HANDLE p0, final int p1);
    
    int GetConsoleTitle(final char[] p0, final int p1);
    
    int GetConsoleOriginalTitle(final char[] p0, final int p1);
    
    boolean SetConsoleTitle(final String p0);
}
