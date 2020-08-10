package com.shalpha.app.config.util;

/**
 * @author Shalpha Aslam
 * Fetches the tenant URI that can be accessed through out the current live thread
 *
 */
public class TenantContext {
	private static ThreadLocal<String> liveTenantUri = new ThreadLocal<>();

	  public static String getLiveTenantUri() {
	    return liveTenantUri.get();
	  }

	  public static void setLiveTenantUri(String tenant) {
	    liveTenantUri.set(tenant);
	  }

	  public static void clear() {
	    liveTenantUri.set(null);
	  }
}
