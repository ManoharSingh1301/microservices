# Assets Module - PetroManage

## Overview

The **Assets Module** is a core microservice of the **PetroManage** platform - a centralized Oil & Gas Asset and Operations Management System. This module is responsible for **Asset Registration & Lifecycle Management**, enabling oil and gas companies to digitally register, track, and manage their critical infrastructure assets.

## Purpose

The Assets module serves as the foundational data layer for the entire PetroManage system by:

- **Centralizing asset information** for rigs, pipelines, and storage units
- **Tracking asset lifecycle** from registration through decommissioning
- **Providing asset verification** for other modules (production, maintenance, compliance)
- **Maintaining operational status** of all company assets
- **Enabling asset-based analytics** and reporting

## Key Features

### 1. Asset Registration
- Create new assets with detailed information (name, type, location)
- Automatic status initialization to `REGISTERED`
- Input validation for data integrity
- Timestamp tracking for audit trails

### 2. Asset Lifecycle Management
- Track assets through multiple lifecycle stages:
  - `REGISTERED` - Newly added asset
  - `OPERATIONAL` - Active and in use
  - `MAINTENANCE` - Under maintenance activities
  - `UNDER_INSPECTION` - Compliance/safety inspection
  - `DECOMMISSIONED` - Retired from service

### 3. Asset Types Supported
- **RIG** - Drilling rigs and platforms
- **PIPELINE** - Transportation pipelines
- **STORAGE** - Storage tanks and facilities

### 4. CRUD Operations
- **Create** new assets
- **Read** individual assets or list all assets
- **Update** asset information and status
- **Delete** assets from the system
- **Verify** asset existence for inter-service communication

## Technology Stack

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 17+
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **API Style**: RESTful
- **Validation**: Jakarta Bean Validation
- **Service Discovery**: Netflix Eureka Client (Spring Cloud 2023.0.1)
- **ORM**: Hibernate/JPA
- **Utilities**: Lombok for boilerplate reduction
- **CORS**: Spring Web CORS Filter

## Architecture

### Project Structure

```
assets/
├── src/main/java/com/example/assets/
│   ├── AssetsApplication.java            # Main Spring Boot application
│   ├── config/
│   │   └── CorsConfig.java               # CORS configuration for frontend
│   └── asset/
│       ├── controller/
│       │   └── AssetController.java      # REST API endpoints
│       ├── dto/
│       │   ├── AssetRequestDTO.java      # Input data transfer object
│       │   └── AssetResponseDTO.java     # Output data transfer object
│       ├── entity/
│       │   └── Asset.java                # JPA entity (database model)
│       ├── enums/
│       │   ├── AssetStatus.java          # Lifecycle status enum
│       │   └── AssetType.java            # Asset type enum
│       ├── exception/
│       │   ├── AssetNotFoundException.java         # Custom exception
│       │   ├── AssetAlreadyExistsException.java   # Custom exception
│       │   ├── InvalidAssetDataException.java     # Custom exception
│       │   ├── ErrorResponse.java                  # Error response model
│       │   └── GlobalExceptionHandler.java        # Centralized error handling
│       ├── repository/
│       │   └── AssetRepository.java      # Data access layer (JPA)
│       └── service/
│           ├── AssetService.java         # Service interface
│           └── impl/
│               └── AssetServiceImpl.java # Business logic implementation
├── src/main/resources/
│   └── application.properties            # Configuration
├── pom.xml                               # Maven dependencies
└── README.md                             # This file
```

