--
-- Let's Go Go Go!
--

--
-- You need to execute this as the postgres superuser
--
-- i.e.,
--
-- psql -h localhost -d postgres -U postgres -f 200-drop-create-startrek-db.sql
--
\c postgres;
DROP DATABASE IF EXISTS startrek;
CREATE DATABASE startrek OWNER theborg;
\c startrek;

--
-- startrek permissions
--
GRANT CONNECT ON DATABASE startrek TO theborg;
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
