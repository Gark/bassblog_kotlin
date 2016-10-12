package pixel.kotlin.bassblog.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.android.ContextHolder;
import org.flywaydb.core.api.callback.BaseFlywayCallback;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Custom {@link SQLiteOpenHelper} that handles the migration using Flyway.
 */
public class FlywaySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = FlywaySQLiteOpenHelper.class.getSimpleName();
    private static final boolean LOG = BuildConfig.DEBUG;

    /**
     * For SQLiteOpenHelper we will use always the same version.
     */
    private static final int DUMMY_VERSION = 1;

    private final Context mContext;
    private final String mName;

    @Nullable
    private MigrationCallback mMigrationCallback;

    private boolean mIsMigrationDone;

    public FlywaySQLiteOpenHelper(@NonNull final Context context, @Nullable final String name) {
        super(context, name, null, DUMMY_VERSION);

        mContext = context;
        mName = name;

        // Set context reference to Flyway.
        ContextHolder.setContext(context);
    }

    public void setInitSqls(String... initSqls) {
        mMigrationCallback = new MigrationCallback(initSqls);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        synchronized (this) {
            checkMigration();
            return super.getWritableDatabase();
        }
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        synchronized (this) {
            checkMigration();
            return super.getReadableDatabase();
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // DO NOTHING, Flyway will take care :)
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // DO NOTHING, Flyway will take care, yeah !
    }

    private void checkMigration() {
        if (!mIsMigrationDone) {
            try (SQLiteDatabase db = mContext.openOrCreateDatabase(mName, Context.MODE_PRIVATE, null)) {
                final Flyway migrationHandler = new Flyway();
                if (mMigrationCallback != null) {
                    migrationHandler.setCallbacks(mMigrationCallback);
                }
                SQLiteDataSource dataSource = new SQLiteDataSource(db.getPath());
                migrationHandler.setDataSource(dataSource);
                migrationHandler.migrate();
            } finally {
                mIsMigrationDone = true;
            }
        }
    }

    private static class MigrationCallback extends BaseFlywayCallback {

        private final String[] mInitSqls;

        MigrationCallback(@NonNull String[] initSqls) {
            mInitSqls = initSqls;
        }

        @Override
        public void afterMigrate(Connection connection) {
            try {
                for (String initSql : mInitSqls) {
                    Statement statement = null;
                    try {
                        statement = connection.createStatement();
                        statement.execute(initSql);
                    } finally {
                        JdbcUtils.closeStatement(statement);
                    }
                }
            } catch (SQLException e) {
                if (LOG) Log.e(TAG, "Error while executing init sqls", e);
            }
        }

    }
}


