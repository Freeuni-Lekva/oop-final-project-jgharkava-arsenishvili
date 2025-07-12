package org.ja.listener;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet filter that disables client-side caching for all HTTP responses.
 * <p>
 * This filter adds HTTP headers to the response to instruct browsers and proxies
 * not to cache any content. It is useful for dynamic web applications where content
 * changes frequently or where sensitive information is served.
 * </p>
 *
 * <p>Headers added:</p>
 * <ul>
 *   <li><strong>Cache-Control:</strong> no-cache, no-store, must-revalidate</li>
 *   <li><strong>Pragma:</strong> no-cache</li>
 *   <li><strong>Expires:</strong> 0</li>
 * </ul>
 */
@WebFilter("/*")
public class NoCacheFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // You can leave this empty
    }


    /**
     * Adds headers to the response to disable caching and then continues the filter chain.
     *
     * @param req   the ServletRequest object
     * @param res   the ServletResponse object
     * @param chain the filter chain to pass the request/response to the next filter or servlet
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        chain.doFilter(req, res); // Continue request
    }

    @Override
    public void destroy() {
        // You can leave this empty
    }
}
