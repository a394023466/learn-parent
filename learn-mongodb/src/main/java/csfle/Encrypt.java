package csfle;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.Document;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * mongodb生成MasterKey，并配置客户端自动解密
 *
 * @author: liyuzhi
 * @date: 2020/8/17 17:59
 * @version: 1
 */
public class Encrypt {
    public static void main(String[] args) throws  Exception {


        String path = "master-key.txt";

        byte[] localMasterKey= new byte[96];

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

        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(clientSettings)
                .keyVaultNamespace(keyVaultNamespace.getFullName())
                .kmsProviders(kmsProviders)
                .build();

        ClientEncryption clientEncryption = ClientEncryptions.create(clientEncryptionSettings);



        MongoClient mongoClient = MongoClients.create(clientSettings);

        MongoCollection<Document> keyVaultCollection = mongoClient
                .getDatabase(keyVaultNamespace.getDatabaseName())
                .getCollection(keyVaultNamespace.getCollectionName());



// Ensure that two data keys cannot share the same keyAltName.
        keyVaultCollection.createIndex(Indexes.ascending("keyAltNames"),
                new IndexOptions().unique(true)
                        .partialFilterExpression(Filters.exists("keyAltNames")));


        MongoCollection<Document> collection = mongoClient.getDatabase("test").getCollection("coll");
        BsonBinary dataKeyId = clientEncryption.createDataKey("local", new DataKeyOptions());
        BsonBinary encryptedFieldValue = clientEncryption.encrypt(new BsonString("12345678911111111111111"),
                new EncryptOptions("AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic").keyId(dataKeyId));
        collection.insertOne(new Document("encryptedField", encryptedFieldValue));

    }
}
