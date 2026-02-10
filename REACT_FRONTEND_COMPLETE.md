# Complete React Frontend Files - Ready to Use

## 📁 File Structure

```
src/
├── config/
│   └── api.js
├── services/
│   ├── axiosConfig.js
│   └── assetService.js
├── components/
│   ├── AssetList.jsx
│   ├── CreateAssetForm.jsx
│   └── AssetDashboard.jsx
├── styles/
│   └── assets.css
└── App.jsx
```

---

## 1. Configuration Files

### `src/config/api.js`
```javascript
const API_CONFIG = {
  USE_GATEWAY: process.env.REACT_APP_USE_GATEWAY === 'true',
  GATEWAY_URL: process.env.REACT_APP_GATEWAY_URL || 'http://localhost:8080',
  ASSETS_SERVICE_URL: process.env.REACT_APP_ASSETS_URL || 'http://localhost:8081',
};

export const getBaseUrl = (serviceName = '') => {
  if (API_CONFIG.USE_GATEWAY) {
    return API_CONFIG.GATEWAY_URL;
  }
  
  if (serviceName.toLowerCase() === 'assets') {
    return API_CONFIG.ASSETS_SERVICE_URL;
  }
  
  return API_CONFIG.GATEWAY_URL;
};

export default API_CONFIG;
```

---

## 2. Service Layer

### `src/services/axiosConfig.js`
```javascript
import axios from 'axios';
import { getBaseUrl } from '../config/api';

export const createServiceApi = (serviceName) => {
  const api = axios.create({
    baseURL: `${getBaseUrl(serviceName)}/api/${serviceName}`,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
    withCredentials: true,
  });

  api.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('authToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      console.error('API Error:', error.response?.data || error.message);
      return Promise.reject(error);
    }
  );

  return api;
};

export const assetsApi = createServiceApi('assets');
```

### `src/services/assetService.js`
```javascript
import { assetsApi } from './axiosConfig';

const assetService = {
  getAllAssets: async () => {
    const response = await assetsApi.get('/');
    return response.data;
  },

  getAssetById: async (id) => {
    const response = await assetsApi.get(`/${id}`);
    return response.data;
  },

  createAsset: async (assetData) => {
    const response = await assetsApi.post('/', assetData);
    return response.data;
  },

  updateAsset: async (id, assetData) => {
    const response = await assetsApi.put(`/${id}`, assetData);
    return response.data;
  },

  deleteAsset: async (id) => {
    await assetsApi.delete(`/${id}`);
  },

  assetExists: async (id) => {
    const response = await assetsApi.get(`/exists/${id}`);
    return response.data;
  }
};

export default assetService;
```

---

## 3. React Components

### `src/components/AssetDashboard.jsx`
```javascript
import React, { useState, useEffect } from 'react';
import AssetList from './AssetList';
import CreateAssetForm from './CreateAssetForm';
import '../styles/assets.css';

const AssetDashboard = () => {
  const [refreshKey, setRefreshKey] = useState(0);

  const handleAssetCreated = (newAsset) => {
    console.log('New asset created:', newAsset);
    // Trigger refresh of asset list
    setRefreshKey(prev => prev + 1);
  };

  return (
    <div className="asset-dashboard">
      <header className="dashboard-header">
        <h1>🏗 PetroManage - Asset Management</h1>
        <p>Centralized Oil & Gas Asset Registration & Lifecycle Management</p>
      </header>

      <div className="dashboard-content">
        <div className="section">
          <CreateAssetForm onAssetCreated={handleAssetCreated} />
        </div>

        <div className="section">
          <AssetList key={refreshKey} />
        </div>
      </div>
    </div>
  );
};

export default AssetDashboard;
```

