<%@ page import="org.apache.commons.fileupload.DiskFileUpload, 
	org.apache.commons.fileupload.FileItem, 
	java.util.List, 
	java.util.Iterator, 
	java.io.File"
%>
<%
try {
	DiskFileUpload fu = new DiskFileUpload();
	fu.setSizeMax(10000000);               
	
	List fileItems = fu.parseRequest(request); 
	Iterator itr = fileItems.iterator();
	while(itr.hasNext()) {
	  FileItem fi = (FileItem)itr.next();
	  if(!fi.isFormField()) {
	    if ( fi.getSize() > 0 ) {
	        File fNew= new File(application.getRealPath("/"), fi.getName());            	            
	        fi.write(fNew);	            
	    }
	  }
	}
	out.println("Upload Successful!");
} catch (Exception e) {
	e.printStackTrace();
}
%>
