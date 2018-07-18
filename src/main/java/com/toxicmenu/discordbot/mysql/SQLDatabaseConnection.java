package com.toxicmenu.discordbot.mysql;

import com.mysql.jdbc.CommunicationsException;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.toxicmenu.discordbot.ToxicBot;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.beans.ConstructorProperties;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class SQLDatabaseConnection {
    public static final EventLoop DEFAULT_LOOP = new EventLoop(12);
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory(){

        @Override
        public void createAsync(Runnable run) {
            this.createAsync(run, SQLDatabaseConnection.DEFAULT_LOOP);
        }

        @Override
        public void createAsync(Runnable run, EventLoop loop) {
            loop.join(run);
        }
    };
    private SQLConfiguration config;
    private boolean connect = false;
    private EventLoop eventLoop = DEFAULT_LOOP;
    private boolean driverSupported;
    private LoopedIterator<AvariableConnection> connections;

    public SQLDatabaseConnection(SQLConfiguration config) {
        this.setConfig(config);
    }

    public void setConfig(SQLConfiguration config) {
        if (!config.isValid()) {
            throw new RuntimeException("SQL Configuration is not valid");
        }
        try {
            Class.forName(config.getDriverClass());
            this.driverSupported = true;
        }
        catch (ClassNotFoundException e) {
            System.err.println("Drivers " + config.getDriverClass() + " not found.");
            this.driverSupported = false;
        }
        this.config = config;
    }

    private void loadConnections() {
        if (this.connections != null) {
            return;
        }
        AvariableConnection[] cons = new AvariableConnection[this.config.getConnectionCount()];
        int i = 0;
        while (i < this.config.getConnectionCount()) {
            cons[i] = this.createNewConnection(i);
            if (i == 0 && cons[i] == null) {
                return;
            }
            ++i;
        }
        this.connections = new LoopedIterator<AvariableConnection>(cons);
        System.out.println("Loaded successfull " + cons.length + " connections!");
    }

    protected Connection createRawNewConnection() throws SQLException {
        DriverManager.setLoginTimeout(5000);
        return DriverManager.getConnection(this.config.createURL(), this.config.getProperties());
    }

    private AvariableConnection createNewConnection(int index) {
        Connection conn = null;
        try {
            conn = this.createRawNewConnection();
            this.connect = true;
        }
        catch (SQLException e) {
            ToxicBot.getTerminal().writeMessage("§cCant create connection to database (" + e.getMessage() + ")");
            this.connect = false;
        }
        return new AvariableConnection(this, index, conn);
    }

    @Deprecated
    public void reconnectBrokenConnections() {
    }

    public void reconnect() {
        this.disconnect();
        this.loadConnections();
    }

    public void disconnect() {
        AvariableConnection[] arravariableConnection = this.connections.getObjects();
        int n = arravariableConnection.length;
        int n2 = 0;
        while (n2 < n) {
            AvariableConnection c = arravariableConnection[n2];
            try {
                if (!c.getConnection().isClosed()) {
                    c.getConnection().close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            ++n2;
        }
        this.connections = null;
    }

    public int getActiveConnections() {
        int count = 0;
        AvariableConnection[] arravariableConnection = this.connections.getObjects();
        int n = arravariableConnection.length;
        int n2 = 0;
        while (n2 < n) {
            AvariableConnection connection = arravariableConnection[n2];
            if (connection.isValid()) {
                ++count;
            }
            ++n2;
        }
        return count;
    }

    @Deprecated
    public AvariableConnection getNextAvariableConnection(int timeout) {
        if (!this.connect) {
            return null;
        }
        long start = System.currentTimeMillis();
        do {
            AvariableConnection current;
            try {
                current = this.connections.next();
                if (!current.tryUse()) continue;
                if (current.getConnection().isClosed()) {
                    current.invalidConnection();
                    System.err.println("Invalid connection " + current);
                    continue;
                }
                if (current.getLastValidCheck() + 120000L > start) {
                    if (!current.getConnection().isValid(500)) {
                        current.invalidConnection();
                        System.err.println("Invalid connection " + current + "(Valid check)");
                        continue;
                    }
                    current.setLastValidCheck(System.currentTimeMillis());
                }
                return current;
            }
            catch (Exception e2) {
                e2.printStackTrace();
                current = null;
                try {
                    Thread.sleep(5L);
                }
                catch (Exception e22) {
                    // empty catch block
                }
            }
        } while (timeout < 0 || System.currentTimeMillis() - start < (long)timeout);
        throw new RuntimeException("Cant get new connection within timeout!");
    }

    public void doActionWithConnection(int timeout, Callback<AvariableConnection> callback) {
        AvariableConnection connection = this.getNextAvariableConnection(timeout);
        Validate.notNull((Object)connection, (String)"Cant get a valid connection!", (Object[])new Object[0]);
        try {
            connection.setLastQuerry(System.currentTimeMillis());
            callback.done(connection, null);
        }
        catch (Exception e) {
            if (e instanceof CommunicationsException || e instanceof MySQLNonTransientConnectionException || e instanceof SQLException && e.toString().contains("Could not retrieve transation read-only status server")) {
                System.err.println("Having mysql disconnect! (Index: " + connection.getConnectionIndex() + ")");
                connection.invalidConnection();
                return;
            }
            System.err.println("Cant applay action to connection " + connection);
            e.printStackTrace();
        }
        connection.setUsed(false);
    }

    public void querySync(@NonNull String sql, ValueFiller filler, Callback<ResultSet> callback, int connectionTimeout) {
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        this.doActionWithConnection(connectionTimeout, (e, unused0) -> {
                    block25 : {
                        Connection connection = e.getConnection();
                        PreparedStatement statement = null;
                        ResultSet result = null;
                        try {
                            try {
                                statement = connection.prepareStatement(sql);
                                statement.closeOnCompletion();
                                if (filler != null) {
                                    filler.applay(statement);
                                }
                                result = statement.executeQuery();
                                callback.done(result, null);
                                result.close();
                                result = null;
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                                if (statement != null) {
                                    try {
                                        if (!statement.isClosed() || !statement.isCloseOnCompletion()) {
                                            statement.close();
                                        }
                                    }
                                    catch (Exception exception) {
                                        // empty catch block
                                    }
                                }
                                if (result == null) break block25;
                                try {
                                    if (!result.isClosed()) {
                                        result.close();
                                    }
                                }
                                catch (Exception exception) {}
                            }
                        }
                        finally {
                            if (statement != null) {
                                try {
                                    if (!statement.isClosed() || !statement.isCloseOnCompletion()) {
                                        statement.close();
                                    }
                                }
                                catch (Exception exception) {}
                            }
                            if (result != null) {
                                try {
                                    if (!result.isClosed()) {
                                        result.close();
                                    }
                                }
                                catch (Exception exception) {}
                            }
                        }
                    }
                }
        );
    }

    public ResultSet query(String qry) {
        final ResultSet[] rs = {null};

        doActionWithConnection(getActiveConnections(), (e, unused0) -> {
                    Statement st = null;
                    ResultSet rs1 = null;
                    try {
                        st = e.getConnection().createStatement();
                        rs1 = st.executeQuery(qry);
                        rs[0] = rs1;
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
        );
        return rs[0];
    }

    public void update(String qry) {
        this.doActionWithConnection(getActiveConnections(), (e, unused0) -> {
                    Statement st = null;
                    try {
                        st = e.getConnection().createStatement();
                        st.executeUpdate(qry);
                        st.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
        );
    }

    public void commandSync(@NonNull String sql, ValueFiller filler, int connectionTimeout) {
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        this.doActionWithConnection(connectionTimeout, (e, unused0) -> {
                    block15 : {
                        Connection connection = e.getConnection();
                        PreparedStatement statement = null;
                        try {
                            try {
                                statement = connection.prepareStatement(sql);
                                statement.closeOnCompletion();
                                if (filler != null) {
                                    filler.applay(statement);
                                }
                                statement.executeUpdate();
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                                if (statement == null) break block15;
                                try {
                                    if (!statement.isClosed() || !statement.isCloseOnCompletion()) {
                                        statement.close();
                                    }
                                }
                                catch (Exception exception) {}
                            }
                        }
                        finally {
                            if (statement != null) {
                                try {
                                    if (!statement.isClosed() || !statement.isCloseOnCompletion()) {
                                        statement.close();
                                    }
                                }
                                catch (Exception exception) {}
                            }
                        }
                    }
                }
        );
    }

    public void querySync(@NonNull String sql, ValueFiller filler, NEXCallback<ResultSet> callback) {
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        this.querySync(sql, filler, callback, -1);
    }

    public void querySync(@NonNull String sql, ValueFiller filler, Callback<ResultSet> callback) {
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        this.querySync(sql, filler, callback, -1);
    }

    public /* varargs */ void commandAsync(@NonNull String sql, ValueFiller filler, Callback<Boolean> ... callbacks) {
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        this.commandAsync(sql, filler, -1, callbacks);
    }

    public /* varargs */ void commandAsync(@NonNull String sql, ValueFiller filler, int connectionTimeout, Callback<Boolean> ... callbacks) {
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        THREAD_FACTORY.createAsync(() -> {
                    Exception error = null;
                    try {
                        this.commandSync(sql, filler, connectionTimeout);
                    }
                    catch (Exception e) {
                        error = e;
                    }
                    if (callbacks.length == 0) {
                        if (error != null) {
                            error.printStackTrace();
                        } else {
                            Callback[] arrcallback2 = callbacks;
                            int n2 = arrcallback2.length;
                            int n3 = 0;
                            while (n3 < n2) {
                                Callback c = arrcallback2[n3];
                                c.done(error == null, error);
                                ++n3;
                            }
                        }
                    }
                }
                , this.eventLoop);
    }

    public ArrayList<String[]> querySync(String select) {
        return this.querySync(select, -1);
    }

    public ArrayList<String[]> querySync(String select, int limit) {
        ArrayList<String[]> x = new ArrayList<String[]>();
        this.querySync(select, e -> {
                }
                , (res, ex) -> {
                    try {
                        int spaltenzahl = res.getMetaData().getColumnCount();
                        this.getTableContains(limit == -1 ? Integer.MAX_VALUE : limit, spaltenzahl, res, x);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                , -1);
        return x;
    }

    public /* varargs */ void query(final String select, final int limit, final Callback<ArrayList<String[]>> ... call) {
        THREAD_FACTORY.createAsync(new Runnable(){

            @Override
            public void run() {
                ArrayList<String[]> out = SQLDatabaseConnection.this.querySync(select, limit);
                Callback[] arrcallback = call;
                int n = arrcallback.length;
                int n2 = 0;
                while (n2 < n) {
                    Callback c = arrcallback[n2];
                    c.done(out, null);
                    ++n2;
                }
            }
        }, this.eventLoop);
    }

    private void getTableContains(int zeilenAnzahl, int spalten, ResultSet result, ArrayList<String[]> x) {
        try {
            int c = 0;
            while (result.next()) {
                if (c >= zeilenAnzahl) break;
                String[] temp = new String[spalten];
                int k = 1;
                while (k <= spalten) {
                    String y = result.getMetaData().getColumnName(k);
                    try {
                        String name;
                        temp[k - 1] = name = result.getString(y);
                    }
                    catch (SQLException e) {
                        temp[k - 1] = null;
                    }
                    ++k;
                }
                x.add(temp);
                ++c;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commandSync(String command) {
        this.commandSync(command, e -> {
                }
                , -1);
    }

    public /* varargs */ void commandSync(String ... commands) {
        String[] arrstring = commands;
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String s = arrstring[n2];
            this.commandSync(s);
            ++n2;
        }
    }

    public /* varargs */ void command(final String command, final Callback<Boolean> ... call) {
        THREAD_FACTORY.createAsync(new Runnable(){

            @Override
            public void run() {
                RuntimeException ex = null;
                try {
                    SQLDatabaseConnection.this.commandSync(command);
                }
                catch (RuntimeException e) {
                    ex = e;
                }
                Callback[] arrcallback = call;
                int n = arrcallback.length;
                int n2 = 0;
                while (n2 < n) {
                    Callback c = arrcallback[n2];
                    c.done(ex == null, ex == null ? null : ex.getCause());
                    ++n2;
                }
                if (call.length == 0 && ex != null) {
                    System.out.println(String.valueOf(ex.getMessage()) + "-" + ex.getCause().getMessage());
                }
            }
        }, this.eventLoop);
    }

    public boolean isConnected() {
        return this.connect;
    }

    public boolean connect() {
        if (!this.driverSupported) {
            return false;
        }
        this.loadConnections();
        return this.connect;
    }

    public List<String> getTables() {
        ArrayList<String> tables = new ArrayList<String>();
        this.doActionWithConnection(-1, (e, ex) -> {
                    try {
                        DatabaseMetaData md = e.getConnection().getMetaData();
                        ResultSet rs = md.getTables(null, null, "%", null);
                        while (rs.next()) {
                            tables.add(rs.getString(3));
                        }
                    }
                    catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                }
        );
        return tables;
    }

    public static void main(String[] args) {
        EventLoop loop = new EventLoop(2);
    }

    public SQLConfiguration getConfig() {
        return this.config;
    }

    public EventLoop getEventLoop() {
        return this.eventLoop;
    }

    public void setEventLoop(EventLoop eventLoop) {
        this.eventLoop = eventLoop;
    }

    public boolean isDriverSupported() {
        return this.driverSupported;
    }

    public LoopedIterator<AvariableConnection> getConnections() {
        return this.connections;
    }

    public static final class AvariableConnection {
        private final SQLDatabaseConnection owner;
        private final int connectionIndex;
        private Connection connection;
        private long lastValidCheck = -1L;
        private boolean used;
        private boolean valid = true;
        private Object lock = new Object();
        private long lastQuerry = -1L;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean tryUse() {
            Object object = this.lock;
            synchronized (object) {
                block4 : {
                    if (!this.used && this.valid) break block4;
                    return false;
                }
                this.used = true;
                return true;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean isUsed() {
            Object object = this.lock;
            synchronized (object) {
                return this.used;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void setUsed(boolean flag) {
            Object object = this.lock;
            synchronized (object) {
                this.used = flag;
            }
        }

        public void invalidConnection() {
            this.valid = false;
            this.used = false;
            this.lastValidCheck = -1L;
        }

        public AvariableConnection(SQLDatabaseConnection owner, int index, Connection connection) {
            this.owner = owner;
            this.connection = connection;
            this.connectionIndex = index;
        }

        public int getConnectionIndex() {
            return this.connectionIndex;
        }

        public Connection getConnection() {
            return this.connection;
        }

        public long getLastValidCheck() {
            return this.lastValidCheck;
        }

        public void setLastValidCheck(long lastValidCheck) {
            this.lastValidCheck = lastValidCheck;
        }

        public boolean isValid() {
            return this.valid;
        }

        public void setLastQuerry(long lastQuerry) {
            this.lastQuerry = lastQuerry;
        }

        public long getLastQuerry() {
            return this.lastQuerry;
        }
    }

    public static interface Callback<T> {
        public void done(T var1, Throwable var2);
    }

    public static class LoopedIterator<T>
            implements Iterator<T> {
        private T[] objects;
        private int index = 0;

        public LoopedIterator(T[] obj) {
            this.objects = obj;
        }

        @Override
        public boolean hasNext() {
            if (this.objects.length != 0) {
                return true;
            }
            return false;
        }

        @Override
        public synchronized T next() {
            if (this.objects.length == 0) {
                return null;
            }
            if (this.index >= this.objects.length) {
                this.index = 0;
            }
            return this.objects[this.index++];
        }

        public T[] getObjects() {
            return this.objects;
        }

        public int getIndex() {
            return this.index;
        }
    }

    @FunctionalInterface
    public static interface NEXCallback<T>
            extends Callback<T> {
        @Override
        default public void done(T obj, Throwable ex) {
            if (ex != null) {
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException)ex;
                }
                throw new RuntimeException(ex);
            }
            try {
                this.done(obj);
            }
            catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                throw new RuntimeException("Cant invoke SQL callback", e);
            }
        }

        public void done(T var1) throws Exception;
    }

    public static abstract class SQLConfiguration {
        public abstract boolean isValid();

        public abstract String createURL();

        public abstract String getDriverClass();

        public abstract int getConnectionCount();

        public Properties getProperties() {
            return new Properties();
        }

        public static class MySQLConfiguration
                extends SQLConfiguration {
            private String host;
            private int port;
            private String database;
            private String user;
            private String password;
            private boolean autoReconnect;
            private int connectins;

            public MySQLConfiguration(String host, int port, String database, String user, String password, boolean autoReconnect, int connectins) {
                this.host = host;
                this.port = port;
                this.database = database;
                this.user = user;
                this.password = password;
                this.autoReconnect = autoReconnect;
                this.connectins = connectins;
            }

            @Override
            public boolean isValid() {
                if (this.host == null || this.host == "") {
                    return false;
                }
                if (this.port == 0) {
                    return false;
                }
                if (this.database == null || this.database == "") {
                    return false;
                }
                if (this.user == null || this.user == "") {
                    return false;
                }
                if (this.password == null || this.password == "") {
                    return false;
                }
                return true;
            }

            @Override
            public String createURL() {
                return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?" + "user=" + this.user + "&" + "password=" + this.password + "&autoReconnect=" + this.autoReconnect;
            }

            @Override
            public String getDriverClass() {
                return "com.mysql.jdbc.Driver";
            }

            @Override
            public int getConnectionCount() {
                return this.connectins;
            }

            public static MySQLConfigurationBuilder builder() {
                return new MySQLConfigurationBuilder();
            }

            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof MySQLConfiguration)) {
                    return false;
                }
                MySQLConfiguration other = (MySQLConfiguration)o;
                if (!other.canEqual(this)) {
                    return false;
                }
                String this$host = this.host;
                String other$host = other.host;
                if (this$host == null ? other$host != null : !this$host.equals(other$host)) {
                    return false;
                }
                if (this.port != other.port) {
                    return false;
                }
                String this$database = this.database;
                String other$database = other.database;
                if (this$database == null ? other$database != null : !this$database.equals(other$database)) {
                    return false;
                }
                String this$user = this.user;
                String other$user = other.user;
                if (this$user == null ? other$user != null : !this$user.equals(other$user)) {
                    return false;
                }
                String this$password = this.password;
                String other$password = other.password;
                if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
                    return false;
                }
                if (this.autoReconnect != other.autoReconnect) {
                    return false;
                }
                if (this.connectins != other.connectins) {
                    return false;
                }
                return true;
            }

            protected boolean canEqual(Object other) {
                return other instanceof MySQLConfiguration;
            }

            public int hashCode() {
                int PRIME = 59;
                int result = 1;
                String $host = this.host;
                result = result * 59 + ($host == null ? 43 : $host.hashCode());
                result = result * 59 + this.port;
                String $database = this.database;
                result = result * 59 + ($database == null ? 43 : $database.hashCode());
                String $user = this.user;
                result = result * 59 + ($user == null ? 43 : $user.hashCode());
                String $password = this.password;
                result = result * 59 + ($password == null ? 43 : $password.hashCode());
                result = result * 59 + (this.autoReconnect ? 79 : 97);
                result = result * 59 + this.connectins;
                return result;
            }

            public String getHost() {
                return this.host;
            }

            public int getPort() {
                return this.port;
            }

            public String getDatabase() {
                return this.database;
            }

            public String getUser() {
                return this.user;
            }

            public String getPassword() {
                return this.password;
            }

            public boolean isAutoReconnect() {
                return this.autoReconnect;
            }

            public int getConnectins() {
                return this.connectins;
            }

            public static class MySQLConfigurationBuilder {
                private String host;
                private int port;
                private String database;
                private String user;
                private String password;
                private boolean autoReconnect;
                private int connectins;

                MySQLConfigurationBuilder() {
                }

                public MySQLConfigurationBuilder host(String host) {
                    this.host = host;
                    return this;
                }

                public MySQLConfigurationBuilder port(int port) {
                    this.port = port;
                    return this;
                }

                public MySQLConfigurationBuilder database(String database) {
                    this.database = database;
                    return this;
                }

                public MySQLConfigurationBuilder user(String user) {
                    this.user = user;
                    return this;
                }

                public MySQLConfigurationBuilder password(String password) {
                    this.password = password;
                    return this;
                }

                public MySQLConfigurationBuilder autoReconnect(boolean autoReconnect) {
                    this.autoReconnect = autoReconnect;
                    return this;
                }

                public MySQLConfigurationBuilder connectins(int connectins) {
                    this.connectins = connectins;
                    return this;
                }

                public MySQLConfiguration build() {
                    return new MySQLConfiguration(this.host, this.port, this.database, this.user, this.password, this.autoReconnect, this.connectins);
                }

                public String toString() {
                    return "SQLDatabaseConnection.SQLConfiguration.MySQLConfiguration.MySQLConfigurationBuilder(host=" + this.host + ", port=" + this.port + ", database=" + this.database + ", user=" + this.user + ", password=" + this.password + ", autoReconnect=" + this.autoReconnect + ", connectins=" + this.connectins + ")";
                }
            }

        }

        public static class SQLLiteConfiguration
                extends SQLConfiguration {
            private String file;

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public String createURL() {
                return "jdbc:sqlite:" + this.file;
            }

            @Override
            public String getDriverClass() {
                return "org.sqlite.JDBC";
            }

            @Override
            public int getConnectionCount() {
                return 1;
            }

            @ConstructorProperties(value={"file"})
            public SQLLiteConfiguration(String file) {
                this.file = file;
            }
        }

    }

    public static interface ThreadFactory {
        public void createAsync(Runnable var1);

        public void createAsync(Runnable var1, EventLoop var2);
    }

    public static interface ValueFiller {
        public void applay(PreparedStatement var1) throws Exception;
    }
}