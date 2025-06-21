package flinkconnect;

import Deserializer.JSONValueDeserializationSchema;
import Dto.Radiation;
import Dto.RadiationAggregation;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.connector.jdbc.JdbcSink;

import java.util.ArrayList;
import java.util.List;

public class DataStreamJob {

	// MySQL database connection details
	private static final String jdbcUrl = "jdbc:mysql://mysql:3306/mydatabase";
	private static final String username = "root";
	private static final String password = "password";

	public static void main(String[] args) throws Exception {
		// Set up the execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// Kafka topics to consume data from
		String[] topics = {"testing-NA", "testing-EU", "testing-AS"};
		// List to hold the data streams from each Kafka topic
		List<DataStream<Radiation>> streams = new ArrayList<>();

		// Create a Kafka source and JDBC sink for each topic
		for (String topic : topics) {
			DataStream<Radiation> radiationStream = createKafkaSource(env, topic);
			createJdbcSink(radiationStream, getTableName(topic));
			streams.add(radiationStream);
		}

		// Combine all the individual topic streams into a single stream
		DataStream<Radiation> combinedStream = streams.get(0);
		for (int i = 1; i < streams.size(); i++) {
			combinedStream = combinedStream.union(streams.get(i));
		}

		// Create a JDBC sink for the combined stream to the "radiations" table
		createJdbcSink(combinedStream, "radiations");

		// Perform aggregation by continent
		DataStream<RadiationAggregation> aggregatedStream = combinedStream
				.keyBy(Radiation::getContinent)
				.window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
				.aggregate(new RadiationAggregator(), new RadiationProcessWindowFunction());

		// Create a JDBC sink for the aggregated results
		createAggregationJdbcSink(aggregatedStream);

		// Execute the Flink job
		env.execute("Flink Kafka to MySQL Job");
	}

	// Method to create a Kafka source for a given topic
	private static DataStream<Radiation> createKafkaSource(StreamExecutionEnvironment env, String topic) {
		KafkaSource<Radiation> source = KafkaSource.<Radiation>builder()
				.setBootstrapServers("kafka:9093")
				.setTopics(topic)
				.setGroupId("flink-group")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new JSONValueDeserializationSchema())
				.build();

		return env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source: " + topic);
	}

	// Method to create a JDBC sink for a given data stream and table name
	private static void createJdbcSink(DataStream<Radiation> radiationStream, String tableName) {
		JdbcExecutionOptions executionOptions = new JdbcExecutionOptions.Builder()
				.withBatchSize(10) // Set the batch size for inserts
				.withBatchIntervalMs(200) // Set the interval between batches
				.withMaxRetries(15) // Set the maximum number of retries for inserts
				.build();

		JdbcConnectionOptions connOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
				.withUrl(jdbcUrl) // Set the JDBC URL
				.withDriverName("com.mysql.cj.jdbc.Driver") // Set the JDBC driver name
				.withUsername(username) // Set the database username
				.withPassword(password) // Set the database password
				.build();

		// Add sink to the data stream
		radiationStream.addSink(JdbcSink.sink(
				"INSERT INTO " + tableName + " (captured_time, latitude, longitude, value, continent) VALUES (?, ?, ?, ?, ?)",
				(JdbcStatementBuilder<Radiation>) (preparedStatement, radiation) -> {
					preparedStatement.setString(1, radiation.getCaptured_time());
					preparedStatement.setString(2, radiation.getLatitude());
					preparedStatement.setString(3, radiation.getLongitude());
					preparedStatement.setString(4, radiation.getValue());
					preparedStatement.setString(5, radiation.getContinent());
				},
				executionOptions,
				connOptions
		)).name("Insert into " + tableName + " table sink");
	}

	// Method to generate table name from topic name
	private static String getTableName(String topic) {
		return "radiations_" + topic.split("-")[1].toUpperCase(); // Extracts region from topic name and converts it to uppercase
	}

	// Method to create a JDBC sink for the aggregated results
	private static void createAggregationJdbcSink(DataStream<RadiationAggregation> aggregatedStream) {
		JdbcExecutionOptions executionOptions = new JdbcExecutionOptions.Builder()
				.withBatchSize(10) // Set the batch size for inserts
				.withBatchIntervalMs(200) // Set the interval between batches
				.withMaxRetries(15) // Set the maximum number of retries for inserts
				.build();

		JdbcConnectionOptions connOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
				.withUrl(jdbcUrl) // Set the JDBC URL
				.withDriverName("com.mysql.cj.jdbc.Driver") // Set the JDBC driver name
				.withUsername(username) // Set the database username
				.withPassword(password) // Set the database password
				.build();

		// Add sink to the aggregated data stream
		aggregatedStream.addSink(JdbcSink.sink(
				"INSERT INTO radiation_by_continents (continent, total_radiation, average_radiation) " +
						"VALUES (?, ?, ?) " +
						"ON DUPLICATE KEY UPDATE total_radiation = total_radiation + VALUES(total_radiation), " +
						"average_radiation = (average_radiation + VALUES(average_radiation)) / 2",
				(JdbcStatementBuilder<RadiationAggregation>) (preparedStatement, agg) -> {
					preparedStatement.setString(1, agg.getContinent());
					preparedStatement.setString(2, Double.toString(agg.getTotalRadiation()));
					preparedStatement.setString(3, Double.toString(agg.getAverageRadiation()));
				},
				executionOptions,
				connOptions
		)).name("Insert into " + "radiation_by_continents" + " table sink");
	}

	// Aggregator to calculate total and average radiation
	private static class RadiationAggregator implements AggregateFunction<Radiation, Tuple2<Double, Integer>, Tuple2<Double, Integer>> {
		@Override
		public Tuple2<Double, Integer> createAccumulator() {
			return new Tuple2<>(0.0, 0);
		}

		@Override
		public Tuple2<Double, Integer> add(Radiation value, Tuple2<Double, Integer> accumulator) {
			return new Tuple2<>(accumulator.f0 + Double.parseDouble(value.getValue()), accumulator.f1 + 1);
		}

		@Override
		public Tuple2<Double, Integer> getResult(Tuple2<Double, Integer> accumulator) {
			return accumulator;
		}

		@Override
		public Tuple2<Double, Integer> merge(Tuple2<Double, Integer> a, Tuple2<Double, Integer> b) {
			return new Tuple2<>(a.f0 + b.f0, a.f1 + b.f1);
		}
	}

	// Process window function to calculate average radiation
	private static class RadiationProcessWindowFunction extends ProcessWindowFunction<Tuple2<Double, Integer>, RadiationAggregation, String, TimeWindow> {
		@Override
		public void process(String key, Context context, Iterable<Tuple2<Double, Integer>> elements, Collector<RadiationAggregation> out) {
			Tuple2<Double, Integer> result = elements.iterator().next();
			double totalRadiation = result.f0;
			double averageRadiation = totalRadiation / result.f1;
			out.collect(new RadiationAggregation(key, totalRadiation, averageRadiation));
		}
	}
}
