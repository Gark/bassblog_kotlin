package pixel.kotlin.bassblog.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

/**
 * Incapsulates content provider query handler for a single entity, to be used together with
 * {@link ProviderUriManager}
 */
public interface IDatabaseQueryHandler {

    /**
     * Query the specified database with the specified data
     * @param database - readable database
     */
    Cursor query(@NonNull SQLiteDatabase database, String[] projection,
                 String selection, String[] selectionArgs, String sortOrder);

    /**
     * Insert the specified data into the specified database
     * @param database - writable database
     * @return new entry id, 0 or greater if the value was inserted successfully
     */
    long insert(@NonNull SQLiteDatabase database, ContentValues values);

    /**
     * Update the specified data in the specified database
     * @param database - writable database
     * @return number of updated entries
     */
    int update(@NonNull SQLiteDatabase database, ContentValues values, String selection,
               String[] selectionArgs);

    /**
     * Delete data with the specified criterias in the specified database
     * @param database - writable database
     * @return number of deleted entries
     */
    int delete(@NonNull SQLiteDatabase database, String selection, String[] selectionArgs);

    /**
     * Allows to customize bulk inserting in a specific query handler.
     * @param database - writable database
     * @return number of inserted records or -1 if default {@link android.content.ContentProvider}
     * implementation must be used.
     */
    int bulkInsert(@NonNull SQLiteDatabase database, @NonNull ContentValues[] values);

}
