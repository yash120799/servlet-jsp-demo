<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <title>Delete Product</title>
</head>
<body>
<h2>Delete Product</h2>
<p>Are you sure you want to delete this product?</p>
<form action="ProductServlet" method="get">
  <input type="hidden" name="action" value="delete">
  <input type="hidden" name="id" value="<%= request.getParameter("id") %>">
  <input type="submit" value="Delete">
</form>
<br>
<a href="ProductServlet">Back to Product List</a>
</body>
</html>
