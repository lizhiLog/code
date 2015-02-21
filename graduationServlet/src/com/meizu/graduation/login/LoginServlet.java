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
		Map<String, String[]> map = request.getParameterMap();
		String[] value = map.get("json");
		String json = value[0];
		System.out.println("json---->:" + json);
		JSONObject jsonObject = JSONObject.fromObject(json);
		System.out.println("json2---->" + jsonObject.toString());
		int code = jsonObject.getInt("code");
		System.out.println("code"+code);
		if (code == 22) {
			JSONObject data = jsonObject.getJSONObject("data");
			System.out.println(data.toString());
			UserData userData = new UserData();
			userData.type = data.getInt("type");
			userData.email = data.getString("email");
			userData.password = data.getString("password");
			DbHelper dbHelper = new DbHelper("studySys", "root", "123456");
			int result = dbHelper.doLogin(userData);
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 22);
			JSONObject resultData=new JSONObject();
			resultData.put("result", result);
			resultJson.put("data", resultData);
			PrintWriter out=response.getWriter();
			out.write(resultJson.toString().trim());
		}
	}

}
