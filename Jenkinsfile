pipeline {
  agent { label 'multibrowser-java-agent' }

  environment {
    SELENIUM_URL = 'http://172.17.0.1:4444/wd/hub'
  }

  stages {
    stage('Run Selenium Tests') {
      steps {
        sh '''
          curl -s --fail $SELENIUM_URL/status || {
            echo "❌ Selenium Grid is not reachable at $SELENIUM_URL"
            exit 1
          }

          mvn clean test -Dselenium.remote.url=$SELENIUM_URL
        '''
      }
    }
  }

  post {
    always {
      junit 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/failure-screenshot.png', allowEmptyArchive: true
    }
  }
}