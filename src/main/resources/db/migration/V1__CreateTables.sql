CREATE TABLE test_table (
    id BIGINT NOT NULL auto_increment,
    description VARCHAR(256) NOT NULL,
    created_datetime TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

