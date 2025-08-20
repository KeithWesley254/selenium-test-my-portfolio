pipeline {
  agent { label 'multibrowser-java-agent' }

  environment {
    SELENIUM_URL = 'http://host.docker.internal:4444/wd/hub'
    GRID_READY_TIMEOUT_SECS = '120'
  }

  options {
    timestamps()
    timeout(time: 30, unit: 'MINUTES')
    // only enable if AnsiColor plugin is installed:
    // ansiColor('xterm')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Verify Selenium Grid & Run Tests') {
      steps {
        // Use withEnv to set environment variables for the entire step
        withEnv([
          'JAVA_HOME=C:/tools/jdk/jdk-21.0.8.9-hotspot',
          'MAVEN_HOME=C:/tools/Apache/apache-maven-3.9.11',
          'PATH+BIN=' + 'C:/tools/jdk/jdk-21.0.8.9-hotspot/bin' + ':' + 'C:/tools/Apache/apache-maven-3.9.11/bin'
        ]) {
          powershell '''
            Write-Host "Verifying JAVA_HOME: $env:JAVA_HOME"
            Write-Host "Verifying MAVEN_HOME: $env:MAVEN_HOME"

            Write-Host "🔎 Checking Selenium Grid at $env:SELENIUM_URL ..."
            $deadline = (Get-Date).AddSeconds([int]$env:GRID_READY_TIMEOUT_SECS)
            $healthy = $false

            while ((Get-Date) -lt $deadline) {
              try {
                $resp = Invoke-WebRequest -Uri "$env:SELENIUM_URL/status" -UseBasicParsing -TimeoutSec 5
                if ($resp.StatusCode -eq 200) {
                  try {
                    $json = $resp.Content | ConvertFrom-Json
                    if ($null -ne $json -and $null -ne $json.value -and $json.value.ready -eq $true) {
                      $healthy = $true
                      Write-Host "✅ Selenium Grid is ready."
                      break
                    } else {
                      Write-Host "⏳ Grid responded 200 but 'ready' != true. Waiting..."
                    }
                  } catch {
                    $healthy = $true
                    Write-Host "✅ Selenium Grid returned 200 (could not parse JSON). Proceeding."
                    break
                  }
                } else {
                  Write-Host "⏳ Grid responded with HTTP $($resp.StatusCode). Retrying..."
                }
              } catch {
                Write-Host "⏳ Grid not reachable yet: $($_.Exception.Message)"
              }
              Start-Sleep -Seconds 3
            }

            if (-not $healthy) {
              Write-Host "❌ Selenium Grid was not ready within $env:GRID_READY_TIMEOUT_SECS seconds at $env:SELENIUM_URL"
              exit 1
            }

            Write-Host "🚀 Running tests with Maven..."
            mvn -B -V clean test -Dselenium.remote.url=$env:SELENIUM_URL
          '''
        }
      }
    }
  }

  post {
    always {
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/failure-screenshot.png', allowEmptyArchive: true
    }
  }
}