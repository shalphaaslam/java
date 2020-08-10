package com.shalpha.app.config.util;

/**
 * @author Shalpha Aslam
 * Parser to fetch the realm information from the access token received
 * through authorization header
 */
public class CustomJwtParser {
	
	String iss;
	String realm;
	public CustomJwtParser() {
		// empty constructor required for object mapper
	}
	public String getIss() {
		return iss;
	}
	public String getRealm() {
		return realm;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}

}
