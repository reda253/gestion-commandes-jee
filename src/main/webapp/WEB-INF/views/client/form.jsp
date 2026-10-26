<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="edition" value="${not empty client.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="${edition ? 'Modifier client' : 'Nouveau client'}"/>
    <jsp:param name="menu" value="clients"/>
</jsp:include>

<div class="page-head"><h1>${edition ? 'Modifier le client' : 'Nouveau client'}</h1></div>

<div class="card">
    <form method="post" action="${ctx}/clients?action=save">
        <input type="hidden" name="id" value="${client.id}">
        <div class="form-grid">
            <div>
                <label for="nom">Nom *</label>
                <input id="nom" name="nom" required maxlength="100" value="${fn:escapeXml(client.nom)}">
            </div>
            <div>
                <label for="prenom">Prénom *</label>
                <input id="prenom" name="prenom" required maxlength="100" value="${fn:escapeXml(client.prenom)}">
            </div>
            <div>
                <label for="email">Email *</label>
                <input id="email" name="email" type="email" required maxlength="150" value="${fn:escapeXml(client.email)}">
            </div>
            <div>
                <label for="telephone">Téléphone</label>
                <input id="telephone" name="telephone" maxlength="20" value="${fn:escapeXml(client.telephone)}">
            </div>
            <div style="grid-column: 1 / -1">
                <label for="adresse">Adresse</label>
                <input id="adresse" name="adresse" maxlength="255" value="${fn:escapeXml(client.adresse)}">
            </div>
        </div>
        <div class="form-actions">
            <button class="btn" type="submit">Enregistrer</button>
            <a class="btn secondary" href="${ctx}/clients">Annuler</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
