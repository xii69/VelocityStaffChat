package dev.imlukas.bukkitstaffchat.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerStatementObject implements StatementObject {

    private final int integer;

    public IntegerStatementObject(int integer) {
        this.integer = integer;
    }

    public static IntegerStatementObject create(int integer) {
        return new IntegerStatementObject(integer);
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        if (integer == 0) {
            statement.setNull(index, java.sql.Types.INTEGER);
            return;
        }

        statement.setInt(index, integer);
    }
}
