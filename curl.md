##Testing [MealRestController](src/main/java/ru/javawebinar/topjava/web/meal/MealRestController.java)  of topjava project via curl utility Windows 10, GitBash
- IDE: IntelliJ IDEA 2021.3.2 Ultimate
- Server: Tomcat 9.0.59
- DBMS: PostgreSQL 13.7
- Spring 5.3.20 activeProfiles = '{datajpa, postgres}'

**testing for specified (authorised) user**

###MealRestController#getAll: all meals of the authorised user (list of transfert object)
###curl http://localhost:8080/topjava/rest/auth-user/meals/

###MealRestController#get: user's meal with id = 100009
###curl http://localhost:8080/topjava/rest/auth-user/meals/100009

###MealRestController#getBetween (/between): get user's meals transfert objects between start DateTime and end DateTime
###curl "http://localhost:8080/topjava/rest/auth-user/meals/between?start=2020-01-30T00:00:00&end=2020-01-31T10:01:00"

###MealRestController#getBetween (/filter): filter user's meals transfert objects between startDate startTime and endDate endTime
###curl "http://localhost:8080/topjava/rest/auth-user/meals/filter?startDate=2020-01-30&startTime=00:00:00&endDate=2020-01-31&endTime=10:01:00"

###MealRestController#delete: delete user's meal with id = 100005
###curl -XDELETE http://localhost:8080/topjava/rest/auth-user/meals/100005

####MealRestController#createWithLocation: create new user's meal
###curl -v -X POST http://localhost:8080/topjava/rest/auth-user/meals/ -H "Content-Type:application/json;charset=UTF-8" -d '{"dateTime":"2022-09-13T12:00:00","description":"New meal","calories":999}'

####MealRestController#update: update user's meal with id = 100018
###curl -X PUT http://localhost:8080/topjava/rest/auth-user/meals/100018 -H "Content-Type:application/json;charset=UTF-8" -d '{"description":"New meal description","calories":1888}'