package com.meizu.graduation.register;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.meizu.graduation.dao.DbHelper;
import com.meizu.graduation.data.UserData;

import net.sf.json.JSONObject;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RegisterServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
		HttpServletResponse response) throws ServletException, IOException {
		Map<String, String[]> map = request.getParameterMap();
		String[] value = map.get("json");
		String json = value[0];
		System.out.println("json---->:" + json);
		JSONObject jsonObject = JSONObject.fromObject(json);
		System.out.println("json2---->" + jsonObject.toString());
		int code = jsonObject.getInt("code");
		System.out.println("code"+code);
		if (code == 20) {
			JSONObject data = jsonObject.getJSONObject("data");
			System.out.println(data.toString());
			UserData userData = new UserData();
			userData.type = data.getInt("type");
			userData.email = data.getString("email");
			userData.name = data.getString("name");
			userData.password = data.getString("password");
			if(userData.type==0){
					userData.no = data.getString("no");
			}
			userData.school = data.getString("school");
			userData.academy = data.getString("academy");
			userData.time = data.getLong("time");
			DbHelper dbHelper = new DbHelper("studySys", "root", "123456");
			int result = dbHelper.doRegister(userData);
			JSONObject resultJson = new JSONObject();
			resultJson.put("code", 20);
			JSONObject resultData=new JSONObject();
			resultData.put("result", result);
			resultJson.put("data", resultData);
			PrintWriter out=response.getWriter();
			out.write(resultJson.toString().trim());
			
		}

	}
}
