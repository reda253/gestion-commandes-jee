<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Accueil"/>
</jsp:include>

<div class="page-head"><h1>Tableau de bord</h1></div>

<div class="stats">
    <a class="card" href="${pageContext.request.contextPath}/clients">
        <div class="muted">Espace client</div>
        <div class="big">${nbClients}</div>
        <div>clients enregistrés</div>
    </a>
    <a class="card" href="${pageContext.request.contextPath}/produits">
        <div class="muted">Espace produit</div>
        <div class="big">${nbProduits}</div>
        <div>produits au catalogue</div>
    </a>
    <a class="card" href="${pageContext.request.contextPath}/commandes">
        <div class="muted">Espace commande</div>
        <div class="big">${nbCommandes}</div>
        <div>commandes passées</div>
    </a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
