package com.frubana.operations.logistics.yms.yard.domain.repository;

import com.frubana.operations.logistics.yms.yard.domain.Yard;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Some repository using JDBI
 */
@Component
public class YardRepository {
    
    /** The JDBI instance to request data to the database, it's never null. */
    private final Jdbi dbi;

    /** Base constructor of the repository.
     *
     * @param jdbi the JDBI instance to use in the queries.
     */
    @Autowired
    public YardRepository(Jdbi jdbi) {
        this.dbi = jdbi;
    }

    public Yard register(Yard yard, String warehouse){
        String sql_query="Insert into yard (color, warehouse)"+
                " values(:color, :warehouse)";
        try(Handle handler=dbi.open();
            Update query_string = handler.createUpdate(sql_query)){
            query_string
                    .bind("color",yard.getColor())
                    .bind("warehouse",warehouse);
            int yard_id=query_string
                    .executeAndReturnGeneratedKeys("id")
                    .mapTo(int.class).first();
            return new Yard(yard_id,yard.getColor(),1);
        }




    }


    /** Mapper of the {@link Yard} for the JDBI implementation.
     */
    @Component
    public static class YardMapper implements RowMapper<Yard> {

        /** Override of the map method to set the fields in the SomeObject
         * object when extracted from the repository.
         *
         * @param rs  result set with the fields of the extracted some object.
         * @param ctx the context of the request that extracted the some
         *            object.
         * @return the {@link Yard} instance with the extracted fields.
         * @throws SQLException if the result set throws an error when
         *                      extracting some field.
         */
        @Override
        public Yard map(ResultSet rs, StatementContext ctx)
                throws SQLException {

            Yard yard = new Yard();
            return yard;
        }
    }
}
