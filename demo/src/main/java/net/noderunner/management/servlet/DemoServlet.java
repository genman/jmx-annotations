package net.noderunner.management.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="demo",value="/demo")
public class DemoServlet extends HttpServlet {

    @Inject
    BusinessBean bean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String result = bean.doSomething("input");
        int status = bean.getStatus();
        if (status != 200)
            res.sendError(status, result);
        res.getWriter().println(result);
        res.getWriter().close();
    }

}
