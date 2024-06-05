package me.xii69.velocitystaffchat.data.database.impl.sql.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class ObjectStatementObject implements StatementObject {

    private final Object object;

    public ObjectStatementObject(Object object) {
        this.object = object;
    }

    @Override
    public void applyTo(PreparedStatement statement, int index) throws SQLException {
        if (object == null) {
            statement.setNull(index, Types.NULL);
            return;
        }

        statement.setObject(index, object);
    }
}