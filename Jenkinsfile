node("") {
    def availableReports = return_available_reports()

    properties([
        parameters([
            choice(
                choices: availableReports,
                description: 'Available Reports from the folder report-samples',
                name: 'Available_Reports'
            )
        ])
    ])
}

pipeline {
    agent any
    tools {
         maven 'Maven 3.9.4'
     }
    stages{
        stage("Debug"){
            steps{
                script{
                         echo "Selected report: ${params.Available_Reports}"
               }
            }
        }
        stage("Print Parser Results"){
           steps{
                       sh "mvn clean install exec:java -Dexec.mainClass='ReportParserApp' -Dexec.args='${params.Available_Reports}'"
           }
        }
    }
}

def return_available_reports() {
    def fileList = findFiles(glob: 'src/main/resources/report-samples/*/').collect { it.name }
    return fileList.join('\n')
}