import React, { useState, useCallback } from 'react';
import axios from 'axios';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import { FaCloudUploadAlt } from 'react-icons/fa';

const UploadDocuments = () => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [dragActive, setDragActive] = useState(false);
    const navigate = useNavigate();

    const ACCEPTED_TYPES = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'image/jpeg',
        'image/png',
        'image/tiff',
        'image/bmp',
    ];

    const handleFile = (file) => {
        if (file && (ACCEPTED_TYPES.includes(file.type) || file.name.match(/\.(pdf|doc|docx|jpg|jpeg|png|tiff|bmp)$/i))) {
            setSelectedFile(file);
        } else {
            toast.warn('Unsupported file type.');
        }
    };

    const handleFileChange = (event) => {
        if (event.target.files[0]) {
            handleFile(event.target.files[0]);
        }
    };

    const handleDrag = useCallback((e) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === 'dragenter' || e.type === 'dragover') {
            setDragActive(true);
        } else if (e.type === 'dragleave') {
            setDragActive(false);
        }
    }, []);

    const handleDrop = useCallback((e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);
        if (e.dataTransfer.files && e.dataTransfer.files[0]) {
            handleFile(e.dataTransfer.files[0]);
        }
    }, []);

    const handleUpload = async (event) => {
        event.preventDefault();

        if (!selectedFile) {
            toast.warn('Please select a file to upload.');
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);

        try {
            setUploading(true);
            setUploadProgress(0);

            await axios.post('/api/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
                onUploadProgress: (progressEvent) => {
                    const percentCompleted = Math.round(
                        (progressEvent.loaded * 100) / progressEvent.total
                    );
                    setUploadProgress(percentCompleted);
                },
            });

            toast.success('File uploaded! OCR processing will begin shortly.');
            setSelectedFile(null);
            setTimeout(() => navigate('/documents'), 1500);
        } catch (error) {
            console.error('Error uploading file:', error);
            toast.error('Failed to upload file. Please try again.');
        } finally {
            setUploading(false);
            setUploadProgress(0);
        }
    };

    return (
        <div className="max-w-lg mx-auto mt-10 p-6 bg-white rounded-md shadow-md">
            <h2 className="text-2xl font-semibold mb-6">Upload Document</h2>
            <form onSubmit={handleUpload}>
                {/* Drag & Drop Zone */}
                <div
                    onDragEnter={handleDrag}
                    onDragLeave={handleDrag}
                    onDragOver={handleDrag}
                    onDrop={handleDrop}
                    onClick={() => document.getElementById('file-input').click()}
                    className={`relative border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors duration-200 mb-4
                        ${dragActive
                            ? 'border-blue-500 bg-blue-50'
                            : selectedFile
                                ? 'border-green-400 bg-green-50'
                                : 'border-gray-300 hover:border-blue-400 hover:bg-gray-50'
                        }`}
                >
                    <input
                        type="file"
                        id="file-input"
                        onChange={handleFileChange}
                        className="hidden"
                        accept=".pdf,.doc,.docx,.jpg,.jpeg,.png,.tiff,.bmp"
                    />
                    <FaCloudUploadAlt className={`mx-auto text-4xl mb-3 ${dragActive ? 'text-blue-500' : 'text-gray-400'}`} />
                    {selectedFile ? (
                        <div>
                            <p className="text-green-700 font-medium">{selectedFile.name}</p>
                            <p className="text-sm text-gray-500 mt-1">
                                {(selectedFile.size / 1024 / 1024).toFixed(2)} MB
                            </p>
                            <p className="text-xs text-gray-400 mt-2">Click or drop to replace</p>
                        </div>
                    ) : (
                        <div>
                            <p className="text-gray-600 font-medium">
                                {dragActive ? 'Drop your file here' : 'Drag & drop a file here, or click to browse'}
                            </p>
                            <p className="text-xs text-gray-400 mt-2">
                                PDF, DOC, DOCX, JPG, PNG, TIFF, BMP
                            </p>
                        </div>
                    )}
                </div>

                {/* Progress Bar */}
                {uploading && (
                    <div className="mb-4">
                        <div className="w-full bg-gray-200 rounded-full h-2.5">
                            <div
                                className="bg-blue-600 h-2.5 rounded-full transition-all duration-300"
                                style={{ width: `${uploadProgress}%` }}
                            ></div>
                        </div>
                        <p className="text-sm text-gray-600 mt-1 text-center">{uploadProgress}%</p>
                    </div>
                )}

                <button
                    type="submit"
                    disabled={uploading || !selectedFile}
                    className={`w-full bg-blue-600 text-white py-2.5 px-4 rounded-md font-medium hover:bg-blue-700 transition duration-200 ${
                        (uploading || !selectedFile) ? 'opacity-50 cursor-not-allowed' : ''
                    }`}
                >
                    {uploading ? 'Uploading...' : 'Upload'}
                </button>
            </form>
        </div>
    );
};

export default UploadDocuments;