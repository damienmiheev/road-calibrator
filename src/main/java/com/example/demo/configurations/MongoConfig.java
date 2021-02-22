package com.example.demo.configurations;

import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoAuditing
@Configuration
@EnableMongoRepositories(basePackages = "com.example.demo.repositories")
public class MongoConfig {
    private static final String CONNECTION_STRING = "mongodb://%s:%d";
    private static final String MONGO_DB_URL = "localhost";
    private static final Integer MONGO_DB_PORT = 27017;
    private static final String MONGO_DB_NAME = "embedded_db";

    @Bean
    public MongoTemplate mongoTemplate() throws IOException {
        IMongodConfig mongoConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                                                             .net(new Net(MONGO_DB_URL, MONGO_DB_PORT,
                                                                     Network.localhostIsIPv6()))
                                                             .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodExecutable mongodExecutable = starter.prepare(mongoConfig);
        mongodExecutable.start();
        return new MongoTemplate(MongoClients.create(String.format(CONNECTION_STRING, MONGO_DB_URL, MONGO_DB_PORT)),
                MONGO_DB_NAME);
    }
}
