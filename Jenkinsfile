pipeline {
  agent {
    docker {
      image 'maven:3-jdk-8'
      args '-e MAVEN_CONFIG=/var/maven/.m2 -v /data/jenkins/m2-config:/var/maven/.m2 -v /data/jenkins/gpg:/var/maven/.gnupg'
    }
    
  }
  stages {
    stage('Build') {
      steps {
        sh 'printenv'
        sh 'cd config-root; MAVEN_OPTS=-Duser.home=/var/maven mvn \\"-Djenkins=true\\" \\"-Drevision=0.3.${env.BUILD_NUMBER}\\" \\"-Dchangelist=\\" clean deploy'
      }
    }
  }
}