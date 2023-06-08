<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="com.es.phoneshop.model.Product" %>

<td class="price">
    <div>
        <a href="#popup${product.id}">
            <fmt:formatNumber value="${product.price}" type="currency"
                              currencySymbol="${product.currency.symbol}"/>
        </a>
    </div>
    <div id="popup${product.id}" class="overlay">
        <div class="popup">
            <h2>Price history</h2>
            <h1>${product.description}</h1>
            <a class="close" href="#">&times;</a>
            <div class="content">
                <c:forEach var="history" items="${product.priceHistory}">
                    <p>${history.createdAt} - <fmt:formatNumber value="${history.price}"
                                                                type="currency"
                                                                currencySymbol="&#36"/></p>
                </c:forEach>
            </div>
        </div>
    </div>
</td>