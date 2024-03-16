package Servlet;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "SkierServlet", urlPatterns = {"/skiers"})
public class SkierServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            RabbitMQPool.initializePool();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize RabbitMQ channel pool", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            SkierRequest skierRequest = gson.fromJson(reader, SkierRequest.class);

            if (skierRequest.getSkierId() == null || skierRequest.getResortId() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Missing or invalid parameters\"}");
                return;
            }

            String jsonMessage = gson.toJson(skierRequest);
            String queueName = RabbitMQPool.getQueueName();
            Channel channel = RabbitMQPool.getChannel();
            try {
                channel.basicPublish("", queueName, null, jsonMessage.getBytes("UTF-8"));
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.write("{\"message\":\"Request processed successfully and sent to RabbitMQ\"}");
            } finally {
                RabbitMQPool.returnChannel(channel);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Invalid request format or failed to send to RabbitMQ\"}");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @Override
    public void destroy() {
        RabbitMQPool.closePool();
        super.destroy();
    }
}
