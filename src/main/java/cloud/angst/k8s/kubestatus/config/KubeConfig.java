package cloud.angst.k8s.kubestatus.config;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
public final class KubeConfig {
    //Authorization: Bearer
    private final String token;
    private final String endpoint;

    private KubeConfig(String token, String endpoint) {
        this.token = token;
        this.endpoint = endpoint;
    }

    public static KubeConfig fromUrl(String endpoint) {
        return new KubeConfig(null, endpoint);
    }

    public static KubeConfig fromCluster() throws IOException {
        String tok;
        try (InputStream in = new FileInputStream("/var/run/secrets/kubernetes.io/serviceaccount/token")) {
            StringBuilder strB = new StringBuilder();
            int b;
            while (-1 != (b = in.read())) {
                strB.append((char) (b));
            }
            tok = strB.toString();
        }
        return new KubeConfig(tok, "https://kubernetes.default.svc");
    }

    public boolean hasToken() {
        return token != null;
    }
}
