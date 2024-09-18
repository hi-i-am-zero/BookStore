/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.authen;

import constant.CommonConst;
import dal.implement.AccountDAO;
import entity.Account;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author ASUS
 */
public class AuthenticationController extends HttpServlet {

    AccountDAO accountDAO = new AccountDAO();

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
            out.println("<title>Servlet AuthenticationController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AuthenticationController at " + request.getContextPath() + "</h1>");
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
        //get ve action
        String action = request.getParameter("action") != null
                ? request.getParameter("action")
                : "";
        //dua theo action set URL trang can chuyen den
        String url;
        switch (action) {
            case "login":
                url = "view/authen/login.jsp";
                break;
            case "log-out":
                url = logOut(request, response);
            case "sign-up":
                url = "view/authen/register.jsp";
                break;
            default:
                url = "home";
        }

        //chuyen trang
        request.getRequestDispatcher(url).forward(request, response);
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
        //get ve action
        String action = request.getParameter("action") != null
                ? request.getParameter("action")
                : "";
        //dựa theo action để xử lí request
        String url;
        switch (action) {
            case "login":
                url = loginDoPost(request, response);
                break;
            case "sign-up":
                url = signUp(request, response);
                break;
            default:
                url = "home";
        }
        request.getRequestDispatcher(url).forward(request, response);
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

    private String loginDoPost(HttpServletRequest request, HttpServletResponse response) {
        String url = null;
        //get về các thong tin người dufg nhập
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //kiểm tra thông tin có tồn tại trong DB ko
        Account account = Account.builder()
                .username(username)
                .password(password)
                .build();
        Account accFoundByUsernamePass = accountDAO.findByUsernameAndPass(account);
        //true => trang home ( set account vao trong session ) 
        if (accFoundByUsernamePass != null) {
            request.getSession().setAttribute(CommonConst.SESSION_ACCOUNT,
                    accFoundByUsernamePass);
            url = "home";
            //false => quay tro lai trang login ( set them thong bao loi )
        } else {
            request.setAttribute("error", "Username or password incorrect!!");
            url = "view/authen/login.jsp";
        }
        return url;
    }

    private String logOut(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(CommonConst.SESSION_ACCOUNT);
        return "home";
    }

    private String signUp(HttpServletRequest request, HttpServletResponse response) {
        String url;
        //get ve cac thong tin nguoi dung nhap
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        //kiem tra xem username da ton tai trong db
        Account account = Account.builder()
                .username(username)
                .password(password)
                .build(); 
        boolean isExistUsername = accountDAO.checkUsernameExist(account);
        //true => quay tro lai trang register (thong bao loi)
        if (isExistUsername) {
            request.setAttribute("error", "Username exist !!!");
            url = "view/authen/register.jsp";
        //false => quay tro lai trang home (ghi tai khoan vao trong DB)
        }else{
            accountDAO.insert(account);
            url = "home";
        }
        return url;
    }
}
