<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.titre}" default="Gestion des commandes"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="topbar">
    <div class="inner">
        <a class="brand" href="${pageContext.request.contextPath}/">Gestion des commandes</a>
        <nav>
            <a href="${pageContext.request.contextPath}/clients"   class="${param.menu == 'clients'   ? 'active' : ''}">Espace client</a>
            <a href="${pageContext.request.contextPath}/produits"  class="${param.menu == 'produits'  ? 'active' : ''}">Espace produit</a>
            <a href="${pageContext.request.contextPath}/commandes" class="${param.menu == 'commandes' ? 'active' : ''}">Espace commande</a>
        </nav>
    </div>
</header>
<main>
<%-- Message flash (affiché une fois puis supprimé de la session) --%>
<c:if test="${not empty sessionScope.flashMessage}">
    <div class="flash ${sessionScope.flashType}"><c:out value="${sessionScope.flashMessage}"/></div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>
