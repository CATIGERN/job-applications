# Job Applications Microservice

## Commands

The following commands need to be executed on a terminal or shell at the root location of the project.

* Build executable JAR: gradlew build
* Run unit and integration tests: gradlew test
* Generate test coverage: gradlew jacocoTestReport
* The coverage results can be seen in the folder build/reports/coverage/index.html
* Run executable JAR: java -jar build/libs/job-applications-0.0.0.jar

The application runs on the 8600 port. The swagger documentation for the application can be viewed on
http://localhost:8600/swagger-ui.html

For testing the application via PostMan, you can use the following two collections. One of them connects
to the local instance and the other one connects to the remotely hosted version of the application.

Local PostMan collection: https://www.getpostman.com/collections/f7f927da4cc3596df7ae

Resources:

Job Offer Resource


|paramters              |type       |description                                                                |
|---                    |---        |---                                                                        |
|id                     |UUID       |The unique id of the job offer. This has to be used for any further calls  |
|jobTitle               |String     |The job Title describing the job offer.                                    |
|jobDescription         |String     |The job Description describing the job offer.                              |
|location               |String     |The job location for the job offer.                                        |
|jobOfferStatus         |Enum       |The job offer status. Possible values are ACTIVE or INACTIVE.              |
|startDate              |String     |The job offer start date. Format is YYYY-MM-DD.                            |
|vacancies              |number     |The number of positions open for the job offer. It's a constant value and when all vacancies are filled, the job offer is marked INACTIVE.                            |
|numberOfApplications   |number     |The number of applications received for the job offer.                     |

Job Application Resource

|paramters              |type       |description                                                                        |
|---                    |---        |---                                                                                |
|id                     |UUID       |The unique id of the job application. This has to be used for any further calls    |
|candidateEmail         |String     |The email ID of the candidate.                                                     |
|resumeText             |String     |The resume of the candidate.                                                       |
|applicationStatus      |Enum       |The application status of the job application. eg: APPLIED,INVITED,REJECTED,HIRED  |

The List of APIs can be seen via the swagger link or the postman collection.

The Application uses HSQLDB as the database, so every fresh run of the application clears any previously saved data.


