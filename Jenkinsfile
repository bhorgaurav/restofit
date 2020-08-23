pipeline {
  agent any
  stages {
    stage('Build Android') {
      steps {
        sh '''#!/bin/bash

./gradlew assembleRelease'''
      }
    }

  }
}