package com.consumer;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.Location.Coordinate;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

@Service
public class Consumer {

  private static final String CONSUMER_KEY = "";
  private static final String CONSUMER_SECRET_KEY = "";
  private static final String TOKEN = "";
  private static final String SECRET = "";

  public static final Coordinate POSITIVE_EDGE = new Coordinate(73.985130, 40.758896);
  public static final Coordinate NEGATIVE_EDGE = new Coordinate(-73.985130, -40.758896);

  private PostData postData;

  private StatusListener listener = new StatusListener() {
    @Override
    public void onStatus(Status status) {
      if (status.getFavoriteCount() > 2 && status.getMediaEntities().length > 0) {
        for (MediaEntity entity : status.getMediaEntities()) {
          Protobuf.Message message = createMessage(status, entity);
          postData.postData(message.toByteArray());

        }
      }

    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int limit) {
    }

    @Override
    public void onScrubGeo(long user, long upToStatus) {
    }

    @Override
    public void onStallWarning(StallWarning warning) {
    }

    @Override
    public void onException(Exception e) {
    }
  };

  @PostConstruct
  void postInit() throws InterruptedException {
    this.run();
  }

  private Protobuf.Message createMessage(Status status, MediaEntity entity) {
    Protobuf.Message message = Protobuf.Message
        .newBuilder()
        .setName(status.getUser().getName())
        .setPhoto(entity.getMediaURL())
        .setLocation(Protobuf.Coordinate.newBuilder()
                         .setLatitude(POSITIVE_EDGE.latitude())
                         .setLongitude(NEGATIVE_EDGE.longitude()).build())
        .build();

    return message;
  }

  public void run() throws InterruptedException {

    BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

    StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    endpoint.locations(Lists.newArrayList(new Location(NEGATIVE_EDGE, POSITIVE_EDGE)));

    Authentication auth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET_KEY, TOKEN, SECRET);

    BasicClient client = new ClientBuilder()
        .hosts(Constants.STREAM_HOST)
        .endpoint(endpoint)
        .authentication(auth)
        .processor(new StringDelimitedProcessor(queue))
        .build();

    int numProcessingThreads = 4;
    ExecutorService service = Executors.newFixedThreadPool(numProcessingThreads);

    Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
        client, queue, Lists.newArrayList(listener), service);

    t4jClient.connect();
    for (int threads = 0; threads < numProcessingThreads; threads++) {
      t4jClient.process();

      Thread.sleep(5000);

    }

    client.stop();
  }

}
