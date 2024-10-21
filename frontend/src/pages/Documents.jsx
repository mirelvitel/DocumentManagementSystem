import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { FaDownload, FaTrash } from 'react-icons/fa';
import {toast} from 'react-toastify';
import Modal from 'react-modal';

Modal.setAppElement('#root');

const Documents = () => {
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [documentToDelete, setDocumentToDelete] = useState(null);

    useEffect(() => {
        const fetchDocuments = async () => {
            try {
                const response = await axios.get('/api/documents');
                setDocuments(response.data);
                toast.success('Documents loaded successfully.');
            } catch (err) {
                console.error('Error fetching documents:', err);
                setError('Failed to load documents. Please try again later.');
                toast.error('Failed to load documents. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        fetchDocuments();
    }, []);

    const handleDownload = async (id, fileName) => {
        try {
            const response = await axios.get(`/api/documents/download/${id}`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName); // Set the desired file name
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
            window.URL.revokeObjectURL(url);
            toast.success('Download started.');
        } catch (error) {
            console.error('Error downloading the file:', error);
            toast.error('Failed to download the file. Please try again.');
        }
    };

    const openModal = (id) => {
        setDocumentToDelete(id);
        setModalIsOpen(true);
    };

    const closeModal = () => {
        setDocumentToDelete(null);
        setModalIsOpen(false);
    };

    const confirmDelete = async () => {
        if (!documentToDelete) return;

        try {
            await axios.delete(`/api/documents/${documentToDelete}`);
            setDocuments(documents.filter(doc => doc.id !== documentToDelete));
            toast.success('Document deleted successfully.');
        } catch (error) {
            console.error('Error deleting the file:', error);
            toast.error('Failed to delete the file. Please try again.');
        } finally {
            closeModal();
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center h-full mt-10">
                <div className="loader ease-linear rounded-full border-8 border-t-8 border-gray-200 h-32 w-32"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="max-w-4xl mx-auto mt-10 p-6 bg-red-100 rounded-md shadow-md">
                <p className="text-red-600">{error}</p>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-md shadow-md">
            <h2 className="text-2xl font-semibold mb-6">Uploaded Documents</h2>
            {documents.length === 0 ? (
                <p>No documents found.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white">
                        <thead>
                        <tr>
                            <th className="py-2 px-4 border-b">ID</th>
                            <th className="py-2 px-4 border-b">Title</th>
                            <th className="py-2 px-4 border-b">File Name</th>
                            <th className="py-2 px-4 border-b">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {documents.map((doc) => (
                            <tr key={doc.id} className="hover:bg-gray-100">
                                <td className="py-2 px-4 border-b text-center">{doc.id}</td>
                                <td className="py-2 px-4 border-b">{doc.title}</td>
                                <td className="py-2 px-4 border-b">{doc.fileName}</td>
                                <td className="py-2 px-4 border-b text-center justify-center space-x-4">
                                    <button
                                        onClick={() => handleDownload(doc.id, doc.fileName)}
                                        className="text-blue-600 hover:text-blue-800"
                                        title="Download"
                                    >
                                        <FaDownload size={20} />
                                    </button>
                                    <button
                                        onClick={() => openModal(doc.id)}
                                        className="text-red-600 hover:text-red-800"
                                        title="Delete"
                                    >
                                        <FaTrash size={20} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Confirmation Modal for Deletion */}
            <Modal
                isOpen={modalIsOpen}
                onRequestClose={closeModal}
                contentLabel="Confirm Delete"
                className="max-w-md mx-auto mt-40 bg-white p-6 rounded-md shadow-lg outline-none"
                overlayClassName="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center"
            >
                <h2 className="text-xl font-semibold mb-4">Confirm Deletion</h2>
                <p>Are you sure you want to delete this document?</p>
                <div className="mt-6 flex justify-end space-x-4">
                    <button
                        onClick={closeModal}
                        className="px-4 py-2 bg-gray-300 text-gray-800 rounded-md hover:bg-gray-400"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={confirmDelete}
                        className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700"
                    >
                        Delete
                    </button>
                </div>
            </Modal>
        </div>
    );
};

export default Documents;
