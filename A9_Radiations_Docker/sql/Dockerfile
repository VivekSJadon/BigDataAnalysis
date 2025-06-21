# Use the official MySQL image as a base
FROM mysql:latest

# Copy the SQL script to the docker-entrypoint-initdb.d directory
COPY create_tables.sql /docker-entrypoint-initdb.d/

# Ensure the script has the correct permissions
RUN chmod 644 /docker-entrypoint-initdb.d/create_tables.sql