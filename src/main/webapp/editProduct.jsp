<%@ page import="java.util.*" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <title>Edit Product</title>
</head>
<body>
<h2>Edit Product</h2>
<%
  Map<String, String> product = (Map<String, String>) request.getAttribute("product");
  if (product != null) {
%>
<form action="ProductServlet" method="post">
  <input type="hidden" name="action" value="update">
  <input type="hidden" name="id" value="<%= product.get("id") %>">
  <label>Name:</label> <input type="text" name="name" value="<%= product.get("name") %>" required><br>
  <label>Quantity:</label> <input type="number" name="quantity" value="<%= product.get("quantity") %>" required><br>
  <label>Price:</label> <input type="number" name="price" value="<%= product.get("price") %>" required><br>
  <input type="submit" value="Update Product">
</form>
<% } else { %>
<p>Product not found.</p>
<% } %>
<br>
<a href="ProductServlet">Back to Product List</a>
</body>
</html>
