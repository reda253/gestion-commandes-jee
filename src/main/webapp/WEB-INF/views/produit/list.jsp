<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Produits"/>
    <jsp:param name="menu" value="produits"/>
</jsp:include>

<div class="page-head">
    <h1>Espace produit</h1>
    <div style="display:flex;gap:10px;flex-wrap:wrap">
        <form class="search" method="get" action="${ctx}/produits">
            <input type="text" name="q" placeholder="Nom du produit" value="${fn:escapeXml(param.q)}">
            <button class="btn secondary" type="submit">Rechercher</button>
        </form>
        <a class="btn" href="${ctx}/produits?action=new">+ Nouveau produit</a>
    </div>
</div>

<div class="card table-wrap">
    <table>
        <thead>
        <tr><th>#</th><th>Nom</th><th>Description</th><th class="num">Prix (DH)</th><th class="num">Stock</th><th>Actions</th></tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${produits}">
            <tr>
                <td>${p.id}</td>
                <td><c:out value="${p.nom}"/></td>
                <td class="muted"><c:out value="${p.description}"/></td>
                <td class="num"><fmt:formatNumber value="${p.prix}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td class="num">
                    <c:choose>
                        <c:when test="${p.quantiteStock == 0}"><span class="badge ANNULEE">Rupture</span></c:when>
                        <c:when test="${p.quantiteStock lt 5}"><span class="badge EN_COURS">${p.quantiteStock}</span></c:when>
                        <c:otherwise>${p.quantiteStock}</c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <a class="btn small secondary" href="${ctx}/produits?action=edit&id=${p.id}">Modifier</a>
                    <form class="inline" method="post" action="${ctx}/produits?action=delete&id=${p.id}"
                          onsubmit="return confirm('Supprimer ce produit ?');">
                        <button class="btn small danger" type="submit">Supprimer</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty produits}">
            <tr><td colspan="6" class="empty">Aucun produit trouvé.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
