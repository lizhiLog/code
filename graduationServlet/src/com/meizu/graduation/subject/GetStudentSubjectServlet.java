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
import com.meizu.graduation.data.PostCommentData;

@WebServlet("/GetStudentSubject")
public class GetStudentSubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetStudentSubjectServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");	
		Map<String, String[]> map = request.getParameterMap();
			String[] value = map.get("json");
			String json = value[0];
			System.out.println("json---->:" + json);
			JSONObject jsonObject = JSONObject.fromObject(json);
			System.out.println("json2---->" + jsonObject.toString());
			int code = jsonObject.getInt("code");
			System.out.println("code"+code);
			if (code == 34) {
				JSONObject data = jsonObject.getJSONObject("data");
				System.out.println(data.toString());
				long id=data.getLong("id");
				DbHelper dbHelper = new DbHelper("test", "root", "123456");
				String resultJson = dbHelper.doGetStudentSubject(id);
				PrintWriter out=response.getWriter();
				out.write(resultJson.toString().trim());
			}
	}

}
