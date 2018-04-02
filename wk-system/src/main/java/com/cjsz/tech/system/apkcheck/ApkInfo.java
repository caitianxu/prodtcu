package com.cjsz.tech.system.apkcheck;

import java.util.List;


@SuppressWarnings("unchecked")
public class ApkInfo {

	private String versionCode;
	private String versionName;
	private String apkPackage;
	private String minSdkVersion;
	private String apkName;
	private List uses_permission;

	public ApkInfo() {
		versionCode = null;
		versionName = null;
		apkPackage = null;
		minSdkVersion = null;
		apkName = null;
		uses_permission = null;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getApkPackage() {
		return apkPackage;
	}

	public void setApkPackage(String apkPackage) {
		this.apkPackage = apkPackage;
	}

	public String getMinSdkVersion() {
		return minSdkVersion;
	}

	public void setMinSdkVersion(String minSdkVersion) {
		this.minSdkVersion = minSdkVersion;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	@SuppressWarnings("unchecked")
	public List getUses_permission() {
		return uses_permission;
	}

	@SuppressWarnings("unchecked")
	public void setUses_permission(List uses_permission) {
		this.uses_permission = uses_permission;
	}

	public String toString() {
//		return (new StringBuilder("ApkInfo [versionCode=")).append(versionCode)
//				.append(", versionName=").append(versionName).append(
//						", apkPackage=").append(apkPackage).append(
//						", minSdkVersion=").append(minSdkVersion).append(
//						", apkName=").append(apkName).append(
//						", uses_permission=").append(uses_permission).append(
//						"]").toString();
            return (new StringBuilder("&version="+versionName+"&versioncode="+versionCode)).toString();
	}
}
