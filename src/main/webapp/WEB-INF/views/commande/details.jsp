<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Détail commande"/>
    <jsp:param name="menu" value="commandes"/>
</jsp:include>

<div class="page-head">
    <h1>Commande N° ${commande.id} <span class="badge ${commande.statut}">${commande.statut}</span></h1>
    <a class="btn secondary" href="${ctx}/commandes?action=edit&id=${commande.id}">Modifier la commande</a>
</div>

<div class="card">
    <p><strong>Client :</strong>
        <a href="${ctx}/clients?action=details&id=${commande.client.id}"><c:out value="${commande.client.nomComplet}"/></a>
        — <c:out value="${commande.client.email}"/></p>
    <p><strong>Date :</strong> ${commande.dateCommande}</p>
</div>

<div class="card table-wrap">
    <h2 style="margin-top:0">Lignes de commande</h2>
    <table>
        <thead>
        <tr><th>Produit</th><th class="num">Prix unitaire</th><th>Quantité</th><th class="num">Sous-total</th><th></th></tr>
        </thead>
        <tbody>
        <c:forEach var="l" items="${commande.lignes}">
            <tr>
                <td><c:out value="${l.produit.nom}"/></td>
                <td class="num"><fmt:formatNumber value="${l.prixUnitaire}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td>
                    <form class="inline" method="post" action="${ctx}/commandes?action=updateLigne" style="display:flex;gap:6px">
                        <input type="hidden" name="id" value="${l.id}">
                        <input type="hidden" name="commandeId" value="${commande.id}">
                        <input type="number" name="quantite" min="1" value="${l.quantite}" style="width:80px">
                        <button class="btn small secondary" type="submit">OK</button>
                    </form>
                </td>
                <td class="num"><fmt:formatNumber value="${l.sousTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td class="actions">
                    <form class="inline" method="post" action="${ctx}/commandes?action=deleteLigne"
                          onsubmit="return confirm('Retirer ce produit de la commande ?');">
                        <input type="hidden" name="id" value="${l.id}">
                        <input type="hidden" name="commandeId" value="${commande.id}">
                        <button class="btn small danger" type="submit">Retirer</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty commande.lignes}">
            <tr><td colspan="5" class="empty">Aucun produit dans cette commande.</td></tr>
        </c:if>
        </tbody>
        <tfoot>
        <tr>
            <td colspan="3" class="num total">Total</td>
            <td class="num total"><fmt:formatNumber value="${commande.total}" minFractionDigits="2" maxFractionDigits="2"/> DH</td>
            <td></td>
        </tr>
        </tfoot>
    </table>
</div>

<div class="card">
    <h2 style="margin-top:0">Ajouter un produit</h2>
    <form method="post" action="${ctx}/commandes?action=addLigne">
        <input type="hidden" name="commandeId" value="${commande.id}">
        <div class="form-grid">
            <div>
                <label for="produitId">Produit</label>
                <select id="produitId" name="produitId" required>
                    <c:forEach var="p" items="${produits}">
                        <option value="${p.id}" ${p.quantiteStock == 0 ? 'disabled' : ''}>
                            <c:out value="${p.nom}"/> — <fmt:formatNumber value="${p.prix}" minFractionDigits="2" maxFractionDigits="2"/> DH
                            (stock : ${p.quantiteStock})
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label for="quantite">Quantité</label>
                <input id="quantite" name="quantite" type="number" min="1" value="1" required>
            </div>
        </div>
        <div class="form-actions"><button class="btn" type="submit">Ajouter à la commande</button></div>
    </form>
</div>

<a class="btn secondary" href="${ctx}/commandes">← Retour aux commandes</a>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
