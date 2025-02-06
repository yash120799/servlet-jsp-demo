<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
  <title>Add Product</title>
</head>
<body>
<h2>Add New Product</h2>
<form action="ProductServlet" method="post">
  <input type="hidden" name="action" value="add">
  <label>ID:</label> <input type="text" name="id" required><br>
  <label>Name:</label> <input type="text" name="name" required><br>
  <label>Quantity:</label> <input type="number" name="quantity" required><br>
  <label>Price:</label> <input type="number" name="price" required><br>
  <input type="submit" value="Add Product">
</form>
<br>
<a href="ProductServlet">Back to Product List</a>
</body>
</html>
