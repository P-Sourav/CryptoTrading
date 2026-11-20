package com.example.CryptoTrading.service;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService 
{
	// previously this field was never injected, so every send threw a NullPointerException
	private final ObjectProvider<JavaMailSender> javaMailSenderProvider;
	
	@Value("${spring.mail.username:}")
	private String fromAddress;
	
	public EmailService(ObjectProvider<JavaMailSender> javaMailSenderProvider)
	{
		this.javaMailSenderProvider = javaMailSenderProvider;
	}
	
	public void sendVerificationOtpEmail(String email, String otp) throws MessagingException
	{
		JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
		
		if (javaMailSender == null || fromAddress == null || fromAddress.isBlank())
		{
			throw new MailSendException("Email is not configured: set MAIL_HOST, MAIL_USERNAME and MAIL_PASSWORD");
		}
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
		
		String subject = "Verify OTP";
		String text = "Your verification code is "+ otp;
		
		mimeMessageHelper.setFrom(fromAddress);
		mimeMessageHelper.setSubject(subject);
		mimeMessageHelper.setText(text);
		
		mimeMessageHelper.setTo(email);
		
		try 
		{
			javaMailSender.send(mimeMessage);
		} 
		catch (MailException e) 
		{
			throw new MailSendException(e.getMessage());
		}
	}
	
}
