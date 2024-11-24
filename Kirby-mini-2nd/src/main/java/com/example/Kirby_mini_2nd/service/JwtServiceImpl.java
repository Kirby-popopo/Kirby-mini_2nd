package com.example.Kirby_mini_2nd.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final String secretKey = "dfadfksfjadfaflsdfnklbnlknklfdfdlff123213123";

    @Override           //입력된 key값과 value를 바탕으로 JWT토큰을 생성
    public String getToken(String key, Object value) {
        Date d = new Date();
        log.info(d.toString() +" : " + d.getTime());
        d.setTime(d.getTime()+60*60*1000);  // 테스트를 위해서 3분간만 유효한 토큰을 만듬

        // secretKey를 바탕으로 HMAC-SHA256알고리즘을 사용하여 서명키를 생성
        byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
        Key signKey = new SecretKeySpec(secretByteKey, SignatureAlgorithm.HS256.getJcaName());
        
        Map<String, Object> headerMap = new HashMap<>();
        // 헤더에 JWT타입 key:typ, value:JWT
        // 알고리즘 alg key:alg, value:HS256
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        //key value를 클레임으로 추가
        JwtBuilder builder = Jwts.builder()
                .setHeader(headerMap)
                .setClaims(map)
                .setExpiration(d)
                .signWith(signKey, SignatureAlgorithm.HS256);
        return " "+builder.compact();
        // Bearer유형의 토큰 문자열
    }

    @Override
    // 토큰에서 클레임을 추출하는 메서드
    public Claims getClaims(String token) {
        log.info("getClaims() 호출 : "+token);
        if (token != null && !"".equals(token)) {
            token = token.replace("Bearer ", "");
            try {
                byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
                // 서명키 생성후 컴증한뒤 클레임 반환
                Key signKey = new SecretKeySpec(secretByteKey, SignatureAlgorithm.HS256.getJcaName());
                return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
            } catch (ExpiredJwtException e) {
                log.error("토큰 만료");
            } catch (JwtException e) {
                log.error("토큰 유효하지 않음");
            }
        }
        return null;
    }

    // 토큰의 클레임에서 사용자 ID 추출하여 반환하는 메서드
    @Override
    public int getUserId(String token) {
        Claims claims = this.getClaims(token);
        if(claims != null){

            return Integer.parseInt(claims.get("id").toString());
        }
        return 0;
    }
}