### `src/components/AssetList.jsx`
```javascript
import React, { useState, useEffect } from 'react';
import assetService from '../services/assetService';

const AssetList = () => {
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAssets();
  }, []);

  const fetchAssets = async () => {
    try {
      setLoading(true);
      const data = await assetService.getAllAssets();
      setAssets(data);
      setError(null);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch assets');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this asset?')) {
      return;
    }

    try {
      await assetService.deleteAsset(id);
      fetchAssets(); // Refresh list
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete asset');
    }
  };

  const getStatusBadge = (status) => {
    const statusColors = {
      REGISTERED: 'badge-blue',
      OPERATIONAL: 'badge-green',
      MAINTENANCE: 'badge-yellow',
      UNDER_INSPECTION: 'badge-orange',
      DECOMMISSIONED: 'badge-red'
    };
    return statusColors[status] || 'badge-gray';
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading assets...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">❌ {error}</p>
        <button onClick={fetchAssets} className="btn-retry">
          Retry
        </button>
      </div>
    );
  }

  return (
    <div className="asset-list-container">
      <div className="list-header">
        <h2>📋 Asset Inventory</h2>
        <button onClick={fetchAssets} className="btn-refresh">
          🔄 Refresh
        </button>
      </div>

      {assets.length === 0 ? (
        <div className="empty-state">
          <p>No assets found. Create your first asset above!</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="asset-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Type</th>
                <th>Location</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {assets.map(asset => (
                <tr key={asset.assetId}>
                  <td>{asset.assetId}</td>
                  <td><strong>{asset.name}</strong></td>
                  <td>
                    <span className="type-badge">
                      {asset.type}
                    </span>
                  </td>
                  <td>{asset.location || 'N/A'}</td>
                  <td>
                    <span className={`badge ${getStatusBadge(asset.status)}`}>
                      {asset.status}
                    </span>
                  </td>
                  <td>
                    <button 
                      onClick={() => handleDelete(asset.assetId)}
                      className="btn-delete"
                      title="Delete asset"
                    >
                      🗑️ Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div className="list-footer">
        <p>Total Assets: <strong>{assets.length}</strong></p>
      </div>
    </div>
  );
};

export default AssetList;
```

### `src/components/CreateAssetForm.jsx`
```javascript
import React, { useState } from 'react';
import assetService from '../services/assetService';

const CreateAssetForm = ({ onAssetCreated }) => {
  const [formData, setFormData] = useState({
    name: '',
    type: 'RIG',
    location: ''
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(false);

    try {
      const newAsset = await assetService.createAsset(formData);
      setSuccess(true);
      setFormData({ name: '', type: 'RIG', location: '' });
      
      if (onAssetCreated) {
        onAssetCreated(newAsset);
      }

      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      if (err.response?.data?.fieldErrors) {
        const errors = Object.entries(err.response.data.fieldErrors)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join(', ');
        setError(errors);
      } else {
        setError(err.response?.data?.message || 'Failed to create asset');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <div className="create-asset-form">
      <h2>➕ Create New Asset</h2>
      
      {error && (
        <div className="alert alert-error">
          ❌ {error}
        </div>
      )}
      
      {success && (
        <div className="alert alert-success">
          ✅ Asset created successfully!
        </div>
      )}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">
            Asset Name <span className="required">*</span>
          </label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            placeholder="e.g., North Sea Rig Alpha"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="type">
            Asset Type <span className="required">*</span>
          </label>
          <select
            id="type"
            name="type"
            value={formData.type}
            onChange={handleChange}
            required
            disabled={loading}
          >
            <option value="RIG">🛢️ RIG - Drilling Platform</option>
            <option value="PIPELINE">🚰 PIPELINE - Transportation</option>
            <option value="STORAGE">🏭 STORAGE - Storage Facility</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="location">
            Location
          </label>
          <input
            type="text"
            id="location"
            name="location"
            value={formData.location}
            onChange={handleChange}
            placeholder="e.g., North Sea, Block 15/21"
            disabled={loading}
          />
        </div>

        <button type="submit" disabled={loading} className="btn-submit">
          {loading ? '⏳ Creating...' : '➕ Create Asset'}
        </button>
      </form>
    </div>
  );
};

export default CreateAssetForm;
```

