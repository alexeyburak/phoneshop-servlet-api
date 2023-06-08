<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="searchMethods" type="java.util.List" scope="request"/>
<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Advanced Search">
    <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>
    <c:set var="productsSize" value="${products.size()}"/>
    <c:if test="${productsSize > 0}">
        <div class="success">
            <h4>We found ${productsSize} products</h4>
        </div>
    </c:if>
    <h3>Advanced Search</h3>
    <form method="GET" action="${contextPath}/advancedSearch">
        <label for="description">Description: </label>
        <input id="description" name="query" value="${param.query}">
        <select name="searchMethod">
            <c:forEach var="searchMethod" items="${searchMethods}">
                <option <c:if test="${searchMethod eq param.searchMethod}">
                    selected</c:if>>
                        ${searchMethod}
                </option>
            </c:forEach>
        </select>
        <br><br>
        <label for="minPrice">Min Price: </label>
        <input id="minPrice" name="minPrice" value="${param.minPrice}">
        <br>
        <c:if test="${not empty errors['minPrice']}">
            <div class="error">
                    ${errors['minPrice']}
            </div>
        </c:if>
        <br>
        <label for="maxPrice">Max Price: </label>
        <input id="maxPrice" name="maxPrice" value="${param.maxPrice}">
        <br>
        <c:if test="${not empty errors['maxPrice']}">
            <div class="error">
                    ${errors['maxPrice']}
            </div>
        </c:if>
        <br>
        <button>Search</button>
    </form>
    <br>
    <c:choose>
        <c:when test="${not empty products}">
            <table>
                <thead>
                <tr>
                    <td>Image</td>
                    <td>
                        Description
                    </td>
                    <td class="price">
                        Price
                    </td>
                </tr>
                </thead>
                <c:forEach var="product" items="${products}">
                    <tr>
                        <td>
                            <img class="product-tile" src="${product.imageUrl}">
                        </td>
                        <td>
                            <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                    ${product.description}
                            </a>
                        </td>
                        <tags:productPriceWithPopup product="${product}"/>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <h4>We didn't find products</h4>
        </c:otherwise>
    </c:choose>
</tags:master>