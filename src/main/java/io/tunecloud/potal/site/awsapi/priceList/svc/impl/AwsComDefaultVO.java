/**
 * 
 */
package io.tunecloud.potal.site.awsapi.priceList.svc.impl;

import lombok.Data;

@Data
public class AwsComDefaultVO {
	//Common Product
	private String servicecode;
	private String usagetype;
	private String servicename;

	//Common Terms
	private String unit;
	private String beginRange;
	private String endRange;
	private String currencyCode;
	private String currencyRate;
	private String description;
	
	private String location;
}
