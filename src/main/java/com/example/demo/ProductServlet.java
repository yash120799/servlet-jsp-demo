package com.example.demo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ProductServlet extends HttpServlet {

    private static final String FILE_PATH = "D:/Test/servlet-jsp-demo/inventory.xlsx";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = Optional.ofNullable(request.getParameter("action")).orElse("list");

        switch (action) {
            case "new":
                request.getRequestDispatcher("addProduct.jsp").forward(request, response);
                break;
            case "edit":
                request.setAttribute("product", getProductById(request.getParameter("id")));
                request.getRequestDispatcher("editProduct.jsp").forward(request, response);
                break;
            case "delete":
                deleteProduct(request.getParameter("id"));
                response.sendRedirect("ProductServlet");
                break;
            default:
                request.setAttribute("products", getAllProducts());
                request.getRequestDispatcher("listProducts.jsp").forward(request, response);
                break;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addProduct(request);
        } else if ("update".equals(action)) {
            updateProduct(request);
        }

        response.sendRedirect("ProductServlet");
    }

    private List<Map<String, String>> getAllProducts() throws IOException {
        return readFromExcel(sheet -> {
            List<Map<String, String>> products = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Map<String, String> product = new HashMap<>();
                product.put("id", getCellValue(row.getCell(0)));
                product.put("name", getCellValue(row.getCell(1)));
                product.put("quantity", getCellValue(row.getCell(2)));
                product.put("price", getCellValue(row.getCell(3)));
                products.add(product);
            }
            return products;
        });
    }

    private void addProduct(HttpServletRequest request) throws IOException {
        writeToExcel(workbook -> {
            Sheet sheet = workbook.getSheet("Products");
            if (Objects.isNull(sheet)) sheet = workbook.createSheet("Products");

            int lastRow = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRow + 1);

            row.createCell(0).setCellValue(request.getParameter("id"));
            row.createCell(1).setCellValue(request.getParameter("name"));
            row.createCell(2).setCellValue(request.getParameter("quantity"));
            row.createCell(3).setCellValue(request.getParameter("price"));
        });
    }

    private void updateProduct(HttpServletRequest request) throws IOException {
        String id = Objects.requireNonNull(request.getParameter("id")).trim();

        writeToExcel(workbook -> {
            Sheet sheet = workbook.getSheet("Products");
            if (Objects.isNull(sheet)) return;

            boolean productFound = false;
            for (Row row : sheet) {
                if (getCellValue(row.getCell(0)).trim().equals(id)) {
                    row.getCell(1).setCellValue(request.getParameter("name"));
                    row.getCell(2).setCellValue(request.getParameter("quantity"));
                    row.getCell(3).setCellValue(request.getParameter("price"));
                    productFound = true;
                    break;
                }
            }

            if (!productFound) {
                throw new IllegalArgumentException("Product not found for ID: " + id);
            }
        });
    }

    private Map<String, String> getProductById(String id) throws IOException {
        FileInputStream fis = new FileInputStream(FILE_PATH);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet("Products");

        if (Objects.isNull(sheet)) {
            workbook.close();
            fis.close();
            return null;
        }

        Map<String, String> product = null;

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            String rowId = getCellValue(row.getCell(0));

            if (rowId.equals(id)) {
                product = new HashMap<>();
                product.put("id", rowId);
                product.put("name", getCellValue(row.getCell(1)));
                product.put("quantity", getCellValue(row.getCell(2)));
                product.put("price", getCellValue(row.getCell(3)));
                break;
            }
        }

        workbook.close();
        fis.close();
        return product;
    }

    private void deleteProduct(String id) throws IOException {
        id = Objects.requireNonNull(id).trim();
        String finalId = id;
        writeToExcel(workbook -> {
            Sheet sheet = workbook.getSheet("Products");
            if (Objects.isNull(sheet)) return ;

            int rowIndex = -1;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (getCellValue(row.getCell(0)).trim().equals(finalId)) {
                    rowIndex = row.getRowNum();
                    break;
                }
            }

            if (rowIndex != -1) {
                int lastRowNum = sheet.getLastRowNum();

                if (rowIndex < lastRowNum) {
                    sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
                }

                Row rowToDelete = sheet.getRow(lastRowNum);
                if (rowToDelete != null) {
                    sheet.removeRow(rowToDelete);
                }
            }
        });
    }

    private String getCellValue(Cell cell) {
        if (Objects.isNull(cell)) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private <T> T readFromExcel(ExcelReader<T> reader) throws IOException {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Products");
            if (Objects.isNull(sheet)) return null;
            return reader.read(sheet);
        }
    }

    private void writeToExcel(ExcelWriter writer) throws IOException {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            writer.write(workbook);
            try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                workbook.write(fos);
            }
        }
    }

    @FunctionalInterface
    private interface ExcelReader<T> {
        T read(Sheet sheet);
    }

    @FunctionalInterface
    private interface ExcelWriter {
        void write(Workbook workbook);
    }

}
