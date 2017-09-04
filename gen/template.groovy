import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import static java.util.stream.Collectors.toList

String PACKAGE = "pl.allegro.tech.selfservice"
String NAME = "newservicesingle"
String PACKAGE_WITH_NAME = PACKAGE + "." + NAME

File scriptDir = new File(getClass().protectionDomain.codeSource.location.path).parentFile
File projectDir = scriptDir.parentFile

String appName = placeholders.projectName
String cleanAppName = placeholders.projectName.replaceAll("-", "")
String appPackage = placeholders.applicationGroup

Files.walk(new File(projectDir, "/src").toPath())
        .filter { path -> !Files.isDirectory(path) }
        .forEach { file ->
    replace(file.toFile()) {
        filter { line -> line.replace(PACKAGE_WITH_NAME, placeholders.fullApplicationName.replaceAll("-", "")) }
        filter { line -> line.replace(NAME, appName) }
    }
}

replace(projectDir, "build.gradle") {
    filter { line -> filterLine(line, PACKAGE, appPackage) }
    filter { line -> filterLine(line, NAME, cleanAppName) }
}

replace(projectDir, "settings.gradle") {
    filter { line -> filterLine(line, NAME, appName) }
}

replace(projectDir, "project.properties") {
    filter { line -> filterLine(line, "{domain}", placeholders.domain) }
    filter { line -> filterLine(line, "{context}", placeholders.context) }
    filter { line -> filterLine(line, "{appName}", appName) }
    filter { line -> filterLine(line, "{fullUserName}", placeholders.fullUserName) }
    filter { line -> filterLine(line, "{userEmail}", placeholders.userEmail) }
    filter { line -> filterLine(line, "{bambooCi}", placeholders.bambooKey) }
}

replace(projectDir, "tycho.yaml") {
    filter { line -> filterLine(line, NAME, appName) }
    filter { line -> filterLine(line, PACKAGE, appPackage) }
}

replace(projectDir, "README.md") {
    filter { line -> filterLine(line, NAME, appName) }
}

def packageDirectory = placeholders.fullApplicationName.replaceAll("\\.", "/").replaceAll("-", "")

def projectDirPath = projectDir.path

move("$projectDirPath/src/main/kotlin/pl/allegro/tech/selfservice/newservicesingle",
        "$projectDirPath/src/main/kotlin/$packageDirectory")

move("$projectDirPath/src/test/groovy/pl/allegro/tech/selfservice/newservicesingle",
        "$projectDirPath/src/test/groovy/$packageDirectory")

move("$projectDirPath/src/integration/groovy/pl/allegro/tech/selfservice/newservicesingle",
        "$projectDirPath/src/integration/groovy/$packageDirectory")
class Replacer {

    File file
    List<String> lines

    Replacer(File file) {
        println "setting file $file"
        this.file = file
        lines = Files.readAllLines(file.toPath(), Charset.forName('UTF-8'))
    }

    def filter(Closure mapper) {
        println("replacing")
        lines = lines.stream().map(mapper).collect(toList())
    }

    def save() {
        println "saving"
        Files.write(file.toPath(), lines, Charset.forName('UTF-8'))
    }
}

def replace(File file, Closure closure) {
    def replacer = new Replacer(file)
    closure.delegate = replacer
    closure()
    replacer.save()
}

def replace(File projectDir, String file, Closure closure) {
    replace(new File(projectDir, file), closure)
}

def move(String from, String to) {
    new File(to).mkdirs()
    Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING)
}

def filterLine(line, searchToken, replaceToken) {
    if (line.contains(searchToken)) {
        line = line.replace(searchToken, replaceToken)
    }
    line
}
