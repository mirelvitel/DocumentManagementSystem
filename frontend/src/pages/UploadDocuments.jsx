import React, { useState } from 'react';
import axios from 'axios';

const UploadDocuments = () => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [message, setMessage] = useState('');

    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
        setMessage('');
    };

    const handleUpload = async (event) => {
        event.preventDefault();

        if (!selectedFile) {
            setMessage('Please select a file to upload.');
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);

        try {
            setUploading(true);
            setUploadProgress(0);
            setMessage('');

            await axios.post('/api/upload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
                onUploadProgress: (progressEvent) => {
                    const percentCompleted = Math.round(
                        (progressEvent.loaded * 100) / progressEvent.total
                    );
                    setUploadProgress(percentCompleted);
                },
            });

            setMessage('File uploaded successfully!');
            setSelectedFile(null);
        } catch (error) {
            console.error('Error uploading file:', error);
            setMessage('Failed to upload file. Please try again.');
        } finally {
            setUploading(false);
            setUploadProgress(0);
        }
    };

    return (
        <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-md shadow-md">
            <h2 className="text-2xl font-semibold mb-4">Upload Documents</h2>
            <form onSubmit={handleUpload}>
                <div className="mb-4">
                    <label
                        htmlFor="file"
                        className="block text-gray-700 text-sm font-bold mb-2"
                    >
                        Select Document:
                    </label>
                    <input
                        type="file"
                        id="file"
                        onChange={handleFileChange}
                        className="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300"
                        accept=".pdf,.doc,.docx,.jpg,.png"
                    />
                </div>
                {uploading && (
                    <div className="mb-4">
                        <div className="w-full bg-gray-200 rounded-full h-2.5">
                            <div
                                className="bg-blue-600 h-2.5 rounded-full"
                                style={{ width: `${uploadProgress}%` }}
                            ></div>
                        </div>
                        <p className="text-sm text-gray-600 mt-1">{uploadProgress}%</p>
                    </div>
                )}
                {message && (
                    <div
                        className={`mb-4 text-sm ${
                            message.includes('successfully')
                                ? 'text-green-600'
                                : 'text-red-600'
                        }`}
                    >
                        {message}
                    </div>
                )}
                <button
                    type="submit"
                    disabled={uploading}
                    className={`w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition duration-200 ${
                        uploading ? 'opacity-50 cursor-not-allowed' : ''
                    }`}
                >
                    {uploading ? 'Uploading...' : 'Upload'}
                </button>
            </form>
        </div>
    );
};

export default UploadDocuments;
