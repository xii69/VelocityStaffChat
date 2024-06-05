package dev.imlukas.bukkitstaffchat.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class StringStatementObject implements StatementObject {

    private final String value;

    public StringStatementObject(String value) {
        this.value = value;
    }

    public static StringStatementObject create(UUID value) {
        return new StringStatementObject(value.toString());
    }

    public static StringStatementObject create(String value) {
        return new StringStatementObject(value);
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        statement.setString(index, value);
    }
}