<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.Cart" scope="request"/>
<tags:master pageTitle="Cart">
  <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>

  <form method="POST" action="${contextPath}/cart">
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
        <c:set var="product" value="${item.product}"/>
        <tr>
          <td>
            <img class="product-tile" src="${product.imageUrl}">
          </td>
          <td>
            <a href="${contextPath}/products/${product.id}">
              ${product.description}
            </a>
          </td>
          <td class="quantity">
            <fmt:formatNumber value="${item.quantity}" var="quantity"/>

            <c:set var="error" value="${errors[product.id]}"/>

            <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : item.quantity}" class="quantity"/>
            <input type="hidden" name="productId" value="${product.id}"/>
            <c:if test="${not empty error}">
              <div class="error">
                  ${errors[product.id]}
              </div>
            </c:if>
          </td>
          <tags:productPriceWithPopup product="${product}"/>
          <td>
            <button form="deleteCartItem"
                    formaction="${contextPath}/cart/deleteCartItem/${product.id}">
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
      <c:if test="${not empty cart.items}">
        <button>Update</button>
        <button formaction="${contextPath}/checkout" form="checkoutOrder">Checkout</button>
      </c:if>
    </p>
  </form>
  <form id="deleteCartItem" method="POST"></form>
  <form id="checkoutOrder" method="GET"></form>
</tags:master>