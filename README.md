# real-time-vocabulary-quiz
Real-Time Vocabulary Quiz using Spring Boot WebSocket and Nextjs

## Architecture Diagram

### Components:
- **Client Application (Frontend)**: React + WebSockets for real-time communication.
- **WebSocket Server (Backend)**: Handles quiz session management, score updates, and leaderboard calculations.
- **Database**: Stores quiz data, user details, and scores in application. I did not use any heavy database for this project. 

### Data Flow:
1. **Join Quiz**:  
   User submits quizId and username from the client to the WebSocket server.

2. **Send Question**:  
   Server broadcasts a question to all participants in the quiz session.

3. **Submit Answer**:  
   User submits an answer, which is validated and scored by the server.

4. **Update Leaderboard**:  
   Server updates scores and broadcasts the updated leaderboard to clients.

### Diagram:

![alt text](https://github.com/sheikhimtiaz/real-time-vocabulary-quiz/blob/main/ArchitecturalOverview.jpg?raw=true)

## Technology Justification

### a) React/Nextjs:
- **Reason**: Efficient and user-friendly framework for building dynamic UIs. React's state management and lifecycle hooks make it ideal for real-time updates.

### b) WebSockets:
- **Reason**: Enables low-latency, bidirectional communication required for real-time features. Native WebSocket support in modern browsers and server libraries ensures a seamless experience.

### c) Backend (Java/Spring Boot):
- **Reason**:
  - **Reason**: Offers lightweight and high-performing WebSocket implementations (Spring WebSocket).
  - **Reason**: A robust alternative for scalability and maintainability.

### d) Database (Map/H2):
- **Reason**: Excellent for flexible, schema-less data storage and rapid development.

### e) Optional Monitoring Tools:
- **Prometheus + Grafana**: Ideal for collecting and visualizing metrics.
- **Elasticsearch + Kibana**: Effective for centralized logging and debugging.


