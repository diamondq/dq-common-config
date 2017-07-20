pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2:/root/.m2 -v /data/jenkins/gpg:/root/.gnupg'
    }
    
  }
  stages {
    stage('Test') {
      steps {
        sh '''cd common-config-root
mvn clean'''
      }
    }
    stage('Build') {
      steps {
        sh '''cd common-config-root
mvn install'''
      }
    }
  }
  environment {
    "gpg.homedir" = '/root/.gnupg'
  }
}
