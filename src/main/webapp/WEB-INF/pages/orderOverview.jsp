<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.Order" scope="request"/>
<tags:master pageTitle="Order overview">
    <h1>Order overview</h1>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
            </td>
            <td class="quantity">
                Quantity
            </td>
            <td class="price">
                Price
            </td>
        </tr>
        </thead>
        <c:forEach var="orderItem" items="${order.items}" varStatus="status">
            <tr>
                <td>
                    <img class="product-tile" src="${orderItem.product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${orderItem.product.id}">
                            ${orderItem.product.description}
                    </a>
                </td>
                <td class="quantity">
                        ${orderItem.quantity}
                </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/products/priceHistory/${orderItem.product.id}">
                        <fmt:formatNumber value="${orderItem.product.price}" type="currency"
                                          currencySymbol="$"/>
                    </a>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">
            </td>
            <td colspan="2">
                Subtotal:
                <fmt:formatNumber value="${order.subTotal}" type="currency"
                                  currencySymbol="$"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">
            </td>
            <td colspan="2">
                Delivery cost:
                <fmt:formatNumber value="${order.deliveryCost}" type="currency"
                                  currencySymbol="$"/>
            </td>
        </tr>
        </tr>
        <tr>
            <td colspan="2">
            </td>
            <td colspan="2">
                Total cost:
                <fmt:formatNumber value="${order.totalCost}" type="currency"
                                  currencySymbol="$"/>
            </td>
        </tr>
    </table>
    <h2>Your details</h2>
    <table>
        <tags:orderOverviewRow name="firstName" label="First Name" customer="${order.customer}"/>
        <tags:orderOverviewRow name="lastName" label="Last Name" customer="${order.customer}"/>
        <tags:orderOverviewRow name="phone" label="Phone" customer="${order.customer}"/>
        <tags:orderOverviewRow name="address" label="Delivery Address" customer="${order.customer}"/>
        <tr>
            <td>deliveryDate</td>
            <td>
                    ${order.deliveryDate}
            </td>
        </tr>

        <tr>
            <td>Payment method</td>
            <td>${order.paymentMethod}</td>
        </tr>
    </table>
</tags:master>