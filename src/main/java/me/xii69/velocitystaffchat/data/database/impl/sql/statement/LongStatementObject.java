package me.xii69.velocitystaffchat.data.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongStatementObject implements StatementObject {

    private final long longNumber;

    public LongStatementObject(long longNumber) {
        this.longNumber = longNumber;
    }

    public static LongStatementObject create(long longNumber) {
        return new LongStatementObject(longNumber);
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        statement.setLong(index, longNumber);
    }
}
