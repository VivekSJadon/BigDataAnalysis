# Importing Packages
import pandas as pd
import json
import datetime as dt
from time import sleep
from kafka import KafkaProducer
from kafka.errors import NoBrokersAvailable
from geopy.geocoders import Nominatim
import pycountry_convert as pc

# Get continent code from latitudes and longitudes
def get_continent_code(lat, lon):
    geolocator = Nominatim(user_agent='nikhilphadkee', timeout=10000)
    location = geolocator.reverse(lat + "," + lon)
    address = location.raw['address']
    country = address.get('country_code', '')
    country_continent_code = pc.country_alpha2_to_continent_code(country.upper())
    return country_continent_code

# Initialize Kafka Producer Client
producer = None
while not producer:
    try:
        producer = KafkaProducer(bootstrap_servers=['kafka:9093'], request_timeout_ms=10000)
        print(f'Initialized Kafka producer at {dt.datetime.utcnow()}')
    except NoBrokersAvailable:
        print('No brokers available, retrying in 5 seconds...')
        sleep(10)

# Set a basic message counter and define the file path
counter = 0
file = "data/measurements-out.csv"

for chunk in pd.read_csv(file, encoding='unicode_escape', chunksize=1):
    chunk = chunk[['Captured Time', 'Latitude', 'Longitude', 'Value', 'Uploaded Time']]
    for i in chunk.index:
        lat, lon = str(chunk['Latitude'][i]), str(chunk['Longitude'][i])
        country_continent_code = get_continent_code(lat, lon)
        cap_time = chunk['Captured Time'][i]
        value = str(chunk['Value'][i])
        chunk["Captured Time"] = pd.to_datetime(chunk["Captured Time"])
        key = str(counter).encode()
        chunkd = {"captured_time": cap_time, "latitude": lat, "longitude": lon, "value": value, "continent": country_continent_code}
        data = json.dumps(chunkd, default=str).encode('utf-8')
        producer.send(topic=f"testing-{country_continent_code}", key=key, value=data)
        counter += 1
        sleep(0.5)
        print(f'Sent record to topic testing_{country_continent_code} at time {dt.datetime.utcnow()}')
