# Build Notes

## Summary
The system level build tool being used is Maven.  There's a pom.xml at the root that, for most tasks, delegates to the pom.xml for each package.  Inside each package, the pom.xml will delegate to a build tool appropriate for the language.  (e.g. maven for java, poetry for python, and npm for typescript).  

From within each package directly, a build can be performed using either maven or the language specific build tool.  The results should be the same.

The only exception to this behavior is for the lifecycle task, "deploy".

## Deploy
In order to maintain consistent version numbers and tagging in the Git repository, all publishing is handled at the root level.  Each time a public is performed, ALL libraries are published to the appropriate public library repositories