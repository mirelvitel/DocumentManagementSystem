import React from 'react';
import { FaUpload, FaLock, FaCloud  } from 'react-icons/fa';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="min-h-screen bg-gray-100 flex flex-col items-center justify-center">
            {/* Hero Section */}
            <div className="bg-white rounded-lg shadow-lg overflow-hidden max-w-4xl w-full">
                {/* Background Image */}
                <div className="relative">
                    <img
                        src="/assets/paper.png"
                        alt="Document Management"
                        className="w-full h-64 object-cover"
                    />
                    {/* Overlay Text */}
                    <div className="absolute inset-0 bg-black opacity-50"></div>
                    <div className="absolute inset-0 flex flex-col items-center justify-center text-center px-4">
                        <h1 className="text-4xl font-bold text-white mb-4">Welcome to Document Management System</h1>
                        <p className="text-lg text-gray-200 mb-6">
                            Efficiently manage, store, and organize your documents all in one place.
                        </p>
                        <Link to="/upload">
                            <button className="flex items-center bg-blue-600 text-white px-6 py-3 rounded-full hover:bg-blue-700 transition duration-300">
                                <FaUpload className="mr-2" />
                                Upload Document
                            </button>
                        </Link>
                    </div>
                </div>

                {/* Features Section */}
                <div className="p-8">
                    <h2 className="text-2xl font-semibold mb-6 text-center">Features</h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        {/* Feature 1 */}
                        <div className="flex flex-col items-center bg-gray-50 p-6 rounded-lg shadow-md">
                            <FaUpload className="text-blue-500 text-4xl mb-4" />
                            <h3 className="text-xl font-semibold mb-2">Easy Uploads</h3>
                            <p className="text-center text-gray-600">
                                Quickly upload your documents with our intuitive interface.
                            </p>
                        </div>
                        {/* Feature 2 */}
                        <div className="flex flex-col items-center bg-gray-50 p-6 rounded-lg shadow-md">
                            <FaLock className="text-blue-500 text-4xl mb-4" /> {/* Replace with appropriate icon */}
                            <h3 className="text-xl font-semibold mb-2">Secure Storage</h3>
                            <p className="text-center text-gray-600">
                                Your documents are stored securely with top-notch encryption.
                            </p>
                        </div>
                        {/* Feature 3 */}
                        <div className="flex flex-col items-center bg-gray-50 p-6 rounded-lg shadow-md">
                            <FaCloud className="text-blue-500 text-4xl mb-4" /> {/* Replace with appropriate icon */}
                            <h3 className="text-xl font-semibold mb-2">Easy Access</h3>
                            <p className="text-center text-gray-600">
                                Access your documents from anywhere, anytime.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;
