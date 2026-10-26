<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Fiche client"/>
    <jsp:param name="menu" value="clients"/>
</jsp:include>

<div class="page-head">
    <h1><c:out value="${client.nomComplet}"/></h1>
    <div style="display:flex;gap:10px">
        <a class="btn secondary" href="${ctx}/clients?action=edit&id=${client.id}">Modifier</a>
        <a class="btn" href="${ctx}/commandes?action=new&clientId=${client.id}">+ Nouvelle commande</a>
    </div>
</div>

<div class="card">
    <p><strong>Email :</strong> <c:out value="${client.email}"/></p>
    <p><strong>Téléphone :</strong> <c:out value="${client.telephone}" default="—"/></p>
    <p><strong>Adresse :</strong> <c:out value="${client.adresse}" default="—"/></p>
</div>

<div class="card table-wrap">
    <h2 style="margin-top:0">Commandes du client</h2>
    <table>
        <thead><tr><th>N°</th><th>Date</th><th>Statut</th><th class="num">Total (DH)</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="co" items="${commandes}">
            <tr>
                <td>${co.id}</td>
                <td>${co.dateCommande}</td>
                <td><span class="badge ${co.statut}">${co.statut}</span></td>
                <td class="num"><fmt:formatNumber value="${co.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td><a class="btn small secondary" href="${ctx}/commandes?action=details&id=${co.id}">Voir</a></td>
            </tr>
        </c:forEach>
        <c:if test="${empty commandes}">
            <tr><td colspan="5" class="empty">Ce client n'a encore passé aucune commande.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<a class="btn secondary" href="${ctx}/clients">← Retour à la liste</a>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
