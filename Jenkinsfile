pipeline {
  agent {
    docker {
      image 'keithwesley254/maven-docker-agent:latest'
      args '--network host'
    }
  }

  environment {
    SELENIUM_URL = 'http://172.17.0.1:4444/wd/hub'
  }

  stages {
    stage('Clone Repo') {
      steps {
        git 'https://github.com/KeithWesley254/selenium-test-my-portfolio.git'
      }
    }

    stage('Run Selenium Tests') {
      steps {
        sh "mvn clean test -Dselenium.remote.url=${SELENIUM_URL}"
      }
    }
  }
}