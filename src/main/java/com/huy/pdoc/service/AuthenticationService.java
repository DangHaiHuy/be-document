package com.huy.pdoc.service;

import com.huy.pdoc.dto.request.AuthenticationRequest;
import com.huy.pdoc.dto.request.ExchangeTokenRequest;
import com.huy.pdoc.dto.request.IntrospectRequest;
import com.huy.pdoc.dto.request.LogoutRequest;
import com.huy.pdoc.dto.request.RefreshTokenRequest;
import com.huy.pdoc.dto.response.AuthenticationResponse;
import com.huy.pdoc.dto.response.ExchangeTokenResponse;
import com.huy.pdoc.dto.response.IntrospectResponse;
import com.huy.pdoc.dto.response.OutboundUserResponse;
import com.huy.pdoc.entity.InvalidatedToken;
import com.huy.pdoc.entity.Role;
import com.huy.pdoc.entity.User;
import com.huy.pdoc.exception.AppException;
import com.huy.pdoc.exception.ErrorCode;
import com.huy.pdoc.repository.InvalidatedTokenRepository;
import com.huy.pdoc.repository.RoleRepository;
import com.huy.pdoc.repository.UserRepository;
import com.huy.pdoc.repository.externalRepo.OutboundIdentityClient;
import com.huy.pdoc.repository.externalRepo.OutboundUserClient;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    @Value("${jwt.SIGNER_KEY}")
    @NonFinal
    String SIGNER_KEY;
    @Value("${jwt.valid-duration}")
    @NonFinal
    long VALID_DURATION;
    @Value("${jwt.refreshable-duration}")
    @NonFinal
    long REFRESH_DURATION;
    InvalidatedTokenRepository invalidatedTokenRepository;
    OutboundIdentityClient outboundIdentityClient;
    OutboundUserClient outboundUserClient;
    @Value("${outbound.client-id}")
    @NonFinal
    String CLIENT_ID;
    @Value("${outbound.client-secret}")
    @NonFinal
    String CLIENT_SECRET;
    @Value("${outbound.redirect-uri}")
    @NonFinal
    String REDIRECT_URI;
    @Value("${outbound.grant-type}")
    @NonFinal
    String GRANT_TYPE;
    RoleRepository roleRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println(SIGNER_KEY);
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED, "Not found this username in database"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isValid)
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Your password is incorrect");
        String token = generateToken(user);
        return AuthenticationResponse.builder().authenticated(isValid).token(token).build();
    }

    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("haihuy")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Mã bị lỗi");
        }
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        String token = introspectRequest.getToken();
        boolean valid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            if (e.getErrorCode().getCode() == 1011) {
                throw new AppException(ErrorCode.NOT_ACTIVATED,
                        "You need to activate your account to access this resource. Please check your mail");
            }
            valid = false;
        }
        return IntrospectResponse.builder().valid(valid).build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getAuthorities())) {
            user.getAuthorities().stream().forEach(role -> stringJoiner.add(role.getAuthority()));
        }
        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            SignedJWT signedJWT = verifyToken(request.getToken(), true);

            String JwtId = signedJWT.getJWTClaimsSet().getJWTID();
            Date exprityDate = signedJWT.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(new InvalidatedToken(JwtId, exprityDate));
        } catch (AppException e) {
            System.out.println("Token already expried or not match jwt format");
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefreshToken) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expirationDate = (isRefreshToken)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                        .plus(REFRESH_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean valid = signedJWT.verify(jwsVerifier);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Token expired");

        if (!(valid && expirationDate.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Token expried");
        }
        String username = signedJWT.getJWTClaimsSet().getSubject();
        if (valid) {
            if (userRepository.findActivatedByUsername(username) == false)
                throw new AppException(ErrorCode.NOT_ACTIVATED, "");
        }
        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest)
            throws JOSEException, ParseException {
        SignedJWT signedJWT = verifyToken(refreshTokenRequest.getToken(), true);

        String JwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder().expiryTime(expiryTime).id(JwtId).build();
        invalidatedTokenRepository.save(invalidatedToken);

        String username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No username in database"));

        String token = generateToken(user);
        return AuthenticationResponse.builder().authenticated(true).token(token).build();
    }

    public AuthenticationResponse outboundAuthentication(String code) {
        ExchangeTokenResponse exchangeTokenResponse = outboundIdentityClient
                .exchangeToken(ExchangeTokenRequest.builder()
                        .code(code)
                        .clientId(CLIENT_ID)
                        .clientSecret(CLIENT_SECRET)
                        .redirectUri(REDIRECT_URI)
                        .grantType(GRANT_TYPE)
                        .build());
        OutboundUserResponse userInfo = outboundUserClient.getUserInfo("json", exchangeTokenResponse.getAccessToken());
        Set<Role> authorities = new HashSet<Role>();
        Role roleUser = roleRepository.findByAuthority("USER");
        authorities.add(roleUser);
        User userExist = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(userInfo.getEmail())
                        .firstName(userInfo.getGivenName())
                        .lastName(userInfo.getFamilyName())
                        .authorities(authorities)
                        .email(userInfo.getEmail())
                        .picture(userInfo.getPicture())
                        .activated(true)
                        .activateCode(UUID.randomUUID().toString())
                        .build()));
        String token = generateToken(userExist);
        return AuthenticationResponse.builder().token(token).build();
    }
}
