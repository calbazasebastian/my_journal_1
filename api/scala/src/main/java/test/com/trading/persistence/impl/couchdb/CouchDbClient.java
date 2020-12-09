package test.com.trading.persistence.impl.couchdb;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.query.PredicateExpression;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import test.com.trading.config.Configuration;
import test.com.trading.persistence.PersistenceAPI;
import test.com.trading.persistence.impl.couchdb.domain.CastEntity;
import test.com.trading.service.domain.Cast;

import java.net.MalformedURLException;
import java.net.URL;

import static com.cloudant.client.api.query.Expression.eq;
import static com.cloudant.client.api.query.Operation.and;
import static com.cloudant.client.api.query.PredicatedOperation.elemMatch;

public class CouchDbClient implements PersistenceAPI {
    private static CloudantClient client;

    static {
        try {
            client = ClientBuilder.url(new URL(Configuration.persistenceUrl()))
                    .username(Configuration.persistenceUser())
                    .password(Configuration.persistencePassword())
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String saveCast(Cast cast) {
        String dbName = "casts";
        CastEntity castEntity = new CastEntity(cast.originatorId() + "-" + cast.bondId() + "-" + cast.buySell(), null, cast);
        Database db = client.database(dbName, true);
        return db.save(castEntity).getId();
    }

    public String updateCast(Cast cast) {
        String dbName = "casts";
        Database db = client.database(dbName, false);
        CastEntity existingCast = findCast(cast.originatorId(), cast.bondId(), cast.buySell());
        CastEntity castEntity = new CastEntity(cast.originatorId() + "-" + cast.bondId() + "-" + cast.buySell(), existingCast._rev(), cast);
        return db.update(castEntity).getId();
    }

    public CastEntity findCast(long originator, int bondId, String buySell) {
        String dbName = "casts";
        Database db = client.database(dbName, false);
        String id = originator + "-" + bondId + "-" + buySell;
        try {
            return db.find(CastEntity.class, id);
        } catch (NoDocumentException ex) {
            return null;
        }
    }

    public CastEntity[] findActiveCasts(long originator, long targetId) {
        String dbName = "casts";
        Database db = client.database(dbName, false);
        return db.query(new QueryBuilder(
                and(
                        eq("originatorId", originator),
                        eq("status", "active"),
                        elemMatch("targetUserIds", PredicateExpression.eq(targetId))
                )
        ).
                build(), CastEntity.class).getDocs().toArray(new CastEntity[0]);

    }
}

