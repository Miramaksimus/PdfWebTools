package edu.uoc.tfg.pdfwebtools.appconfig.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

public class PasswordEncoder {

	private static final Logger logger = LoggerFactory
			.getLogger(PasswordEncoder.class);

	public static String encode(String password) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
		String encodedPassword = encoder.encode(password);
		return encodedPassword;
	}


	public static void main(String[] args) {
		// introduce contraseñas para codificarlas
		String[] passwords = {"admin"};

		if (args.length == 1) {
			logger.info(encode(args[0]));
		}
		else {
			logger.info("Encoding passwords: {}", Arrays.toString(passwords));
			for(String psswd: passwords){
				logger.info("{}: [{}]", psswd, encode(psswd));
			}
		}
	}

}