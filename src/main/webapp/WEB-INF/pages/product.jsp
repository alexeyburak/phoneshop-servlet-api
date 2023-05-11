<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.Product" scope="request"/>
<tags:master pageTitle="${product.description}">
    <div class="product-details">
        <div class="product-image">
            <img class="product-image__img" src="${product.imageUrl}" alt="${product.description}">
        </div>
        <div class="product-info">
            <h1 class="product-info__title">${product.description}</h1>
            <p class="product-info__code">Code: ${product.code}</p>
            <p class="product-info__stock">In stock: ${product.stock}</p>
            <p class="product-info__price"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/></p>
            <div class="product-info__button">
                <a href="javascript:history.back()">Go back</a>
            </div>
        </div>
    </div>
</tags:master>