### Module Flow Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React)                         │
│                    http://localhost:5173                         │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTP/REST (CORS enabled)
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Optional)                        │
│                    Port: 8080 (if used)                          │
└────────────────────────┬────────────────────────────────────────┘
                         │ Routes to microservices
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                    EUREKA DISCOVERY SERVER                       │
│                       Port: 8761                                 │
│              (Service Registry & Discovery)                      │
└────────────────────────┬────────────────────────────────────────┘
                         │ Service Registration
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                   ASSETS MICROSERVICE                            │
│                       Port: 8081                                 │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  1. CORS Filter (CorsConfig)                            │   │
│  │     ↓ Validates origin & headers                        │   │
│  │  2. Controller Layer (AssetController)                  │   │
│  │     ↓ @Valid validates DTO                              │   │
│  │  3. Service Layer (AssetServiceImpl)                    │   │
│  │     ↓ Business logic & validation                       │   │
│  │  4. Repository Layer (AssetRepository)                  │   │
│  │     ↓ JPA/Hibernate                                     │   │
│  │  5. Database (MySQL)                                    │   │
│  │                                                           │   │
│  │  Exception Flow:                                         │   │
│  │  Any Exception → GlobalExceptionHandler → JSON Error    │   │
│  └─────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ Database Connection
                         ↓
┌─────────────────────────────────────────────────────────────────┐
│                    MySQL Database                                │
│                  Database: assets_db                             │
│                    Port: 3306                                    │
└─────────────────────────────────────────────────────────────────┘
```

### Design Patterns

- **Layered Architecture**: Controller → Service → Repository → Database
- **DTO Pattern**: Separation of internal entities and API contracts
- **Repository Pattern**: Abstraction over data access using Spring Data JPA
- **Dependency Injection**: Constructor-based injection for testability
- **Exception Handling Pattern**: Global exception handler with custom exceptions
- **Service Discovery Pattern**: Eureka client for microservice registration

### Request-Response Flow

```
1. Frontend Request
   ↓
2. CORS Filter (validates origin, headers, methods)
   ↓
3. Controller (validates @Valid DTO, maps to service)
   ↓
4. Service Layer (business logic, validation, exception handling)
   ↓
5. Repository (JPA queries, database operations)
   ↓
6. Database (MySQL - CRUD operations)
   ↓
7. Response flows back up the chain
   ↓
8. Controller returns DTO or Exception Handler returns error
   ↓
9. JSON Response to Frontend
```

## API Endpoints

### Base URL
```
http://localhost:<port>/api/assets
```

### Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/assets` | Create new asset | AssetRequestDTO | AssetResponseDTO |
| GET | `/api/assets` | Get all assets | - | List<AssetResponseDTO> |
| GET | `/api/assets/{id}` | Get asset by ID | - | AssetResponseDTO |
| PUT | `/api/assets/{id}` | Update asset | AssetRequestDTO | AssetResponseDTO |
| DELETE | `/api/assets/{id}` | Delete asset | - | void |
| GET | `/api/assets/exists/{id}` | Check if asset exists | - | boolean |

### Request/Response Examples

#### Create Asset
**Request:**
```json
POST /api/assets
{
  "name": "North Sea Rig Alpha",
  "type": "RIG",
  "location": "North Sea, Block 15/21",
  "status": null
}
```

**Response:**
```json
{
  "assetId": 1,
  "name": "North Sea Rig Alpha",
  "type": "RIG",
  "location": "North Sea, Block 15/21",
  "status": "REGISTERED"
}
```

#### Update Asset Status
**Request:**
```json
PUT /api/assets/1
{
  "name": "North Sea Rig Alpha",
  "type": "RIG",
  "location": "North Sea, Block 15/21",
  "status": "OPERATIONAL"
}
```

**Response:**
```json
{
  "assetId": 1,
  "name": "North Sea Rig Alpha",
  "type": "RIG",
  "location": "North Sea, Block 15/21",
  "status": "OPERATIONAL"
}
```

## Data Model

### Asset Entity

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| assetId | Long | Primary key | Auto-generated |
| name | String | Asset name | Not blank |
| type | AssetType | Asset category | Not null, ENUM |
| location | String | Physical location | Optional |
| status | AssetStatus | Lifecycle status | ENUM, default: REGISTERED |
| createdAt | LocalDateTime | Creation timestamp | Auto-set |
| updatedAt | LocalDateTime | Last update timestamp | Auto-updated |

