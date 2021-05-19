-- Create initial tables.

CREATE TABLE IF NOT EXISTS yms.yard (
          Id serial not null,
          Color varchar(10),
          Warehouse varchar(10),
          constraint PK_Yard primary key(ID)
);