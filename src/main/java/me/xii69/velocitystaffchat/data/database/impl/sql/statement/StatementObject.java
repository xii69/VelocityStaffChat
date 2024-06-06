package me.xii69.velocitystaffchat.data.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementObject {

    void applyTo(PreparedStatement statement, int index) throws SQLException;


}
