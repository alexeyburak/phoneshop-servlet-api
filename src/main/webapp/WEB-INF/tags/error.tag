<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="title" required="true" %>

<div class="error-page">
    <h1>${title}</h1>
    <p>An error occurred with status code ${pageContext.errorData.statusCode}.</p>
    <p>Please try again later or contact support if the problem persists.</p>
    <p><a href="javascript:history.back()">Go back</a></p>
</div>

<style>
    .error-page {
        max-width: 800px;
        margin: 0 auto;
        padding: 50px;
        text-align: center;
        font-size: 18px;
        color: #333;
    }

    .error-page h1 {
        font-size: 48px;
        font-weight: bold;
        margin-bottom: 30px;
    }

    .error-page p {
        margin-bottom: 20px;
    }
</style>
