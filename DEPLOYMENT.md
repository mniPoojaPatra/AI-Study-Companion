# Deployment

This app is a Java 17 Tomcat/Jakarta Servlet application packaged as a WAR. The recommended deployment target is Railway because it can run the Dockerfile and provision a MySQL service in the same project.

## Required Environment Variables

Set these on the hosting platform:

```text
PORT=8080
DB_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
GEMINI_API_KEY=your_gemini_api_key
```

The Railway MySQL service should be named `MySQL` for the reference variables above to resolve exactly as written.

## Build Locally

```powershell
.\apache-maven-3.9.15\bin\mvn.cmd -q package
```

The WAR is created at:

```text
target/ai-study-companion.war
```

## Docker Deployment

Build and run the container:

```powershell
docker build -t ai-study-companion .
docker run --rm -p 8080:8080 --env-file .env ai-study-companion
```

Open:

```text
http://localhost:8080
```

## Railway Deployment

1. Create a Railway project.
2. Add a MySQL service to the project.
3. Deploy this app from the repository root. Railway will detect `Dockerfile` and `railway.json`.
4. On the app service, set the required variables shown above.
5. Generate a public domain for the app service.

The app exposes `/health` for Railway health checks and initializes the required MySQL tables on startup from `src/main/resources/schema.sql`.

## Live Production URL

```text
https://ai-study-companion-production-db52.up.railway.app
```
