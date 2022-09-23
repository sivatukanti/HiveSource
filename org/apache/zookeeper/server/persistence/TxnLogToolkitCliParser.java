// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

class TxnLogToolkitCliParser
{
    private String txnLogFileName;
    private boolean recoveryMode;
    private boolean verbose;
    private boolean force;
    
    String getTxnLogFileName() {
        return this.txnLogFileName;
    }
    
    boolean isRecoveryMode() {
        return this.recoveryMode;
    }
    
    boolean isVerbose() {
        return this.verbose;
    }
    
    boolean isForce() {
        return this.force;
    }
    
    void parse(final String[] args) throws TxnLogToolkit.TxnLogToolkitParseException {
        if (args == null) {
            throw new TxnLogToolkit.TxnLogToolkitParseException(1, "No arguments given", new Object[0]);
        }
        this.txnLogFileName = null;
        for (final String arg : args) {
            if (arg.startsWith("--")) {
                final String par = arg.substring(2);
                if ("help".equalsIgnoreCase(par)) {
                    printHelpAndExit(0);
                }
                else if ("recover".equalsIgnoreCase(par)) {
                    this.recoveryMode = true;
                }
                else if ("verbose".equalsIgnoreCase(par)) {
                    this.verbose = true;
                }
                else if ("dump".equalsIgnoreCase(par)) {
                    this.recoveryMode = false;
                }
                else {
                    if (!"yes".equalsIgnoreCase(par)) {
                        throw new TxnLogToolkit.TxnLogToolkitParseException(1, "Invalid argument: %s", new Object[] { par });
                    }
                    this.force = true;
                }
            }
            else if (arg.startsWith("-")) {
                final String par = arg.substring(1);
                if ("h".equalsIgnoreCase(par)) {
                    printHelpAndExit(0);
                }
                else if ("r".equalsIgnoreCase(par)) {
                    this.recoveryMode = true;
                }
                else if ("v".equalsIgnoreCase(par)) {
                    this.verbose = true;
                }
                else if ("d".equalsIgnoreCase(par)) {
                    this.recoveryMode = false;
                }
                else {
                    if (!"y".equalsIgnoreCase(par)) {
                        throw new TxnLogToolkit.TxnLogToolkitParseException(1, "Invalid argument: %s", new Object[] { par });
                    }
                    this.force = true;
                }
            }
            else {
                if (this.txnLogFileName != null) {
                    throw new TxnLogToolkit.TxnLogToolkitParseException(1, "Invalid arguments: more than one TXN log file given", new Object[0]);
                }
                this.txnLogFileName = arg;
            }
        }
        if (this.txnLogFileName == null) {
            throw new TxnLogToolkit.TxnLogToolkitParseException(1, "Invalid arguments: TXN log file name missing", new Object[0]);
        }
    }
    
    static void printHelpAndExit(final int exitCode) {
        System.out.println("usage: TxnLogToolkit [-dhrvy] txn_log_file_name\n");
        System.out.println("    -d,--dump      Dump mode. Dump all entries of the log file. (this is the default)");
        System.out.println("    -h,--help      Print help message");
        System.out.println("    -r,--recover   Recovery mode. Re-calculate CRC for broken entries.");
        System.out.println("    -v,--verbose   Be verbose in recovery mode: print all entries, not just fixed ones.");
        System.out.println("    -y,--yes       Non-interactive mode: repair all CRC errors without asking");
        System.exit(exitCode);
    }
}
