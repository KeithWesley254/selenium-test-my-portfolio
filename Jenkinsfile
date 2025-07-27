pipeline {
  agent { label 'multi-browser-java-agent1' }

  environment {
    SELENIUM_URL = 'http://172.17.0.1:4444/wd/hub'
  }

  stages {
    stage('Check Selenium Grid') {
      steps {
        sh '''
          echo "Checking Selenium Grid status..."
          curl -s --fail $SELENIUM_URL/status || {
            echo "❌ Selenium Grid is not reachable at $SELENIUM_URL"
            exit 1
          }
        '''
      }
    }

    stage('Run Selenium Tests') {
      steps {
        sh "mvn clean test -Dselenium.remote.url=${SELENIUM_URL}"
      }
    }
  }
}