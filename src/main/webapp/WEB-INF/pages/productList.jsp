<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <form>
    <input name="query" type="text" value="${param.query}" placeholder="Search product">
    <button>Search</button>
  </form>
  <tags:successAndErrorMesages errorMessage="There was an error while adding to the cart" successMessage="${param.message}"/>
    <table>
      <thead>
        <tr>
          <td>Image</td>
          <td>
            Description
            <tags:sortLink order="asc" sort="description"/>
            <tags:sortLink order="desc" sort="description"/>
          </td>
          <td>Quantity</td>
          <td>
            Price
            <tags:sortLink order="asc" sort="price"/>
            <tags:sortLink order="desc" sort="price"/>
          </td>
          <td></td>
        </tr>
      </thead>
      <c:forEach var="product" items="${products}">
        <form method="POST" action="${pageContext.servletContext.contextPath}/products">
          <tr>
            <td>
              <img class="product-tile" src="${product.imageUrl}">
            </td>
            <td>
              <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                ${product.description}
              </a>
            </td>
            <td>
              <input name="quantity" value="${not empty error and param.productId eq product.id ? param.quantity : 1}">
              <input type="hidden" name="productId" value="${product.id}"/>
              <c:if test="${not empty error && product.id == param.productId}">
                <p class="error">
                    ${error}
                </p>
              </c:if>
            </td>
            <td class="price">
              <div>
                <a href="#popup${product.id}">
                  <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </a>
              </div>
              <div id="popup${product.id}" class="overlay">
                <div class="popup">
                  <h2>Price history</h2>
                  <h1>${product.description}</h1>
                  <a class="close" href="#">&times;</a>
                  <div class="content">
                    <c:forEach var="history" items="${product.priceHistory}">
                      <p>${history.createdAt} - <fmt:formatNumber value="${history.price}" type="currency" currencySymbol="&#36"/></p>
                    </c:forEach>
                  </div>
                </div>
              </div>
            </td>
            <td>
              <button>
                Add to cart
              </button>
            </td>
          </tr>
        </form>
      </c:forEach>
    </table>
  <div>
    <tags:recentlyViewedProductList recentlyViewed="${recentlyViewed}"/>
  </div>
</tags:master>