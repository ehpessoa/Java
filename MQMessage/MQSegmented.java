import java.io.BufferedInputStream;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.ibm.mq.*;

public class MQSegmented {

	private String QMGR_HOSTNAME = null;
	private int QMGR_PORT = 0;
	private String QMGR_CHANNEL = null;
	private String QMGR_NAME = null;
	private String QMGR_QUEUE = null;
	private String FILE_NAME = null;
	
	public MQSegmented(String host, String port, String queueMgr, String channel, String queue, String file) throws Exception {
		
		this.QMGR_HOSTNAME = host;
		this.QMGR_PORT = new Integer(port);
		this.QMGR_NAME = queueMgr;
		this.QMGR_CHANNEL = channel;		
		this.QMGR_QUEUE = queue;
		this.FILE_NAME = file;
		
		MQEnvironment.hostname = QMGR_HOSTNAME;
		MQEnvironment.port = QMGR_PORT;
		MQEnvironment.channel = QMGR_CHANNEL;
		
	}
	
	
	@SuppressWarnings("resource")
	public void put() throws Exception {
		
		try {
			
			System.out.println("MQ Message started");
			
			MQQueueManager queueManager = new MQQueueManager(QMGR_NAME);		
			MQQueue queue = queueManager.accessQueue(QMGR_QUEUE, MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING);
			
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = MQC.MQPMO_LOGICAL_ORDER | MQC.MQPMO_SYNCPOINT;			
			MQMessage message = new MQMessage();
			//message.format = MQC.MQFMT_STRING;
			
			File f = new File(FILE_NAME);
			int blockSize = 4194304;
			int totalLen = (int) f.length();
			int totalsegs = totalLen / blockSize;			 
			int segs = 0;
			
		    BufferedInputStream bis = null;
			bis = new BufferedInputStream(new FileInputStream(f));			
			byte abyte0[] = new byte[blockSize];
			int k = bis.read(abyte0, 0, blockSize-1);
			while (k  > 0){
				StringBuffer sb = new StringBuffer();
				sb.append((new String(abyte0)).substring(0, k));				
                k = bis.read(abyte0, 0, blockSize-1);
				if (segs<totalsegs)
					message.messageFlags = MQC.MQMF_SEGMENT;
		        else
		        	message.messageFlags = MQC.MQMF_LAST_SEGMENT;
				segs++;
				message.write(sb.toString().getBytes());
				queue.put(message,pmo);	
				message.clearMessage();
            }
				
			queue.close();
			queueManager.disconnect();
			
			System.out.println("Message put on queue");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception();
		}
		
	}
	
	public void get() throws Exception {
		
		try {
			
			System.out.println("MQ Message started");
			
			MQQueueManager queueManager = new MQQueueManager(QMGR_NAME);		
			MQQueue queue = queueManager.accessQueue(QMGR_QUEUE, MQC.MQOO_INPUT_EXCLUSIVE | MQC.MQOO_INQUIRE | MQC.MQOO_FAIL_IF_QUIESCING);
			
			MQGetMessageOptions gmo = new MQGetMessageOptions();			
			gmo.options = MQC.MQGMO_SYNCPOINT | MQC.MQGMO_LOGICAL_ORDER | MQC.MQGMO_ALL_SEGMENTS_AVAILABLE | MQC.MQGMO_WAIT; 
			gmo.matchOptions = MQC.MQMO_NONE; 
			gmo.segmentation = MQC.MQSEG_ALLOWED; 
			gmo.waitInterval=MQC.MQWI_UNLIMITED;

			ByteArrayOutputStream baos = new ByteArrayOutputStream(100 * 1024);
			MQMessage msg = new MQMessage();
			queue.get(msg, gmo);
			byte[] v = new byte[msg.getMessageLength()];
			msg.readFully(v);
			baos.write(v);
			byte[] groupId = msg.groupId;
			if (gmo.segmentStatus == MQC.MQSS_SEGMENT) {
				gmo.matchOptions = MQC.MQMO_MATCH_GROUP_ID;
				gmo.waitInterval = MQC.MQWI_UNLIMITED;
				do {
					msg = new MQMessage();
					msg.groupId = groupId;
					queue.get(msg, gmo);
					v = new byte[msg.getMessageLength()];
					msg.readFully(v);
					baos.write(v);

				} while (!(gmo.segmentStatus == MQC.MQSS_LAST_SEGMENT));
			}
			FileOutputStream fos = new FileOutputStream (new File(FILE_NAME));		    
		    baos.writeTo(fos);
		      
		    fos.close();
		    baos.close();
			queue.close();
			queueManager.disconnect();
			
			System.out.println("Message got from queue");

		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new Exception();
		}
		
	}
	
	public static void main(String[] args) {
		
		if (args.length == 7) {
			String op = args[0];
			String host = args[1];
			String port = args[2];			
			String queueMgr = args[3];
			String channel = args[4];
			String queue = args[5];
			String file = args[6];			
			try {
				MQSegmented mq = new MQSegmented(host, port, queueMgr, channel, queue, file);
				if (op.equalsIgnoreCase("put")) {
					mq.put();
				} else if (op.equalsIgnoreCase("get")) {
					mq.get();
				} else {
					System.out.println("Invalid operation. It should be put or get!");
					System.exit(1);
				}
				System.exit(0);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		} else {
			System.out.println("You should define the operation, queue and files!");
		}

	}
}