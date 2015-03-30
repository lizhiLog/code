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


@WebServlet("/ModifyPasswordServlet")
public class ModifyPasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public ModifyPasswordServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			Map<String, String[]> map = request.getParameterMap();
			String[] value = map.get("json");
			String json = value[0];
			JSONObject jsonObject = JSONObject.fromObject(json);
			int code = jsonObject.getInt("code");
			if (code == 50) {
				JSONObject data = jsonObject.getJSONObject("data");
				DbHelper dbHelper = new DbHelper("test", "root", "123456");
				String result = dbHelper.doModifyPassword(data.getString("email"),data.getString("oldPassword"),data.getString("newPassword"));
				PrintWriter out=response.getWriter();
				out.write(result);	
			}
	}

}
