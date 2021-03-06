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

@WebServlet("/GetSubjectResource")
public class GetSubjectResourceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetSubjectResourceServlet() {
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
		System.out.println(json);
		JSONObject jsonObject = JSONObject.fromObject(json);
		int code = jsonObject.getInt("code");
		if (code == 38) {
			JSONObject data = jsonObject.getJSONObject("data");
			long id=data.getLong("id");
			int type=data.getInt("type");
			DbHelper dbHelper = new DbHelper("test", "root", "123456");
			String result = dbHelper.doGetSubjectResource(id,type);
			PrintWriter out=response.getWriter();
			out.write(result);
		}
	}

}
