// src/components/NavBar.jsx
import React from 'react';
import { Link } from 'react-router-dom';

const NavBar = () => {
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
                                Upload Documents
                            </Link>
                        </div>
                    </div>
                    <div className="-mr-2 flex md:hidden">
                        {/* Mobile menu button can be implemented here */}
                    </div>
                </div>
            </div>
        </nav>
    );
};

export default NavBar;
