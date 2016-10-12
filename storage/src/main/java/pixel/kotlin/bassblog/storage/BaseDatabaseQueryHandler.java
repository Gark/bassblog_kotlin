package pixel.kotlin.bassblog.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

class BaseDatabaseQueryHandler implements IDatabaseQueryHandler {

    @Override
    public Cursor query(@NonNull SQLiteDatabase database, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long insert(@NonNull SQLiteDatabase database, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull SQLiteDatabase database, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull SQLiteDatabase database, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int bulkInsert(@NonNull SQLiteDatabase database, @NonNull ContentValues[] values) {
        return -1; // Use default implementation
    }
}
