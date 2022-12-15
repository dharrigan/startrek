--
-- Let's Go Go Go!
--

--
-- You need to execute this as the postgres superuser
--
-- i.e.,
--
-- psql -h localhost -d postgres -U postgres -f 300-drop-create-startrek-test-db.sql
--
\c postgres;
DROP DATABASE IF EXISTS startrek_test;
CREATE DATABASE startrek_test OWNER theborg;
\c startrek_test;

--
-- startrek permissions
--
GRANT CONNECT ON DATABASE startrek_test TO theborg;
CREATE SCHEMA IF NOT EXISTS theborg AUTHORIZATION theborg;

--
-- Remove public permissions for ALL users (good practice)
--
REVOKE ALL ON SCHEMA public FROM PUBLIC;

--
-- All done
--
\c postgres;

--
-- END
--
