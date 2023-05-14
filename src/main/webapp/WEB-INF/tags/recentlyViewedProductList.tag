<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="recentlyViewed" type="java.util.Set<com.es.phoneshop.model.Product>" required="true" %>

<c:if test="${not empty recentlyViewed}">
    <div>
        <h3>Recently viewed products</h3>
        <div class="recent-products-container">
            <c:forEach var="product" items="${recentlyViewed}">
                <div class="recent-product-card">
                    <img class="recent-product-image" src="${product.imageUrl}" alt="Product image">
                    <div class="recent-product-description">
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                ${product.description}
                        </a>
                    </div>
                    <span class="recent-product-price">
                        <fmt:formatNumber value="${product.price}" type="currency"
                                          currencySymbol="${product.currency.symbol}"/>
                    </span>
                </div>
            </c:forEach>
        </div>
    </div>
</c:if>
