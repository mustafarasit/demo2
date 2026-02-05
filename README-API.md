Demo API - quick guide

Endpoints:

- POST /api/auth/register  -- register a new user (body: username,password,email)
- POST /api/auth/login     -- login, returns token {"token":"..."}
- GET  /api/users/me       -- get current user (use Authorization header)
- PUT  /api/users/me       -- update current user
- GET  /api/users          -- list users (admin only)

Example curl:

```bash
# register
curl -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d '{"username":"user1","password":"pass","email":"u1@example.com"}'

# login
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"user1","password":"pass"}'

# use token for protected endpoints
# assuming TOKEN holds the returned token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/users/me
```

Import `postman_collection.json` into Postman to try requests interactively.
