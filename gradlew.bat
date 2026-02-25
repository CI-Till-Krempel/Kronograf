@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find the application home.
set "APP_HOME=%~dp0"

@rem Find the application name.
set "APP_NAME=Gradle"
set "APP_BASE_NAME=%~n0"

@rem Use the maximum available, or set MAX_FD != -1 to use that value.
set MAX_FD=maximum

@rem For Darwin, add options to specify how the application appears in the dock
if "%OS%" == "Darwin" (
    set "GRADLE_OPTS=%GRADLE_OPTS% \"-Xdock:name=%APP_NAME%\" \"-Xdock:icon=%APP_HOME%media\gradle.icns\""
)

@rem OS specific support (must be 'true' or 'false').
set cygwin=false
set msys=false
set darwin=false
set nonstop=false
if "%OS%" == "CYGWIN_NT-10.0" (
    set cygwin=true
)
if "%OS%" == "Darwin" (
    set darwin=true
)
if "%OS%" == "MINGW64_NT-10.0" (
    set msys=true
)
if "%OS%" == "NONSTOP_KERNEL" (
    set nonstop=true
)

@rem Attempt to find JAVACMD
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\jre\bin\java.exe" (
        set "JAVACMD=%JAVA_HOME%\jre\bin\java.exe"
    ) else (
        set "JAVACMD=%JAVA_HOME%\bin\java.exe"
    )
    if not exist "%JAVACMD%" (
        echo ERROR: JAVA_HOME is set to an invalid directory: "%JAVA_HOME%"
        echo.
        echo Please set the JAVA_HOME variable in your environment to match the
        echo location of your Java installation.
        goto L_End
    )
) else (
    set "JAVACMD=java.exe"
    where %JAVACMD% >nul 2>nul
    if %errorlevel% neq 0 (
        echo ERROR: JAVA_HOME is not set and no 'java.exe' command could be found in your PATH.
        echo.
        echo Please set the JAVA_HOME variable in your environment to match the
        echo location of your Java installation.
        goto L_End
    )
)

@rem Increase the maximum number of open file descriptors
if "%cygwin%" == "false" (
    if "%darwin%" == "false" (
        if "%nonstop%" == "false" (
            if not "%MAX_FD%" == "maximum" (
                if not "%MAX_FD%" == "max" (
                    if not "%MAX_FD%" == "" (
                        ulimit -n %MAX_FD%
                    )
                )
            )
        )
    )
)

@rem Collect all arguments for the java command, following the shell quoting and substitution rules
set "CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar"

@rem Split up the JVM options again from the Gradle options
set "JVM_OPTS="
set "GRADLE_OPTS="

:L_Loop
if "x%~1" == "x" goto L_Done
if "x%~1" == "x-D" (
    set "JVM_OPTS=%JVM_OPTS% %~1%~2"
    shift
) else if "x%~1" == "x-X" (
    set "JVM_OPTS=%JVM_OPTS% %~1"
) else if "x%~1" == "x-XX" (
    set "JVM_OPTS=%JVM_OPTS% %~1"
) else (
    goto L_Done
)
shift
goto L_Loop
:L_Done

@rem Execute Gradle
"%JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:L_End
if "%OS%"=="Windows_NT" endlocal

:L_Exit
exit /b %errorlevel%
