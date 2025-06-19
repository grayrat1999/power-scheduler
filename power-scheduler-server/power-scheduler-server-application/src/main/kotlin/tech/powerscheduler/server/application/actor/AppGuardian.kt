package tech.powerscheduler.server.application.actor

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.Props
import akka.actor.typed.javadsl.Behaviors
import akka.cluster.typed.ClusterSingleton
import akka.cluster.typed.SingletonActor
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.javadsl.AkkaManagement
import com.typesafe.config.ConfigFactory
import org.springframework.context.ApplicationContext
import tech.powerscheduler.server.application.actor.singleton.JobAssignorActor
import tech.powerscheduler.server.application.actor.singleton.JobInstanceCleanActor
import tech.powerscheduler.server.application.actor.singleton.TaskStatusChangeEventHandlerActor
import tech.powerscheduler.server.application.actor.singleton.WorkerRegistryCleanActor
import java.util.concurrent.TimeUnit

class AppGuardian(
    endpoints: List<String>,
    clusterMode: ClusterMode,
    akkaHost: String,
    akkaManagementHttpPort: Int = 8558,
    akkaRemotePort: Int,
    private val applicationContext: ApplicationContext,
) {

    val actorSystem: ActorSystem<Void>

    init {
        val overrides = mutableMapOf<String, Any?>(
            "akka.management.http.port" to akkaManagementHttpPort,
            "akka.remote.artery.canonical.port" to akkaRemotePort,
        )
        when (clusterMode) {
            ClusterMode.SINGLETON -> {
                val singletonEndpoint = "${LOCALHOST}:$akkaManagementHttpPort"
                overrides[AKKA_CLUSTER_ENDPOINTS] = formatEndpointsConfig(listOf(singletonEndpoint))
                overrides[AKKA_CLUSTER_REQUIRED_NODE_NUM] = 1
                overrides[AKKA_MANAGEMENT_HOST] = LOCALHOST
                overrides[AKKA_REMOTE_HOST] = LOCALHOST
            }

            ClusterMode.CLUSTER -> {
                if (endpoints.isEmpty()) {
                    throw IllegalArgumentException("endpoints is required when use [${ClusterMode.CLUSTER}] mode")
                }
                if (akkaHost.isBlank()) {
                    throw IllegalArgumentException("akkaHost is required when use [${ClusterMode.CLUSTER}] mode")
                }
                overrides[AKKA_CLUSTER_ENDPOINTS] = formatEndpointsConfig(endpoints)
                overrides[AKKA_CLUSTER_REQUIRED_NODE_NUM] = endpoints.size
                overrides[AKKA_MANAGEMENT_HOST] = akkaHost
                overrides[AKKA_REMOTE_HOST] = akkaHost
            }
        }

        val config = ConfigFactory.parseMap(overrides)
            .withFallback(ConfigFactory.load())
        actorSystem = ActorSystem.create(create(), "SchedulerSystem", config)
        AkkaManagement.get(actorSystem).start()
        ClusterBootstrap.get(actorSystem).start()
    }

    private fun formatEndpointsConfig(endpoints: List<String>): List<Map<String, Any?>> {
        return endpoints.map {
            val split = it.split(":")
            if (split.size != 2) {
                throw IllegalArgumentException("the format of endpoint must be [ip:port], but got [$it]")
            }
            mapOf(
                "host" to split[0],
                "port" to split[1].toInt(),
            )
        }
    }

    private fun create(): Behavior<Void> {
        return Behaviors.setup { context ->
            val singleton = ClusterSingleton.get(actorSystem)

            SingletonActor.of(
                JobInstanceCleanActor.create(applicationContext = applicationContext),
                JobInstanceCleanActor::class.simpleName,
            )
                .withProps(Props.empty().withDispatcherFromConfig("job-instance-clean-dispatcher"))
                .let { singleton.init(it) }

            SingletonActor.of(
                JobAssignorActor.create(applicationContext = applicationContext),
                JobAssignorActor::class.simpleName,
            ).let { singleton.init(it) }

            SingletonActor.of(
                TaskStatusChangeEventHandlerActor.create(applicationContext = applicationContext),
                TaskStatusChangeEventHandlerActor::class.simpleName,
            ).let { singleton.init(it) }

            context.spawn(
                WorkerRegistryCleanActor.create(applicationContext),
                WorkerRegistryCleanActor::class.simpleName,
            )
            context.spawn(
                JobSchedulerActor.create(applicationContext),
                JobSchedulerActor::class.simpleName,
                Props.empty().withDispatcherFromConfig("job-scheduler-dispatcher")
            )
            context.spawn(
                TaskDispatcherActor.create(applicationContext),
                TaskDispatcherActor::class.simpleName,
                Props.empty().withDispatcherFromConfig("job-dispatcher-dispatcher")
            )

            Behaviors.empty()
        }
    }

    fun shutdown() {
        actorSystem.terminate()
        try {
            actorSystem.whenTerminated.toCompletableFuture().get(5, TimeUnit.SECONDS)
        } catch (_: Exception) {
            // handle timeout or other exceptions
        }
    }

}