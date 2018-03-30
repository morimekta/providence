CREATE TABLE default_mappings (
    id INT NOT NULL PRIMARY KEY,
    present BIT NULL,
    tiny TINYINT NULL,
    small SMALLINT NULL,
    medium INT NULL,
    large BIGINT NULL,
    `real` DOUBLE NULL,
    name VARCHAR(128) NULL,
    data BINARY(128) NULL,
    fib INT NULL,
    compact VARCHAR(128) NULL,

    timestamp_s TIMESTAMP NULL,
    timestamp_ms TIMESTAMP NULL,
    binary_message VARBINARY(128) NULL,
    blob_message BLOB NULL,
    other_message CLOB NULL,
    blob_data BLOB NULL,
    base64_data VARCHAR(255) NULL
);