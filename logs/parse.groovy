import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

Date initialDate
Date finalDate 
int nLines = 0
new File("logs.txt").eachLine { line ->
  logLine = jsonSlurper.parseText(line) 
  nLines++

  // dates 
  if (initialDate == null) {
    initialDate = new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", logLine["@timestamp"]) 
  }
  finalDate = new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", logLine["@timestamp"]) 
}

// output: 
println("Analysis output")
println("===============") 
printf("Logs started at: %s\n", initialDate) 
printf("Logs ended at:   %s\n", finalDate) 
printf("Number of lines: %d\n", nLines) 
