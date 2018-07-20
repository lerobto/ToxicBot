package com.toxicmenu.discordbot.mysql;

import com.toxicmenu.discordbot.ToxicBot;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAPI {
    public String id;
    public SQLDatabaseConnection sqlDatabaseConnection;

    public UserAPI(String id) {
        this.id = id;
        this.sqlDatabaseConnection = ToxicBot.getSqlDatabaseConnection();
    }

    public boolean userExists() {
        try {
            ResultSet rs = rs = sqlDatabaseConnection.query("SELECT * FROM users WHERE discordid= '" + id + "'");
            if (rs.next()) {
                return rs.getString("discordid") != null;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void create() {
        if (!userExists()) {
            sqlDatabaseConnection.update("INSERT INTO users(discordid, muted) VALUES ('" + id + "', 'false');");
        }
    }

    public String get(String key) {
        ResultSet rs = sqlDatabaseConnection.query("SELECT * FROM users WHERE discordid= '" + id + "'");
        if (userExists()) {
            try {
                if ((rs.next()) && (rs.getString(key) == null)) {}
                return rs.getString(key);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return "Not exist";
        }
        return null;
    }

    public boolean set(String key, String value) {
        if (userExists()) {
            sqlDatabaseConnection.update("UPDATE users SET " + key + "= '" + value + "' WHERE discordid= '" + id + "';");
            return true;
        } else {
            return false;
        }
    }
}