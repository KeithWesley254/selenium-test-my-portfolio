pipeline {
  agent { label 'multibrowser-java-agent' }

  environment {
    SELENIUM_URL = 'http://host.docker.internal:4444/wd/hub'
  }

  stages {
    stage('Run Selenium Tests') {
      steps {
        powershell '''
          Write-Host "🔎 Checking Selenium Grid at $env:SELENIUM_URL ..."
          try {
            $resp = Invoke-WebRequest -Uri "$env:SELENIUM_URL/status" -UseBasicParsing -TimeoutSec 10
            Write-Host "✅ Selenium Grid is reachable."
          } catch {
            Write-Host "❌ Could not connect to Selenium Grid at $env:SELENIUM_URL"
            exit 1
          }

          # Run tests with Maven
          mvn clean test -Dselenium.remote.url=$env:SELENIUM_URL
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