pipeline {
  agent any
  stages {
    stage('Build Android') {
      steps {
        sh '''#!/bin/bash

chmod +x ./gradlew
./gradlew assembleRelease'''
      }
    }

  }
}