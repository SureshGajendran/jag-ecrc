package ca.bc.gov.open.ecrc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import ca.bc.gov.open.ecrc.configuration.EcrcProperties;
import ca.bc.gov.open.ecrc.exception.OauthServiceException;
import ca.bc.gov.open.ecrc.service.OauthServicesImpl;
import ca.bc.gov.open.ecrc.util.AES256;
import ca.bc.gov.open.ecrc.util.JwtTokenGenerator;
import ch.qos.logback.classic.Logger;

import java.util.UUID;

/**
 * 
 * Oauth Controller class. 
 * 
 * Provides OIDC endpoints serving the eCRC front end. 
 * 
 * @author shaunmillargov
 *
 */
@Configuration
@EnableConfigurationProperties(EcrcProperties.class)
@RestController
public class OauthController {
	
	@Autowired
	private OauthServicesImpl oauthServices;
	
	@Autowired
	private EcrcProperties ecrcProps;
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(OauthController.class);

	/*
	 * Redirects to IDP to initiate authorization process. 
	 * 
	 */
	@ResponseStatus(code = HttpStatus.FOUND)
	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	public void authenticate(HttpServletRequest request, HttpServletResponse response) throws OauthServiceException {
		
		//TODO - need to add and validated the incoming token for this request. This token 
		// has been generated by the front end and does not contain any IdP information at this point. 
		
		System.out.println("/authorize called...");
		
		try {
			response.setHeader("Location", oauthServices.getIDPRedirect().toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new OauthServiceException("Configuration Error");
		}
	}

	@ResponseStatus(code = HttpStatus.FOUND)
	@RequestMapping(value = "/public/getBCSCUrl", method = RequestMethod.GET)
	public ResponseEntity<String> getBCSCUrl(@RequestParam(name = "redirectUrl", required = true) String redirectUrl) throws OauthServiceException {
		//TODO: Extract guid generated from front end
		logger.info("BCSC URL request received {}", UUID.randomUUID().toString());

		try {
			return new ResponseEntity(oauthServices.getIDPRedirect(redirectUrl).toString(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new OauthServiceException("Configuration Error");
		}
	}

	/*
	 * 
	 * Uses authorization code provided back from call to /authorize to generate access token from IdP. 
	 * 
	 * Responds to SPA with new JWT (complete with userInfo and encrypted IdP token).
	 * 
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam(name = "code", required = true) String authCode) throws OauthServiceException {
		
		//TODO - need to add and validated the incoming token for this request. This token 
		// has been generated by the front end and does not contain any IdP information at this point. 
		
		System.out.println("login called... Authorization code being used to generate token = " + authCode);
		
		AccessTokenResponse token = null; 
		try {
			token = oauthServices.getToken(authCode);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new OauthServiceException("Error generating client token. ", e);
		}
		
		// Fetch corresponding Userinfo from the IdP server.  
		UserInfo userInfo = oauthServices.getUserInfo((BearerAccessToken)token.toSuccessResponse().getTokens().getAccessToken());
		
		// Encrypt the token received from the IdP. This token contains the accessToken, the refreshToken, and the ID Token. This block 
		// must be decrypted and used for subsequent calls back to the IdP from this layer (e.g. /refreshToken). 
	    String encryptedTokens = null; 
	    try {
	    	System.out.println("token : " + token.toJSONObject().toJSONString());
			encryptedTokens = AES256.encrypt(token.toJSONObject().toJSONString(), ecrcProps.getOauthSecret());
			System.out.println("encrypted token = " + encryptedTokens);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new OauthServiceException("Error encrypting token. ", e);
		}
		
		// Send the new FE JWT in the response body to the caller. 
        return JwtTokenGenerator.generateFEAccessToken(userInfo, encryptedTokens, ecrcProps.getJwtSecret(), ecrcProps.getOauthJwtExpiry(), ecrcProps.getJwtRole());
		
	}

}