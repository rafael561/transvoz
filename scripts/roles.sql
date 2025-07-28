-- Create application roles and users
-- This script should be run as superuser

-- Create application role
CREATE ROLE transvoz_app WITH LOGIN PASSWORD 'transvoz123';

-- Grant necessary permissions
GRANT CONNECT ON DATABASE transvoz TO transvoz_app;
GRANT USAGE ON SCHEMA public TO transvoz_app;
GRANT CREATE ON SCHEMA public TO transvoz_app;

-- Grant permissions on tables
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO transvoz_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO transvoz_app;

-- Grant permissions on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO transvoz_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO transvoz_app;

-- Create read-only role for monitoring/reporting
CREATE ROLE transvoz_readonly WITH LOGIN PASSWORD 'readonly123';
GRANT CONNECT ON DATABASE transvoz TO transvoz_readonly;
GRANT USAGE ON SCHEMA public TO transvoz_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO transvoz_readonly;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO transvoz_readonly;
