pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2:/root/.m2'
    }
    
  }
  stages {
    stage('Test') {
      steps {
        sh '''cd common-config-root
mvn clean'''
        echo 'Test Again'
      }
    }
    stage('Build') {
      steps {
        sh '''cd common-config-root
mvn install'''
      }
    }
  }
}
