-- Create initial tables.

CREATE TABLE IF NOT EXISTS yms.vehicle_type (
       Id serial not null,
       name varchar(16) not null,
       constraint PK_vehicle_type primary key(ID)
);

ALTER TABLE yms.yard ADD vehicle_type int;

ALTER TABLE yms.yard
ADD CONSTRAINT fk_yard__vehicle_type
FOREIGN KEY (vehicle_type)
REFERENCES yms.vehicle_type (id);
