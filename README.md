# maven-afiz
Custom Maven plugin to add arbitrary files into any archive during compilation.

# Usage

Clone repo and run: `mvn clean install`

Now that it's installed locally, you can add the dependency in your pom:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.satsana</groupId>
            <artifactId>afiz-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>add-files-into-archive</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <archive>~/my/amazing/uber.jar</archive>
                <files>
                    <file>~/sexy.jpg</file>
                    <file>~/readme.txt</file>
                    <file>~/idk/why/but/add/this/too/ok.mkv</file>
                </files>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Then it will automatically run when you compile like this: `mvn clean compile` and the 3 files will be added inside the archive
