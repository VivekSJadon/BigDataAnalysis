package flinkconnect;

import Deserializer.JSONValueDeserializationSchema;
import Dto.Radiation;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class DataStreamJob {

	public static void main(String[] args) throws Exception {
		// Sets up the execution environment, which is the main entry point
		// to building Flink applications.
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// Change topics according to need
		String topic = "testing-AN";

		KafkaSource<Radiation> source = KafkaSource.<Radiation>builder()
				.setBootstrapServers("localhost:9092")
				.setTopics(topic)
				.setGroupId("flink-group")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new JSONValueDeserializationSchema())
				.build();
		DataStream<Radiation> radiationStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka source");
		radiationStream.print();  //To see data in UI console

		radiationStream.addSink(new MongoDBSink()).name("Insert into MongoDB");


		// Execute program, beginning computation.
		env.execute("Flink Java API Skeleton");
	}
}


