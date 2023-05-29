<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="errorMessage" required="true" %>
<%@ attribute name="successMessage" required="true" %>

<c:if test="${not empty param.message}">
    <div class="success">
            ${successMessage}
    </div>
</c:if>
<c:if test="${not empty error or not empty errors}">
    <div class="error">
        ${errorMessage}
    </div>
</c:if>
