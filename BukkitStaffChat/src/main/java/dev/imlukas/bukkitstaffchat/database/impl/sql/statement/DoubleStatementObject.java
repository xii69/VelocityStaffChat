package dev.imlukas.bukkitstaffchat.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoubleStatementObject implements StatementObject {

    private final double doubleValue;

    public DoubleStatementObject(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public static DoubleStatementObject create(double doubleValue) {
        return new DoubleStatementObject(doubleValue);
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        if (doubleValue == 0) {
            statement.setNull(index, java.sql.Types.INTEGER);
            return;
        }

        statement.setDouble(index, doubleValue);
    }
}
