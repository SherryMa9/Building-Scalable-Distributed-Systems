package DAO;

import Client.LiftRideEvent;
import utils.DBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class DailySkiRecordDaoImpl implements DailySkiRecordDao {

    private MongoCollection<Document> collection;

    public DailySkiRecordDaoImpl() {
        // Use DBConnection to get the MongoClient instance
        MongoClient mongoClient = DBConnection.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("SkierData");
        collection = database.getCollection("DailySkiRecords");
    }

    @Override
    public void updateDailySkiRecord(LiftRideEvent event) {
        // Create a filter to find the document that should be updated
        Document filter = new Document("skierId", event.getSkierID())
                .append("date", event.getDayID());

        // Create an update operation
        Document update = new Document("$inc", new Document("lifts", 1)
                .append("vertical", event.getLiftID() * 10));

        // Perform the update
        collection.updateOne(filter, update);
    }
}
