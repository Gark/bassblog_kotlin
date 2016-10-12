package pixel.kotlin.bassblog.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;


/**
 * Database provider for current {@link }. Handles cases when account is not yet available
 * and removing existing database, after which it will just return null for database until
 * the new account is available.
 */
class AccountDatabaseProvider implements IDatabaseProvider {

    private final Context mContext;
    private final String mDatabaseName;

    @Nullable
    private FlywaySQLiteOpenHelper mSQLiteOpenHelper;

    public AccountDatabaseProvider(Context context, String databaseName) {
        mContext = context;
        mDatabaseName = databaseName;
    }

    @Nullable
    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mSQLiteOpenHelper == null) {
            openDatabaseForAccount();
        }
        if (mSQLiteOpenHelper != null) {
            return mSQLiteOpenHelper.getReadableDatabase();
        }
        return null;
    }

    @Nullable
    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mSQLiteOpenHelper == null) {
            openDatabaseForAccount();
        }
        if (mSQLiteOpenHelper != null) {
            return mSQLiteOpenHelper.getWritableDatabase();
        }
        return null;
    }

    private synchronized void openDatabaseForAccount() {
        mSQLiteOpenHelper = new FlywaySQLiteOpenHelper(mContext, mDatabaseName);
        mSQLiteOpenHelper.setInitSqls();
    }


    /**
     * Removes database, if exists, closing connection to it, if it was established before.
     */
    public synchronized void removeDatabase() {
        if (mSQLiteOpenHelper != null) {
            mSQLiteOpenHelper.close();
            mSQLiteOpenHelper = null;
        }
        mContext.deleteDatabase(mDatabaseName);
    }
}
