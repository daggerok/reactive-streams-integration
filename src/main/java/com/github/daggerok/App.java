package com.github.daggerok;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.impl.JavaFlowAndRsConverters;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.JavaFlowSupport;
import io.reactivex.Flowable;
import org.reactivestreams.Processor;
import reactor.adapter.JdkFlowAdapter;

import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

public class App {
  public static void main(String[] args) {
    var reactorPublisher = reactorPublisher();
    var akkaStreamProcessor = akkaStreamProcessor();
    reactorPublisher.subscribe(akkaStreamProcessor);

    var reactiveStreamsPublisher = reactiveStreamsPublisherFrom(akkaStreamProcessor);
    Flowable.fromPublisher(reactiveStreamsPublisher)
            .subscribe(System.out::println);
  }

  static Processor<Long, Long> reactiveStreamsPublisherFrom(java.util.concurrent.Flow.Processor<Long, Long> processor) {
    return JavaFlowAndRsConverters.asRs(processor);
  }

  static java.util.concurrent.Flow.Processor<Long, Long> akkaStreamProcessor() {
    var flip = Flow.of(Long.class).map(param -> -param);
    var system = ActorSystem.create();
    var materializer = ActorMaterializer.create(system);
    return JavaFlowSupport.Flow.toProcessor(flip).run(materializer);
  }

  static Publisher<Long> reactorPublisher() {
    var interval = reactor.core.publisher.Flux.interval(Duration.ofSeconds(1));
    return JdkFlowAdapter.publisherToFlowPublisher(interval);
  }
}
