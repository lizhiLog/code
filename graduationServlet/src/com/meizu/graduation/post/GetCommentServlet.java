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

@WebServlet("/getComment")
public class GetCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 
    public GetCommentServlet() {
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
		if (code == 28) {
			JSONObject data = jsonObject.getJSONObject("data");
			long id=data.getLong("id");
			DbHelper dbHelper = new DbHelper("studySys", "root", "123456");
			String resultJson=dbHelper.doGetComment(id);
			PrintWriter out=response.getWriter();
			out.write(resultJson);
		}
	}

}
