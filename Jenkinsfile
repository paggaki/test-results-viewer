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
    stages{
        stage("Print"){
            steps{
                script{
                         echo "Selected report: ${params.Available_Reports}"
               }
            }
        }
    }
}

def return_available_reports() {
    def fileList = findFiles(glob: 'src/main/resources/report-samples/**/*').collect { it.name }
    return fileList.join('\n')
}