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
		 response.setContentType("text/html;charset=utf-8");
		Map<String, String[]> map = request.getParameterMap();
		String[] value = map.get("json");
		String json = value[0];
		System.out.print("xsfdbfbdf--->"+json);
		JSONObject jsonObject = JSONObject.fromObject(json);
		int code = jsonObject.getInt("code");
		if (code == 20) {
			JSONObject data = jsonObject.getJSONObject("data");
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
			DbHelper dbHelper = new DbHelper("test", "root", "123456");
			String result = dbHelper.doRegister(userData);
			PrintWriter out=response.getWriter();
			out.write(result);	
		}

	}
}
