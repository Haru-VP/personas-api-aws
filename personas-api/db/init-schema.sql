CREATE USER app_personas WITH PASSWORD '<APP_DB_PASSWORD>';
GRANT app_personas TO postgres_admin;
CREATE SCHEMA personas AUTHORIZATION app_personas;
GRANT CONNECT ON DATABASE personasdb TO app_personas;
ALTER ROLE app_personas SET search_path = personas;
REVOKE app_personas FROM postgres_admin;
