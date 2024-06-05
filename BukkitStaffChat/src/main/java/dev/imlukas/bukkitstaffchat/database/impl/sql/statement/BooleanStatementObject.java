package dev.imlukas.bukkitstaffchat.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooleanStatementObject implements StatementObject {

    private final boolean booleanValue;

    public BooleanStatementObject(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public static BooleanStatementObject create(boolean booleanValue) {
        return new BooleanStatementObject(booleanValue);
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        statement.setBoolean(index, booleanValue);
    }
}
