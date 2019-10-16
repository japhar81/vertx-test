import co.elastic.apm.attach.ElasticApmAttacher;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

public class Main {
    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        ElasticApmAttacher.attach();
        MicrometerMetricsOptions options = new MicrometerMetricsOptions()
                .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true))
                .setEnabled(true);
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(options));

        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setInstances(1);
        try {
            vertx.deployVerticle("MainVerticle", deploymentOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