### Asset Enums

**AssetType:**
- `RIG`
- `PIPELINE`
- `STORAGE`

**AssetStatus:**
- `REGISTERED`
- `OPERATIONAL`
- `MAINTENANCE`
- `UNDER_INSPECTION`
- `DECOMMISSIONED`

## Configuration

### Application Properties

```properties
# Server configuration
server.port=8081

# Application Name (for Eureka service registration)
spring.application.name=ASSETS-SERVICE

# Eureka Client Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/assets_db
spring.datasource.username=root
spring.datasource.password=admin

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### CORS Configuration

The module uses a centralized `CorsConfig` class for handling Cross-Origin Resource Sharing:

**Location**: `com.example.assets.config.CorsConfig`

**Features**:
- ✅ Allows credentials (cookies, authorization headers)
- ✅ Configurable allowed origins (dev and production)
- ✅ Supports all REST methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
- ✅ Allows common headers (Authorization, Content-Type, etc.)
- ✅ Preflight request caching (1 hour)

**Default Allowed Origins**:
- `http://localhost:5173` - Vite/React dev server
- `http://localhost:3000` - React dev server (alternative)
- `http://localhost:4200` - Angular dev server

**To add production origins**, update `CorsConfig.java`:
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "https://your-production-domain.com"
));
```

## Setup & Installation

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git
- Eureka Discovery Server (running on port 8761)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/ManoharSingh1301/microservices.git
   cd microservices/assets
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE assets_db;
   ```

3. **Configure database credentials**
   - Open `src/main/resources/application.properties`
   - Update database URL, username, and password if needed

4. **Ensure Eureka Server is running**
   ```bash
   # Start Eureka Discovery Server first
   cd ../eureka-discovery-space
   ./mvnw spring-boot:run
   ```

5. **Build the project**
   ```bash
   # From assets directory
   ./mvnw clean install
   ```

6. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

7. **Verify the service**
   ```bash
   # Check if service is running
   curl http://localhost:8081/api/assets
   
   # Check Eureka registration
   # Open browser: http://localhost:8761
   # You should see ASSETS-SERVICE registered
   ```

### Troubleshooting

**Issue**: Port 8081 already in use
```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Change port in application.properties if needed
server.port=8082
```

**Issue**: Cannot connect to MySQL
- Verify MySQL is running
- Check credentials in application.properties
- Ensure database `assets_db` exists

**Issue**: Cannot register with Eureka
- Ensure Eureka server is running on port 8761
- Check `eureka.client.service-url.defaultZone` in properties

## Testing

### Manual API Testing with cURL

**Get All Assets**:
```bash
curl -X GET http://localhost:8081/api/assets
```

**Get Asset by ID**:
```bash
curl -X GET http://localhost:8081/api/assets/1
```

**Create Asset**:
```bash
curl -X POST http://localhost:8081/api/assets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "North Sea Rig Alpha",
    "type": "RIG",
    "location": "North Sea, Block 15/21"
  }'
```

**Update Asset**:
```bash
curl -X PUT http://localhost:8081/api/assets/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "North Sea Rig Alpha - Updated",
    "type": "RIG",
    "location": "North Sea, Block 15/21",
    "status": "OPERATIONAL"
  }'
```

**Delete Asset**:
```bash
curl -X DELETE http://localhost:8081/api/assets/1
```

**Check Asset Exists**:
```bash
curl -X GET http://localhost:8081/api/assets/exists/1
```

### Testing with Postman

1. **Import Collection**: Create a new collection "Assets API"
2. **Set Base URL**: `http://localhost:8081/api/assets`
3. **Add Environment Variables**:
   - `baseUrl`: `http://localhost:8081`
   - `assetId`: `1` (for dynamic testing)

