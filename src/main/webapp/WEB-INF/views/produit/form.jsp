<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="edition" value="${not empty produit.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="${edition ? 'Modifier produit' : 'Nouveau produit'}"/>
    <jsp:param name="menu" value="produits"/>
</jsp:include>

<div class="page-head"><h1>${edition ? 'Modifier le produit' : 'Nouveau produit'}</h1></div>

<div class="card">
    <form method="post" action="${ctx}/produits?action=save">
        <input type="hidden" name="id" value="${produit.id}">
        <div class="form-grid">
            <div>
                <label for="nom">Nom *</label>
                <input id="nom" name="nom" required maxlength="150" value="${fn:escapeXml(produit.nom)}">
            </div>
            <div>
                <label for="prix">Prix (DH) *</label>
                <input id="prix" name="prix" type="number" step="0.01" min="0" required
                       value="${edition ? produit.prix : ''}">
            </div>
            <div>
                <label for="quantiteStock">Quantité en stock *</label>
                <input id="quantiteStock" name="quantiteStock" type="number" min="0" required
                       value="${edition ? produit.quantiteStock : ''}">
            </div>
            <div style="grid-column: 1 / -1">
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="3" maxlength="500"><c:out value="${produit.description}"/></textarea>
            </div>
        </div>
        <div class="form-actions">
            <button class="btn" type="submit">Enregistrer</button>
            <a class="btn secondary" href="${ctx}/produits">Annuler</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
