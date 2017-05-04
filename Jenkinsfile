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
				git(url: 'https://github.com/bop80aws/PC.git', branch: 'master', credentialsId: '')
            	sh([script:"${tool 'ADOP Maven'}/bin/mvn clean install -DskipTests"])
            	archiveArtifacts artifacts: '**/*'
			}
      	}
    }
    stage('Unit_Tests_ST') {
      steps {
		node(label: 'java8') {
        	echo 'Build Number: ' + env.BUILD_NUMBER
			sh([script:"${tool 'ADOP Maven'}/bin/mvn test"])
			archiveArtifacts artifacts: '**/*' 
		}
      }
    }
    stage('Code_Analysis_ST') {
      steps {
		node(label: 'java8') {
			echo 'Code_Analysis_ST...'
			echo 'Build Number: ' + env.BUILD_NUMBER
			build job: 'Extra_Jobs/Sonar_Code_Analysis', parameters: [[$class: 'StringParameterValue', name: 'ProjectName', value: JOB_NAME]]
		}
      }
    }
    stage('Deploy_Environment_ST') {
      steps {
		node(label:'docker'){
			echo 'Deploy_Environment_ST...'
			echo 'Build Number: ' + env.BUILD_NUMBER
						
			sh '''echo "
			FROM tomcat:8.0 
			ADD target/petclinic.war /usr/local/tomcat/webapps/
			" > ${WORKSPACE}/Dockerfile
			export REPO_NAME="$(echo ${JOB_NAME} | tr '/' '_' | tr '[:upper:]' '[:lower:]')"
			docker build -t ${REPO_NAME}/adop-foss-java:0.0.${BUILD_NUMBER} .
			echo "New image has been build - ${REPO_NAME}/adop-foss-java:0.0.${BUILD_NUMBER}"'''

		}
      }
    }
    stage('Test_Build_ST') {
      steps {
		node(label: 'All_NT') {
			deleteDir()
			git(url: 'https://github.com/bop80aws/BOP.git', branch: 'master', credentialsId: '')
			bat([script:"${tool 'ADOP Maven'}/bin/mvn clean compile install -DskipTests"])
		}
      }
    }
	stage('Continuous_Testing_ST') {
	agent {
		node { label 'All_NT' }
		}
	steps {
	  parallel(
		"01-Functional": {
			echo 'Functional Testing...'
			
				bat([script:'set MAVEN_OPTS = –Xmx2048m'])
				bat([script:'mvn exec:java -X -Dexec.mainClass="com.accenture.runner.selenium.SELENIUM_Executor" -Dexec.classpathScope=test'])
			
		},
          "02-Platform": {
            echo 'Platform Testing...'
			
				bat([script:'mvn exec:java -X -Dexec.mainClass="com.accenture.runner.platform.PLATFORM_Executor" -Dexec.classpathScope=test'])
			
        },
          "03-BDD": {
            echo 'BDD Testing...'
			
				bat([script:'mvn exec:java -X -Dexec.mainClass="com.accenture.runner.bdd.BDD_Executor" -Dexec.classpathScope=test'])
			
        },
          "04-API": {
            echo ' API Testing...'
			
				bat([script:'start /b mvn jetty:run'])
				bat([script:'mvn integration-test'])
				bat([script:'mvn jetty:stop'])
			
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
