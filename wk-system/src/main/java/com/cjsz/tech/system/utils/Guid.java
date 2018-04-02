package com.cjsz.tech.system.utils;

import java.util.UUID;

public class Guid {
	
	public static String newId() {
		return UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
	}
	public static String newIdUp() {
		return UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	}
}
