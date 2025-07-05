#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create databases for each microservice
    CREATE DATABASE job;
    CREATE DATABASE company;
    CREATE DATABASE review;
    
    -- Grant privileges to postgres user
    GRANT ALL PRIVILEGES ON DATABASE job TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE company TO postgres;
    GRANT ALL PRIVILEGES ON DATABASE review TO postgres;
    
    -- Connect to each database and create extensions if needed
    \c job;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    
    \c company;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    
    \c review;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL 