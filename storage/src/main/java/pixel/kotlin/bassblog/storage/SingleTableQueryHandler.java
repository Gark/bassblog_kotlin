package pixel.kotlin.bassblog.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Implementation of {@link IDatabaseQueryHandler} to perform operations on a single table.
 * Allows to customize conflict handling algorithm for inserting
 */
public class SingleTableQueryHandler extends BaseDatabaseQueryHandler {

    private final String mTableName;
    private final int mConflictAlgorithm;

    /**
     * Creates a query handler for the specified table and with the specified conflict resolution.
     * Do not use this constructor directly: the correctness of conflict algorithm parameter is
     * ensured by static factory methods.
     *
     * @param tableName         - must not be empty
     * @param conflictAlgorithm - one of the values in {@link SQLiteDatabase},
     *                          like {@link SQLiteDatabase#CONFLICT_REPLACE}.
     */
    protected SingleTableQueryHandler(String tableName, int conflictAlgorithm) {
        mTableName = tableName;
        mConflictAlgorithm = conflictAlgorithm;
    }

    @Override
    public Cursor query(@NonNull SQLiteDatabase database, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return database.query(mTableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public long insert(@NonNull SQLiteDatabase database, ContentValues values) {
        return database.insertWithOnConflict(mTableName, null, values, mConflictAlgorithm);
    }

    @Override
    public int update(@NonNull SQLiteDatabase database, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(mTableName, values, selection, selectionArgs);
    }

    @Override
    public int delete(@NonNull SQLiteDatabase database, String selection, String[] selectionArgs) {
        return database.delete(mTableName, selection, selectionArgs);
    }

    /**
     * Creates a query handler for the specified table and without
     * conflict resolution ({@link SQLiteDatabase#CONFLICT_NONE})
     *
     * @param tableName - must not be empty
     */
    public static SingleTableQueryHandler newWithNoConflictResolution(String tableName) {
        return new SingleTableQueryHandler(tableName, SQLiteDatabase.CONFLICT_NONE);
    }

    public static SingleTableQueryHandler newWithConflictIgnoreResolution(String tableName) {
        return new SingleTableQueryHandler(tableName, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Creates a query handler for the specified table and with
     * conflict resolution ({@link SQLiteDatabase#CONFLICT_REPLACE})
     *
     * @param tableName - must not be empty
     */
    public static SingleTableQueryHandler newWithReplaceOnConflict(String tableName) {
        return new SingleTableQueryHandler(tableName, SQLiteDatabase.CONFLICT_REPLACE);
    }

}
