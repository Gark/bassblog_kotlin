package pixel.kotlin.bassblog.storage;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

/**
 * Database provider interface for {@link BaseDatabaseProvider}
 */
public interface IDatabaseProvider {

    /**
     * @return readable database instance, or <code>null</code> if database is not available
     */
    @Nullable
    SQLiteDatabase getReadableDatabase();

    /**
     * @return writable database instance, or <code>null</code> if database is not available
     */
    @Nullable
    SQLiteDatabase getWritableDatabase();

}
