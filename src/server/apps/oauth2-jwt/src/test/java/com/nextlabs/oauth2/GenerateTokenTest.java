/*
 * Copyright 2017 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Jan 2017
 * 
 * Author: sduan
 *
 */
package com.nextlabs.oauth2;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class GenerateTokenTest {
	public static void main(String[] args) throws IllegalArgumentException, UnsupportedEncodingException {
		String token = JWT.create()
		        .withIssuer("nextlabs")
		        .withSubject("duanshiqiang")
		        .withExpiresAt(new Date(System.currentTimeMillis() + 1000*120))
		        .sign(Algorithm.HMAC256("newsecret"));
		System.out.println(token);
	}
}
