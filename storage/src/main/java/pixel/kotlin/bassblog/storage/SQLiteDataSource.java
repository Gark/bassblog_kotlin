package pixel.kotlin.bassblog.storage;

import org.sqldroid.SQLDroidDriver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Fork of {@link org.sqldroid.DroidDataSource} which creates connection based on real database path
 * rather than database name using hardcoded incorrect path afterwards.
 */
public class SQLiteDataSource implements DataSource {

    private final String mDatabasePath;

    public SQLiteDataSource(String databasePath) {
        mDatabasePath = databasePath;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String url = SQLDroidDriver.sqldroidPrefix + mDatabasePath;
        return new SQLDroidDriver().connect(url , new Properties());
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        PrintWriter logWriter = null;
        try {
            logWriter = new PrintWriter("droid.log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return logWriter;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        try {
            DriverManager.setLogWriter(new PrintWriter("droid.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("isWrapperfor");
    }

    @Override
    public  <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("unwrap");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

}
