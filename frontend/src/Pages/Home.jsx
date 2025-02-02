import React, { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

function Home() {
  const [city, setCity] = useState("");
  const [weatherData, setWeatherData] = useState(null);
  const [history, setHistory] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    if (!localStorage.getItem("tokenForWeather")) {
      toast.warning("Please Signin First!");
      navigate('/signin');
    }
    async function getUserData() {
      try {
        const response = await axios.get(`${import.meta.env.VITE_BACKEND_URI}/api/user/getData`,
          {
            headers: { "Authorization": `Bearer ${localStorage.getItem("tokenForWeather")}` }
          }
        );
        console.log(response.data);
        toast.success(`Welcome back ${response.data.name}!`);
      } catch (error) {
        toast.error(error.response?.data?.message || "Failed to fetch user data.");
      }
    }
    getUserData();
  }, []);


  const fetchWeather = async () => {
    try {
      const response = await axios.get(`${import.meta.env.VITE_BACKEND_URI}/api/weather/${city}`,
        {
          headers: { "Authorization": `Bearer ${localStorage.getItem("tokenForWeather")}` }
        }
      );
      console.log(response);
      setWeatherData(response.data);
      toast.success("Weather data fetched successfully!");
    } catch (error) {
      setWeatherData(null);
      toast.error(error.response?.data?.message || "Failed to fetch weather data.");
    }
  };

  const fetchHistory = async () => {
    try {
      const response = await axios.get(`${import.meta.env.VITE_BACKEND_URI}/api/weather/history/${city}`,
        {
          headers: { "Authorization": `Bearer ${localStorage.getItem("tokenForWeather")}` }
        });
      setHistory(response.data);
      toast.success("Weather history fetched successfully!");
    } catch (error) {
      setHistory(null);
      toast.error(error.response?.data?.message || "Failed to fetch weather history.");
    }
  };

  return (
    <div className="container mx-auto p-4 sm:p-6 lg:p-8 bg-gradient-to-r from-blue-50 via-purple-50 to-pink-50 rounded-lg shadow-lg min-h-screen">
      <button
        onClick={() => {
          localStorage.removeItem('tokenForWeather');
          navigate('/signin');
        }}
        className="bg-red-500 text-white text-sm sm:text-base px-4 py-2 sm:px-6 sm:py-3 rounded-lg hover:bg-red-600 transition duration-300 mb-4 font-semibold"
      >
        Logout
      </button>

      <h1 className="text-2xl sm:text-4xl font-extrabold text-center text-indigo-600 mb-6">
        World Weather Tracker
      </h1>

      <div className="flex flex-col sm:flex-row justify-center gap-4 mb-6">
        <input
          type="text"
          placeholder="Enter city name"
          className="flex-1 border-2 border-indigo-300 rounded-lg px-4 py-2 sm:px-6 sm:py-3 text-base sm:text-lg focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500"
          value={city}
          onChange={(e) => setCity(e.target.value)}
        />
        <button
          onClick={fetchWeather}
          className="bg-indigo-600 text-white font-semibold text-sm sm:text-base px-4 py-2 sm:px-6 sm:py-3 rounded-lg hover:bg-indigo-800 transition duration-300"
        >
          Get Weather
        </button>
      </div>

      {weatherData && (
        <div className="bg-white p-4 sm:p-6 rounded-lg shadow-xl mb-6">
          <h2 className="text-xl sm:text-2xl font-bold text-indigo-700 mb-4">
            Current Weather in {weatherData.city}
          </h2>
          <div className="space-y-2">
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Region:</span> {weatherData.region}
            </p>
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Country:</span> {weatherData.country}
            </p>
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Temperature:</span> {weatherData.temperature}°C
            </p>
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Condition:</span> {weatherData.weatherCondition}
            </p>
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Humidity:</span> {weatherData.humidity}%
            </p>
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Wind Speed:</span> {weatherData.windSpeed} km/h
            </p>
            <p className="text-sm sm:text-lg text-gray-700">
              <span className="font-semibold">Time at Location:</span> {new Date(weatherData.timestamp).toLocaleString()}
            </p>
          </div>
        </div>
      )}

      <div className="flex justify-center mb-6">
        <button
          onClick={fetchHistory}
          className="bg-green-500 text-white text-sm sm:text-base px-4 py-2 sm:px-6 sm:py-3 rounded-lg hover:bg-green-600 transition duration-300 font-semibold"
        >
          Get Weather History
        </button>
      </div>

      {history?.length > 0 && (
        <div className="bg-white p-4 sm:p-6 rounded-lg shadow-xl">
          <h2 className="text-xl sm:text-2xl font-bold text-green-700 mb-4">
            Weather History for {weatherData?.city}, {weatherData?.region}, {weatherData?.country}
          </h2>
          <div className="overflow-x-auto">
            <table className="table-auto w-full border-collapse border border-gray-300 rounded-lg shadow-sm">
              <thead>
                <tr>
                  <th className="border-b-2 border-gray-300 px-4 py-2 sm:px-6 sm:py-3 text-sm sm:text-lg text-gray-600">Temperature</th>
                  <th className="border-b-2 border-gray-300 px-4 py-2 sm:px-6 sm:py-3 text-sm sm:text-lg text-gray-600">Condition</th>
                  <th className="border-b-2 border-gray-300 px-4 py-2 sm:px-6 sm:py-3 text-sm sm:text-lg text-gray-600">Timestamp (IST)</th>
                </tr>
              </thead>
              <tbody>
                {history.map((record, index) => (
                  <tr key={index} className="hover:bg-gray-100 transition duration-200">
                    <td className="border-b border-gray-300 px-4 py-2 sm:px-6 sm:py-3 text-sm sm:text-lg text-center">
                      {record.temperature}°C
                    </td>
                    <td className="border-b border-gray-300 px-4 py-2 sm:px-6 sm:py-3 text-sm sm:text-lg text-center">
                      {record.weatherCondition}
                    </td>
                    <td className="border-b border-gray-300 px-4 py-2 sm:px-6 sm:py-3 text-sm sm:text-lg text-center">
                      {new Date(record.timestamp).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>

  );
}

export default Home;
