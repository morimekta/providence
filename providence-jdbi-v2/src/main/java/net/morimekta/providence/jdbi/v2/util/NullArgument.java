package net.morimekta.providence.jdbi.v2.util;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NullArgument implements Argument {
    private final int type;

    public NullArgument(int type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
        statement.setNull(position, type);
    }
}
