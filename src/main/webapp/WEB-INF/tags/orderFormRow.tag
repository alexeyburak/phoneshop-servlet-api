<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="customer" required="true" type="com.es.phoneshop.model.Customer" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>

<tr>
    <td>${label}<span class="required">*</span></td>
    <td>
        <c:set var="error" value="${errors[name]}"/>
        <input name=${name} value="${not empty errors ? param[name] : customer[name]}">
        <c:if test="${not empty error}">
            <div class="error">
                    ${error}
            </div>
        </c:if>
    </td>
</tr>