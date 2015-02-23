package com.meizu.graduation.dao;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.meizu.graduation.data.PostCommentData;
import com.meizu.graduation.data.PostData;
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
	
	//评论接口
	public String doSendComment( PostCommentData postCommentData){
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
			data1.put("photo", resultSet.getString("photo"));
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
				value.put("photo", resultSet.getString("photo"));
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
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		json.put("result", 1);
		return json.toString().trim() ;
	}
	
	
	//帖子列表获取帖子信息
	public String doGetPost(){
		try {
			Statement statement = conn.createStatement();
			// 检验no是否重复
			String sql = "select post._id as id,appuser.photo as photo,appuser.name as name,post.content as content ,post.time as time "
					+ "from appuser,post where appuser._id=post.author order by time desc";
			resultSet = statement.executeQuery(sql);
			resultSet.last();
			int count=resultSet.getRow();
			System.out.println("count:"+count);
			JSONObject json=new JSONObject();
			json.put("code", 26);
			if (count>0) {
				json.put("result", 0);
				System.out.print("yyy");
				JSONArray array=new JSONArray();
				resultSet.beforeFirst();
				while(resultSet.next()){
					long id=resultSet.getLong("id");
					String photo=resultSet.getString("photo");
					String name=resultSet.getString("name");
					String content=resultSet.getString("content");
					long time=resultSet.getLong("time");
					System.out.println("info:"+id+":"+photo+":"+name+":"+content+":"+time);
				  /*  String sql1="select count(*)  as count from postcomment where post="+id;
					ResultSet resultSet1=statement.executeQuery(sql1);
					resultSet1.last();
					int num=resultSet1.getInt("count");
					resultSet1.close();*/
					JSONObject value=new JSONObject();
					value.put("id", id);
					value.put("photo", "123");
					value.put("name", name);
					value.put("content", content);
					value.put("time", time);
				   value.put("count", 2);
					array.add(value);
				}
				System.out.print("yyy");
				json.put("data", array);
				System.out.println("json---<:"+json.toString().trim());
			}else{
				json.put("result", 1);
			}
			System.out.println(json.toString().trim());
			return  json.toString().trim();
		} catch (SQLException e) {
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return "error";
	}

	public int  doLogin(UserData userData) {
		try {
			Statement statement = conn.createStatement();
			// 检验no是否重复
			String sql = "select *  from appuser where type="+userData.type+" and email='"+userData.email+"' and password='"+userData.password+"'";
			resultSet = statement.executeQuery(sql);
			resultSet.last();
			int count=resultSet.getRow();
			System.out.println(count);
			if (count!=0) {
				if(userData.type==0){
					return 0;
				}else{
					return 1;
				}
			}
			return 2;
		} catch (SQLException e) {
		} finally {
			try {
				conn.close();
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return 3;
	}
	
	public int doSendPost(PostData postData){
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
			System.out.println(result);
			if (result>0) {
				return 0;  //发贴成功
			} else {
				return 1;//发贴出现问题
			}
		} catch (SQLException e) {
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	return 1;
	}
	
	public int doRegister(UserData userData) {
			try {
				Statement statement = conn.createStatement();
				// 检验no是否重复
				String sql0 = "select *  from appuser where email='"+userData.email+"'";
				resultSet = statement.executeQuery(sql0);
				resultSet.last();
				int count=resultSet.getRow();
				System.out.println(count);
				if (count!=0) {
					return 1; // email被注册
				}
				String sql = "insert into appuser(type,email,name,no,password,school,academy,time) values ('"
						+ userData.type
						+ "','"
						+ userData.email
						+ "','"
						+ userData.name
						+ "','"
						+ userData.no
						+ "','"
						+ userData.password
						+ "','"
						+ userData.school
						+ "','"
						+ userData.academy
						+"',"
						+userData.time
						+ ")";
				int result = statement.executeUpdate(sql);
				if (result>0) {
					return 0;  //注册成功
				} else {
					return 2;//注册出现问题
				}
			} catch (SQLException e) {
			} finally {
				try {
					conn.close();
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}		
		return 2;
	}
	
}
