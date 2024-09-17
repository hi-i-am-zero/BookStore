/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dal.implement.CategoryDAO;
import dal.implement.ProductDAO;
import entity.Product;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;

/**
 *
 * @author ASUS
 */
@MultipartConfig
public class ProductAdminServlet extends HttpServlet {

    ProductDAO pdao = new ProductDAO();
    CategoryDAO cateDAO = new CategoryDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ProductAdminServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProductAdminServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //set enconding UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        //get session
        HttpSession session = request.getSession();
        //get action
        String action = request.getParameter("action") == null
                ? "Default" : request.getParameter("action");
        switch (action) {
            case "add":
                addProduct(request);
                break;
            case "delete":
                deleteProduct(request);
                break;
            case "edit":
                editProduct(request);
                break;
            default:

        }
        response.sendRedirect("dashboard");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void addProduct(HttpServletRequest request) {
        try {
            //get name
            String name = request.getParameter("name");
            //get price
            float price = Float.parseFloat(request.getParameter("price"));
            //get quantity
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            //get description
            String description = request.getParameter("description");
            //get catefory Id
            int categoryId = Integer.parseInt(request.getParameter("category"));

            //image
            Part part = request.getPart("image");
            String imagePath = null;
            if (part.getSubmittedFileName() == null || part.getSubmittedFileName().trim().isEmpty() || part == null) {
                imagePath = null;
            } else {
                //duong dan luu anh
                String path = request.getServletContext().getRealPath("/images");
                File dir = new File(path);
                //xem duong dan nay ton tai chua
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File image = new File(dir, part.getSubmittedFileName());
                //ghi file vao trong duong dan
                part.write(image.getAbsolutePath());
                //lay ra cai context path cua project
                imagePath = request.getContextPath() + "/images/" + image.getName();
            }

            Product product = Product.builder()
                    .name(name)
                    .price(price)
                    .quantity(quantity)
                    .description(description)
                    .categoryId(categoryId)
                    .image(imagePath)
                    .build();
            pdao.insert(product);
        } catch (ServletException | IOException | NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteProduct(HttpServletRequest request) {
        //get ve id
        int id = Integer.parseInt(request.getParameter("id"));
        pdao.deleteById(id);
    }

    private void editProduct(HttpServletRequest request) {
        try {
            //get ve data
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            float price = Float.parseFloat(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String description = request.getParameter("description");
            int categoryId = Integer.parseInt(request.getParameter("category"));

            Part part = request.getPart("image");
            String imagePath = null;
            if (part.getSubmittedFileName() == null || part.getSubmittedFileName().trim().isEmpty() || part == null) {
                imagePath = request.getParameter("currentImage");
            } else {
                //duong dan luu anh
                String path = request.getServletContext().getRealPath("/images");
                File dir = new File(path);
                //xem duong dan nay ton tai chua
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File image = new File(dir, part.getSubmittedFileName());
                //ghi file vao trong duong dan
                part.write(image.getAbsolutePath());
                //lay ra cai context path cua project
                imagePath = request.getContextPath() + "/images/" + image.getName();
            }

            Product product = Product.builder()
                    .id(id)
                    .name(name)
                    .price(price)
                    .quantity(quantity)
                    .description(description)
                    .categoryId(categoryId)
                    .image(imagePath)
                    .build();
            pdao.update(product);
        } catch (NumberFormatException | IOException | ServletException ex) {
            ex.printStackTrace();
        }
    }
}
