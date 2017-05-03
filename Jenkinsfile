pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '3'))
  }
  stages {
    stage('App_Build_ST') {
        steps {
			echo 'Build Number: ' + env.BUILD_NUMBER
			node(label: 'java8') {
				git(url: 'http://52.19.50.152/gerrit/ExampleWorkspace/ExampleProject/spring-petclinic', branch: 'master', credentialsId: 'f8e5a0d0-b489-4884-ace9-a74149ba8a30')
            	sh([script:"${tool 'ADOP Maven'}/bin/mvn compile -DskipTests"])
            	archiveArtifacts artifacts: '**/*'
			}
      	}
    }
    stage('Unit_Tests_ST') {
      steps {
		node(label: 'java8') {
        	echo 'Build Number: ' + env.BUILD_NUMBER
			sh([script:"${tool 'ADOP Maven'}/bin/mvn clean test"])
			archiveArtifacts artifacts: '**/*' 
		}
      }
    }
    stage('Code_Analysis_ST') {
      steps {
		echo 'Code_Analysis_ST...'
        echo 'Build Number: ' + env.BUILD_NUMBER
      }
    }
    stage('Deploy_Environment_ST') {
      steps {
		node(label:'docker'){
			echo 'Deploy_Environment_ST...'
			echo 'Build Number: ' + env.BUILD_NUMBER
			echo env.JOB_NAME
		}
      }
    }
    stage('Test_Build_ST') {
      steps {
		node(label: 'All_NT') {
			deleteDir()
			git(url: 'http://52.19.50.152/gerrit/BlueOceanProject', branch: 'master', credentialsId: 'f8e5a0d0-b489-4884-ace9-a74149ba8a30')
			bat([script:"${tool 'ADOP Maven'}/bin/mvn clean compile install -DskipTests"])
		}
      }
    }
	stage('Continuous_Testing_ST') {
	steps {
	  parallel(
		"01-Functional": {
			echo 'Functional Testing...'
			node(label: 'All_NT') {
				bat([script:'set MAVEN_OPTS = –Xmx2048m'])
				bat([script:'mvn exec:java -X -Dexec.mainClass="com.accenture.runner.selenium.SELENIUM_Executor" -Dexec.classpathScope=test'])
			}
		},
          "02-Platform": {
            echo 'Platform Testing...'
			node(label: 'All_NT') {
				bat([script:'mvn exec:java -X -Dexec.mainClass="com.accenture.runner.platform.PLATFORM_Executor" -Dexec.classpathScope=test'])
			}
        },
          "03-BDD": {
            echo 'BDD Testing...'
			node(label: 'All_NT') {
				bat([script:'mvn exec:java -X -Dexec.mainClass="com.accenture.runner.bdd.BDD_Executor" -Dexec.classpathScope=test'])
			}
        },
          "04-API": {
            echo ' API Testing...'
			node(label: 'All_NT') {
				bat([script:'start /b mvn jetty:run'])
				bat([script:'mvn integration-test'])
				bat([script:'mvn jetty:stop'])
			}
        }
        )
      }
    }
    stage('Pre-Prod-Deploy') {
      steps {
        echo 'Pre-Prod-Deployment started...'
      }
    }
  }
  post {
    always {
      echo 'Post Build Step'
    }
  }
}