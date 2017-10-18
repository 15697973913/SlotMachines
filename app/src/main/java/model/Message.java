package model;
/**
 * 
 * @author Administrator
 *
 */
public class Message {
 
	private String erminalId ;     //客户端Id
	private String content;        //内容
	private String paperMoney;     //纸币金额
	private String hardMoney;      //硬币金额
	private String zfbMoneny;      //支付宝金额
	private String wxMoneny ;      //微信金额
	private String driverName;     //司机姓名
	private String sendTime;       //发送时间
	private String QRcode;        // 付款二维码编号
	private String payName ;       // 支付名称


	public String getQRcode() {
		return QRcode;
	}

	public void setQRcode(String QRcode) {
		this.QRcode = QRcode;
	}

	public String getPayName() {
		return payName;
	}

	public void setPayName(String payName) {
		this.payName = payName;
	}
	public String getErminalId() {
		return erminalId;
	}
	public void setErminalId(String erminalId) {
		this.erminalId = erminalId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPaperMoney() {
		return paperMoney;
	}
	public void setPaperMoney(String paperMoney) {
		this.paperMoney = paperMoney;
	}
	public String getHardMoney() {
		return hardMoney;
	}
	public void setHardMoney(String hardMoney) {
		this.hardMoney = hardMoney;
	}
	public String getZfbMoneny() {
		return zfbMoneny;
	}
	public void setZfbMoneny(String zfbMoneny) {
		this.zfbMoneny = zfbMoneny;
	}
	public String getWxMoneny() {
		return wxMoneny;
	}
	public void setWxMoneny(String wxMoneny) {
		this.wxMoneny = wxMoneny;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	@Override
	public String toString() {
		return "Message [erminalId=" + erminalId + ", content=" + content
				+ ", paperMoney=" + paperMoney + ", hardMoney=" + hardMoney
				+ ", zfbMoneny=" + zfbMoneny + ", wxMoneny=" + wxMoneny
				+ ", payName=" + payName + ", QRcode=" + QRcode
				+ ", driverName=" + driverName + ", sendTime=" + sendTime + "]";
	}
	
	

}
