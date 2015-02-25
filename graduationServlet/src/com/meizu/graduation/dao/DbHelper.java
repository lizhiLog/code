package com.meizu.graduation.dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.crypto.Data;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.meizu.graduation.data.PostCommentData;
import com.meizu.graduation.data.PostData;
import com.meizu.graduation.data.ResourceData;
import com.meizu.graduation.data.SubjectCommentData;
import com.meizu.graduation.data.SubjectData;
import com.meizu.graduation.data.UserData;
import com.mysql.jdbc.Connection;

public class DbHelper {
	private static final String DRIVER = "com.mysql.jdbc.Driver";

	Connection conn;
	ResultSet resultSet;

	public DbHelper(String DatabaseName, String user, String password) {
	
			try {
				Class.forName(DRIVER).newInstance();
				String url = "jdbc:mysql://127.0.0.1:3306/" + DatabaseName;
				conn = (Connection) DriverManager
						.getConnection(url, user, password);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public String doUploadFile(ResourceData resourceData){
		JSONObject json=new JSONObject();
		json.put("code", 40);
		try {
			Statement statement = conn.createStatement();
			System.out.println("xx");
			String sql = "insert into resource (type,subject,title,name,detail,time) values ("
					+resourceData.type+ ","
					+resourceData.subject+",'"
					+resourceData.title+ "','"
					+resourceData.name+ "','"
					+resourceData.detail+"',"
					+resourceData.time+ ")";
			int result = statement.executeUpdate(sql);
			if (result>0) {
				json.put("result", 0);
			} else {
				json.put("result", 1);
			}
		} catch (SQLException e) {
			json.put("result", 1);
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	return json.toString().trim();
	}
	
	public String doGetSubjectResource(long id,int type){
		JSONObject json=new JSONObject();
		json.put("code", 38);
		try {
			Statement statement = conn.createStatement();
		     //查询课程ID，type类资源列表
		    JSONArray data=new JSONArray();
		    String sql ="select resource._id as id,resource.title as title"
		    		+ ",resource.name as name,resource.detail as detail from resource"
		    		+ " where resource.subject="+id+" and resource.type="+type;
			resultSet = statement.executeQuery(sql);
			resultSet.beforeFirst();
			while(resultSet.next()){
				JSONObject value=new JSONObject();
				value.put("id", resultSet.getLong("id"));
				value.put("title", resultSet.getString("title"));
				value.put("name", resultSet.getString("name"));
				value.put("detail", resultSet.getString("detail"));
				data.add(value);
			}
			json.put("result", 0);
			json.put("data", data);
		} catch (SQLException e) {
			json.put("result", 1);
		} finally {
			try {
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(json.toString());
		return json.toString().trim() ;
	}
	
	public String  doGetSubjectComment(long id) {
		JSONObject json=new JSONObject();
		json.put("code", 36);
		try {
			Statement statement = conn.createStatement();
			// 查询post信息
			JSONObject data1=new JSONObject();
			String sql = "select  subject.photo as photo,subject.name as name,appuser.name as author,"
					+ "appuser.school as school,appuser.academy as academy,"
					+ "subject.detail as detail from subject,appuser where appuser._id=subject.author"
					+ " and subject._id="+id;
			resultSet = statement.executeQuery(sql);
			resultSet.first();
			String image= resultSet.getString("photo");
			data1.put("photo",image==null?"empty":image);
			data1.put("name",resultSet.getString("name"));
			data1.put("author", resultSet.getString("author"));
			data1.put("school", resultSet.getString("school"));
			data1.put("academy", resultSet.getString("academy"));
			data1.put("detail",resultSet.getString("detail"));
		    //查询学生人数
			String sql0="select count(*) as studentcount from selectsubject where selectsubject.subject="+id;
			resultSet = statement.executeQuery(sql0);
			resultSet.first();
		    data1.put("studentCount",  resultSet.getInt("studentcount"));
			//查询评论数目
			String sql1 = "select  count(*) as subjectcount from subjectcomment where subjectcomment.subject="+id;
			resultSet = statement.executeQuery(sql1);
			resultSet.first();
		    data1.put("commentCount",  resultSet.getInt("subjectcount"));
		    System.out.println(data1.toString());
		     //查询评论信息
		    JSONArray data2=new JSONArray();
		    String sql2 = "select subjectcomment._id as id,appuser.photo as photo,appuser.name as name,"
		    		+ "subjectcomment.content as content, subjectcomment.time as time"
		    		+ " from subjectcomment,appuser where subjectcomment.author=appuser._id and"
		    		+ " subjectcomment.subject="+id;
			resultSet = statement.executeQuery(sql2);
			resultSet.beforeFirst();
			while(resultSet.next()){
				JSONObject value=new JSONObject();
				value.put("id", resultSet.getLong("id"));
				String photo=resultSet.getString("photo");
				value.put("photo", photo==null?"empty":photo);
				value.put("name", resultSet.getString("name"));
				value.put("content", resultSet.getString("content"));
				value.put("time", resultSet.getLong("time"));
				data2.add(value);
			}
					
			json.put("result", 0);
			json.put("data1", data1);
			json.put("data2", data2);
			System.out.println(json.toString().trim());
			return  json.toString().trim();
			
		} catch (SQLException e) {
			e.printStackTrace();
			json.put("result", 1);
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(json.toString().trim());
		return json.toString().trim() ;
	}
	
	public String doGetTeacherSubject(long id){
		JSONObject json=new JSONObject();
		json.put("code", 36);
		try {
			Statement statement = conn.createStatement();
		     //查询老师(ID)开设的的课程信息
		    JSONArray data=new JSONArray();
		    String sql ="select  subject._id as id,subject.photo as photo,subject.name as name,appuser.name as author, "
		    		+"appuser.school as school,appuser.academy as academy,subject.detail as detail"
		    		+" from subject,appuser  where  subject.author=appuser._id and subject.author= "+id;
			resultSet = statement.executeQuery(sql);
			resultSet.beforeFirst();
			while(resultSet.next()){
				JSONObject value=new JSONObject();
				value.put("id", resultSet.getLong("id"));
				value.put("photo", resultSet.getString("photo"));
				value.put("name", resultSet.getString("name"));
				value.put("author", resultSet.getString("author"));
				value.put("school", resultSet.getString("school"));
				value.put("academy", resultSet.getString("academy"));
				value.put("detail", resultSet.getString("detail"));
				data.add(value);
			}
			json.put("result", 0);
			json.put("data", data);
		} catch (SQLException e) {
			json.put("result", 1);
		} finally {
			try {
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString().trim() ;
	}
	
	public String doSelectSubject(long userId,long subject){
		  System.out.println(userId+":"+subject);
		   JSONObject jsonObject=new JSONObject();
		   jsonObject.put("code", 44);
			try {
				Statement statement = conn.createStatement();
				String sql0 = "select *  from selectsubject where subject="+subject+" and user="+userId;
				resultSet = statement.executeQuery(sql0);
				resultSet.last();
				int count=resultSet.getRow();
				System.out.println("count:"+count);
				if (count!=0) {
					jsonObject.put("result", 1);
				}else{
				  String sql = "insert into selectsubject(user,subject) values ("+userId+","+subject+")";
				   int result = statement.executeUpdate(sql);
				   System.out.println("result:"+result);
				   if (result>0) {
					   jsonObject.put("result", 0);
				   } else {
					   jsonObject.put("result", 2);
				  }
				}
			} catch (SQLException e) {
				 e.printStackTrace();
				  jsonObject.put("result", 2);
			} finally {
				try {
					conn.close();
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}		
		return jsonObject.toString().trim();
	}
	
	public String doGetAllSubject(){
		JSONObject json=new JSONObject();
		json.put("code", 42);
		try {
			Statement statement = conn.createStatement();
		     //查询学生ID的课程信息
		    JSONArray data=new JSONArray();
		    String sql ="select  subject._id as id,subject.photo as photo,subject.name as name,appuser.name as author, "
		    		+"appuser.school as school,appuser.academy as academy,subject.detail as detail  "
		    		+"from subject,appuser  where  subject.author=appuser._id"; 
			resultSet = statement.executeQuery(sql);
			resultSet.beforeFirst();
			while(resultSet.next()){
				JSONObject value=new JSONObject();
				value.put("id", resultSet.getLong("id"));
				value.put("photo", resultSet.getString("photo"));
				value.put("name", resultSet.getString("name"));
				value.put("author", resultSet.getString("author"));
				value.put("school", resultSet.getString("school"));
				value.put("academy", resultSet.getString("academy"));
				value.put("detail", resultSet.getString("detail"));
				data.add(value);
			}		
			json.put("result", 0);
			json.put("data", data);
			System.out.println(json.toString().trim());
			return  json.toString().trim();
			
		} catch (SQLException e) {
		} finally {
			try {
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		json.put("result", 1);
		return json.toString().trim() ;
	}
	
	public String doGetStudentSubject(long id){
		JSONObject json=new JSONObject();
		json.put("code", 34);
		try {
			Statement statement = conn.createStatement();
		     //查询学生ID的课程信息
		    JSONArray data=new JSONArray();
		    String sql ="select  subject._id as id,subject.photo as photo,subject.name as name,appuser.name as author, "
		    		+"appuser.school as school,appuser.academy as academy,subject.detail as detail  "
		    		+"from selectsubject,subject,appuser  where selectsubject.subject=subject._id "
		    		+"and subject.author=appuser._id and selectsubject.user="+id; 
			resultSet = statement.executeQuery(sql);
			resultSet.beforeFirst();
			while(resultSet.next()){
				JSONObject value=new JSONObject();
				value.put("id", resultSet.getLong("id"));
				value.put("photo", resultSet.getString("photo"));
				value.put("name", resultSet.getString("name"));
				value.put("author", resultSet.getString("author"));
				value.put("school", resultSet.getString("school"));
				value.put("academy", resultSet.getString("academy"));
				value.put("detail", resultSet.getString("detail"));
				data.add(value);
			}		
			json.put("result", 0);
			json.put("data", data);
			System.out.println(json.toString().trim());
			return  json.toString().trim();
			
		} catch (SQLException e) {
		} finally {
			try {
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		json.put("result", 1);
		return json.toString().trim() ;
	}
	
	public String  doCreateSubject(SubjectData subjectData) {
		JSONObject json=new JSONObject();
		json.put("code", 32);
		try {
			Statement statement = conn.createStatement();
			System.out.println("xx");
			String sql = "insert into subject (author,photo,name,detail,time) values ("
					+ subjectData.author
					+ ",'"
					+ subjectData.photo
					+"','"
					+subjectData.name
					+ "','"
					+subjectData.describe
					+ "',"
					+subjectData.time
					+ ")";
			int result = statement.executeUpdate(sql);
			System.out.println("xx");
			if (result>0) {
				json.put("result", 0);
			} else {
				json.put("result", 1);
			}
		} catch (SQLException e) {
			json.put("result", 1);
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	return json.toString().trim();
	}
	//课程评论
	
	public String doSendSubjectComment( SubjectCommentData subjectCommentData){
		JSONObject json=new JSONObject();
		json.put("code", 46);
		try {
			Statement statement = conn.createStatement();
			String sql = "insert into subjectcomment (author,subject,content,time) values ("
					+ subjectCommentData.author
					+ ","
					+ subjectCommentData.subject
					+",'"
					+subjectCommentData.content
					+ "',"
					+subjectCommentData.time
					+ " )";
			System.out.println("back0"+json.toString().trim());
			int result = statement.executeUpdate(sql);
			System.out.println("back"+json.toString().trim()+" "+result);
			System.out.println(result);
			if (result>0) {
				json.put("result", 0);
			} else {
				json.put("result", 1);
			}
		} catch (SQLException e) {
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	System.out.println("back"+json.toString().trim());
	return json.toString().trim();
	}
	
	//删除学生选择的课程
	public String doDeleteSelectSubject(long user,long subject ){
		JSONObject json=new JSONObject();
		json.put("code", 48);
		try {
			Statement statement = conn.createStatement();
		    resultSet=statement.executeQuery("select _id from selectsubject where user="+user+" and subject="+subject);
			resultSet.first();
			long id=resultSet.getLong("_id");
			int result = statement.executeUpdate("delete from selectsubject where _id="+id);
			if (result>0) {
				json.put("result", 0);
			} else {
				json.put("result", 1);
			}
		} catch (SQLException e) {
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	return json.toString().trim();
	}
	
	//评论接口
	public String doSendPostComment( PostCommentData postCommentData){
		JSONObject json=new JSONObject();
		json.put("code", 30);
		System.out.println(postCommentData.content);
		try {
			Statement statement = conn.createStatement();
			String sql = "insert into postcomment (author,post,content,time) values ("
					+ postCommentData.author
					+ ","
					+ postCommentData.post
					+",'"
					+postCommentData.content
					+ "',"
					+postCommentData.time
					+ " )";
			System.out.println("back0"+json.toString().trim());
			int result = statement.executeUpdate(sql);
			System.out.println("back"+json.toString().trim()+" "+result);
			System.out.println(result);
			if (result>0) {
				json.put("result", 0);
			} else {
				json.put("result", 1);
			}
		} catch (SQLException e) {
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	System.out.println("back"+json.toString().trim());
	return json.toString().trim();
	}
	
	
	//获取帖子信息以及评论列表
	public String doGetComment(long id){
		JSONObject json=new JSONObject();
		json.put("code", 28);
		try {
			Statement statement = conn.createStatement();
			// 查询post信息
			JSONObject data1=new JSONObject();
			String sql = "select  appuser.photo as photo,appuser.name as name,post.content as content,post.time as time "
					+"from post,appuser where post._id= "+id+" and post.author=appuser._id";
			resultSet = statement.executeQuery(sql);
			resultSet.first();
			String image= resultSet.getString("photo");
			data1.put("photo",image==null?"empty":image);
			data1.put("name",resultSet.getString("name"));
			data1.put("content",resultSet.getString("content"));
			data1.put("time",resultSet.getLong("time"));
					
			//查询评论数目
			String sql1 = "select  count(*) as count from postcomment where postcomment.post="+id;
			resultSet = statement.executeQuery(sql1);
			resultSet.first();
		    data1.put("count",  resultSet.getInt("count"));
		     //查询评论信息
		    JSONArray data2=new JSONArray();
		    String sql2 = "select postcomment._id as id,appuser.photo as photo,appuser.name as name,postcomment.content as content, "
		    		+"postcomment.time as time from postcomment,appuser where postcomment.author=appuser._id and postcomment.post="+id;
			resultSet = statement.executeQuery(sql2);
			resultSet.beforeFirst();
			while(resultSet.next()){
				JSONObject value=new JSONObject();
				value.put("id", resultSet.getLong("id"));
				System.out.println("photo:"+resultSet.getString("photo"));
				String photo=resultSet.getString("photo");
				value.put("photo", photo==null?"empty":photo);
				value.put("name", resultSet.getString("name"));
				value.put("content", resultSet.getString("content"));
				value.put("time", resultSet.getLong("time"));
				data2.add(value);
			}
					
			json.put("result", 0);
			json.put("data1", data1);
			json.put("data2", data2);
			System.out.println(json.toString().trim());
			return  json.toString().trim();
			
		} catch (SQLException e) {
			json.put("result", 1);
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return json.toString().trim() ;
	}
	
	
	//帖子列表获取帖子信息
	public String doGetPost(){
		JSONObject json=new JSONObject();
		try {
			Statement statement = conn.createStatement();
			String sql = "select post._id as id,appuser.photo as photo,appuser.name as name,"
			        +"post.content as content ,post.time as time "
					+ "from appuser,post where appuser._id=post.author "
			        +" order by time desc";
			resultSet = statement.executeQuery(sql);
			resultSet.last();
			int count=resultSet.getRow();
			json.put("code", 26);
			System.out.println(count);
			if (count>0) {
				json.put("result", 0);
				JSONArray array=new JSONArray();
				resultSet.beforeFirst();
				while(resultSet.next()){
					JSONObject value=new JSONObject();
					value.put("id",resultSet.getString("id") );
					String photo=resultSet.getString("photo");
					value.put("photo", photo==null?"empty":photo);
					value.put("name", resultSet.getString("name"));
					value.put("content", resultSet.getString("content"));
					value.put("time", resultSet.getLong("time"));
				   value.put("count", 1);
					array.add(value);
				}
				System.out.println(array.size());
				json.put("data", array);
			}else{
				json.put("result", 1);
			}
		} catch (SQLException e) {
		    	json.put("result", 1);
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println(json.toString());
		return json.toString().trim();
	}

	public String  doLogin(UserData userData) {
		JSONObject resultJson = new JSONObject();
		try {	
			resultJson.put("code", 22);			
			Statement statement = conn.createStatement();
			// 检验no是否重复
			String sql = "select *  from appuser where type="+userData.type+" and email='"+userData.email+"' and password='"+userData.password+"'";
			resultSet = statement.executeQuery(sql);
			resultSet.last();
			int count=resultSet.getRow();
			if (count!=0) {
				resultJson.put("result", 0);
				resultSet.first();
				JSONObject data=new JSONObject();
				data.put("id", resultSet.getLong("_id"));
				data.put("email", resultSet.getString("email"));
				data.put("name", resultSet.getString("name"));
				data.put("type", resultSet.getInt("type"));
				resultJson.put("data", data);
			}else{
				resultJson.put("result", 1);
			}
		} catch (SQLException e) {
			resultJson.put("result", 2);
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return  resultJson.toString().trim();
	}
	
	public String doSendPost(PostData postData){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("code", 24);
		try {
			Statement statement = conn.createStatement();
			String sql = "insert into post (author,content,time) values ('"
					+ postData.author
					+ "','"
					+ postData.content
					+ "',"
					+postData.time
					+ " )";
			int result = statement.executeUpdate(sql);
			if (result>0) {
				jsonObject.put("result", 0);
			} else {
				jsonObject.put("result", 1);
			}
		} catch (SQLException e) {
			jsonObject.put("result", 1);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	return jsonObject.toString().trim();
	}
	
	public String doRegister(UserData userData) {
		   JSONObject jsonObject=new JSONObject();
		   jsonObject.put("code", 20);
			try {
				Statement statement = conn.createStatement();
				String sql0 = "select *  from appuser where email='"+userData.email+"'";
				resultSet = statement.executeQuery(sql0);
				resultSet.last();
				int count=resultSet.getRow();
				if (count!=0) {
					jsonObject.put("result", 1);
				}else{
				    String sql = "insert into appuser(type,email,name,no,password,school,academy,time) values ('"
						+ userData.type+ "','"
						+ userData.email+ "','"
						+ userData.name+ "','"
						+ userData.no+ "','"
						+ userData.password+ "','"
						+ userData.school+ "','"
						+ userData.academy+"',"
						+userData.time+ ")";
				   int result = statement.executeUpdate(sql);
				   if (result>0) {
					  jsonObject.put("result", 0);
				   } else {
					 jsonObject.put("result", 2);
				  }
				}
			} catch (SQLException e) {
				  jsonObject.put("result", 2);
			} finally {
				try {
					conn.close();
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}		
		return jsonObject.toString().trim();
	}
	
}
