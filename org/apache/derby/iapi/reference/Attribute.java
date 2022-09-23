// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.reference;

public interface Attribute
{
    public static final String PROTOCOL = "jdbc:derby:";
    public static final String SQLJ_NESTED = "jdbc:default:connection";
    public static final String DNC_PROTOCOL = "jdbc:derby://";
    public static final String JCC_PROTOCOL = "jdbc:derby:net:";
    public static final String DECRYPT_DATABASE = "decryptDatabase";
    public static final String DATA_ENCRYPTION = "dataEncryption";
    public static final String BOOT_PASSWORD = "bootPassword";
    public static final String NEW_BOOT_PASSWORD = "newBootPassword";
    public static final String REPLICATION_START_MASTER = "startMaster";
    public static final String REPLICATION_STOP_MASTER = "stopMaster";
    public static final String REPLICATION_START_SLAVE = "startSlave";
    public static final String REPLICATION_STOP_SLAVE = "stopSlave";
    public static final String REPLICATION_INTERNAL_SHUTDOWN_SLAVE = "internal_stopslave";
    public static final String REPLICATION_SLAVE_HOST = "slaveHost";
    public static final String REPLICATION_FAILOVER = "failover";
    public static final String REPLICATION_SLAVE_PORT = "slavePort";
    public static final String DBNAME_ATTR = "databaseName";
    public static final String SHUTDOWN_ATTR = "shutdown";
    public static final String DEREGISTER_ATTR = "deregister";
    public static final String CREATE_ATTR = "create";
    public static final String DROP_ATTR = "drop";
    public static final String USERNAME_ATTR = "user";
    public static final String PASSWORD_ATTR = "password";
    public static final String DRDAID_ATTR = "drdaID";
    public static final String UPGRADE_ATTR = "upgrade";
    public static final String LOG_DEVICE = "logDevice";
    public static final String TERRITORY = "territory";
    public static final String CRYPTO_PROVIDER = "encryptionProvider";
    public static final String CRYPTO_ALGORITHM = "encryptionAlgorithm";
    public static final String CRYPTO_KEY_LENGTH = "encryptionKeyLength";
    public static final String CRYPTO_EXTERNAL_KEY = "encryptionKey";
    public static final String NEW_CRYPTO_EXTERNAL_KEY = "newEncryptionKey";
    public static final String CRYPTO_EXTERNAL_KEY_VERIFY_FILE = "verifyKey.dat";
    public static final String CREATE_FROM = "createFrom";
    public static final String RESTORE_FROM = "restoreFrom";
    public static final String ROLL_FORWARD_RECOVERY_FROM = "rollForwardRecoveryFrom";
    public static final String CLIENT_SECURITY_MECHANISM = "securityMechanism";
    public static final String DRDA_SECTKN_IN = "drdaSecTokenIn";
    public static final String DRDA_SECTKN_OUT = "drdaSecTokenOut";
    public static final String DRDA_SECMEC = "drdaSecMec";
    public static final String SOFT_UPGRADE_NO_FEATURE_CHECK = "softUpgradeNoFeatureCheck";
    public static final String COLLATION = "collation";
}
