<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.Cart" scope="request"/>
<tags:master pageTitle="Cart">
  <form method="POST" action="${pageContext.servletContext.contextPath}/cart">
    <tags:successAndErrorMesages errorMessage="There was an error updating the cart" successMessage="${param.message}"/>
    <br>
    <table>
      <thead>
        <tr>
          <td>Image</td>
          <td>Description</td>
          <td>Quantity</td>
          <td>Price</td>
          <td></td>
        </tr>
      </thead>
      <c:forEach var="item" items="${cart.items}" varStatus="status">
        <tr>
          <td>
            <img class="product-tile" src="${item.product.imageUrl}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
              ${item.product.description}
            </a>
          </td>
          <td class="quantity">
            <fmt:formatNumber value="${item.quantity}" var="quantity"/>

            <c:set var="error" value="${errors[item.product.id]}"/>

            <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}" class="quantity"/>
            <input type="hidden" name="productId" value="${item.product.id}"/>
            <c:if test="${not empty error}">
              <div class="error">
                  ${errors[item.product.id]}
              </div>
            </c:if>
          </td>
          <td class="price">
            <div>
              <a href="#popup${item.product.id}">
                <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="${item.product.currency.symbol}"/>
              </a>
            </div>
            <div id="popup${item.product.id}" class="overlay">
              <div class="popup">
                <h2>Price history</h2>
                <h1>${item.product.description}</h1>
                <a class="close" href="#">&times;</a>
                <div class="content">
                  <c:forEach var="history" items="${item.product.priceHistory}">
                    <p>${history.createdAt} - <fmt:formatNumber value="${history.price}" type="currency" currencySymbol="&#36"/></p>
                  </c:forEach>
                </div>
              </div>
            </div>
          </td>
          <td>
            <button form="deleteCartItem"
                    formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
              Delete
            </button>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td>Total price:
          <b>${cart.totalCost}</b>
        </td>
      </tr>
    </table>
    <p>
      <button>Update</button>
    </p>
  </form>
  <form id="deleteCartItem" method="POST"></form>
</tags:master>