<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="titre" value="Erreur"/>
</jsp:include>

<div class="card">
    <h1>Une erreur est survenue</h1>
    <p class="muted"><c:out value="${pageContext.exception.message}" default="Erreur inattendue."/></p>
    <p>Vérifiez que MySQL est démarré et que <code>db.properties</code> contient les bons identifiants.</p>
    <a class="btn" href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
