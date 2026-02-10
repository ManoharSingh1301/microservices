# Frontend Integration Guide - PetroManage Microservices

## 🎯 CORS Configuration Strategy

### Current Setup: **Hybrid Approach** (Best of Both Worlds)

You have TWO options for frontend to connect:

#### Option 1: Via API Gateway (✅ Recommended for Production)
```
Frontend (React) → API Gateway (Port 8080) → Microservices
```
- **URL**: `http://localhost:8080/api/assets`
- **CORS**: Handled by API Gateway
- **Benefits**: Centralized routing, security, load balancing

#### Option 2: Direct Access (✅ Good for Development)
```
Frontend (React) → Direct to Service (Port 8081)
```
- **URL**: `http://localhost:8081/api/assets`
- **CORS**: Handled by each microservice
- **Benefits**: Faster development, easier debugging

---

## 🔧 Current CORS Configuration

### 1. API Gateway CORS (Port 8080)
**File**: `apigateway/src/main/java/com/example/apigateway/config/CorsConfig.java`

✅ **Configured to allow**:
- Origins: `http://localhost:5173`, `http://localhost:3000`, `http://localhost:4200`
- Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Headers: All common headers including Authorization
- Credentials: Enabled

### 2. Assets Service CORS (Port 8081)
**File**: `assets/src/main/java/com/example/assets/config/CorsConfig.java`

✅ **Configured to allow**:
- Same as Gateway configuration
- Allows direct access during development

---

## 📱 Frontend Configuration

### Environment-Based API Configuration

Create this file in your React project:

**`src/config/api.js`**:
```javascript
// API Configuration based on environment
const API_CONFIG = {
  // Use Gateway in production, direct access in development
  USE_GATEWAY: process.env.REACT_APP_USE_GATEWAY === 'true' || process.env.NODE_ENV === 'production',
  
  // Base URLs
  GATEWAY_URL: process.env.REACT_APP_GATEWAY_URL || 'http://localhost:8080',
  ASSETS_SERVICE_URL: process.env.REACT_APP_ASSETS_URL || 'http://localhost:8081',
  MAINTENANCE_SERVICE_URL: process.env.REACT_APP_MAINTENANCE_URL || 'http://localhost:8082',
  COMPLIANCE_SERVICE_URL: process.env.REACT_APP_COMPLIANCE_URL || 'http://localhost:8083',
  PRODUCTION_SERVICE_URL: process.env.REACT_APP_PRODUCTION_URL || 'http://localhost:8084',
};

// Get base URL based on strategy
export const getBaseUrl = (serviceName = '') => {
  if (API_CONFIG.USE_GATEWAY) {
    return API_CONFIG.GATEWAY_URL;
  }
  
  // Direct service access
  switch (serviceName.toLowerCase()) {
    case 'assets':
      return API_CONFIG.ASSETS_SERVICE_URL;
    case 'maintenance':
      return API_CONFIG.MAINTENANCE_SERVICE_URL;
    case 'compliance':
      return API_CONFIG.COMPLIANCE_SERVICE_URL;
    case 'production':
      return API_CONFIG.PRODUCTION_SERVICE_URL;
    default:
      return API_CONFIG.GATEWAY_URL;
  }
};

export default API_CONFIG;
```

### Environment Variables

**`.env.development`** (Direct Access):
```env
REACT_APP_USE_GATEWAY=false
REACT_APP_ASSETS_URL=http://localhost:8081
REACT_APP_MAINTENANCE_URL=http://localhost:8082
REACT_APP_COMPLIANCE_URL=http://localhost:8083
REACT_APP_PRODUCTION_URL=http://localhost:8084
```

**`.env.production`** (Via Gateway):
```env
REACT_APP_USE_GATEWAY=true
REACT_APP_GATEWAY_URL=https://api.yourcompany.com
```

---

## 🔌 API Service Layer

### Axios Configuration

**`src/services/axiosConfig.js`**:
```javascript
import axios from 'axios';
import { getBaseUrl } from '../config/api';

// Create axios instances for each service
export const createServiceApi = (serviceName) => {
  const api = axios.create({
    baseURL: `${getBaseUrl(serviceName)}/api/${serviceName}`,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
    withCredentials: true, // Important for CORS with credentials
  });

  // Request interceptor (add auth token)
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

  // Response interceptor (handle errors globally)
  api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        // Redirect to login
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );

  return api;
};

// Create service-specific instances
export const assetsApi = createServiceApi('assets');
export const maintenanceApi = createServiceApi('maintenance');
export const complianceApi = createServiceApi('compliance');
export const productionApi = createServiceApi('production');
```

### Assets Service API

