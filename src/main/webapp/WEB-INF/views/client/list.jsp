<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Clients"/>
    <jsp:param name="menu" value="clients"/>
</jsp:include>

<div class="page-head">
    <h1>Espace client</h1>
    <div style="display:flex;gap:10px;flex-wrap:wrap">
        <form class="search" method="get" action="${ctx}/clients">
            <input type="text" name="q" placeholder="Nom, prénom ou email" value="${fn:escapeXml(param.q)}">
            <button class="btn secondary" type="submit">Rechercher</button>
        </form>
        <a class="btn" href="${ctx}/clients?action=new">+ Nouveau client</a>
    </div>
</div>

<div class="card table-wrap">
    <table>
        <thead>
        <tr><th>#</th><th>Nom</th><th>Prénom</th><th>Email</th><th>Téléphone</th><th>Adresse</th><th>Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach var="cl" items="${clients}">
            <tr>
                <td>${cl.id}</td>
                <td><c:out value="${cl.nom}"/></td>
                <td><c:out value="${cl.prenom}"/></td>
                <td><c:out value="${cl.email}"/></td>
                <td><c:out value="${cl.telephone}"/></td>
                <td><c:out value="${cl.adresse}"/></td>
                <td class="actions">
                    <a class="btn small secondary" href="${ctx}/clients?action=details&id=${cl.id}">Détails</a>
                    <a class="btn small secondary" href="${ctx}/clients?action=edit&id=${cl.id}">Modifier</a>
                    <form class="inline" method="post" action="${ctx}/clients?action=delete&id=${cl.id}"
                          onsubmit="return confirm('Supprimer ce client et toutes ses commandes ?');">
                        <button class="btn small danger" type="submit">Supprimer</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty clients}">
            <tr><td colspan="7" class="empty">Aucun client trouvé.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
