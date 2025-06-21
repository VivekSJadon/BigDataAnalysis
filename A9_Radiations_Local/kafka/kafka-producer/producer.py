# Importing Packages
import pandas as pd
import json
import datetime as dt
from time import sleep
from kafka import KafkaProducer
from geopy.geocoders import Nominatim
import pycountry_convert as pc

# Get continent code from latitudes and longitudes
def get_continent_code(lat, lon):
    geolocator = Nominatim(user_agent= 'nikhilphadkee', timeout= 10000)
    location = geolocator.reverse(lat+","+lon)

    address = location.raw['address']
    country = address.get('country_code', '')

    country_continent_code = pc.country_alpha2_to_continent_code(country.upper())
    return country_continent_code

# Initialize Kafka Producer Client
producer = KafkaProducer(bootstrap_servers=['localhost:9092'])
print(f'Initialized Kafka producer at {dt.datetime.utcnow()}')

# Set a basic message counter and define the file path
counter = 0
file = "kafka\measurements\measurements-out.csv"

for chunk in pd.read_csv(file,encoding='unicode_escape',chunksize=1):
    chunk = chunk[['Captured Time', 'Latitude', 'Longitude', 'Value', 'Uploaded Time']]
    for i in chunk.index:
        lat, lon = str(chunk['Latitude'][i]), str(chunk['Longitude'][i])
        country_continent_code = get_continent_code(lat, lon)
        cap_time = chunk['Captured Time'][i]
        value = str(chunk['Value'][i])
        
    # For each chunk, convert the invoice date into the correct time format
    chunk["Captured Time"] = pd.to_datetime(chunk["Captured Time"])

    # Set the counter as the message key
    key = str(counter).encode()

    chunkd = {"captured_time": cap_time, "latitude": lat, "longitude": lon, "value": value, "continent": country_continent_code}

    # Encode the dictionary into a JSON Byte Array
    data = json.dumps(chunkd, default=str).encode('utf-8')

    # Send the data to Kafka
    producer.send(topic=f"testing-{country_continent_code}", key=key, value=data)

    # Sleep to simulate a real-world interval
    #sleep(0.5)
      
    # Increment the message counter for the message key
    counter = counter + 1
    print(f'Sent record to topic testing-{country_continent_code} at time {dt.datetime.utcnow()}')