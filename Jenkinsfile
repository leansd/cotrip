

pipeline {
    agent any

    tools {
        maven 'maven3.9.4'
    }

    environment {
        DOCKER_IMAGE = 'registry.cn-hangzhou.aliyuncs.com/leansd/cotrip:latest'
    }


    triggers {
        pollSCM('H/10 * * * *')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: 'master']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                        credentialsId: 'git',
                        url: 'git@gitee.com:leansd/cotrip.git'
                    ]]
                ])
            }
        }

        stage('Maven Build & Test') {
            steps {
                sh 'mvn clean install'
            }
        }

    stage('Docker Build & Push') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            steps {
                script {
                    docker.build("${DOCKER_IMAGE}")
                    docker.withRegistry('https://registry.cn-hangzhou.aliyuncs.com/emergentdesign/leansd', 'aliyun-docker-registry-credentials') {
                        docker.image("${DOCKER_IMAGE}").push()
                    }
                }
            }
        }

    }
}