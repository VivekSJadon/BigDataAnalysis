// Initialize the map
const map = L.map('map', {
    zoomControl: false // Disable the default zoom control
}).setView([0, 0], 2);

// Set up the OpenStreetMap layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 18,
}).addTo(map);

// Add zoom control with bottom left position
L.control.zoom({
    position: 'bottomleft'
}).addTo(map);

let offset = 0;
const limit = 10;
let continentfilter = 'all';
const plottedPoints = new Set();
let radiationValues = [];

function getColor(radiation) {
    return radiation > 45 ? '#ff0000' :
           radiation > 30  ? '#ff6600' :
           radiation > 20  ? '#ffcc00' :
           radiation > 15  ? '#99ff33' :
                            '#33cc33';
}

function updateDashboard() {
    const maxRadiationElement = document.getElementById('max-radiation').querySelector('p');
    const minRadiationElement = document.getElementById('min-radiation').querySelector('p');
    const avgRadiationElement = document.getElementById('avg-radiation').querySelector('p');
    const totalRadiationElement = document.getElementById('total-radiation').querySelector('p');

    if (radiationValues.length === 0) {
        maxRadiationElement.textContent = 'N/A';
        minRadiationElement.textContent = 'N/A';
        avgRadiationElement.textContent = 'N/A';
        totalRadiationElement.textContent = 'N/A';
        return;
    }

    const maxRadiation = Math.max(...radiationValues);
    const minRadiation = Math.min(...radiationValues);
    const avgRadiation = (radiationValues.reduce((acc, val) => acc + val, 0) / radiationValues.length).toFixed(2);
    const totalRadiation = radiationValues.reduce((acc, val) => acc + val, 0).toFixed(2);

    maxRadiationElement.textContent = maxRadiation;
    minRadiationElement.textContent = minRadiation;
    avgRadiationElement.textContent = avgRadiation;
    totalRadiationElement.textContent = totalRadiation;

    document.getElementById('max-radiation').style.backgroundColor = getColor(maxRadiation);
    document.getElementById('min-radiation').style.backgroundColor = getColor(minRadiation);
    document.getElementById('avg-radiation').style.backgroundColor = getColor(avgRadiation);
    document.getElementById('total-radiation').style.backgroundColor = getColor(totalRadiation);
}


function plotDataPoint(dataPoint) {
    const { captured_time, latitude, longitude, continent } = dataPoint;
    const pointKey = `${captured_time}-${latitude}-${longitude}-${continent}`;



    // Check if the point has already been plotted
    if (plottedPoints.has(pointKey)) return;

    // Add the point to the set of plotted points
    plottedPoints.add(pointKey);

    const radiationValue = parseFloat(dataPoint.value);
    if (isNaN(radiationValue)) {
        console.warn(`Invalid radiation value: ${dataPoint.value}`);
        return;
    }

    // Filter out points not within specified latitude and longitude ranges for Asia filter
    if (continentfilter === 'Asia' && !(latitude > 30 && latitude < 40 && longitude > 130 && longitude < 150)) {
        return;
    }

    if (continentfilter === 'Europe' && !(latitude > 50 && latitude < 60 && longitude > -5 && longitude < 12)) {
        return;
    }

    if (continentfilter === 'North America' && !(latitude > 30 && latitude < 50 && longitude > -122 && longitude < -70)) {
        return;
    }

    radiationValues.push(radiationValue);
    const fillColor = getColor(radiationValue);

    const circleMarker = L.circleMarker([latitude, longitude], {
        radius: 5,
        fillColor: fillColor,
        color: "#000",
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
    }).addTo(map);

    const popupContent = `Radiation: ${radiationValue}<br>Time: ${captured_time}`;
    circleMarker.bindPopup(popupContent);

    if (radiationValues.length % 10 === 0) {
        updateDashboard();
    }


}


function plotDataPointsWithDelay(dataPoints, index = 0, delay = 300) {
    if (index < dataPoints.length) {
        plotDataPoint(dataPoints[index]);
        setTimeout(() => plotDataPointsWithDelay(dataPoints, index + 1, delay), delay);
    } else {
        fetchDataAndPlot();
    }
}

function fetchDataAndPlot() {
    fetch(`/data?offset=${offset}&limit=${limit}&continent=${encodeURIComponent(continentfilter)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.data.length > 0) {
                offset += limit;
                plotDataPointsWithDelay(data.data);

                // Calculate and update total radiation dynamically
                const totalRadiation = radiationValues.reduce((acc, val) => acc + val, 0).toFixed(2);
                const totalRadiationElement = document.getElementById('total-radiation').querySelector('p');
                totalRadiationElement.textContent = totalRadiation;
            } else {
                console.log('No more data to fetch.');
            }
        })
        .catch(error => console.error('Error fetching data:', error));
}


document.getElementById('continent-select').addEventListener('change', (event) => {
    continentfilter = event.target.value;
    offset = 0;
    
    // Clear existing plotted points and data arrays
    plottedPoints.clear();
    radiationValues = [];
    
    // Remove existing markers from the map
    map.eachLayer((layer) => {

        if (layer instanceof L.CircleMarker) {
            map.removeLayer(layer);
        }
    });
    
    // Fetch data and plot points for the selected continent
    fetchDataAndPlot();
});



fetchDataAndPlot();
