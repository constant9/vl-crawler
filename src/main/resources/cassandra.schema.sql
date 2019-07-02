CREATE KEYSPACE vl
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
AND durable_writes = true;

CREATE TABLE vl.slice (
    url text,
    slice_id int,
    content text,
    PRIMARY KEY (url, slice_id)
) WITH CLUSTERING ORDER BY (slice_id ASC);
