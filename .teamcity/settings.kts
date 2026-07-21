import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.Qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.qodana
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.projectFeatures.youtrack
import jetbrains.buildServer.configs.kotlin.triggers.VcsTrigger
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
    buildType(PublishPackages)

    params {
        // PAT classic GitHub (scopes `repo` + `write:packages`), partagé par GenerateDoc et
        // PublishPackages. Défini une seule fois ici → un seul endroit à mettre à jour à la rotation.
        // Valeur saisie via l'UI (icône clé) ; hérité par tous les builds du projet.
        password("env.GH_TOKEN", "credentialsJSON:00b7d8c6-e92f-42c2-b132-7d7fc6ba194b")
    }

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
        // env.GH_TOKEN est hérité du projet.
        param("docs.instance", "Writerside/kda")
        param("docs.artifact", "webHelpKDA2-all.zip")
        param("docs.repo", "github.com/Esteban-DA-COSTA/KtorDiscordAPI.git")
    }

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            branchFilter = "+:main"
            enableQueueOptimization = false
        }
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
                git push -f "https://x-access-token:%env.GH_TOKEN%@%docs.repo%" gh-pages
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = "+:main"
            triggerRules = "+:Writerside/**"
        }
    }

    dependencies {
        // La doc ne se publie que si Build & Test passe (même révision).
        snapshot(Build) {
            onDependencyFailure = FailureAction.FAIL_TO_START
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

object PublishPackages : BuildType({
    name = "Publish packages"
    description = "Sur un tag v*, publie les modules sur GitHub Packages puis crée la release GitHub"

    params {
        // Username GitHub Packages (public, pas secret) — propriétaire du repo par défaut.
        param("packages.username", "Esteban-DA-COSTA")
        // env.GH_TOKEN est hérité du projet (gh CLI le lit nativement pour créer la release).
        // Renseignés à l'exécution par le step "Resolve version" (via setParameter).
        param("release.tag", "")
        param("release.version", "")
    }

    vcs {
        root(DslContext.settingsRoot)
        cleanCheckout = true
    }

    steps {
        script {
            name = "Resolve version from tag"
            id = "resolve_version"
            scriptContent = """
                set -e
                TAG="%teamcity.build.branch%"
                VERSION="${'$'}{TAG#v}"
                echo "Tag=${'$'}TAG  Version=${'$'}VERSION"
                # Injecte la version du tag dans la source unique (indentation 4 espaces sous publishing:)
                sed -i "s/^    version:.*/    version: ${'$'}VERSION/" lib.module-template.yaml
                echo "##teamcity[setParameter name='release.tag' value='${'$'}TAG']"
                echo "##teamcity[setParameter name='release.version' value='${'$'}VERSION']"
            """.trimIndent()
        }
        script {
            name = "Set credentials"
            id = "set_credentials"
            scriptContent = """
                {
                  echo "username=%packages.username%"
                  echo "password=${'$'}GH_TOKEN"
                } > creds.properties
            """.trimIndent()
        }
        script {
            name = "Publish"
            id = "publish"
            scriptContent = """
                chmod +x ./kotlin
                ./kotlin publish github
            """.trimIndent()
        }
        script {
            name = "Create GitHub release"
            id = "create_release"
            scriptContent = """
                set -e
                gh release create "%release.tag%" \
                  --repo Esteban-DA-COSTA/KtorDiscordAPI \
                  --title "%release.tag%" \
                  --generate-notes \
                  "components/build/libs/components-jvm.jar#components-%release.version%.jar" \
                  "websocket/build/libs/websocket-jvm.jar#websocket-%release.version%.jar" \
                  "core/build/libs/core-jvm.jar#kda-%release.version%.jar"
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            // Se déclenche sur les tags v* — nécessite `+:refs/tags/(v*)` dans le branch spec du VCS root.
            branchFilter = "+:v*"
        }
    }

    dependencies {
        snapshot(Build) {
        }
    }
})
