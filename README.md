# Airport Gate Management System

**Coding challenge**

---

## Setup database

In this project object-relational database systems postgresql or mysql can be used as a db management system

If you choose postgresql (default in application.properties):

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

If you choose mysql (under comments by default):

After installing mysql server:

mysql -u root -p
```
CREATE USER 'airport' IDENTIFIED BY 'airport';
CREATE database airport;
GRANT ALL PRIVILEGES ON airport.* to 'airport';
```

*logout*

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

mvn spring-boot:run

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

## Project structure

Configuration:
- Auth: define rules for user login using jwt
- Config: app configuration
- Exception: global exception handle
Api:
- Controller
Logic:
- Service
Data:
- Dto
- ModelMapper
- Repo + Model

## User roles:

- Admin

## Use cases

*Before user is logged in*

- Authentication using jwt: login and logout

*After user is logged in*

- Create new airplane with new flight number
- Assign flight to specific gate
- Fetch all gates
- Create new gate
- Fetch all time availabilities for specific gate (by gate id)
- Update gate availability (true or false)
- Add list of availabilities (start and end times) for gate
- Fetch first available gate
- Clear all availabilities for specific gate

## Data integrity

Optimistic locking used.
After one of data integrity exceptions is thrown that specific request will be repeated.

Alternatives to achieve this would be:
- Creating more complex logic for data collision and exception handling
- Using pessimistic locking 

## Multithreading

- Async is enabled in project
- Custom taskExecutor is specified
- Async calls to some main service methods are enabled
- Main use case of assigning gate to flight is tested using ExecutorService

## Possible logic and code improvements

- Create abstract service or interface(s)
- Add date field to flight entity
- Add offset field to flight date, before and after flight time needed for plane to be there or
- automatically enter specific before and after availability when assigning a flight
- Update gate to unavailable and cancel assign automatically, if before offset time
- Create sql scripts instead of postConstruct improvement
- Make secret id more secret, use external service, oauth2 for example

## Tests

- Created 7 unit tests for some of the main app features

## Time spent on this project

- Development: 12-14h
- Analysis, improvements, bug fix: 4-6h
- Write description: 2-3h
- Write tests: 2-3h