**Sample Requests**:
- GET: `{{baseUrl}}/api/assets`
- POST: `{{baseUrl}}/api/assets` with JSON body
- PUT: `{{baseUrl}}/api/assets/{{assetId}}`
- DELETE: `{{baseUrl}}/api/assets/{{assetId}}`

### Unit Testing

Run all tests:
```bash
./mvnw test
```

Run with coverage:
```bash
./mvnw test jacoco:report
```

### Test coverage includes:
- ✅ Unit tests for service layer (`AssetServiceImplTest`)
- ✅ Integration tests for repository layer
- ✅ API endpoint tests for controller layer
- ✅ Exception handling tests
- ✅ Validation tests for DTOs

## Integration with Other Modules

The Assets module provides essential data for:

### 1. Production Module
- **Validates asset existence** before production planning
- **Provides asset status** for production feasibility checks
- **Links production records** to specific assets
- **Endpoint**: `/api/assets/exists/{id}` used for validation

### 2. Maintenance Module
- **References assets** for work order creation
- **Updates asset status** to `MAINTENANCE` during activities
- **Tracks maintenance history** per asset
- **Status Flow**: `OPERATIONAL` → `MAINTENANCE` → `OPERATIONAL`

### 3. Compliance Module
- **Provides asset data** for compliance reports
- **Tracks inspection status** via `UNDER_INSPECTION` status
- **Links safety incidents** to specific assets
- **Audit Trail**: Uses `createdAt` and `updatedAt` timestamps

### 4. Analytics Module
- **Supplies asset data** for operational dashboards
- **Enables asset utilization** analysis
- **Supports lifecycle reporting** (status distribution)
- **Data aggregation** for business intelligence

### Inter-Service Communication

**Service Discovery via Eureka**:
```java
// Other microservices can call Assets service using:
@FeignClient(name = "ASSETS-SERVICE")
public interface AssetClient {
    @GetMapping("/api/assets/{id}")
    AssetResponseDTO getAsset(@PathVariable Long id);
    
    @GetMapping("/api/assets/exists/{id}")
    Boolean assetExists(@PathVariable Long id);
}
```

**REST Template Alternative**:
```java
RestTemplate restTemplate = new RestTemplate();
String url = "http://ASSETS-SERVICE/api/assets/" + id;
AssetResponseDTO asset = restTemplate.getForObject(url, AssetResponseDTO.class);
```

## Error Handling

The module handles common scenarios:
- **404 Not Found**: Asset ID doesn't exist (`AssetNotFoundException`)
- **400 Bad Request**: Invalid input data or validation errors
- **409 Conflict**: Asset already exists (`AssetAlreadyExistsException`)
- **500 Internal Server Error**: Database or system errors

### Error Response Format

All errors return a standardized JSON response:

```json
{
  "timestamp": "2026-02-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Asset not found with id: 123",
  "path": "/api/assets/123"
}
```

### Validation Errors

Field validation errors include detailed field-level information:

```json
{
  "timestamp": "2026-02-10T14:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "fieldErrors": {
    "name": "must not be blank",
    "type": "must not be null"
  },
  "path": "/api/assets"
}
```

## Frontend Integration

### Connecting React Frontend to Assets Module

The Assets module is configured to accept requests from React frontend via CORS.

#### Option 1: Direct Connection (Development)

**Frontend Configuration**: No special configuration needed! The backend already allows your frontend origin.

**React/Axios Example**:

