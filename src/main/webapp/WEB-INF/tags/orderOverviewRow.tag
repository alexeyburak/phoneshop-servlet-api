<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="customer" required="true" type="com.es.phoneshop.model.Customer" %>

<tr>
    <td>${label}</td>
    <td>
        ${customer[name]}
    </td>
</tr>