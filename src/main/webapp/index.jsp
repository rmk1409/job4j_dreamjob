<%@ page import="java.util.Map" %>
<%@ page import="ru.job4j.dream.store.PsqlStore" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="ru.job4j.dream.model.City" %>
<%@ page import="ru.job4j.dream.store.Store" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
            crossorigin="anonymous"></script>

    <title>Работа мечты</title>
</head>
<body>
<%
    Store store = PsqlStore.instOf();
    Map<Integer, String> idToCityName = store.findAllCities().stream().collect(Collectors.toMap(City::getId, City::getName));
    pageContext.setAttribute("idToCityName", idToCityName);
    pageContext.setAttribute("candidates", store.findTodayCandidates());
    pageContext.setAttribute("posts", store.findTodayPosts());
%>
<div class="container">
    <c:import url="nav-menu.jsp"/>
    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">
                Сегодняшние вакансии.
            </div>
            <div class="card-body">
                <c:if test="${not empty posts}">
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">Названия</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${posts}" var="post">
                            <tr>
                                <td>
                                    <a href='<c:url value="/post/edit.jsp?id=${post.id}"/>'>
                                        <i class="fa fa-edit mr-3"></i>
                                    </a>
                                    <c:out value="${post.name}"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>
    <div class="row pt-3">
        <div class="card" style="width: 100%">
            <div class="card-header">
                Сегодняшние кандидаты.
            </div>
            <div class="card-body">
                <c:if test="${not empty candidates}">
                    <table class="table">
                        <thead>
                        <tr>
                            <th scope="col">Название</th>
                            <th scope="col">Город</th>
                            <th scope="col">Фото</th>
                            <th scope="col" colspan="2"></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${candidates}" var="candidate">
                            <tr>
                                <td>
                                    <a href='<c:url value="/candidate/edit.jsp?id=${candidate.id}"/>'>
                                        <i class="fa fa-edit mr-3"></i>
                                    </a>
                                    <c:out value="${candidate.name}"/>
                                </td>
                                <td>
                                    <c:out value="${idToCityName[candidate.cityId]}"/>
                                </td>
                                <td>
                                    <img src="<c:url value='/download?name=${candidate.id}'/>" alt="candidate photo"
                                         width="100px" height="100px">
                                </td>
                                <td>
                                    <a href='<c:url value="/candidate/PhotoUpload.jsp?id=${candidate.id}"/>'>
                                        Загрузить фото
                                        <i class="fa fa-upload mr-3"></i>
                                    </a>
                                </td>
                                <td>
                                    <a href='<c:url value="/candidate/remove.do?id=${candidate.id}"/>'>
                                        Удалить кандидата
                                        <i class="fa fa-trash mr-3"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </div>
    </div>
</div>
</body>
</html>