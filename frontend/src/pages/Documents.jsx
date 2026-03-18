import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { FaDownload, FaTrash, FaEye } from 'react-icons/fa';
import { toast } from 'react-toastify';
import Modal from 'react-modal';

Modal.setAppElement('#root');

const modalStyles = {
    content: {
        top: '50%',
        left: '50%',
        right: 'auto',
        bottom: 'auto',
        marginRight: '-50%',
        transform: 'translate(-50%, -50%)',
        maxWidth: '600px',
        width: '90%',
        maxHeight: '80vh',
        overflow: 'auto',
        borderRadius: '8px',
        padding: '24px',
    },
    overlay: {
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        zIndex: 1000,
    },
};

const Documents = () => {
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [deleteModalIsOpen, setDeleteModalIsOpen] = useState(false);
    const [viewModalIsOpen, setViewModalIsOpen] = useState(false);
    const [documentToDelete, setDocumentToDelete] = useState(null);
    const [selectedDocument, setSelectedDocument] = useState(null);
    const [viewLoading, setViewLoading] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');

    const fetchDocuments = async (query = '') => {
        setLoading(true);
        setError('');
        try {
            let response;
            if (query.trim() !== '') {
                response = await axios.get('/api/documents/search', { params: { keyword: query } });
            } else {
                response = await axios.get('/api/documents');
            }
            setDocuments(response.data);
        } catch (err) {
            console.error('Error fetching documents:', err);
            setError('Failed to load documents. Please try again later.');
            toast.error('Failed to load documents.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDocuments();
    }, []);

    const handleSearch = () => {
        fetchDocuments(searchQuery);
    };

    const handleSearchKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

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
            toast.error('Failed to download the file.');
        }
    };

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
            toast.success('Document deleted.');
        } catch (error) {
            console.error('Error deleting the file:', error);
            toast.error('Failed to delete the document.');
        } finally {
            closeDeleteModal();
        }
    };

    const openViewModal = async (id) => {
        setViewLoading(true);
        setSelectedDocument(null);
        setViewModalIsOpen(true);
        try {
            const response = await axios.get(`/api/documents/${id}`);
            setSelectedDocument(response.data);
        } catch (error) {
            console.error('Error fetching document details:', error);
            toast.error('Failed to load document details.');
        } finally {
            setViewLoading(false);
        }
    };

    const closeViewModal = () => {
        setSelectedDocument(null);
        setViewModalIsOpen(false);
    };

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64 mt-10">
                <div className="loader ease-linear rounded-full border-8 border-t-8 border-gray-200 h-16 w-16"></div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="max-w-4xl mx-auto mt-10 p-6 bg-red-100 rounded-md shadow-md">
                <p className="text-red-600">{error}</p>
                <button
                    onClick={() => fetchDocuments()}
                    className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-md shadow-md">
            <h2 className="text-2xl font-semibold mb-6">Documents</h2>

            <div className="mb-4 flex space-x-2">
                <input
                    type="text"
                    placeholder="Search by content..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    onKeyDown={handleSearchKeyDown}
                    className="border px-4 py-2 rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-400"
                />
                <button
                    onClick={handleSearch}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 whitespace-nowrap"
                >
                    Search
                </button>
                {searchQuery && (
                    <button
                        onClick={() => { setSearchQuery(''); fetchDocuments(); }}
                        className="px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 whitespace-nowrap"
                    >
                        Clear
                    </button>
                )}
            </div>

            {documents.length === 0 ? (
                <p className="text-gray-500 text-center py-8">
                    {searchQuery ? 'No documents match your search.' : 'No documents uploaded yet.'}
                </p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white">
                        <thead>
                        <tr className="bg-gray-50">
                            <th className="py-3 px-4 border-b text-left font-medium text-gray-600">Title</th>
                            <th className="py-3 px-4 border-b text-left font-medium text-gray-600">File Name</th>
                            <th className="py-3 px-4 border-b text-center font-medium text-gray-600">Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {documents.map((doc) => (
                            <tr key={doc.id} className="hover:bg-gray-50 transition-colors">
                                <td className="py-3 px-4 border-b">{doc.title}</td>
                                <td className="py-3 px-4 border-b text-gray-600 text-sm">{doc.fileName}</td>
                                <td className="py-3 px-4 border-b text-center space-x-3">
                                    <button onClick={() => handleDownload(doc.id, doc.fileName)} title="Download">
                                        <FaDownload size={18} className="text-blue-600 hover:text-blue-800 inline" />
                                    </button>
                                    <button onClick={() => openViewModal(doc.id)} title="View Details">
                                        <FaEye size={18} className="text-green-600 hover:text-green-800 inline" />
                                    </button>
                                    <button onClick={() => openDeleteModal(doc.id)} title="Delete">
                                        <FaTrash size={18} className="text-red-600 hover:text-red-800 inline" />
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* Delete Confirmation Modal */}
            <Modal isOpen={deleteModalIsOpen} onRequestClose={closeDeleteModal} style={modalStyles}>
                <h2 className="text-xl font-semibold mb-4">Confirm Deletion</h2>
                <p className="text-gray-600 mb-6">Are you sure you want to delete this document? This action cannot be undone.</p>
                <div className="flex justify-end space-x-3">
                    <button onClick={closeDeleteModal} className="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300">
                        Cancel
                    </button>
                    <button onClick={confirmDelete} className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700">
                        Delete
                    </button>
                </div>
            </Modal>

            {/* View Details Modal */}
            <Modal isOpen={viewModalIsOpen} onRequestClose={closeViewModal} style={modalStyles}>
                <h2 className="text-xl font-semibold mb-4">Document Details</h2>
                {viewLoading ? (
                    <div className="flex justify-center py-8">
                        <div className="loader ease-linear rounded-full border-4 border-t-4 border-gray-200 h-8 w-8"></div>
                    </div>
                ) : selectedDocument ? (
                    <div className="space-y-3">
                        <div>
                            <span className="font-medium text-gray-700">Title:</span>
                            <span className="ml-2">{selectedDocument.title}</span>
                        </div>
                        <div>
                            <span className="font-medium text-gray-700">File:</span>
                            <span className="ml-2 text-sm text-gray-600">{selectedDocument.fileName}</span>
                        </div>
                        <div>
                            <span className="font-medium text-gray-700">OCR Content:</span>
                            <div className="mt-2 p-3 bg-gray-50 rounded text-sm max-h-64 overflow-auto whitespace-pre-wrap">
                                {selectedDocument.textContent || 'No text extracted yet. OCR may still be processing.'}
                            </div>
                        </div>
                    </div>
                ) : (
                    <p className="text-gray-500">Failed to load document details.</p>
                )}
                <div className="flex justify-end mt-6">
                    <button onClick={closeViewModal} className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
                        Close
                    </button>
                </div>
            </Modal>
        </div>
    );
};

export default Documents;