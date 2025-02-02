# World Weather Tracker

## Overview
Universal Weather Tracker is a full-stack application built with **Spring Boot (Backend), React (Vite) (Frontend), and MySQL (Database)**. It provides real-time weather updates, tracks historical weather data, and sends email alerts for extreme weather conditions. The application integrates with an external weather API using **RestTemplate** and utilizes **JavaMailSender** for email notifications. All API requests, responses, and errors are logged in a `.txt` file.

## Features

### Backend (Spring Boot)
- **RESTful API Endpoints:**
  - `GET /weather/{city}` → Fetches real-time weather data for the given city.
  - `GET /weather/history/{city}` → Retrieves the last five weather requests for a city.
- **Database (MySQL)**
  - Stores weather data for historical tracking.
- **External API Integration**
  - Uses `RestTemplate` to fetch data from an external weather API.
- **Email Alerts**
  - Sends alerts using `JavaMailSender` when:
    - Temperature exceeds **40°C**.
    - Temperature drops below **0°C**.
- **Logging**
  - Logs API requests, responses, errors, and email notifications in a `.txt` file.

### Frontend (React + Vite)
- **User Interface**
  - Search bar to enter city names.
  - Weather display for real-time weather data.
  - History table showing past weather data.
  - Alert banner for extreme weather conditions.
- **API Integration**
  - Uses `Axios` to fetch weather data from the Spring Boot backend.

## Technology Stack
- **Backend**: Spring Boot, JavaMailSender, RestTemplate, MySQL, Logging to `.txt`
- **Frontend**: React (Vite), Axios, Tailwind CSS
- **Database**: MySQL
- **Email Service**: JavaMailSender
