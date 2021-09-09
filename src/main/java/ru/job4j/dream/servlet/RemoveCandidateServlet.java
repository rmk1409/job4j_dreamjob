package ru.job4j.dream.servlet;

import ru.job4j.dream.store.MemStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class RemoveCandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String path = "/home/rmk/images";
        new File(path + File.separator + id + ".jpg").delete();
        MemStore.instOf().removeCandidate(Integer.parseInt(id));
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
