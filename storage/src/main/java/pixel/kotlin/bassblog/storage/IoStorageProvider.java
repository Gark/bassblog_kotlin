package pixel.kotlin.bassblog.storage;

import android.content.Context;
import android.support.annotation.NonNull;


/**
 * Implements iO4Teams storage based on {@link IoContract}
 */
public class IoStorageProvider extends BaseDatabaseProvider {

    private static final String DB_NAME = "bassblog-storage.sqlite";

    public IoStorageProvider() {
        super(createUriManager());
    }

    private static ProviderUriManager<IDatabaseQueryHandler> createUriManager() {
        ProviderUriManager<IDatabaseQueryHandler> uriManager = new ProviderUriManager<>(IoContract.AUTHORITY);
        uriManager.add(IoContract.Post.PATH, SingleTableQueryHandler.newWithConflictIgnoreResolution(IoContract.Post.TABLE_NAME));
        return uriManager;
    }

    @Override
    protected IDatabaseProvider createDatabaseProvider(@NonNull Context context) {
        return new AccountDatabaseProvider(context, DB_NAME);
    }


}