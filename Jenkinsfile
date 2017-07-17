pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
    }
    
  }
  stages {
    stage('Test') {
      steps {
        sh '''echo "Hello"
mvn clean'''
      }
    }
    stage('Build') {
      steps {
        sh 'mvn install'
      }
    }
  }
}