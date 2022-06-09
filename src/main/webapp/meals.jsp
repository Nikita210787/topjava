<%@ page import="ru.javawebinar.topjava.util.MealsUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table border="1">
    <tr>
        <td>DATETAME</td>
        <td>DESCRIPTION</td>
        <td>CALORIES</td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <c:forEach items="${requestScope.mealTos}" var="meal">
    <tr span style="color: ${meal.excess ? 'red': 'blue'}">
        <td><c:out value="${meal.dateTime}"/></td>
        <td><c:out value="${meal.description}"/></td>
        <td>${meal.calories}</td>
        <td span style="color: black">DELITE</td>
        <td span style="color: black">UPDATE</td>
        <%--   <td><a href="meals?action=edit=<c:out value="${meal.id}"/>">Update</a></td>
        <td><a href="meals?action=delite=<c:out value="${meal.id}"/>">Delite</a></td>      --%>
    </tr>
    </c:forEach>
    </tr>
    <%-- <jsp:useBean id="mealTos" scope="request" type="java.util.List"/>--%>
</table>
</body>
</html>
