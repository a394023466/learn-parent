package csfle;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.Document;
import org.bson.types.Binary;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Lenovo
 * @date:Create：in 2020/8/17 18:12
 */
public class Decrypt {
    public static void main(String[] args) throws IOException {

        String path = "master-key.txt";

        byte[] localMasterKey = new byte[96];

        try (FileInputStream fis = new FileInputStream(path)) {
            fis.read(localMasterKey, 0, 96);
        }


        Map<String, Map<String, Object>> kmsProviders = new HashMap<String, Map<String, Object>>() {{
            put("local", new HashMap<String, Object>() {{
                put("key", localMasterKey);
            }});
        }};
        MongoNamespace keyVaultNamespace = new MongoNamespace("encryption.testKeyVault");


        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://test:27017"))
                .autoEncryptionSettings(AutoEncryptionSettings.builder()
                        .keyVaultNamespace(keyVaultNamespace.getFullName())
                        .kmsProviders(kmsProviders)
                        .bypassAutoEncryption(true)
                        .build())
                .build();

        MongoClient mongoClient = MongoClients.create(clientSettings);

        MongoCollection<Document> collection = mongoClient.getDatabase("test").getCollection("coll");

        for (Document document : collection.find()) {
            System.out.println(document.toJson());
        }
    }
}
