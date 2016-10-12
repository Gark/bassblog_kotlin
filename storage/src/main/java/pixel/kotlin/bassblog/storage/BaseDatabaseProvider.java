package pixel.kotlin.bassblog.storage;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a content provider that delegates interaction with database to specific
 * {@link IDatabaseQueryHandler}s.
 */
public abstract class BaseDatabaseProvider extends ContentProvider {

    private static final String TAG = BaseDatabaseProvider.class.getSimpleName();
    private static final boolean LOG = BuildConfig.DEBUG;

    private final ProviderUriManager<IDatabaseQueryHandler> mUriManager;

    private final ThreadLocal<Set<Uri>> mTLBatchNotifications = new ThreadLocal<>();

    private ContentResolver mContentResolver;
    private IDatabaseProvider mDatabaseProvider;

    protected BaseDatabaseProvider(ProviderUriManager<IDatabaseQueryHandler> uriManager) {
        mUriManager = uriManager;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        //noinspection ConstantConditions - context is guaranteed to be not null in onCreate
        mDatabaseProvider = createDatabaseProvider(context.getApplicationContext());
        mContentResolver = context.getContentResolver();

        return true;
    }

    protected abstract IDatabaseProvider createDatabaseProvider(@NonNull Context context);

    protected IDatabaseQueryHandler getHandlerForUri(Uri uri) {
        return mUriManager.getUriHandler(uri);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDatabaseProvider.getReadableDatabase();
        if (db == null) {
            return null;
        }
        IDatabaseQueryHandler handler = getHandlerForUri(uri);
        try {
            final Cursor cursor = handler.query(db, projection, selection, selectionArgs, sortOrder);
            cursor.setNotificationUri(mContentResolver, uri);
            return cursor;
        } catch (IllegalStateException e) {
            // IllegalStateException may be thrown by SQLiteConnectionPool
            // when database is closed while a database operation is being executed
            if (LOG) Log.e(TAG, "Query error", e);
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseProvider.getWritableDatabase();
        if (db == null) {
            return null;
        }
        IDatabaseQueryHandler handler = getHandlerForUri(uri);
        try {
            long id = handler.insert(db, values);
            if (id >= 0) {
                Uri inserted = ContentUris.withAppendedId(uri, id);
                notifyChange(inserted);
                return inserted;
            }
        } catch (IllegalStateException e) {
            // IllegalStateException may be thrown by SQLiteConnectionPool
            // when database is closed while a database operation is being executed
            if (LOG) Log.e(TAG, "Insert error", e);
        }
        return uri;// TODO
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseProvider.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        IDatabaseQueryHandler handler = getHandlerForUri(uri);
        try {
            int deleted = handler.delete(db, selection, selectionArgs);
            if (deleted > 0) {
                notifyChange(uri);
            }
            return deleted;
        } catch (IllegalStateException e) {
            // IllegalStateException may be thrown by SQLiteConnectionPool
            // when database is closed while a database operation is being executed
            if (LOG) Log.e(TAG, "Delete error", e);
            return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseProvider.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        IDatabaseQueryHandler handler = getHandlerForUri(uri);
        try {
            int updated = handler.update(db, values, selection, selectionArgs);
            if (updated > 0) {
                notifyChange(uri);
            }
            return updated;
        } catch (IllegalStateException e) {
            // IllegalStateException may be thrown by SQLiteConnectionPool
            // when database is closed while a database operation is being executed
            if (LOG) Log.e(TAG, "Update error", e);
            return 0;
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mDatabaseProvider.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        IDatabaseQueryHandler handler = getHandlerForUri(uri);
        try {
            int result = handler.bulkInsert(db, values);
            if (result == -1) {
                result = super.bulkInsert(uri, values);
            } else if (result > 0) {
                notifyChange(uri);
            }
            return result;
        } catch (IllegalStateException e) {
            // IllegalStateException may be thrown by SQLiteConnectionPool
            // when database is closed while a database operation is being executed
            if (LOG) Log.e(TAG, "Bulk insert error", e);
            return 0;
        }
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        SQLiteDatabase db = mDatabaseProvider.getWritableDatabase();

        if (db == null) {
            return new ContentProviderResult[0];
        }

        final ContentProviderResult[] result;
        final HashSet<Uri> notifications = new HashSet<>();
        setBatchNotificationsSet(notifications);
        db.beginTransaction();
        try {
            result = super.applyBatch(operations);
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            setBatchNotificationsSet(null);
        }
        if (notifications.size() > 0) {
            for (Uri uri : notifications) {
                mContentResolver.notifyChange(uri, null);
            }
        }
        return result;
    }

    private void notifyChange(Uri uri) {
        final Set<Uri> batchNotifications = getBatchNotificationsSet();
        if (batchNotifications != null) {
            batchNotifications.add(uri);
        } else {
            mContentResolver.notifyChange(uri, null);
        }
    }

    private Set<Uri> getBatchNotificationsSet() {
        return mTLBatchNotifications.get();
    }

    private void setBatchNotificationsSet(Set<Uri> batchNotifications) {
        mTLBatchNotifications.set(batchNotifications);
    }

}