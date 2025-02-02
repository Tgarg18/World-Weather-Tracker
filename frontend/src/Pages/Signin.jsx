import axios from "axios";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";

function Signin() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        if (localStorage.getItem("tokenForWeather")) {
            navigate("/");
            return;
        };
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const userData = { email, password };

        try {
            const response = await axios.post(`${import.meta.env.VITE_BACKEND_URI}/auth/signin`, userData);
            console.log(response);
            localStorage.setItem("tokenForWeather", response.data.jwt);
            toast.success(`Signin successful!`);
            navigate("/");
        } catch (error) {
            console.log(error);
            toast.error(error.response?.data?.message || "Error");
        }

    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
                <h2 className="text-2xl font-bold text-center mb-6">Sign In</h2>
                {error && <div className="text-red-500 text-center mb-4">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                            Email
                        </label>
                        <input
                            type="email"
                            id="email"
                            className="mt-2 p-2 w-full border border-gray-300 rounded-md"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-4">
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                            Password
                        </label>
                        <input
                            type="password"
                            id="password"
                            className="mt-2 p-2 w-full border border-gray-300 rounded-md"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-blue-500 text-white p-2 rounded-md hover:bg-blue-700"
                    >
                        Sign In
                    </button>
                </form>
                <div className="text-center mt-4">
                    <span className="text-sm text-blue-600 cursor-pointer hover:text-blue-400" onClick={() => navigate("/signup")}>
                        Don't have an account?{" "}Sign Up
                    </span>
                </div>
            </div>
        </div>
    );
}

export default Signin;
