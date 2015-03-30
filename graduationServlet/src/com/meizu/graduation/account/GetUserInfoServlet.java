package com.meizu.graduation.account;

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

@WebServlet("/GetUserInfo")
public class GetUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetUserInfoServlet() {
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
			if (code == 54) {
				JSONObject data = jsonObject.getJSONObject("data");
				long id=data.getLong("id");
				DbHelper dbHelper = new DbHelper("test", "root", "123456");
				String resultJson = dbHelper.doGetUserInfo(id);
				PrintWriter out=response.getWriter();
				out.write(resultJson.toString().trim());
			}
	}

}
