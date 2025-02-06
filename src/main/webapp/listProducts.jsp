<%@ page import="java.util.*" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <title>Product List</title>
</head>
<body>
<h2>Product List</h2>
<a href="addProduct.jsp">Add New Product</a>
<table border="1">
  <tr>
    <th>ID</th> <th>Name</th> <th>Quantity</th> <th>Price</th> <th>Actions</th>
  </tr>
  <%
    List<Map<String, String>> products = (List<Map<String, String>>) request.getAttribute("products");
    if (products != null) {
      for (Map<String, String> product : products) {
  %>
  <tr>
    <td><%= product.get("id") %></td>
    <td><%= product.get("name") %></td>
    <td><%= product.get("quantity") %></td>
    <td><%= product.get("price") %></td>
    <td>
      <a href="editProduct.jsp?id=<%= product.get("id") %>">Edit</a> |
      <a href="ProductServlet?action=delete&id=<%= product.get("id") %>">Delete</a>
    </td>
  </tr>
  <%
    }
  } else {
  %>
  <tr>
    <td colspan="5">No products found.</td>
  </tr>
  <% } %>
</table>
</body>
</html>
