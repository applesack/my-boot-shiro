package xyz.scootaloo.bootshiro.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import xyz.scootaloo.bootshiro.domain.dto.JwtAccount;
import xyz.scootaloo.bootshiro.support.Assert;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * jwt工具
 * 用于 签发，解析，验证
 *
 * @author : flutterdash@qq.com
 * @since : 2020年12月06日 14:21
 */
public class JwtUtils {

    // 密钥， json->javaObject， 加密解密器
    public static final String SECRET_KEY = "?::4343fdf4fdf6cvf):";
    private static final ObjectMapper CONVERTER = new ObjectMapper();
    private static final CompressionCodecResolver codecResolver = new DefaultCompressionCodecResolver();

    /**
     * jwt默认属性
     * COUNT_3 : 这个数字3代表一个jwt字符串按照小数点来分割只能被分成3段
     * DEFAULT_ISSUER : 默认签发人的标识
     * DEFAULT_PERIOD : 默认的失效时间，单位(毫秒)
     * DEFAULT_COMPRESSION : 默认的解压方式
     * SECRECY_KEY_BYTES : 使用密钥base64加密生成的比特数组
     */
    private static final int COUNT_3 = 3;
    private static final String DEFAULT_ISSUER = "token-server";
    private static final Long   DEFAULT_PERIOD = 36000L;
    private static final SignatureAlgorithm DEFAULT_ALGORITHM = SignatureAlgorithm.HS512;
    private static final CompressionCodec DEFAULT_COMPRESSION = CompressionCodecs.DEFLATE;
    private static final byte[] SECRECY_KEY_BYTES = DatatypeConverter.parseBase64Binary(SECRET_KEY);

    /**
     * 由于生成jwt所需要的参数比较多，而且一般情况下很多属性都有默认值，
     * 所以这里使用一个Builder来填充这些属性，方法的使用者只需要根据自己的需要设置对应的属性即可。
     * @see DefaultValueBuilder
     * @see JwtUtils#issueJWT(DefaultValueBuilder)
     * -----------------------------------------------------------------------
     * @param appId 必选项，生成jwt需要一个标识用户的id
     * @return 含有默认值的构建者模式对象
     */
    public static DefaultValueBuilder issueJWT(String appId) {
        return new DefaultValueBuilder(appId);
    }

    private static String issueJWT(DefaultValueBuilder dvBuilder) {
        // 获取当前时间戳
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setId(dvBuilder.id)
                .setSubject(dvBuilder.subject)
                .setIssuer(dvBuilder.issuer)
                .setIssuedAt(getDateOfCurrentTimeMillis(currentTimeMillis))
                .setExpiration(expirationOf(dvBuilder.period, currentTimeMillis))
                .claim("roles", dvBuilder.roles)
                .claim("perms", dvBuilder.permissions)
                .compressWith(DEFAULT_COMPRESSION)
                .signWith(dvBuilder.algorithm, SECRECY_KEY_BYTES)
                .compact();
    }

    public static JwtAccount parseJwt(String jwt, String appKey)
            throws ExpiredJwtException, UnsupportedJwtException,
                    MalformedJwtException, SignatureException, IllegalArgumentException  {

        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(appKey))
                .parseClaimsJws(jwt)
                .getBody();
        JwtAccount jwtAccount = new JwtAccount();
        jwtAccount.setTokenId(claims.getId());    // 令牌ID
        jwtAccount.setAppId(claims.getSubject()); // 客户标识
        jwtAccount.setIssuer(claims.getIssuer()); // 签发者
        jwtAccount.setIssuedAt(claims.getIssuedAt()); // 签发时间
        jwtAccount.setAudience(claims.getAudience()); // 接收方
        jwtAccount.setRoles(claims.get("roles", String.class)); // 访问主张-角色
        jwtAccount.setPerms(claims.get("perms", String.class)); // 访问主张-权限
        return jwtAccount;
    }

    public static String parseJwtPayload(String jwt) {
        Assert.hasText(jwt, "JWT String argument cannot be null or empty.");
        List<String> segments = StringUtils.splitBy(jwt, '.');
        if (segments.size() != COUNT_3) {
            String msg = "JWT strings must contain exactly 2 period characters. Found: " + segments.size();
            throw new MalformedJwtException(msg);
        }

        String base64UrlEncodedHeader = segments.get(0);  // 头部: 包含 类型，算法
        String base64UrlEncodedPayload = segments.get(1); // 内容载体
        String base64UrlEncodedDigest = segments.get(2);  // 签名，用于校验内容载体的有效性

        if (base64UrlEncodedPayload == null) {
            throw new MalformedJwtException("JWT string '" + jwt + "' is missing a body/payload.");
        }
        // =============== Header =================
        DefaultHeader<?> header;
        CompressionCodec compressionCodec = null;
        if (base64UrlEncodedHeader != null) {
            String origValue = TextCodec.BASE64URL.decodeToString(base64UrlEncodedHeader);
            Map<String, Object> m = readValue(origValue);
            if (base64UrlEncodedDigest != null) {
                header = new DefaultJwsHeader(m);
                compressionCodec = codecResolver.resolveCompressionCodec(header);
            }
        }
        // =============== Body =================
        String payload;
        if (compressionCodec != null) {
            byte[] decompressed = compressionCodec.decompress(TextCodec.BASE64URL.decode(base64UrlEncodedPayload));
            payload = new String(decompressed, io.jsonwebtoken.lang.Strings.UTF_8);
        } else {
            payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);
        }
        return payload;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValue(String val) {
        try {
            return CONVERTER.readValue(val, Map.class);
        } catch (IOException e) {
            throw new MalformedJwtException("Unable to read JSON value: " + val, e);
        }
    }

    private static Date getDateOfCurrentTimeMillis(long currentTimeMillis) {
        return new Date(currentTimeMillis);
    }

    /**
     * 生成一个过期时间，标识一段时间后的时间点的Date对象。
     * 过期时间 = 当前时间 + 一段时间(period 单位毫秒)。
     * @param period period毫秒后的日期对象
     * @param currentTimeMillis System.currentTimeMillis() 获取
     * @return java.util.Date
     */
    private static Date expirationOf(long period, long currentTimeMillis) {
        return new Date(currentTimeMillis + (period * 1000));
    }

    private JwtUtils() {
    }

    public static class DefaultValueBuilder {
        private final String subject;
        private String id     = "";
        private String issuer = DEFAULT_ISSUER;
        private Long   period = DEFAULT_PERIOD;
        private String roles  = "";
        private String permissions = "";
        private SignatureAlgorithm algorithm = DEFAULT_ALGORITHM;

        public DefaultValueBuilder(String subject) {
            this.subject = subject == null ? "" : subject;
        }

        public DefaultValueBuilder id(String id) {
            this.id = id;
            return this;
        }

        public DefaultValueBuilder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public DefaultValueBuilder period(Long period) {
            this.period = period;
            return this;
        }

        public DefaultValueBuilder permissions(String permissions) {
            this.permissions = permissions;
            return this;
        }

        public DefaultValueBuilder roles(String roles) {
            this.roles = roles;
            return this;
        }

        public DefaultValueBuilder algorithm(SignatureAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public String create() {
            return issueJWT(this);
        }
    }

}