**`src/services/assetService.js`**:
```javascript
import { assetsApi } from './axiosConfig';

const assetService = {
  // Get all assets
  getAllAssets: async () => {
    try {
      const response = await assetsApi.get('/');
      return response.data;
    } catch (error) {
      console.error('Error fetching assets:', error);
      throw error;
    }
  },

  // Get asset by ID
  getAssetById: async (id) => {
    try {
      const response = await assetsApi.get(`/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching asset:', error);
      throw error;
    }
  },

  // Create new asset
  createAsset: async (assetData) => {
    try {
      const response = await assetsApi.post('/', assetData);
      return response.data;
    } catch (error) {
      console.error('Error creating asset:', error);
      throw error;
    }
  },

  // Update asset
  updateAsset: async (id, assetData) => {
    try {
      const response = await assetsApi.put(`/${id}`, assetData);
      return response.data;
    } catch (error) {
      console.error('Error updating asset:', error);
      throw error;
    }
  },

  // Delete asset
  deleteAsset: async (id) => {
    try {
      await assetsApi.delete(`/${id}`);
    } catch (error) {
      console.error('Error deleting asset:', error);
      throw error;
    }
  },

  // Check if asset exists
  assetExists: async (id) => {
    try {
      const response = await assetsApi.get(`/exists/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error checking asset existence:', error);
      throw error;
    }
  }
};

export default assetService;
```

---

## 🎨 React Components

### Asset List Component

**`src/components/AssetList.jsx`**:
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
    if (window.confirm('Are you sure you want to delete this asset?')) {
      try {
        await assetService.deleteAsset(id);
        fetchAssets(); // Refresh list
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete asset');
      }
    }
  };

  if (loading) return <div className="loading">Loading assets...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="asset-list">
      <h2>Assets Management</h2>
      <table>
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
          {assets.length === 0 ? (
            <tr>
              <td colSpan="6">No assets found</td>
            </tr>
          ) : (
            assets.map(asset => (
              <tr key={asset.assetId}>
                <td>{asset.assetId}</td>
                <td>{asset.name}</td>
                <td>{asset.type}</td>
                <td>{asset.location}</td>
                <td>
                  <span className={`status ${asset.status.toLowerCase()}`}>
                    {asset.status}
                  </span>
                </td>
                <td>
                  <button onClick={() => handleDelete(asset.assetId)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AssetList;
```

### Create Asset Form

**`src/components/CreateAssetForm.jsx`**:
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
      
      // Callback to parent component
      if (onAssetCreated) {
        onAssetCreated(newAsset);
      }

      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      // Handle validation errors
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
      <h2>Create New Asset</h2>
      
      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">Asset created successfully!</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Asset Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            placeholder="Enter asset name"
          />
        </div>

        <div className="form-group">
          <label htmlFor="type">Asset Type *</label>
          <select
            id="type"
            name="type"
            value={formData.type}
            onChange={handleChange}
            required
          >
            <option value="RIG">RIG</option>
            <option value="PIPELINE">PIPELINE</option>
            <option value="STORAGE">STORAGE</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="location">Location</label>
          <input
            type="text"
            id="location"
            name="location"
            value={formData.location}
            onChange={handleChange}
            placeholder="Enter location"
          />
        </div>

        <button type="submit" disabled={loading}>
          {loading ? 'Creating...' : 'Create Asset'}
        </button>
      </form>
    </div>
  );
};

export default CreateAssetForm;
```

---

## 🧪 Testing CORS

### Test from Browser Console

```javascript
// Test direct access (Port 8081)
fetch('http://localhost:8081/api/assets')
  .then(res => res.json())
  .then(data => console.log('Direct access works:', data))
  .catch(err => console.error('Direct access failed:', err));

// Test via Gateway (Port 8080)
fetch('http://localhost:8080/api/assets')
  .then(res => res.json())
  .then(data => console.log('Gateway access works:', data))
  .catch(err => console.error('Gateway access failed:', err));
```

### Expected Behavior

✅ **Both should work** without CORS errors!

If you see CORS error:
```
Access to fetch at 'http://localhost:8080/api/assets' from origin 
'http://localhost:5173' has been blocked by CORS policy
```

**Solution**: Make sure Gateway CorsConfig.java has your frontend origin.

---

## 🚀 Recommended Workflow

### Development Phase
```javascript
// .env.development
REACT_APP_USE_GATEWAY=false
REACT_APP_ASSETS_URL=http://localhost:8081
```
- Direct access to services
- Faster development
- Easier debugging

### Production Phase
```javascript
// .env.production
REACT_APP_USE_GATEWAY=true
REACT_APP_GATEWAY_URL=https://api.yourcompany.com
```
- All requests via Gateway
- Centralized security
- Load balancing

---

## ✅ Quick Checklist

- [x] API Gateway has CorsConfig.java
- [x] Assets Service has CorsConfig.java (for direct access)
- [x] Frontend has environment-based configuration
- [x] Axios is configured with withCredentials
- [x] Service APIs are properly structured
- [x] Error handling is implemented

---

## 🆘 Troubleshooting

**Problem**: CORS error when accessing via Gateway
- **Solution**: Check CorsConfig.java in API Gateway has your origin

**Problem**: 404 Not Found via Gateway
- **Solution**: Check application.properties routes configuration

**Problem**: Direct access works, Gateway doesn't
- **Solution**: Ensure Gateway is running and registered with Eureka

**Problem**: Cannot set Authorization header
- **Solution**: Ensure `withCredentials: true` in axios config

---

## 📚 Summary

**Your current setup allows BOTH approaches**:

1. ✅ **Via Gateway** (`http://localhost:8080/api/assets`) - Production ready
2. ✅ **Direct Access** (`http://localhost:8081/api/assets`) - Development friendly

**The frontend can choose which approach to use** based on environment variables!

This gives you maximum flexibility during development and production-ready architecture! 🎉
