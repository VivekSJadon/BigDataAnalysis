# # Use the Maven image with JDK
# FROM maven:3.8.1-jdk-8 AS builder

# # Set the working directory
# WORKDIR /app

# # Copy the pom.xml and source code
# COPY pom.xml /app/pom.xml
# COPY src /app/src

# # Build the Maven project
# RUN mvn clean package

# # Use the official Apache Flink image
# FROM flink:1.17.2-scala_2.12-java8

# # Set the working directory
# WORKDIR /opt/flink/job

# # Copy the built JAR from the Maven image to the Flink image
# COPY --from=builder /app/target/flink-connect-1.0-SNAPSHOT.jar /opt/flink/job/flink-connect-1.0-SNAPSHOT.jar

# # Set Flink classpath
# ENV FLINK_CLASSPATH="/opt/flink/job/flink-connect-1.0-SNAPSHOT.jar"

# Use the official Apache Flink image
FROM flink:1.17.2-scala_2.12-java8

# Set the working directory
WORKDIR /opt/flink/job

# Copy the .jar file from the local 'target' directory into the Docker image
COPY target/flink-connect-1.0-SNAPSHOT.jar /opt/flink/job/flink-connect-1.0-SNAPSHOT.jar

# Add MySQL JDBC driver
RUN wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.28/mysql-connector-java-8.0.28.jar \
    -O /opt/flink/lib/mysql-connector-java-8.0.28.jar

# Set Flink classpath
ENV FLINK_CLASSPATH="/opt/flink/job/flink-connect-1.0-SNAPSHOT.jar:/opt/flink/lib/mysql-connector-java-8.0.28.jar"

