package pixel.kotlin.bassblog.storage;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

import java.util.Objects;

/**
 * Container for objects that are capable or handling certain uris (uri handlers).
 * You can set the authority and fill it with custom uri handlers for specific paths in this
 * authority.
 */
public class ProviderUriManager<T> {

    private final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final SparseArray<T> mUriHandlers = new SparseArray<>();
    private final String mAuthority;

    /**
     * Creates an instance of uri manager to handle paths with the specified authority
     * @param authority - must not be empty
     */
    public ProviderUriManager(String authority) {
        if (isNullOrEmpty(authority)) {
            throw new IllegalArgumentException("Invalid authority: " + authority);
        }
        mAuthority = authority;
    }

    /**
     * Adds an uri handler for the specified path. The same rules apply as in
     * {@link UriMatcher#addURI(String, String, int)}
     * @param path - path for the specified uri handler, must not be empty.
     * @param uriHandler - uri handler for the path
     */
    public void add(String path, T uriHandler) {
        if (isNullOrEmpty(path)) {
            throw new IllegalArgumentException("Invalid path: " + path);
        }
        Objects.requireNonNull(uriHandler);
        int code = mUriHandlers.size();
        mUriMatcher.addURI(mAuthority, path, code);
        mUriHandlers.append(code, uriHandler);
    }

    /**
     * Returns a handler associated with this uri. If no handler found for the specified uri,
     * throws IllegalArgumentException
     */
    public T getUriHandler(Uri uri) {
        Objects.requireNonNull(uri);
        int code = mUriMatcher.match(uri);
        T uriHandler = mUriHandlers.get(code);
        if (uriHandler == null) {
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        return uriHandler;
    }

    private static boolean isNullOrEmpty(final CharSequence text) {
        return text == null || text.toString().trim().isEmpty();
    }

}
