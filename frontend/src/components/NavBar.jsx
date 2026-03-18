import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaBars, FaTimes } from 'react-icons/fa';

const NavBar = () => {
    const [mobileOpen, setMobileOpen] = useState(false);

    return (
        <nav className="bg-blue-600">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">
                    <div className="flex-shrink-0">
                        <Link to="/" className="text-white font-bold text-xl">
                            DocScan
                        </Link>
                    </div>
                    <div className="hidden md:block">
                        <div className="ml-10 flex items-baseline space-x-4">
                            <Link
                                to="/documents"
                                className="text-white px-3 py-2 rounded-md text-sm font-medium hover:bg-blue-700"
                            >
                                Documents
                            </Link>
                            <Link
                                to="/upload"
                                className="text-white px-3 py-2 rounded-md text-sm font-medium hover:bg-blue-700"
                            >
                                Upload
                            </Link>
                        </div>
                    </div>
                    <div className="flex md:hidden">
                        <button
                            onClick={() => setMobileOpen(!mobileOpen)}
                            className="text-white p-2"
                            aria-label="Toggle menu"
                        >
                            {mobileOpen ? <FaTimes size={20} /> : <FaBars size={20} />}
                        </button>
                    </div>
                </div>
            </div>
            {mobileOpen && (
                <div className="md:hidden bg-blue-700">
                    <div className="px-2 pt-2 pb-3 space-y-1">
                        <Link
                            to="/documents"
                            className="text-white block px-3 py-2 rounded-md text-base font-medium hover:bg-blue-800"
                            onClick={() => setMobileOpen(false)}
                        >
                            Documents
                        </Link>
                        <Link
                            to="/upload"
                            className="text-white block px-3 py-2 rounded-md text-base font-medium hover:bg-blue-800"
                            onClick={() => setMobileOpen(false)}
                        >
                            Upload
                        </Link>
                    </div>
                </div>
            )}
        </nav>
    );
};

export default NavBar;