package me.xii69.bukkitstaffchat.database.impl.sql.functions;

import java.sql.ResultSet;

@FunctionalInterface
public interface ResultSetFunction<T> {

    T apply(ResultSet resultSet) throws Exception;

}