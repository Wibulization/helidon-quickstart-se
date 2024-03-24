package io.helidon.examples.quickstart.se;

import java.io.File;
import java.io.IOException;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData.Array;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import io.helidon.common.context.Contexts;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.examples.User;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import java.lang.StringBuilder;

public class UserService implements HttpService {
    private final DbClient dbClient;

    UserService() {
        this.dbClient = Contexts.globalContext()
                .get(DbClient.class)
                .orElseGet(() -> DbClient.create(Config.global().get("db")));
        dbClient.execute().namedDml("create-table");
    }

    @Override
    public void routing(HttpRules rules) {
        rules
                .get("/getUser", this::getUser)
                .post("/postUser", this::postUser);
    }

    private void getUser(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException {
        // Deserialize Users from disk
        DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> dataFileReader = new DataFileReader<User>(new File("users.avro"), userDatumReader);
        User user = null;
        StringBuilder respondContent = new StringBuilder();
        while (dataFileReader.hasNext()) {
            // Reuse user object by passing it to next(). This saves us from
            // allocating and garbage collecting many objects for files with
            // many items.
            user = dataFileReader.next(user);
            System.out.println(user);
            respondContent.append(user.toString()).append("\n");
        }
        serverResponse.send(respondContent.toString());
        dataFileReader.close();
    }

    private void postUser(ServerRequest serverRequest, ServerResponse serverResponse) throws IOException {
        User user1 = new User();
        user1.setName("Alyssa");
        user1.setFavoriteNumber(256);
        // Leave favorite color null

        // Alternate constructor
        User user2 = new User("Ben", 7, "red");

        // Construct via builder
        User user3 = User.newBuilder()
                .setName("Charlie")
                .setFavoriteColor("blue")
                .setFavoriteNumber(null)
                .build();

        // Serialize user1, user2 and user3 to disk
        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);
        dataFileWriter.create(user1.getSchema(), new File("users.avro"));
        dataFileWriter.append(user1);
        dataFileWriter.append(user2);
        dataFileWriter.append(user3);
        serverResponse.send("postUser send and users.arvo create");
        dataFileWriter.close();
    }
}
