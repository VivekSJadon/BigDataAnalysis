CREATE DATABASE IF NOT EXISTS mydatabase;

USE mydatabase;

CREATE TABLE IF NOT EXISTS radiations (
    captured_time VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    value VARCHAR(255),
    continent VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS radiations_AS (
    captured_time VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    value VARCHAR(255),
    continent VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS radiations_EU (
    captured_time VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    value VARCHAR(255),
    continent VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS radiations_OC (
    captured_time VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    value VARCHAR(255),
    continent VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS radiations_NA (
    captured_time VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    value VARCHAR(255),
    continent VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS radiations_SA (
    captured_time VARCHAR(255),
    latitude VARCHAR(255),
    longitude VARCHAR(255),
    value VARCHAR(255),
    continent VARCHAR(255)
);

CREATE TABLE radiation_by_continents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    continent VARCHAR(255) NOT NULL,
    total_radiation VARCHAR(255) NOT NULL,
    average_radiation VARCHAR(255) NOT NULL,
    UNIQUE (continent)
);