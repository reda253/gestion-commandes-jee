<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="edition" value="${not empty commande.id}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="${edition ? 'Modifier commande' : 'Nouvelle commande'}"/>
    <jsp:param name="menu" value="commandes"/>
</jsp:include>

<div class="page-head"><h1>${edition ? 'Modifier la commande N° ' += commande.id : 'Nouvelle commande'}</h1></div>

<div class="card">
    <c:choose>
        <c:when test="${empty clients}">
            <p>Aucun client n'existe encore. <a href="${ctx}/clients?action=new">Créez d'abord un client</a>.</p>
        </c:when>
        <c:otherwise>
            <form method="post" action="${ctx}/commandes?action=save">
                <input type="hidden" name="id" value="${commande.id}">
                <div class="form-grid">
                    <div>
                        <label for="clientId">Client *</label>
                        <select id="clientId" name="clientId" required>
                            <option value="">-- Choisir un client --</option>
                            <c:forEach var="cl" items="${clients}">
                                <option value="${cl.id}" ${cl.id == commande.client.id ? 'selected' : ''}>
                                    <c:out value="${cl.nomComplet}"/> (<c:out value="${cl.email}"/>)
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="dateCommande">Date *</label>
                        <input id="dateCommande" name="dateCommande" type="date" required value="${commande.dateCommande}">
                    </div>
                    <div>
                        <label for="statut">Statut *</label>
                        <select id="statut" name="statut">
                            <c:forEach var="s" items="${statuts}">
                                <option value="${s}" ${s == commande.statut ? 'selected' : ''}>${s}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="form-actions">
                    <button class="btn" type="submit">${edition ? 'Enregistrer' : 'Créer et ajouter des produits'}</button>
                    <a class="btn secondary" href="${ctx}/commandes">Annuler</a>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
