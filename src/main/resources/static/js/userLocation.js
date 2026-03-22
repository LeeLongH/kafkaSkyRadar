let locationReady = false;
// Default location
let myLat = 46.056946;
let myLng = 14.505751;
let markers = {};
let visibilityCircle = null;

// Init map
var map = L.map('map').setView([myLat, myLng], 6);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
}).addTo(map);

function createPlaneIcon(rotation) {
    return L.divIcon({
        html: `<img src="/images/plane.png" style="width:40px; transform: rotate(${rotation}deg);">`,
        className: '',
        iconSize: [40, 40],
        iconAnchor: [20, 20]
    });
}

function is_plane_outside_circle(lat, lng) {
    if (!visibilityCircle) {
        console.log("No circle yet");
        return true;
    }

    const R = 6371;
    const circleCenter = visibilityCircle.getLatLng();
    const radiusMeters = visibilityCircle.getRadius();

    const dLat = (lat - circleCenter.lat) * Math.PI / 180;
    const dLng = (lng - circleCenter.lng) * Math.PI / 180;

    const a = Math.sin(dLat / 2) ** 2 +
        Math.cos(circleCenter.lat * Math.PI / 180) *
        Math.cos(lat * Math.PI / 180) *
        Math.sin(dLng / 2) ** 2;

    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    const distanceKm = R * c;

    const distanceMeters = distanceKm * 1000;

    console.log(`Plane distance: ${distanceKm.toFixed(2)} km | Radius: ${(radiusMeters / 1000).toFixed(2)} km`);

    return distanceMeters > radiusMeters;
}

// Fetch user location
if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
        function (position) {
            myLat = position.coords.latitude;
            myLng = position.coords.longitude;
            console.log("User location:", myLat, myLng);

            map.setView([myLat, myLng], 7);

            locationReady = true;

            //Send user location to BE
            fetch(`/api/location?lat=${myLat}&lng=${myLng}`)
                .then(res => res.text())
                .then(text => {
                    if (!text) return [];
                    return JSON.parse(text);
                })
                .then(data => {
                    data.forEach(plane => {
                        const [callsign, lat, lng, height, country, rotation] = plane;
                        const marker = L.marker([lat, lng], { icon: createPlaneIcon(rotation) })
                            .addTo(map)
                            .bindPopup(`<b>${callsign}</b><br>Altitude: ${height || 'N/A'} m<br>Origin: ${country || 'N/A'}`);
                        markers[callsign] = marker;
                    });
                })
                .catch(err => console.error("Error fetching planes:", err));

            initWebSocket();
        },
        function (error) {
            switch (error.code) {
                case error.PERMISSION_DENIED:
                    alert("Location access denied. Please allow location in your browser.");
                    break;
                case error.POSITION_UNAVAILABLE:
                    alert("Location information is unavailable.");
                    break;
                case error.TIMEOUT:
                    alert("Location request timed out.");
                    break;
                default:
                    alert("An unknown error occurred.");
                    break;
            }
        }
    );
} else {
    alert("Geolocation is not supported by this browser.");
}

// Init WebSocket connection
function initWebSocket() {
    var socket = new SockJS('/plane-websocket');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("Connected to websocket:", frame);

        // Get planes from BE
        stompClient.subscribe('/topic/planes', function (message) {
            const plane = JSON.parse(message.body);
            const latitude = plane[6];
            const longitude = plane[5];
            const callsign = plane[1];
            const rotation = plane[10] || 0;
            const height = plane[7];
            const country = plane[2];

            if (latitude == null || longitude == null) return;

            if (is_plane_outside_circle(latitude, longitude)) {
                console.log("Removing plane outside circle:", callsign);

                if (markers[callsign]) {
                    map.removeLayer(markers[callsign]);
                    delete markers[callsign];
                }
                return;
            }

            if (markers[callsign]) {
                markers[callsign].setLatLng([latitude, longitude]);
            } else {
                const marker = L.marker([latitude, longitude], { icon: createPlaneIcon(rotation) })
                    .addTo(map)
                    .bindPopup(`<b>${callsign}</b><br>Altitude: ${height || 'N/A'} m<br>Origin: ${country || 'N/A'}`);
                markers[callsign] = marker;
            }
        });

        // Get visibility from BE
        stompClient.subscribe('/topic/visibility', function (message) {
            const radiusKm = parseFloat(message.body);

            console.log("Received visibility from BE:", radiusKm, "km");

            if (!visibilityCircle) {
                console.log("Creating circle with radius:", radiusKm);

                visibilityCircle = L.circle([myLat, myLng], {
                    radius: radiusKm * 1000,
                    color: 'blue',
                    fillColor: '#3f8cff',
                    fillOpacity: 0.2
                }).addTo(map);

            } else {
                console.log("Updating circle radius:", radiusKm);

                visibilityCircle.setRadius(radiusKm * 1000);
            }
        });
    });
}