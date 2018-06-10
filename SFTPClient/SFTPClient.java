import java.io.File;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * 
 * @author Avon Cosmetics
 * 
 */
public class SFTPClient {

	private static Logger log = Logger.getLogger(SFTPClient.class);
	private JSch jsch = new JSch();
	private Session session = null;
	private Channel channel = null;
	private ChannelSftp sftp = null;
	private int ftpPort = 22;

	/**
	 * 
	 */
	public SFTPClient() {

	}
	
	public SFTPClient(String server, String username, String password) throws Exception {
		
		try {
			open(server, username, password);
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}

	}
	
	/**
	 * 
	 * @param server
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	public void open(String server, String username, String password)
			throws Exception {

		try {
			
			String newServer = "";
			if ( server.indexOf(":") != -1  ) {
				newServer = server.substring(0,server.indexOf(":"));
				this.ftpPort = Integer.parseInt(server.substring(server.indexOf(":")+1,server.length()));
				server = newServer;
			}
			log.info("Conneting SFTP Server: " + server + ", port: " + this.ftpPort);
			
			session = jsch.getSession(username, server, this.ftpPort);
			log.info("Session created.");
			session.setPassword(password);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			log.info("Session connected.");

			log.info("Opening Channel.");
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}

	}


	/**
	 * 
	 * @param source
	 * @param target
	 * @param type
	 * @throws Exception
	 */
	public void put(String source, String target) throws Exception {

		try {

			log.info("Put the file " + source + " to " + target);
			File f = new File(source);
			FileInputStream fis = null;
			fis = new FileInputStream(f);			
			sftp.put(fis, target);
			if (fis != null) {
				fis.close();	
			}			
			String backup = f.getParent() + File.separator + "backup" + File.separator + f.getName();  
			move(source, backup);

		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	public void get(String source, String target) throws Exception {

		try {

			log.info("Get the file " + source + " to " + target);
			File f = new File(target);
			sftp.get(source, new FileOutputStream(f));
			log.info("File " + source + " was successfully retrieved");
			
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}

	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @throws Exception
	 */	
	public void move(String source, String target) throws Exception {
		
		try {
			
			log.info("Moving " + source + " to " + target);
			
			File fsource = new File(source);
			File ftarget = new File(target);
			if (fsource.canRead() && fsource.isFile()) {
				String dir;
				int indexSep = target.lastIndexOf(File.separatorChar);
				dir = (indexSep >= 0) ? target.substring(0, indexSep) : "." + File.separator;
				mkdir(dir);
				if ( !fsource.renameTo(ftarget) ) {
					log.info("For some reason the file " + source + " cannot be moved!");					
				}
			}
 
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}
	}

	/**
	 * 
	 * @param sDir
	 * @throws Exception
	 */
	public void mkdir(String sDir) throws Exception {

		File fDir;
		String subDir;
		try {						
			if (sDir.startsWith(File.separator)) {
				subDir = File.separator;
			} else {
				subDir = "";
			}
			StringTokenizer st = new StringTokenizer(sDir, File.separator);
			while (st.hasMoreTokens()) {
				subDir += File.separator + st.nextToken();
				fDir = new File(subDir);
				if (!fDir.exists()) {
					fDir.mkdir();
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}

	}	
	
	/**
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {

		try {
			log.info("Close connection.");
			sftp.quit();
		} catch (Exception e) {
			log.error(e);
			throw new Exception(e);
		}

	}
	
	public static void main(String[] args) {
		
		/*
		String operation = "put";
		String server = "spopessoaev7402.sa.avonet.net";		
		String username = "guest";
		String password = "guest123";
		String source = "c:\\SFTP\\ASSERTH\\avon1.txt";
		String target = "/AVON/avon1.txt";
		SFTPClient sftp = null;
		try {
			sftp = new SFTPClient(server, username, password);
			if ( operation.equalsIgnoreCase("put") ) {										
				sftp.put(source, target);
			} else if ( operation.equalsIgnoreCase("get") ) {
				sftp.get(source, target);
			} else {
				System.out.println( "Invalid operation. Must be PUT or GET");
			}			
			System.exit(0);
		} catch (Exception e) {
			log.error(e);
			System.exit(1);
		} finally {
			try {
				if ( sftp != null ) {
					sftp.close();
				}					
			} catch (Exception e) {
				log.error(e);
				System.exit(1);
			}			
		}
		*/
		
		if ( args.length < 6 ) {
			System.out.println("Number of arguments: " + args.length);
			System.out.println("Usage: SFtpClient <operation> <server> <username> <password> <source> <target>");
			System.out.println("Example 1: SFTPClient put server1 user1 pass1 c:/temp/test.txt /home/joe/test.txt");
			System.out.println("Example 2: SFTPClient get server1 joe doe /home/joe/test.txt c:/temp/test.txt");			
		} else {			
			String operation = args[0];
			String server = args[1];		
			String username = args[2];
			String password = args[3];			
			String source = args[4];
			String target = args[5];
			SFTPClient sftp = null;
			try {
				sftp = new SFTPClient(server, username, password);
				if ( operation.equalsIgnoreCase("put") ) {										
					sftp.put(source, target);
				} else if ( operation.equalsIgnoreCase("get") ) {
					sftp.get(source, target);
				} else {
					System.out.println( "Invalid operation. Must be PUT or GET");					
				}			
				System.exit(0);
			} catch (Exception e) {
				log.error(e);
				System.exit(1);
			} finally {
				try {
					if ( sftp != null ) {
						sftp.close();
					}					
				} catch (Exception e) {
					log.error(e);
					System.exit(1);
				}			
			}
		}
		
	}
	
	

}