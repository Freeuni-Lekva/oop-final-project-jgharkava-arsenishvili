package org.ja.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet that handles user logout by invalidating the current HTTP session.
 * After logout, the user is redirected to the login page (index.jsp).
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    /**
     * Handles HTTP POST requests to log the user out.
     * Invalidates the current session to clear all session data,
     * then redirects the client to the login page.
     *
     * @param request  the HttpServletRequest object containing client request data
     * @param response the HttpServletResponse object to send the redirect response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during redirect
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, IOException {
        request.getSession().invalidate();
        response.sendRedirect("index.jsp");
    }
}
