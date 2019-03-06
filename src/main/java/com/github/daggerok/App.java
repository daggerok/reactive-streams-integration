package com.github.daggerok;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.impl.JavaFlowAndRsConverters;
import akka.stream.javadsl.JavaFlowSupport;
import io.reactivex.Flowable;
import org.reactivestreams.Processor;
import reactor.adapter.JdkFlowAdapter;

import java.time.Duration;
import java.util.concurrent.Flow;

public class App {
  public static void main(String[] args) {
    var flowPublisher = createPublisherFromReactor();
    var flowProcessor = createProcessorFromAkkaStream();
    flowPublisher.subscribe(flowProcessor);

    var reactiveStreamsProcessor = createReactiveStreamsProcessorFrom(flowProcessor);
    createRxJavaSubscriptionFrom(reactiveStreamsProcessor);
  }

  private static void createRxJavaSubscriptionFrom(Processor<Long, Long> reactiveStreamsPublisher) {
    Flowable.fromPublisher(reactiveStreamsPublisher)
            .subscribe(System.out::println);
  }

  private static Processor<Long, Long> createReactiveStreamsProcessorFrom(Flow.Processor<Long, Long> processor) {
    return JavaFlowAndRsConverters.asRs(processor);
  }

  private static Flow.Processor<Long, Long> createProcessorFromAkkaStream() {
    var flip = akka.stream.javadsl.Flow.of(Long.class)
                                       .map(param -> -param);
    var system = ActorSystem.create();
    var materializer = ActorMaterializer.create(system);
    return JavaFlowSupport.Flow.toProcessor(flip).run(materializer);
  }

  private static Flow.Publisher<Long> createPublisherFromReactor() {
    var interval = reactor.core.publisher.Flux.interval(Duration.ofSeconds(1));
    return JdkFlowAdapter.publisherToFlowPublisher(interval);
  }
}
