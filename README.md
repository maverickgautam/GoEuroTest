# GoEuroTest
JSON respone parser from a REST endpoint



Apache Maven 3.0.5
java 1.7.0_91

Key Points 
ouput available at : /tmp/goeuro/query/parser/output.txt (configurable via properties file)

Interface defined .
Impl is custom with source=REST API and sink= File Path 
queryparser.properties can be used for passing any external resource path 
http client used so that custom strategy for retry/ConnectionReuse/keepAlive can be supported 
All constants present in Constant Class 
Util Folder has ultility functions 
Jar designed like a lib so that it can be used by other programs 
Pom defined with all posible params to have smooth transitioning from one version to another
