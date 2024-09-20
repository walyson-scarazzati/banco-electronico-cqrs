**Fourth project from Curso de microservicios con Spring Boot y Spring Cloud - La Tecnología Avanza - Udemy**
https://www.udemy.com/course/curso-de-microservicios-con-spring-boot-y-spring-cloud

**First project:** https://github.com/walyson-scarazzati/microservicios-hotel.git

**Second project:** https://github.com/walyson-scarazzati/microserviciosProductOrder.git

**Third project:** https://github.com/walyson-scarazzati/producto-service-cqrs.git 

- Run docker ```docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver``` and ```docker run --name mysql8-container -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 -d mysql:8.0``` to start axon and MySQL 

- Start project
![image](https://github.com/user-attachments/assets/dc8b375b-91cf-4532-b8e9-b720f7624fe9)

- Teste application: http://localhost:8083/open-api/swagger-ui/index.html#
![image](https://github.com/user-attachments/assets/2a7c3f2c-2f18-405c-a14b-9948a5cfb11f)

- Acess Axon: http://localhost:8024/
![image](https://github.com/user-attachments/assets/e49567d3-06a2-47ff-affa-3657387cffa4)
