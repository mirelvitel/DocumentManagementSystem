import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { FaDownload, FaTrash, FaEye } from 'react-icons/fa';
import { toast } from 'react-toastify';
import Modal from 'react-modal';

Modal.setAppElement('#root');

const Documents = () => {
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [deleteModalIsOpen, setDeleteModalIsOpen] = useState(false);
    const [viewModalIsOpen, setViewModalIsOpen] = useState(false);
    const [documentToDelete, setDocumentToDelete] = useState(null);
    const [selectedDocument, setSelectedDocument] = useState(null);
    const [viewLoading, setViewLoading] = useState(false);

    // Added search query state
    const [searchQuery, setSearchQuery] = useState('');

    // Modified fetchDocuments to accept a query parameter
    const fetchDocuments = async (query = '') => {
        setLoading(true);
        try {
            let response;
            if (query.trim() !== '') {
                // Call the search endpoint with a keyword
                response = await axios.get(`/api/documents/search`, { params: { keyword: query } });
            } else {
                // Call the regular endpoint to fetch all documents
                response = await axios.get('/api/documents');
            }

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


    // Fetch all documents on component load
    useEffect(() => {
        fetchDocuments();
    }, []);

    // Trigger search when the user clicks "Search" button
    const handleSearch = () => {
        fetchDocuments(searchQuery);
    };

    // Download file handler
    const handleDownload = async (id, fileName) => {
        try {
            const response = await axios.get(`/api/documents/download/${id}`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', fileName);
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

    // Open delete confirmation modal
    const openDeleteModal = (id) => {
        setDocumentToDelete(id);
        setDeleteModalIsOpen(true);
    };

    const closeDeleteModal = () => {
        setDocumentToDelete(null);
        setDeleteModalIsOpen(false);
    };

    const confirmDelete = async () => {
        if (!documentToDelete) return;

        try {
            await axios.delete(`/api/documents/${documentToDelete}`);
            setDocuments(documents.filter((doc) => doc.id !== documentToDelete));
            toast.success('Document deleted successfully.');
        } catch (error) {
            console.error('Error deleting the file:', error);
            toast.error('Failed to delete the file. Please try again.');
        } finally {
            closeDeleteModal();
        }
    };

    // Open view details modal
    const openViewModal = async (id) => {
        setViewLoading(true);
        setSelectedDocument(null); // Reset previous state
        setViewModalIsOpen(true);
        try {
            const response = await axios.get(`/api/documents/${id}`);
            setSelectedDocument(response.data);
            toast.success('Document details loaded successfully.');
        } catch (error) {
            console.error('Error fetching document details:', error);
            toast.error('Failed to load document details. Please try again.');
        } finally {
            setViewLoading(false);
        }
    };

    const closeViewModal = () => {
        setSelectedDocument(null);
        setViewModalIsOpen(false);
    };

    // Handle loading state
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

    // UI rendering
    return (
        <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-md shadow-md">
            <h2 className="text-2xl font-semibold mb-6">Uploaded Documents</h2>

            {/* Search bar */}
            <div className="mb-4 flex space-x-2">
                <input
                    type="text"
                    placeholder="Search documents..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="border px-4 py-2 rounded w-full"
                />
                <button
                    onClick={handleSearch}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                    Search
                </button>
            </div>

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
                                <td className="py-2 px-4 border-b text-center space-x-4">
                                    <button
                                        onClick={() => handleDownload(doc.id, doc.fileName)}
                                        title="Download"
                                    >
                                        <FaDownload size={20} className="text-blue-600 hover:text-blue-800" />
                                    </button>
                                    <button
                                        onClick={() => openViewModal(doc.id)}
                                        title="View"
                                    >
                                        <FaEye size={20} className="text-green-600 hover:text-green-800" />
                                    </button>
                                    <button
                                        onClick={() => openDeleteModal(doc.id)}
                                        title="Delete"
                                    >
                                        <FaTrash size={20} className="text-red-600 hover:text-red-800" />
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Delete Confirmation Modal */}
            <Modal isOpen={deleteModalIsOpen} onRequestClose={closeDeleteModal}>
                <h2>Confirm Deletion</h2>
                <p>Are you sure you want to delete this document?</p>
                <div className="flex justify-end mt-4 space-x-4">
                    <button onClick={closeDeleteModal} className="px-4 py-2 bg-gray-300 rounded">
                        Cancel
                    </button>
                    <button onClick={confirmDelete} className="px-4 py-2 bg-red-600 text-white rounded">
                        Delete
                    </button>
                </div>
            </Modal>

            {/* View Details Modal */}
            <Modal isOpen={viewModalIsOpen} onRequestClose={closeViewModal}>
                <h2>Document Details</h2>
                {viewLoading ? (
                    <p>Loading...</p>
                ) : selectedDocument ? (
                    <div>
                        <p><strong>Title:</strong> {selectedDocument.title}</p>
                        <p><strong>Content:</strong> {selectedDocument.textContent}</p>
                    </div>
                ) : (
                    <p>Failed to load document details.</p>
                )}
                <button onClick={closeViewModal} className="mt-4 px-4 py-2 bg-blue-600 text-white rounded">
                    Close
                </button>
            </Modal>
        </div>
    );
};

export default Documents;
