
# **Why**

 This repo is just for laying out my thoughts. A **personal** journal started from a **failed** interview.


#### Thought 1. 
  As stated above, this started from a failed interview. I can say I did not have a good first impression. I had a good feeling about the tech stack that I choose, 
but at the end demo did not work as the user expected (damn CORS settings). 
 
#### Thought 2
   Show what I did for the interview,to have something to show, describe, analyze, evolve. As I dont like interviews, this might help me prove my skills for future interviews...
who knows

#### Thought 3
   The task was to create a tool for supporting the usual buyer/seller interaction that happens in the trading industry. Implement it with tools as close as possible as requested by the interviewer (there was a list to choose from).
   
#### Thought 4
   Started with enthusiasm as it was the first interview that had a more practical approach. It made a good impression on me, was totally into it
   
#### Thought 5   
   Started creating the app, thinking it will be cool, trying to put a lot of attention on the client needs. I felt I started doing things automatically, applying learned patterns, but implemented with 
   different/new tools. Trying to use the tools to satisfy  my own expectations of how the process of creating should happen. 

#### Thought 6
   I had it all in my mind,it was nice, but had realized I'm not good enough at presenting my mode of thinking without a structuerd plan. I started writing the following things (more for me than for the reader) :
    
   - About the qualities of the app:
        - maintainable
        - testable, debuggable, more coverage with fewer tests, no mocks.
            - start app embedded in tests
                - startup and test times should be short
                - tests should run against deployed or embedded api
       - define contracts for business logic/persistence,whatever layer I would have thought was important to define. Swapping implementation should be easy. 
       - scriptable, starting should be easy manually and in automated envs
       - satisfy the interviewer's requirements (implementation and business needs)
       - focus on busines logic, rely as much as possible on tool features
   
   - About the chosen tools for implementation:
       - Scala (was a almost new language to me)  :
           - initial impression is Scala seems to not have anything in common with java at the syntax  level
           - dont like implicit values approach in scala
           - methods and classes looking like operators (in http4s)
           - compiler extensions for sbt
           - monads
           - class, case class, trait distinction , to much
           - pattern matching in case expressions looks nice
           - [] for type polimorfism, () for accessing arrays
           - instrumented code, debugging issues
           - cloudant couched client scala  issues
           - sbt experience not so good
           - cool IntVar validates path value type when implementing the rest api
            
       - RESTfull API approach for exposing the business layer using http4s
       
       - CouchDB for persistence
          - document database and change notification features that was a good fit for the requirements
          
#### Thought 7 
   Take a pause