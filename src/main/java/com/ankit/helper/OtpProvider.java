package com.ankit.helper;

import java.util.Random;

public class OtpProvider {

	public int generateOtp() {
		Random random=new Random();
		int lowerLimit=1000;
		int upperLimit=9999;
		int otp=lowerLimit+random.nextInt(upperLimit);
		return otp;
	}
}
