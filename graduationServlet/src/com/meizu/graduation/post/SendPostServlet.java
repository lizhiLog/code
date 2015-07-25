package com.meizu.graduation.post;

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
import com.meizu.graduation.data.PostData;

@WebServlet("/SendPost")
public class SendPostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public SendPostServlet() {
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
		if (code == 24) {
			JSONObject data = jsonObject.getJSONObject("data");
			PostData postData = new PostData();
			postData.author = data.getLong("author");
			postData.content = data.getString("content");
			postData.time = data.getLong("time");
			DbHelper dbHelper = new DbHelper("test", "root", "123456");
			String result = dbHelper.doSendPost(postData);
			PrintWriter out=response.getWriter();
			out.write(result);
		}
	}

}
