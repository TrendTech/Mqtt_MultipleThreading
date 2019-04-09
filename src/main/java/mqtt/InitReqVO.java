package mqtt;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * 初始化请求
 */
public class InitReqVO {
	
	@JSONField(ordinal = 0)
	public int cmd;
	@JSONField(ordinal = 1)
	public String tempUid;
	@JSONField(ordinal = 2)
	private String platform;
	@JSONField(ordinal = 3)
	private String version;

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public String getTempUid() {
		return tempUid;
	}

	public void setTempUid(String tempUid) {
		this.tempUid = tempUid;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "InitReqVO [cmd=" + cmd + ", tempUid=" + tempUid + ", platform=" + platform + ", version=" + version
				+ "]";
	}

}
