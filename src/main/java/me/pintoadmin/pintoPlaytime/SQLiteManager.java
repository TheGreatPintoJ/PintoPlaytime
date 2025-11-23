package me.pintoadmin.pintoPlaytime;

import java.sql.*;

public class SQLiteManager {
    private final PintoPlaytime plugin;
    
    public SQLiteManager(PintoPlaytime plugin) {
        this.plugin = plugin;
    }
    Connection connection;

    public void init() {
        connection = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish a connection to the database (creates a new file if it doesn't exist)
            String url = "jdbc:sqlite:%s/playtimes.db".formatted(plugin.getDataFolder().getAbsolutePath());
            connection = DriverManager.getConnection(url);

            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS playtimes (uuid TEXT PRIMARY KEY, playtime INTEGER DEFAULT 0, timesjoined INTEGER DEFAULT 0);");
            ps.execute();

            Statement statement = connection.createStatement();
            boolean columnExists = false;
            ResultSet rs = statement.executeQuery("PRAGMA table_info(playtimes);");
            while (rs.next()) {
                if (rs.getString("name").equalsIgnoreCase("timesjoined")) {
                    columnExists = true;
                    break;
                }
            }
            rs.close();

            if(!columnExists) {
                PreparedStatement tablePS = connection.prepareStatement("ALTER TABLE playtimes ADD COLUMN timesjoined INTEGER DEFAULT 0;");
                tablePS.execute();
            }

            plugin.getLogger().info("Connection to SQLite established.");

        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            plugin.getLogger().severe("Error connecting to SQLite: " + e.getMessage());
        }
    }
    public void deinit(){
        if(connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing SQLite connection: " + e.getMessage());
        }
    }
    public Connection getConnection(){
        if (connection == null) {
            init();
        }
        return connection;
    }
}
