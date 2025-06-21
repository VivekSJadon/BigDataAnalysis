from flask import Flask, render_template, jsonify, request
import mysql.connector
import pandas as pd

app = Flask(__name__)

# MySQL database configuration
db_config = {
    'user': 'root',
    'password': 'password',
    'host': 'mysql',
    'database': 'mydatabase',
}

# Map continents to their respective tables
continent_table_map = {
    'Asia': 'radiations_AS',
    'North America': 'radiations_NA',
    'Europe': 'radiations_EU',
    'all': 'radiations'  
}

continent_code_map = {
    'Asia': 'AS',
    'Europe': 'EU',
    'North America': 'NA',
    'all': 'AS'  # Assuming 'all' is a special case to select from all tables
}


def fetch_data(offset=0, limit=50, continent='all'):
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)

    table_name = continent_table_map.get(continent, 'radiations')

    # Fetch data query
    query = f"SELECT DISTINCT captured_time, latitude, longitude, continent, value FROM {table_name} ORDER BY captured_time LIMIT %s OFFSET %s"
    cursor.execute(query, (limit, offset))

    result = cursor.fetchall()

    cursor.close()
    conn.close()

    return {
        'data': result
    }


@app.route('/')
def index():
    return render_template('index.html')

@app.route('/data')
def data():
    offset = int(request.args.get('offset', 0))
    limit = int(request.args.get('limit',50))
    continent = request.args.get('continent', 'all')
    try:
        data = fetch_data(offset=offset, limit=limit, continent=continent)
        return jsonify(data)
    except Exception as e:
        print(f"Error fetching data: {e}")
        return jsonify({"error": "Failed to fetch data"}), 500
    


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')


