<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Commandes"/>
    <jsp:param name="menu" value="commandes"/>
</jsp:include>

<div class="page-head">
    <h1>Espace commande</h1>
    <a class="btn" href="${ctx}/commandes?action=new">+ Nouvelle commande</a>
</div>

<div class="card table-wrap">
    <table>
        <thead>
        <tr><th>N°</th><th>Date</th><th>Client</th><th>Statut</th><th class="num">Articles</th><th class="num">Total (DH)</th><th>Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach var="co" items="${commandes}">
            <tr>
                <td>${co.id}</td>
                <td>${co.dateCommande}</td>
                <td><a href="${ctx}/clients?action=details&id=${co.client.id}"><c:out value="${co.client.nomComplet}"/></a></td>
                <td><span class="badge ${co.statut}">${co.statut}</span></td>
                <td class="num">${co.lignes.size()}</td>
                <td class="num"><fmt:formatNumber value="${co.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td class="actions">
                    <a class="btn small secondary" href="${ctx}/commandes?action=details&id=${co.id}">Détails</a>
                    <a class="btn small secondary" href="${ctx}/commandes?action=edit&id=${co.id}">Modifier</a>
                    <form class="inline" method="post" action="${ctx}/commandes?action=delete&id=${co.id}"
                          onsubmit="return confirm('Supprimer cette commande ?');">
                        <button class="btn small danger" type="submit">Supprimer</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty commandes}">
            <tr><td colspan="7" class="empty">Aucune commande pour le moment.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
