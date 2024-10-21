import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import NavBar from './components/NavBar';
import Home from './pages/Home';
import UploadDocuments from './pages/UploadDocuments';
import Documents from './pages/Documents';

const App = () => {
    return (
        <Router>
            <div className="flex flex-col min-h-screen">
                <NavBar />
                <div className="flex-grow">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/upload" element={<UploadDocuments />} />
                        <Route path="/documents" element={<Documents />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
};

export default App;
