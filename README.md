# STEMulator
REST API for running interactive and AI guided simulations of STEM education science labs. The teaching approach and learning goals of science labs are closely aligned with Next Generation Science Standards (NGSS).

## Quickstart Steps

<b>1. Clone Repo</b>

- git clone https://github.com/ngulley/stemulator-api.git


<b>2. Mongo DB Integration</b>

- Download and install Mongo DB 
- Create a "stemulator" schema 
- Create a "labs" collection under the "stemulator" schema 
- Import sample lab objects from "src/main/resources/data/stemulator.labs.json" into the Mongo DB's "labs" collection


<b>3. LLM Integration</b>

- Create an account with an LLM provider (e.g. OpenAI) 
- Add the API key that was provided to you as an environment variable (e.g. set OPENAI_API_KEY=mykeystring )
- Reference the LLM key's environment variable in the application.properties file (e.g. spring.ai.openai.api-key=${OPENAI_API_KEY} )

<b>4. Start application</b>

- build the project
- run as a Spring Boot application

<b>5. Call REST APIs</b>

- Get Science Lab: 
   
    curl --location 'http://localhost:8080/stemulator/v1/labs/HS-LS4-2'

- Get Science Lab List:  
     
    curl --location 'http://localhost:8080/stemulator/v1/labs'

- Create Science Lab: 


    curl --location 'http://localhost:8080/stemulator/v1/labs' \
    --form 'labId="HS-LS4-2"' \
	--form 'discipline="Life Science"' \
	--form 'topic="Biological Evolution: Unity and Diversity"' \
	--form 'subTopic="Natural Selection"' \
	--form 'expertise="Evolutionary Biology"' \
	--form 'simulation="Natual Selection"' \
	--form 'screenshot=@"/C:/Users/Nate/Documents/Grad School/MSSE692-  Spring2026/week2/discovery/natural_selection_simulation.jpg"'
   
- Get Science Guidance: 

	curl --location 'http://localhost:8080/stemulator/v1/guides/lab/HS-LS4-2/part/1' \
	--form 'evidence=@"/C:/Users/Nate/Documents/Grad School/MSSE692-Spring2026/week3/assignments/HS-LS4-2_part1_evidence1.csv"' \
	--form 'scienceGuideRequest="{\"studentName\":\"Barack\",\"setup\":[\"no starting traits are selected\"],\"observations\":[\"the closer their fur color is to the color of the environment, the less visible they are to predators\",\"the initial population distribution oft traits is random\",\"the more limited the food, the smaller the population size. The more wolves the smaller the population size \"],\"predictions\":[\"brown fur, longer teeth and floppy ears\",\"if the environment turned white due to snow then white fur would be favored over brown fur.\"]}"'

---

## Project Structure

```
stemulator-api/
├── README.md                                   # Project overview, environment setup, build and run info
├── HELP.md                                     # Links to helpful resources
├── mvnw.cmd                                    # Maven wrapper script for Windows platform
├── mvnw                                        # Maven wrapper Bash script
├── pom.xml                                     # Definitions for project's library dependencies
├── .gitignore                                  # Git ignore rules
│
├── src/
│   ├── main/
│   │   ├── java                                # Java packages   
│   │   │   ├── edu/regis/stemulator/config     
│   │   │   ├── edu/regis/stemulator/controller 
│   │   │   ├── edu/regis/stemulator/model
│   │   │   ├── edu/regis/stemulator/repository
│   │   │   ├── edu/regis/stemulator/request
│   │   │   ├── edu/regis/stemulator/response
│   │   │   └── edu/regis/stemulator/service
│   │   │
│   │   └── resources                           # Resource files
│   │       ├── data                            # MongoDB data and schema
│   │       └── application.properties          # main properties file for application
│   │
│   └── test/                                   # Unit & integration tests
│
├── .github/
│   └── workflows 
│       └── main.yml                            # Github Actions config
│
└── doc/
    ├── API Specification.docx                  # API specs
    ├── Architecture Decisions.docx             # Record of architecture decisions
    ├── Architecture.docx                       # Application architecture
    └── Detailed Design.docx                    # Application detailed designs
```
---







