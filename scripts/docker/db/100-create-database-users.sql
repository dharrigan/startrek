--
-- Create Star Trek users.
--

-- You need to execute this as the postgres superuser
--
-- e.g.,
--
-- psql -h localhost -d postgres -U postgres -f 100-create-database-users.sql
--
DO
$do$
    BEGIN
        IF NOT EXISTS(SELECT FROM pg_catalog.pg_roles WHERE rolname = 'theborg') THEN CREATE USER theborg WITH ENCRYPTED PASSWORD 'resistanceisfutile'; END IF;
    END;
$do$;

--
-- END
--
