pipeline {
  agent { label 'multibrowser-java-agent' }

  environment {
    SELENIUM_URL = 'http://host.docker.internal:4444/wd/hub'
  }

  stages {
    stage('Run Selenium Tests') {
      steps {
        bat '''
          echo 🔎 Checking Selenium Grid at %SELENIUM_URL% ...

          powershell -NoProfile -ExecutionPolicy Bypass -Command ^
            "try { ^
                $resp = Invoke-WebRequest '%SELENIUM_URL%/status' -UseBasicParsing; ^
                if ($resp.StatusCode -ne 200) { ^
                  Write-Host '❌ Selenium Grid is not ready'; exit 1 ^
                } else { ^
                  Write-Host '✅ Selenium Grid is reachable' ^
                } ^
              } catch { ^
                Write-Host '❌ Could not connect to Selenium Grid at %SELENIUM_URL%'; exit 1 ^
              }"

          mvn clean test -Dselenium.remote.url=%SELENIUM_URL%
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