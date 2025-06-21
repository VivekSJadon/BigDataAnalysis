from kafka.admin import KafkaAdminClient, NewTopic


admin_client = KafkaAdminClient(
    bootstrap_servers="localhost:9092", 
    client_id='test'
)

topic_list = []
'''
topic_list.append(NewTopic(name="AS", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="EU", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="OC", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="NA", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="SA", num_partitions=1, replication_factor=1))
'''
topic_list.append(NewTopic(name="testing-AS", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="testing-EU", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="testing-OC", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="testing-NA", num_partitions=1, replication_factor=1))
topic_list.append(NewTopic(name="testing-SA", num_partitions=1, replication_factor=1))
admin_client.create_topics(new_topics=topic_list, validate_only=False)