---

## 4. Styling

### `src/styles/assets.css`
```css
/* Dashboard Layout */
.asset-dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 10px;
}

.dashboard-header h1 {
  margin: 0 0 10px 0;
  font-size: 2.5em;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 30px;
}

@media (max-width: 1024px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}

/* Section Styles */
.section {
  background: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

/* Form Styles */
.create-asset-form h2 {
  margin-top: 0;
  color: #333;
  border-bottom: 3px solid #667eea;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
}

.required {
  color: #e74c3c;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.form-group input:disabled,
.form-group select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

/* Button Styles */
.btn-submit {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Alert Styles */
.alert {
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-weight: 500;
}

.alert-error {
  background-color: #fee;
  color: #c33;
  border-left: 4px solid #e74c3c;
}

.alert-success {
  background-color: #efe;
  color: #2c3;
  border-left: 4px solid #27ae60;
}

/* Asset List Styles */
.asset-list-container h2 {
  margin-top: 0;
  color: #333;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 3px solid #667eea;
}

.btn-refresh {
  padding: 8px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.btn-refresh:hover {
  background: #5568d3;
}

/* Table Styles */
.table-container {
  overflow-x: auto;
}

.asset-table {
  width: 100%;
  border-collapse: collapse;
}

.asset-table thead {
  background-color: #f8f9fa;
}

.asset-table th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  color: #555;
  border-bottom: 2px solid #dee2e6;
}

.asset-table td {
  padding: 12px;
  border-bottom: 1px solid #e0e0e0;
}

.asset-table tbody tr:hover {
  background-color: #f8f9fa;
}

/* Badge Styles */
.badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.badge-blue {
  background-color: #e3f2fd;
  color: #1976d2;
}

.badge-green {
  background-color: #e8f5e9;
  color: #388e3c;
}

.badge-yellow {
  background-color: #fff9c4;
  color: #f57f17;
}

.badge-orange {
  background-color: #fff3e0;
  color: #e65100;
}

.badge-red {
  background-color: #ffebee;
  color: #c62828;
}

.type-badge {
  display: inline-block;
  padding: 4px 10px;
  background-color: #f0f0f0;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 600;
}

/* Action Buttons */
.btn-delete {
  padding: 6px 12px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
}

.btn-delete:hover {
  background-color: #c0392b;
}

/* Loading Styles */
.loading-container {
  text-align: center;
  padding: 40px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Error Styles */
.error-container {
  text-align: center;
  padding: 40px;
}

.error-message {
  color: #e74c3c;
  font-size: 16px;
  margin-bottom: 20px;
}

.btn-retry {
  padding: 10px 20px;
  background-color: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 16px;
}

/* Footer */
.list-footer {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
  text-align: right;
  color: #666;
}
```

---

## 5. Main App Integration

### `src/App.jsx`
```javascript
import React from 'react';
import AssetDashboard from './components/AssetDashboard';
import './styles/assets.css';

function App() {
  return (
    <div className="App">
      <AssetDashboard />
    </div>
  );
}

export default App;
```

---

## 6. Environment Files

### `.env.development`
```env
REACT_APP_USE_GATEWAY=false
REACT_APP_ASSETS_URL=http://localhost:8081
```

### `.env.production`
```env
REACT_APP_USE_GATEWAY=true
REACT_APP_GATEWAY_URL=http://localhost:8080
```

---

## 🚀 Quick Start

1. **Copy all files** to your React project
2. **Install dependencies**:
   ```bash
   npm install axios
   ```
3. **Create `.env.development`** file
4. **Start backend services** (Eureka, Gateway, Assets)
5. **Start React app**:
   ```bash
   npm run dev
   ```

That's it! You have a fully functional asset management interface! 🎉
