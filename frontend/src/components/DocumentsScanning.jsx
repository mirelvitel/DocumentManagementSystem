import React, { useState } from 'react';
import axios from 'axios';

const DocumentsScanning = () => {
    const [response, setResponse] = useState('');

    const sendTestRequest = () => {
        axios.get('/api/test')
            .then((res) => {
                setResponse(res.data);
            })
            .catch((error) => {
                console.error("There was an error making the request!", error);
            });
    };

    return (
        <div>
            <h1>Test Request</h1>
            <button onClick={sendTestRequest}>Send Request</button>
            {response && <p>Response from backend: {response}</p>}
        </div>
    );
};

export default DocumentsScanning;