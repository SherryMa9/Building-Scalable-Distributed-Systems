package utils;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.mongodb.ConnectionString;


public class DBConnection {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static MongoClient mongoClient = null;

    public static MongoClient getMongoClient() {
        if (mongoClient == null) {
            ConnectionString connString = new ConnectionString(CONNECTION_STRING);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .retryWrites(true)
                    .build();
            mongoClient = MongoClients.create(settings);
        }
        return mongoClient;
    }
}
