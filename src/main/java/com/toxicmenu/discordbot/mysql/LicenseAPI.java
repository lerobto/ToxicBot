package com.toxicmenu.discordbot.mysql;

import com.toxicmenu.discordbot.ToxicBot;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LicenseAPI {
    public String id;
    public SQLDatabaseConnection sqlDatabaseConnection;

    public LicenseAPI(String id) {
        this.id = id;
        this.sqlDatabaseConnection = ToxicBot.getSqlDatabaseConnection();
    }

    public boolean userExists() {
        try {
            ResultSet rs = rs = sqlDatabaseConnection.query("SELECT * FROM license WHERE discordid= '" + id + "'");
            if (rs.next()) {
                return rs.getString("discordid") != null;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createLicense(String license, String valid, String create) {
        if (!userExists()) {
            sqlDatabaseConnection.update("INSERT INTO license(discordid, licensekey, status, createdate, lastuse) VALUES ('" + id + "', '" + license + "', '" + valid + "', '" + String.valueOf(create) + "', 'Never used');");
        }
    }

    public String get(String key) {
        ResultSet rs = sqlDatabaseConnection.query("SELECT * FROM license WHERE discordid= '" + id + "'");
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
            sqlDatabaseConnection.update("UPDATE license SET " + key + "= '" + value + "' WHERE discordid= '" + id + "';");
            return true;
        } else {
            return false;
        }
    }
}