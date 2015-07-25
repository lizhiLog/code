package com.meizu.graduation.subject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.meizu.graduation.dao.DbHelper;

@WebServlet("/GetAllSubjectServlet")
public class GetAllSubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetAllSubjectServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");	
		DbHelper dbHelper = new DbHelper("test", "root", "123456");
		String resultJson = dbHelper.doGetAllSubject();
		System.out.println("all:"+resultJson);
		PrintWriter out=response.getWriter();
		out.write(resultJson.toString().trim());
			
	}

}
