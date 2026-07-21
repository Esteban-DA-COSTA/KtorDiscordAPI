import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.Qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.youtrack
import jetbrains.buildServer.configs.kotlin.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs

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
    description = "Build l'instance Writerside 'kda' et la publie sur GitHub Pages (branche gh-pages)"

    params {
        // Secret : PAT fine-grained, scope 'Contents: write' sur ce repo uniquement.
        // Remplacer par le token chiffré généré via l'UI TeamCity (icône clé).
        password("env.GH_PAGES_TOKEN", "credentialsJSON:REMPLACER-PAR-LE-TOKEN-CHIFFRE")
        param("docs.instance", "Writerside/kda")
        param("docs.artifact", "webHelpKDA2-all.zip")
        param("docs.repo", "github.com/Esteban-DA-COSTA/KtorDiscordAPI.git")
    }

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }

    steps {
        script {
            name = "Build docs"
            id = "build_docs"
            scriptContent = """
                export DISPLAY=:99
                Xvfb :99 &
                /opt/builder/bin/idea.sh helpbuilderinspect \
                  --source-dir "%teamcity.build.checkoutDir%/Writerside" \
                  --product "%docs.instance%" \
                  --runner other \
                  --output-dir "%teamcity.build.checkoutDir%/artifacts"
                test -f "artifacts/%docs.artifact%"
            """.trimIndent()
            dockerImage = "registry.jetbrains.team/p/writerside/builder/writerside-builder:243.21565"
            dockerPull = true
        }
        script {
            name = "Check report"
            id = "check_report"
            scriptContent = """
                if command -v jq >/dev/null; then
                  n=${'$'}(jq '[.. | .problems? // empty] | add | length' artifacts/report.json 2>/dev/null || echo 0)
                  echo "Problèmes détectés : ${'$'}n"
                  [ "${'$'}n" = "0" ] || { echo "##teamcity[buildProblem description='Writerside report has problems']"; exit 1; }
                fi
            """.trimIndent()
        }
        script {
            name = "Publish to gh-pages"
            id = "publish_gh_pages"
            scriptContent = """
                rm -rf site && unzip -q -o "artifacts/%docs.artifact%" -d site
                cd site
                touch .nojekyll
                git init -q
                git checkout -q -b gh-pages
                git config user.email "ci@teamcity"
                git config user.name  "TeamCity CI"
                git add -A
                git commit -qm "Deploy docs (build %build.number%)"
                git push -f "https://x-access-token:%env.GH_PAGES_TOKEN%@%docs.repo%" gh-pages
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            triggerRules = "+:Writerside/**"
        }
    }

    requirements {
        exists("docker.server.version")
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
