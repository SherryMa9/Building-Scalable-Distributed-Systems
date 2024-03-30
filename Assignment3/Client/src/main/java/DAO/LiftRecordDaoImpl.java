package DAO;

import Client.LiftRideEvent;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import utils.DBConnection;

public class LiftRecordDaoImpl implements LiftRecordDao {

    private MongoCollection<Document> collection;

    public LiftRecordDaoImpl() {
        MongoClient mongoClient = DBConnection.getMongoClient();
        MongoDatabase database = mongoClient.getDatabase("SkierData");
        collection = database.getCollection("LiftRecords");
    }

    @Override
    public void insertLiftRide(LiftRideEvent event) {
        // Convert LiftRideEvent to Document
        Document doc = new Document("skierId", event.getSkierID())
                .append("resortId", event.getResortID())
                .append("liftId", event.getLiftID())
                .append("date", event.getDayID())
                .append("time", event.getTime());

        // Insert the document into the collection
        collection.insertOne(doc);
    }
}

