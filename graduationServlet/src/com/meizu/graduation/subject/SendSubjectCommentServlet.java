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
import com.meizu.graduation.data.PostCommentData;
import com.meizu.graduation.data.SubjectCommentData;


@WebServlet("/SendSubjectCommentServlet")
public class SendSubjectCommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public SendSubjectCommentServlet() {
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
				JSONObject jsonObject = JSONObject.fromObject(json);
				int code = jsonObject.getInt("code");
				System.out.println("code"+code);
				if (code == 46) {
					JSONObject data = jsonObject.getJSONObject("data");
					System.out.println(data.toString());
					SubjectCommentData subjectCommentData = new SubjectCommentData();
					subjectCommentData.author=data.getLong("author");
					subjectCommentData.subject=data.getLong("subject");
					subjectCommentData.content=data.getString("content");
					subjectCommentData.time=data.getLong("time");
					DbHelper dbHelper = new DbHelper("test", "root", "123456");
					String resultJson = dbHelper.doSendSubjectComment(subjectCommentData);
					PrintWriter out=response.getWriter();
					out.write(resultJson.toString().trim());
				}
	}

}
