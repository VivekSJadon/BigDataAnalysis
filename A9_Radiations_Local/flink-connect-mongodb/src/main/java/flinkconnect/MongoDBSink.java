package flinkconnect;

import Dto.Radiation;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import java.util.Collections;


public class MongoDBSink extends RichSinkFunction<Radiation> {
    private transient MongoCollection<Document> collection;

    @Override
    public void open(Configuration parameters) {
        // Create the MongoDB client to establish connection
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress("localhost", 27017))))
                .codecRegistry(createCodecRegistry())
                .build();

        com.mongodb.client.MongoClient mongoClient = MongoClients.create(settings);

        // Access the MongoDB database and collection
        // At this stage, if the Mongo DB and collection does not exist, they would get auto-created
        MongoDatabase database = mongoClient.getDatabase("radiation_db");
        collection = database.getCollection("radiation_measurements");
    }

    @Override
    public void invoke(Radiation radiation, Context context) {
        // Create a BSON document from the radiation data
        Document radiationDocument = new Document()
                .append("capturedTime", radiation.getCaptured_time())
                .append("latitude", radiation.getLatitude())
                .append("longitude", radiation.getLongitude())
                .append("value", radiation.getValue())
                .append("continent", radiation.getContinent());

        // Insert the document into the MongoDB collection
        collection.insertOne(radiationDocument);
    }

    @Override
    public void close() {
        // Clean up resources, if needed
    }

    private CodecRegistry createCodecRegistry() {
        // The method createCodecRegistry is a helper method that is used to create a CodecRegistry object for MongoDB.
        // In MongoDB, a CodecRegistry is responsible for encoding and decoding Java objects to
        // BSON (Binary JSON) format, which is the native format used by MongoDB to store and retrieve data.
        return CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
    }
}
