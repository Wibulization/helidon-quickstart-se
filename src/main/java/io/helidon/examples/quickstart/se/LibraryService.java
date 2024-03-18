package io.helidon.examples.quickstart.se;

import io.helidon.common.context.Contexts;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.http.NotFoundException;
import io.helidon.http.Status;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import jakarta.json.JsonObject;

public class LibraryService implements HttpService {

    private final DbClient dbClient;

    LibraryService() {
        this.dbClient = Contexts.globalContext()
                .get(DbClient.class)
                .orElseGet(() -> DbClient.create(Config.global().get("db")));
        dbClient.execute().namedDml("create-table");

    }

    @Override
    public void routing(HttpRules rules) {
        rules
                .get("/{name}", this::getBook)
                .put("/{name}", this::addBook)
                .delete("/{name}", this::deleteBook)
                .get("/json/{name}", this::getJsonBook);
    }

    private void getBook(ServerRequest serverRequest, ServerResponse serverResponse) {
        String bookName = serverRequest.path().pathParameters().get("name");
        String bookInfo = dbClient.execute().namedGet("select-book", bookName)
                .map(row -> row.column("INFO").asString().get())
                .orElseThrow(() -> new NotFoundException("Book not found: " + bookName));
        serverResponse.send(bookInfo);
    }

    private void addBook(ServerRequest serverRequest, ServerResponse serverResponse) {
        String bookName = serverRequest.path().pathParameters().get("name");

        String newValue = serverRequest.content().as(String.class);
        dbClient.execute().createNamedInsert("insert-book")
                .addParam("name", bookName)
                .addParam("info", newValue)
                .execute();
        serverResponse.status(Status.CREATED_201).send();
    }

    private void deleteBook(ServerRequest serverRequest, ServerResponse serverResponse) {
        String bookName = serverRequest.path().pathParameters().get("name");

        dbClient.execute().namedDelete("delete-book", bookName);
        serverResponse.status(Status.NO_CONTENT_204).send();
    }

    private void getJsonBook(ServerRequest serverRequest, ServerResponse serverResponse) {
        String bookName = serverRequest.path().pathParameters().get("name");

        JsonObject bookJson = dbClient.execute().namedGet("select-book", bookName)
                .map(row -> row.as(JsonObject.class))
                .orElseThrow(() -> new NotFoundException("Book not found: " + bookName));
        serverResponse.send(bookJson);
    }
}
