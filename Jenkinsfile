pipeline {
    agent any

    environment {
        GITHUB_URL = "https://github.com/D-TECH-DEV/spring-api.git"
        GIT_ID = "github-creds"
    }

    parameters {
        string(
            name: "BRANCH",
            defaultValue: "main",
            description: "La branche de déploiement"
        )
    }

    stages {
        stage("Pre-check") {
            steps {
                echo "Test des outils"
                sh "docker --version"
                sh "java --version"
            }
        }

        stage("Pulling project") {
            steps {
                echo "Clonage du projet"
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH}"]],
                    userRemoteConfigs: [[
                        url: "${GITHUB_URL}",
                        credentialsId: "${GIT_ID}"
                    ]]
                ])
            }
        }

        stage("Build JAR") {
            steps {
                echo "Compilation du projet..."
                sh "chmod +x mvnw"
                sh "./mvnw clean package -DskipTests"
            }
        }

        stage("Déploiement") {
            steps {
               echo "Relance du conteneur..."
               sh "/usr/bin/docker compose up -d --build"
            }
        }
    }

    post {
        success {
            echo "Déploiement OK"
            sh "docker system prune -f"
        }

        failure {
            echo "Pipeline échouée, check logs"
        }
    }
}