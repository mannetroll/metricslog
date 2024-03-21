# metricslog

### ECS maven

    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-layout-template-json</artifactId>
      <version>2.23.1</version>
    </dependency>


### ECS log4j

    Configuration:
       Appenders:
          Console:
             name: Console_Appender
             target: SYSTEM_OUT
             JsonTemplateLayout:
                eventTemplateUri: classpath:EcsLayout.json

       Loggers:
          Root:
             level: info
             AppenderRef:
             -  ref: Console_Appender    