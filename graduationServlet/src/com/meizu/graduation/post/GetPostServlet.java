package com.meizu.graduation.post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.meizu.graduation.dao.DbHelper;


@WebServlet("/GetPostServlet")
public class GetPostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 
    public GetPostServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			DbHelper dbHelper = new DbHelper("test", "root", "123456");
			String json = dbHelper.doGetPost();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out=response.getWriter();
			out.write(json);
	}

}
