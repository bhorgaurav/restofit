pipeline {
  agent any
  stages {
    stage('Build Android') {
      steps {
        sh '''#!/bin/bash

pwd

chmod +x ./gradlew
./gradlew assembleRelease'''
      }
    }

  }
  environment {
    ANDROID_HOME = 'C:\\Users\\bhorg\\AppData\\Local\\Android\\Sdk'
  }
}