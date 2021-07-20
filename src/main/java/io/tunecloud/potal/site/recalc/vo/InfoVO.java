package io.tunecloud.potal.site.recalc.vo;


import lombok.Getter;

@Getter
public class InfoVO {
	
	private String accessKey;
	private String secretAccessKey;
	private String serviceCode;	
	private String serviceName;
	private String start;	
	private String end;	
	
	
	public void setAccountInf() {
		String accessKey = "AKIAVXG4IB3B57753VJC";		
		this.accessKey = accessKey;		
		String secretAccessKey = "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";
		this.secretAccessKey = secretAccessKey;
	}
	public void setsecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
}
