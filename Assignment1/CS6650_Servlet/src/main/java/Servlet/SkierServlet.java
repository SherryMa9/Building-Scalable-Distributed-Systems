package Servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import com.google.gson.Gson;

@WebServlet(name = "SkierServlet", urlPatterns = {"/skiers"})
public class SkierServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Read the request body and convert it from JSON to a Java object
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            SkierRequest skierRequest = gson.fromJson(reader, SkierRequest.class);

            // Perform basic parameter validation
            if (skierRequest.getSkierId() == null || skierRequest.getResortId() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Missing or invalid parameters\"}");
                return;
            }

            // If the request is valid, return a 200/201 response code and some dummy data
            response.setStatus(HttpServletResponse.SC_CREATED); // Use SC_OK for HTTP 200
            out.write("{\"message\":\"Request processed successfully\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Invalid request format\"}");
        } finally {
            out.close();
        }
    }

}