```javascript
// src/services/assetService.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api/assets';

const assetService = {
  // Get all assets
  getAllAssets: async () => {
    try {
      const response = await axios.get(API_BASE_URL);
      return response.data;
    } catch (error) {
      console.error('Error fetching assets:', error);
      throw error;
    }
  },

  // Get asset by ID
  getAssetById: async (id) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching asset:', error);
      throw error;
    }
  },

  // Create new asset
  createAsset: async (assetData) => {
    try {
      const response = await axios.post(API_BASE_URL, assetData);
      return response.data;
    } catch (error) {
      console.error('Error creating asset:', error);
      throw error;
    }
  },

  // Update asset
  updateAsset: async (id, assetData) => {
    try {
      const response = await axios.put(`${API_BASE_URL}/${id}`, assetData);
      return response.data;
    } catch (error) {
      console.error('Error updating asset:', error);
      throw error;
    }
  },

  // Delete asset
  deleteAsset: async (id) => {
    try {
      await axios.delete(`${API_BASE_URL}/${id}`);
    } catch (error) {
      console.error('Error deleting asset:', error);
      throw error;
    }
  },

  // Check if asset exists
  assetExists: async (id) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/exists/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error checking asset existence:', error);
      throw error;
    }
  }
};

export default assetService;
```

**React Component Example**:

```javascript
// src/components/AssetList.jsx
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

  if (loading) return <div>Loading assets...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h2>Assets</h2>
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
          {assets.map(asset => (
            <tr key={asset.assetId}>
              <td>{asset.assetId}</td>
              <td>{asset.name}</td>
              <td>{asset.type}</td>
              <td>{asset.location}</td>
              <td>{asset.status}</td>
              <td>
                <button onClick={() => handleDelete(asset.assetId)}>
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AssetList;
```

**Create Asset Form Example**:

```javascript
// src/components/CreateAssetForm.jsx
import React, { useState } from 'react';
import assetService from '../services/assetService';

const CreateAssetForm = ({ onAssetCreated }) => {
  const [formData, setFormData] = useState({
    name: '',
    type: 'RIG',
    location: '',
    status: null
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const newAsset = await assetService.createAsset(formData);
      alert('Asset created successfully!');
      setFormData({ name: '', type: 'RIG', location: '', status: null });
      if (onAssetCreated) onAssetCreated(newAsset);
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

  return (
    <form onSubmit={handleSubmit}>
      <h2>Create New Asset</h2>
      
      {error && <div className="error">{error}</div>}
      
      <div>
        <label>Name:</label>
        <input
          type="text"
          value={formData.name}
          onChange={(e) => setFormData({...formData, name: e.target.value})}
          required
        />
      </div>

      <div>
        <label>Type:</label>
        <select
          value={formData.type}
          onChange={(e) => setFormData({...formData, type: e.target.value})}
        >
          <option value="RIG">RIG</option>
          <option value="PIPELINE">PIPELINE</option>
          <option value="STORAGE">STORAGE</option>
        </select>
      </div>

      <div>
        <label>Location:</label>
        <input
          type="text"
          value={formData.location}
          onChange={(e) => setFormData({...formData, location: e.target.value})}
        />
      </div>

      <button type="submit" disabled={loading}>
        {loading ? 'Creating...' : 'Create Asset'}
      </button>
    </form>
  );
};

export default CreateAssetForm;
```

#### Option 2: Via API Gateway (Production Recommended)

When using API Gateway, frontend connects to a single gateway URL:

**Frontend Configuration**:
```javascript
const API_BASE_URL = 'http://localhost:8080/assets-service/api/assets';
// Gateway routes /assets-service/* to Assets microservice
```

**API Gateway routes**:
- `/assets-service/**` → `http://localhost:8081/**`

#### Option 3: Environment-Based Configuration

```javascript
// src/config/api.js
const getApiBaseUrl = () => {
  if (process.env.NODE_ENV === 'production') {
    return 'https://api.yourcompany.com/assets-service/api/assets';
  } else if (process.env.REACT_APP_USE_GATEWAY === 'true') {
    return 'http://localhost:8080/assets-service/api/assets';
  } else {
    return 'http://localhost:8081/api/assets';
  }
};

export const API_BASE_URL = getApiBaseUrl();
```

### CORS Configuration Details

**Current Setup**:
- ✅ Centralized CORS configuration in `CorsConfig.java`
- ✅ Allows multiple origins (development servers)
- ✅ Supports credentials and authorization headers
- ✅ Handles preflight requests (OPTIONS)

