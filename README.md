
Start Kafka manually:
- cd C:\kafka
- wsl
- bin/kafka-server-start.sh config/server.properties

<img width="2063" height="1343" alt="Kafka-architecture" src="https://github.com/user-attachments/assets/2a00efbe-58ac-4cbf-aaf3-f3f27f724d38" />
---
<img width="801" height="807" alt="Screenshot 2026-03-22 100235" src="https://github.com/user-attachments/assets/5dec3798-886e-44b6-908d-087e75de3ac8" />


---

## ✈️ Real-Time Flight Visibility Tracker

This project is a real-time web application that displays nearby aircraft based on the user's current location and weather visibility conditions. It combines live aviation data, weather data, and streaming technologies to provide an interactive and dynamic map experience.

---

## 📌 Overview

The application determines how far a user can “see” in the sky using weather data, then shows only the aircraft within that visible range. Aircraft positions are continuously updated in real time and rendered on an interactive map.

---

## ⚙️ How It Works

1. **User Location**

   * The browser retrieves the user's geographic location using the Geolocation API.

2. **Visibility Calculation**

   * The backend calls the Weatherstack API to determine current visibility (in kilometers) for the user’s location.

3. **Flight Data Retrieval**

   * Using the visibility radius, the backend queries the OpenSky API to fetch aircraft within a bounding box (square area).

4. **Streaming Pipeline**

   * Retrieved aircraft data is published to a Kafka topic.
   * A Kafka consumer processes the data stream and forwards updates to clients via WebSocket (STOMP).

5. **Real-Time Updates**

   * The frontend subscribes to WebSocket topics and receives continuous updates about aircraft positions and visibility radius.

6. **Filtering & Visualization**

   * Since the OpenSky API returns data in a square region, additional filtering ensures only aircraft within the circular visibility radius are displayed.
   * Aircraft are rendered as rotating icons on a Leaflet map.
   * A circle overlay represents the visibility range.

---

## 🧩 Architecture

The system consists of three main parts:

* **Frontend (Browser)**

  * Interactive map built with Leaflet
  * Displays aircraft and visibility radius
  * Receives real-time updates via WebSocket

* **Backend**

  * Handles API calls (Weatherstack & OpenSky)
  * Publishes aircraft data to Kafka
  * Streams updates via STOMP/WebSocket

* **Streaming Layer**

  * Apache Kafka for decoupled, real-time data processing

(See the architecture diagram above for a visual overview.)

---

## 🚀 Features

* 🌍 Automatic user geolocation
* ☁️ Weather-based visibility radius
* ✈️ Real-time aircraft tracking
* 🔄 Live updates via WebSocket
* 🧭 Interactive map with dynamic markers
* 🎯 Circular filtering of aircraft positions

---

## 🛠️ Technologies Used

* **Frontend:** JavaScript, Leaflet.js
* **Backend:** Spring Boot
* **Streaming:** Apache Kafka
* **WebSocket:** STOMP over SockJS
* **APIs:** Weatherstack API, OpenSky Network API

---

## 📈 Future Improvements

* Move circle filtering fully to backend for efficiency
* Add additonal aircraft details (speed, heading, airline)
* Drawn plane path
* Add user control buttons to interact with the map

