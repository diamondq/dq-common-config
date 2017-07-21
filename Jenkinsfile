pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2:/.m2 -v /data/jenkins/gpg:/.gnupg'
    }
    
  }
  stages {
    stage('Test') {
      steps {
        sh '''cd common-config-root
mvn "-Duser.home=/" clean'''
      }
    }
    stage('Build') {
      steps {
        sh '''cd common-config-root
mvn "-Duser.home=/" install'''
      }
    }
  }
  environment {
    GPG_TTY = /dev/console
  }
}