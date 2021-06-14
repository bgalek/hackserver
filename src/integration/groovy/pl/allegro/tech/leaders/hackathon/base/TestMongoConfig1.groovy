package pl.allegro.tech.leaders.hackathon.base

import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.Defaults
import de.flapdoodle.embed.mongo.config.MongoCmdOptions
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.config.RuntimeConfig
import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.runtime.Executable
import de.flapdoodle.embed.process.runtime.Network
import de.flapdoodle.embed.process.runtime.Processes
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration

@Configuration
class TestMongoConfig1 implements InitializingBean, DisposableBean {

    private MongodExecutable executable

    @Override
    void afterPropertiesSet() throws Exception {
        MongoCmdOptions cmdOptions = MongoCmdOptions.builder().isVerbose(false).build()
        RuntimeConfig runtimeConfig = Defaults.runtimeConfigFor(Command.MongoD, LoggerFactory.getLogger(getClass().getName()))
                .processOutput(ProcessOutput.getDefaultInstanceSilent())
                .build()
        MongodConfig mongodConfig = MongodConfig.builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(Network.getFreeServerPort(), Network.localhostIsIPv6()))
                .cmdOptions(cmdOptions)
                .build()
        MongodStarter starter = MongodStarter.getInstance(runtimeConfig)
        executable = starter.prepare(mongodConfig)
        executable.start()
    }

    @Override
    void destroy() throws Exception {
        executable.stop()
    }
}