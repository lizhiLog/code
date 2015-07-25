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
import com.meizu.graduation.data.PostCommentData;

@WebServlet("/SendCommentServlet")
public class SendPostCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SendPostCommentServlet() {
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
			JSONObject jsonObject = JSONObject.fromObject(json);
			int code = jsonObject.getInt("code");
			System.out.println("code"+code);
			if (code == 30) {
				JSONObject data = jsonObject.getJSONObject("data");
				System.out.println(data.toString());
				PostCommentData postCommentData = new PostCommentData();
				postCommentData.author=data.getLong("author");
				postCommentData.post=data.getLong("post");
				postCommentData.content=data.getString("content");
				postCommentData.time=data.getLong("time");
				DbHelper dbHelper = new DbHelper("test", "root", "123456");
				String resultJson = dbHelper.doSendPostComment(postCommentData);
				PrintWriter out=response.getWriter();
				out.write(resultJson.toString().trim());
			}
	}

}
