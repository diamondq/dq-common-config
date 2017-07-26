pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-v /data/jenkins/m2:/.m2 -v /data/jenkins/gpg:/.gnupg'
    }
    
  }
  stages {
    stage('Clean') {
      steps {
        sh '''cd common-config-root
mvn "-Duser.home=/" "-Djenkins=true" clean'''
      }
    }
    stage('Build') {
      steps {
        sh '''cd common-config-root
mvn "-Duser.home=/" "-Djenkins=true" install'''
      }
    }
    stage('Publish') {
      steps {
        sh '''cd common-config-root
mvn "-Duser.home=/" "-Djenkins=true" deploy'''
      }
    }
  }
}