<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html lang="ru">
<head>
    <title>${param.action == 'update' ? 'Meal update' : 'Meal create'}</title>
</head>
<body>
<section>
    <hr>
    <h3><a href="index.html">Home</a></h3><hr>
    <h2>${param.action == 'update' ? 'Meal update' : 'Meal create'}</h2>
    <hr>
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
    <form method="post" action="meals">
        <input type="hidden" name="id" value="${meal.id}">
        <dt>DateTime:   </dt>
        <label>
            <input type="datetime-local" value="${meal.dateTime}" name="dateTime">
        </label>
        <br><br>
        <dt>Description:</dt>
        <label>
            <input type="text" value="${meal.description}" name="description" placeholder="meal description" >
        </label>
        <br><br>
        <dt>Calories:   </dt>
        <label>
            <input type="number" value="${meal.calories}" name="calories">
        </label>
        <br><br>
        <button type="submit">Save</button>
        <button onclick="window.history.back()" type="button">Cancel</button>
    </form>
</section>
</body>
</html>