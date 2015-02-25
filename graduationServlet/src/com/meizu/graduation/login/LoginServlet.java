package com.meizu.graduation.login;

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
import com.meizu.graduation.data.UserData;


@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		Map<String, String[]> map = request.getParameterMap();
		String[] value = map.get("json");
		String json = value[0];
		JSONObject jsonObject = JSONObject.fromObject(json);
		int code = jsonObject.getInt("code");
		if (code == 22) {
			JSONObject data = jsonObject.getJSONObject("data");
			UserData userData = new UserData();
			userData.type = data.getInt("type");
			userData.email = data.getString("email");
			userData.password = data.getString("password");
			DbHelper dbHelper = new DbHelper("test", "root", "123456");
			String result = dbHelper.doLogin(userData);
			PrintWriter out=response.getWriter();
			System.out.print(result);
			out.write(result);
		}
	}

}
