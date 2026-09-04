# Intelligent Issue Router

An AI-powered backend system that automatically analyzes software issues, determines their priority and category, recommends the appropriate engineering team, and allows users to query their issue data using natural language.

## Features

- Create, retrieve, update, and delete software issues
- Store issue data in Amazon DynamoDB
- Analyze issues using OpenAI
- Automatically generate:
    - Issue category
    - Priority
    - Recommended engineering team
    - AI-generated summary
- Ask natural-language questions about stored issues
- REST API for interacting with the system
- Global exception handling for missing issues

## Architecture

```text
                    Client / Postman
                           |
                           v
                  Spring Boot REST API
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
        IssueService    AiService    Exception Handler
             |             |
             v             v
         DynamoDB       OpenAI API
             |
             v
        Stored Issues
```

## How AI Analysis Works

When an issue is submitted, the backend can send the issue title and description to OpenAI.

```text
Issue
  |
  v
Spring Boot
  |
  v
AiService
  |
  v
OpenAI API
  |
  v
Structured AI Analysis
  |
  +--> Category
  +--> Priority
  +--> Recommended Team
  +--> Summary
  |
  v
DynamoDB
```

## Natural-Language Queries

The system can also answer questions about the stored issues.

For example:

> Which issues have the highest priority?

The backend retrieves the stored issues and provides their information to the AI along with the user's question.

```text
User Question
      |
      v
Spring Boot
      |
      v
Retrieve Issues from DynamoDB
      |
      v
Send Issue Data + Question to OpenAI
      |
      v
Natural-Language Response
```

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Backend programming language |
| Spring Boot | REST API and application framework |
| Amazon DynamoDB | Issue data storage |
| OpenAI API | AI analysis and natural-language queries |
| Maven | Dependency management and build |
| Postman | API testing |
| Git/GitHub | Version control and documentation |

## API Endpoints

### Create Issue

```http
POST /issues
```

Example request:

```json
{
  "title": "Users cannot log in",
  "description": "Users receive a 500 error when attempting to log in.",
  "status": "OPEN"
}
```

### Get All Issues

```http
GET /issues
```

### Get Issue

```http
GET /issues/{id}
```

### Analyze Issue

```http
POST /issues/{id}/analyze
```

This sends the issue to OpenAI and stores the resulting AI analysis.

### Ask an AI Question

```http
POST /issues/ai/query
```

Example:

```json
{
  "question": "Which issues have the highest priority?"
}
```

### Resolve Issue

```http
PUT /issues/{id}/resolve
```

Example:

```json
{
  "resolution": "Fixed the database connection configuration."
}
```

### Delete Issue

```http
DELETE /issues/{id}
```

## Example AI Analysis

For an issue describing a login failure caused by database connection errors, the system may return:

```json
{
  "category": "Authentication",
  "priority": "High",
  "recommendedTeam": "Backend Development",
  "summary": "Users are unable to log in because of database connection failures in the authentication service."
}
```

## Project Structure

```text
src/main/java/com/example/intelligent_issue_router/
│
├── controller/
│   └── IssueController.java
│
├── service/
│   ├── IssueService.java
│   └── AiService.java
│
├── model/
│   └── Issue.java
│
├── dto/
│   ├── AiAnalysis.java
│   ├── AIQueryRequest.java
│   └── ResolveIssueRequest.java
│
├── config/
│   └── OpenAIConfig.java
│
└── exception/
    ├── IssueNotFoundException.java
    └── GlobalExceptionHandler.java
```

## Future Improvements

Potential extensions include:

- Semantic search and RAG
- Vector database integration
- S3 storage for issue attachments and logs
- AWS Lambda for asynchronous processing
- Authentication and authorization
- Web-based dashboard
- Issue analytics
- Automated team routing
- Deployment to AWS

## Learning Goals

This project was built to explore:

- REST API development with Spring Boot
- AWS DynamoDB integration
- Integrating LLM APIs into backend applications
- Structured AI responses
- Natural-language interfaces
- Backend architecture
- Cloud-based application development
- Error handling and API design