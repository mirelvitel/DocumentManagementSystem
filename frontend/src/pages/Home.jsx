import React from 'react';
import { FaUpload, FaSearch, FaCloud } from 'react-icons/fa';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="min-h-screen bg-gray-100 flex flex-col items-center justify-center p-4">
            <div className="bg-white rounded-lg shadow-lg overflow-hidden max-w-4xl w-full">
                {/* Hero Section */}
                <div className="relative bg-gradient-to-br from-blue-600 to-blue-800 py-16 px-8">
                    <div className="flex flex-col items-center justify-center text-center">
                        <h1 className="text-4xl font-bold text-white mb-4">DocScan</h1>
                        <p className="text-lg text-blue-100 mb-8 max-w-2xl">
                            Upload documents, extract text with OCR, and search through your content instantly.
                        </p>
                        <Link to="/upload">
                            <button className="flex items-center bg-white text-blue-600 px-6 py-3 rounded-full font-semibold hover:bg-blue-50 transition duration-300">
                                <FaUpload className="mr-2" />
                                Upload Document
                            </button>
                        </Link>
                    </div>
                </div>

                {/* Features Section */}
                <div className="p-8">
                    <h2 className="text-2xl font-semibold mb-6 text-center">How It Works</h2>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                        <div className="flex flex-col items-center bg-gray-50 p-6 rounded-lg">
                            <FaUpload className="text-blue-500 text-3xl mb-4" />
                            <h3 className="text-lg font-semibold mb-2">1. Upload</h3>
                            <p className="text-center text-gray-600 text-sm">
                                Upload PDF, DOC, or image files through the simple upload interface.
                            </p>
                        </div>
                        <div className="flex flex-col items-center bg-gray-50 p-6 rounded-lg">
                            <FaCloud className="text-blue-500 text-3xl mb-4" />
                            <h3 className="text-lg font-semibold mb-2">2. OCR Processing</h3>
                            <p className="text-center text-gray-600 text-sm">
                                Tesseract OCR automatically extracts text from your documents.
                            </p>
                        </div>
                        <div className="flex flex-col items-center bg-gray-50 p-6 rounded-lg">
                            <FaSearch className="text-blue-500 text-3xl mb-4" />
                            <h3 className="text-lg font-semibold mb-2">3. Search</h3>
                            <p className="text-center text-gray-600 text-sm">
                                Full-text search powered by Elasticsearch across all your documents.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Home;