# Airport Gate Management System

**Coding challenge**

---

## Setup database

In this project object-relational database system postgresql is used as a db management system

After installing psql server and enabling access in pg conf:

psql -U postgres
```
CREATE ROLE airport WITH PASSWORD 'airport';
CREATE database airport;
GRANT ALL ON DATABASE airport to airport;
ALTER ROLE airport WITH LOGIN;
```

*logout*


*future logins to airport database:*

psql -U airport

database entities:
- airplane (id, model)
- flight (id, number, gateFK, airplaneFK)
- gate (id, number, available, version)
- availability (id, start_time, end_time, gateFK)
- user (id, username, password, email, first_name, last_name)

## Run project

checkout project with git clone...

mvn clean install

./mvnw spring-boot:run

## How to use swagger

go to http://localhost:9099/api/v1/swagger-ui.html to see available endpoints

go to AuthorizationController/auth/login

perform action with created credentials (user:airport pass:airport123) 

if successfull service will return JWT token

hit Authorization button on swagger and fill it with "Bearer YOUR-TOKEN" and click authorize

## Data created by default on project start (using @PostConstruct in @Service layer):
```
- Airplanes: [{model:model1}, {model:model2}]
- Flights: [{number:number_1, number:number_11}]
- Gates: [{number:1,available:'t'}, {number:2,available:'f'}, {number:3,available:'f'}]
- Availabilities
   - (
    - [
        - {start_time:'2020-11-29 09:00:00', end_time:'2020-11-29 10:00:00', gate_id:1},
        - {start_time:'2020-11-29 16:00:00', end_time:'2020-11-29 15:00:00', gate_id:1},
        - {start_time:'2020-11-29 17:35:00', end_time:'2022-11-29 20:00:00', gate_id:1}
    - ]
   - )
- User: [{username:'airport',password:'airport123'}]
```