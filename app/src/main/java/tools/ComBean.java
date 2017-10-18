package tools;

import java.text.SimpleDateFormat;

/**
 * @author benjaminwan
 *��������
 */
public class ComBean {
	private static final String TAG = ComBean.class.getSimpleName();
		public byte[] bRec=null;
		public String sRecTime="";
		public String sComPort="";
		public ComBean(String sPort, byte[] buffer, int size){
			sComPort=sPort;
			bRec=new byte[size];
			for (int i = 0; i < size; i++)
			{
				bRec[i]=buffer[i];
			}
			lg.v(TAG,"获取的数据为：" + MyFunc.ByteArrToHex(bRec));
			SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
			sRecTime = sDateFormat.format(new java.util.Date()); 
		}
}

