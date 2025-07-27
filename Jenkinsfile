pipeline {
  agent {
    label 'java-selenium-chrome-agent'
  }

  environment {
    SELENIUM_URL = 'http://172.17.0.1:4444/wd/hub'
  }

  stages {
    stage('Run in Docker Container') {
      agent {
        docker {
          image 'keithwesley254/maven-docker-agent:latest'
          args '--network host'
        }
      }
      steps {
        git 'https://github.com/KeithWesley254/selenium-test-my-portfolio.git'
        sh 'mvn clean test -Dselenium.remote.url=$SELENIUM_URL'
      }
    }
  }
}