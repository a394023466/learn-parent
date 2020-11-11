package csfle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 用于生成本地管理的主密钥
 *
 * @author: liyuzhi
 * @date: 2020/8/17 16:55
 * @version: 1
 */
public class CreateMasterKeyFile {
    public static void main(String[] args) throws IOException {

        byte[] localMasterKey = new byte[96];
        new SecureRandom().nextBytes(localMasterKey);

        try (FileOutputStream stream = new FileOutputStream("master-key.txt")) {
            stream.write(localMasterKey);
        }
    }
}
