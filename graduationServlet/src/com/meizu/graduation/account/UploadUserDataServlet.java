package com.meizu.graduation.account;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.meizu.graduation.dao.DbHelper;
import com.meizu.graduation.data.SubjectData;
import com.meizu.graduation.data.UserData;

@WebServlet("/UploadUserData")
public class UploadUserDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UploadUserDataServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		  response.setCharacterEncoding("utf-8");
			 Map map = new HashMap(); 
			FileItemFactory factory=new DiskFileItemFactory();
			ServletFileUpload upload=new ServletFileUpload(factory);
			try {
				List items=upload.parseRequest(request);
				Iterator iterator=items.iterator();
				while(iterator.hasNext()){
					FileItem item=(FileItem) iterator.next();
					if (item.isFormField()) {       
	                    String paramName = item.getFieldName();    
	                    String paramValue = item.getString();
	                    map.put(paramName, paramValue);
	                    String json=(String) map.get("json");  
	                    JSONObject jsonObject = JSONObject.fromObject(json);
	                    int code=jsonObject.getInt("code");
	                    if(code==52){
	                    	JSONObject data=jsonObject.getJSONObject("data");
	                    	System.out.println(data.toString());
	                    	UserData userData=new UserData();
	                    	userData.id=data.getLong("user");
	                    	userData.photo=data.getString("photo");
	                    	userData.name=data.getString("name");
	                    	userData.sex=data.getInt("sex");
	                    	userData.school=data.getString("school");
	                    	userData.academy=data.getString("academy");
	                    	userData.detail=data.getString("detail");
	                    	DbHelper dbHelper = new DbHelper("test", "root", "123456");
	                    	String result = dbHelper.doUploadUserData(userData);
	            			PrintWriter out=response.getWriter();
	            			out.write(result);                       
	                    }
	                } else{
	                	String fileName = item.getName();
	                   byte[] data = item.get();
	                   String path= request.getSession().getServletContext().getRealPath("/")+"photo/user";
	                   File dir = new File(path);
	                   if (!dir.exists()) {
	                       dir.mkdirs();
	                   }
	                    String filePath =path+"/"+fileName;
	                    FileOutputStream fos = new FileOutputStream(filePath);    
	                    fos.write(data);    
	                    fos.close();
	                }
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}

	}