**Why Global CORS Config Instead of @CrossOrigin**:
1. **Centralized Management**: Single place to manage all CORS rules
2. **Consistent Policy**: Same rules apply to all endpoints
3. **Production Ready**: Easy to add/remove origins for different environments
4. **More Control**: Fine-grained control over methods, headers, credentials

**Adding New Origins**:

Edit `CorsConfig.java`:
```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "http://localhost:3000",
    "https://your-production-frontend.com",  // Add production URL
    "https://staging.yourcompany.com"         // Add staging URL
));
```

### Testing Frontend Connection

**Test CORS from Browser Console**:
```javascript
fetch('http://localhost:8081/api/assets')
  .then(res => res.json())
  .then(data => console.log(data))
  .catch(err => console.error(err));
```

**Expected Response**: Array of assets (empty if no data)

**CORS Error Example**:
```
Access to fetch at 'http://localhost:8081/api/assets' from origin 
'http://localhost:5173' has been blocked by CORS policy
```

**Solution**: Add your frontend origin to `CorsConfig.java`

## Future Enhancements

### Phase 1 - Core Features
- [ ] Asset health scoring algorithm
- [ ] Complete audit log (track all changes)
- [ ] Soft delete implementation (retain historical data)
- [ ] Asset search with filters (by type, status, location)
- [ ] Pagination support for large datasets

### Phase 2 - Advanced Features
- [ ] Bulk import/export functionality (CSV/Excel)
- [ ] Asset relationships (parent-child hierarchy)
- [ ] Document attachment support (PDFs, images)
- [ ] Asset transfer between locations
- [ ] Asset reservation system

### Phase 3 - Integration & Analytics
- [ ] Real-time notifications on status changes (WebSocket)
- [ ] Asset depreciation tracking
- [ ] Asset performance metrics
- [ ] GraphQL API support
- [ ] Advanced analytics dashboard

### Phase 4 - Security & Optimization
- [ ] Role-based access control (RBAC)
- [ ] JWT authentication integration
- [ ] API rate limiting
- [ ] Caching with Redis
- [ ] Database query optimization

## Performance Considerations

### Current Optimizations
- ✅ Constructor-based dependency injection (faster than field injection)
- ✅ JPA query optimization with `findAll()` and `findById()`
- ✅ Lombok reduces boilerplate and compilation time
- ✅ Connection pooling (HikariCP - default in Spring Boot)

### Recommended for Production
- **Database Indexing**: Index on `assetId`, `type`, `status` columns
- **Caching**: Add Redis for frequently accessed assets
- **Pagination**: Implement for `/api/assets` endpoint
- **Connection Pool Tuning**: Adjust based on load
- **Monitoring**: Add Spring Boot Actuator for health checks

### Database Indexes
```sql
CREATE INDEX idx_asset_type ON assets(type);
CREATE INDEX idx_asset_status ON assets(status);
CREATE INDEX idx_asset_location ON assets(location);
```

## Monitoring & Health Checks

### Add Spring Boot Actuator

**pom.xml**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**application.properties**:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

**Endpoints**:
- Health: `http://localhost:8081/actuator/health`
- Metrics: `http://localhost:8081/actuator/metrics`
- Info: `http://localhost:8081/actuator/info`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/asset-enhancement`)
3. Commit your changes (`git commit -m 'Add asset feature'`)
4. Push to the branch (`git push origin feature/asset-enhancement`)
5. Open a Pull Request

## License

This project is part of the PetroManage system and follows the project's licensing terms.

## Contact & Support

For questions or support regarding the Assets module:
- **Repository**: https://github.com/ManoharSingh1301/microservices
- **Project**: PetroManage - Oil & Gas Asset Management System
- **Module**: Asset Registration & Lifecycle Management

---

**Note**: This module is designed for structured manual/batch input and does not integrate with IoT sensors or real-time monitoring devices. All data is managed through web-based interfaces and API calls.
