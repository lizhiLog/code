package com.meizu.graduation.subject;

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
import com.meizu.graduation.data.ResourceData;
import com.meizu.graduation.data.SubjectData;


@WebServlet("/UploadFileServlet")
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public UploadFileServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		 Map map = new HashMap(); 
	    boolean isMultipart = ServletFileUpload.isMultipartContent(request);
	    System.out.print(isMultipart);
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
                   if(code==40){
                   	JSONObject data=jsonObject.getJSONObject("data");
                   	System.out.println(data.toString());
                   	ResourceData resourceData=new ResourceData();
                   	resourceData.subject=data.getLong("subject");
                   	resourceData.type=data.getInt("type");
                   	resourceData.title=data.getString("title");
                   	resourceData.name=data.getString("name");
                   	resourceData.detail=data.getString("detail");
                   	resourceData.time=data.getLong("time");
                   	DbHelper dbHelper = new DbHelper("test", "root", "123456");
                   	String result = dbHelper.doUploadFile(resourceData);
           			PrintWriter out=response.getWriter();
           			out.write(result);                       
                   }
               } else{
               	String fileName = item.getName();
                  byte[] data = item.get();
                  String path= request.getSession().getServletContext().getRealPath("/")+"resource";
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
