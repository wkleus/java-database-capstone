This Spring Boot application follows a hybrid architecture that combines traditional MVC with REST‑based interactions. The Admin and Doctor dashboards are rendered using Thymeleaf templates, providing server‑side HTML views, while the rest of the system communicates through RESTful APIs designed for modularity and external integration. The application connects to two separate databases: MySQL, which stores structured data such as patients, doctors, appointments, and admin records, and MongoDB, which manages prescription documents for more flexible, schema‑less storage.

All incoming requests—whether from MVC controllers or REST endpoints—flow through a unified service layer that encapsulates business logic and ensures consistent behavior across modules. This service layer delegates persistence operations to repository interfaces. MySQL entities are modeled using JPA, enabling ORM‑based data handling, while MongoDB interactions rely on document‑oriented models. This separation of concerns keeps the architecture clean, scalable, and easy to maintain as the application grows.

1. A user initiates an action—either by navigating to a Thymeleaf‑based dashboard (Admin/Doctor) or by calling a REST endpoint from the client side.

2. The request is routed to the appropriate controller: MVC controllers handle view‑based pages, while REST controllers handle API calls.

3. The controller validates the request and forwards it to the corresponding service method in the shared service layer.

4. The service layer processes business logic, coordinates any required operations, and determines which data sources or repositories need to be accessed.

5. Depending on the type of data, the service layer interacts with either JPA repositories (for MySQL entities) or MongoDB repositories (for prescription documents).

6. The repositories perform the necessary database operations—queries, inserts, updates, or deletions—and return the results back to the service layer.

7. The service layer sends the processed response back to the controller, which then either renders a Thymeleaf view or returns a JSON response to the client.