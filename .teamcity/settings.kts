import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.Qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.youtrack
import jetbrains.buildServer.configs.kotlin.triggers.finishBuildTrigger

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2026.1"

project {

    buildType(Build)
    buildType(Qodana_1)
    buildType(GenerateDoc)

    features {
        youtrack {
            id = "PROJECT_EXT_3"
            displayName = "Youtrack"
            host = "https://estebandcprojects.youtrack.cloud/"
            userName = ""
            password = ""
            projectExtIds = ""
            accessToken = "credentialsJSON:69baf0dc-16c0-4ae9-8383-6e5ba31aa2d9"
            useAutomaticIds = true
        }
    }
}

object Build : BuildType({
    name = "Build & Test"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "Build"
            id = "Build"
            scriptContent = """
                chmod +x ./kotlin
                ./kotlin build
            """.trimIndent()
        }
        script {
            name = "Test"
            id = "test"
            scriptContent = """
                chmod +x ./kotlin
                ./kotlin test --format=teamcity
            """.trimIndent()
        }
    }
})

object GenerateDoc : BuildType({
    name = "Generate doc"

    vcs {
        root(DslContext.settingsRoot)
    }
})

object Qodana_1 : BuildType({
    id("Qodana")
    name = "Qodana"
    paused = true

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        qodana {
            name = "Static analysis"
            id = "Static_analysis"
            linter = jvmCommunity {
                version = Qodana.JVMCommunityVersion.LATEST
            }
            inspectionProfile = default()
            cloudToken = "credentialsJSON:e863f0a4-ba73-4e10-a9a6-4d13f6e4a2eb"
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "${Build.id}"
        }
    }

    dependencies {
        snapshot(Build) {
        }
    }

    requirements {
        exists("docker.server.version")
    }
})
