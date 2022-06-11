<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%--<%@ page import="java.time.format.DateTimeFormatter" %>--%>
<html lang="ru">
<head>
    <title>Meals</title>
    <style>
        .raw {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr>
    <h2>Meals</h2>
    <code class="hljs xml">
        <table border="2" width="60%">
            <col style="width:5%">
            <col style="width:15%">
            <col style="width:30%">
            <col style="width:5%">
            <col style="width:10%">
            <col style="width:10%">
            <thead>
            <tr>
                <th>ID</th>
                <th>DateTime</th>
                <th>Description</th>
                <th>Calories</th>
                <th colspan=2>Action</th>
            </tr>
            </thead>
            <tbody>
            <jsp:useBean id="meals" scope="request" type="java.util.List"/>
            <c:forEach items="${meals}" var="meal">
                <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
                <tr class="${meal.excess ? 'excess' : 'raw'}">
                    <td>${meal.id}</td>
                    <td>
                        <fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate"/>
                        <fmt:formatDate pattern="yyyy-MM-dd HH:mm" value="${parsedDate}"/>
<%--    https://stackoverflow.com/questions/35606551/jstl-localdatetime-format#35607225--%>
<%--                            ${meal.dateTime.format( DateTimeFormatter.ofPattern("dd.MM.yyyy"))}--%>
                    </td>
                    <td>${meal.description}</td>
                    <td>${meal.calories}</td>
                    <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
                    <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </code>
    <hr/>
    <a href="meals?action=create">Add Meal</a>
</section>
</body>
</html>