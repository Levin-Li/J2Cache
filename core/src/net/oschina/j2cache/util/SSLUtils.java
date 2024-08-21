/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * 使用 Redis SSL连接
 *
 * @author llsydn
 */
public class SSLUtils {

    private final static Logger log = LoggerFactory.getLogger(SSLUtils.class);

    /**
     * 创建一个SSLSocketFactory，该工厂信任keystoreFile中的所有证书。
     */
    public static SSLSocketFactory createTrustStoreSslSocketFactory(String keyStore, String keystoreFile, String keystorePassword) {
        try {
            KeyStore clientStore = KeyStore.getInstance(keyStore); //密钥库文件格式：JKS、JCEKS、PKCS12、BKS、UBER
            clientStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManagerFactory factory = KeyManagerFactory.getInstance("SunX509");
            factory.init(clientStore, keystorePassword.toCharArray());
            sslContext.init(factory.getKeyManagers(), new TrustManager[]{new AllTrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            log.error("SSLSocketFactory 创建失败：" + e.getMessage());
        }
        log.debug("SSLSocketFactory 无法创建");
        return null;
    }

    /**
     * 不校验域名
     */
    public static class UnverifiedHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 绕过验证X.509证书
     */
    public static class AllTrustManager implements X509TrustManager {

        public AllTrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
