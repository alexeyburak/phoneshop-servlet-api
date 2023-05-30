<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.Order" scope="request"/>
<tags:master pageTitle="Checkout">
  <c:set var="contextPath" value="${pageContext.servletContext.contextPath}"/>

  <form method="POST" action="${contextPath}/checkout">
    <tags:successAndErrorMesages errorMessage="There was an error placing order" successMessage="${param.message}"/>
    <br>
    <table>
      <thead>
        <tr>
          <td>Image</td>
          <td>Description</td>
          <td>Quantity</td>
          <td>Price</td>
        </tr>
      </thead>
      <c:forEach var="item" items="${order.items}" varStatus="status">
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
            ${item.quantity}
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
        </tr>
      </c:forEach>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td>Subtotal:
          <b>
            ${order.subTotal}
          </b>
        </td>
      </tr>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td>Delivery cost:
          <b>
            ${order.deliveryCost}
          </b>
        </td>
      </tr>
      <tr>
        <td></td>
        <td></td>
        <td></td>
        <td>Total cost:
          <b>
              ${order.totalCost}
          </b>
        </td>
      </tr>
    </table>

    <h2>Your details</h2>
    <table>
      <tags:orderFormRow name="firstName" label="First Name" customer="${order.customer}"
                         errors="${errors}"/>
      <tags:orderFormRow name="lastName" label="Last Name" customer="${order.customer}"
                         errors="${errors}"/>
      <tags:orderFormRow name="phone" label="Phone" customer="${order.customer}" errors="${errors}"/>
      <tags:orderFormRow name="deliveryAddress" label="Delivery Address" customer="${order.customer}"
                         errors="${errors}"/>
      <tr>
        <c:set var="error" value="${errors['deliveryDate']}"/>
        <td>Delivery Date<span class="required">*</span></td>
        <td><input type="date" name="deliveryDate"
                   value="${not empty error ? param.deliveryDate : LocalDate.now()}">
          <c:if test="${not empty error}">
            <div class="error">
                ${error}
            </div>
          </c:if>
        </td>
      </tr>
      <tr>
        <td>Payment method<span class="required">*</span></td>
        <td><select name="paymentMethod">
          <c:forEach var="paymentMethod" items="${paymentMethods}">
            <option>${paymentMethod}</option>
          </c:forEach>
        </select></td>
      </tr>
    </table>

    <p>
      <button>Place order</button>
    </p>
  </form>
</tags:master>