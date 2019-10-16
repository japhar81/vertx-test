import co.elastic.apm.opentracing.ElasticApmTracer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.micrometer.backends.BackendRegistries;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);
        PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();

        router.route("/metrics").handler(ctx -> {
            System.out.println("Metrics Request");
            String response = registry.scrape();
            ctx.response().end(response);
        });

        Tracer tracer = new ElasticApmTracer();
        router.get("/").handler(ctx -> {
            Span span = tracer.buildSpan("new-request").start();
            ctx.response().end("Hello World");
            span.finish();
        });
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }
}