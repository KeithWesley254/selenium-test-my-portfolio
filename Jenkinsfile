pipeline {
  agent { label 'multibrowser-java-agent' }

  options {
    // Keep console readable & avoid runaway logs
    ansiColor('xterm')
    timestamps()
    timeout(time: 30, unit: 'MINUTES')
  }

  environment {
    // No hardcoded IPs; works on Windows host talking to Linux Docker
    SELENIUM_URL = 'http://host.docker.internal:4444/wd/hub'
    // How long to wait for Grid readiness
    GRID_READY_TIMEOUT_SECS = '120'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Verify Selenium Grid & Run Tests') {
      steps {
        powershell '''
          Write-Host "🔎 Checking Selenium Grid at $env:SELENIUM_URL ..."

          $deadline = (Get-Date).AddSeconds([int]$env:GRID_READY_TIMEOUT_SECS)
          $healthy = $false

          while ((Get-Date) -lt $deadline) {
            try {
              $resp = Invoke-WebRequest -Uri "$env:SELENIUM_URL/status" -UseBasicParsing -TimeoutSec 5
              if ($resp.StatusCode -eq 200) {
                # Try to parse JSON, but don't fail if it isn't JSON
                try {
                  $json = $resp.Content | ConvertFrom-Json
                  $ready = $json?.value?.ready
                  if ($ready -eq $true) {
                    $healthy = $true
                    Write-Host "✅ Selenium Grid is ready."
                    break
                  } else {
                    Write-Host "⏳ Grid responded 200 but 'ready' != true. Waiting..."
                  }
                } catch {
                  # Not JSON? Treat 200 as OK and proceed
                  $healthy = $true
                  Write-Host "✅ Selenium Grid returned 200. Proceeding."
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

  post {
    always {
      // Always attempt to publish reports and artifacts
      junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
      archiveArtifacts artifacts: 'target/failure-screenshot.png', allowEmptyArchive: true
    }
  }
}