<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- author Geoffrey De Smet --%>
<html>
    <head>
        <title>spring-richclient Petclinic</title>
        <style type="text/css" media="all">
            @import url("<%=request.getContextPath()%>/css/style.css");
        </style>
    </head>
    <body>
        <h1>Welcome to Spring-richclient Petclinic</h1>
        <!-- Detect if java webstart is available -->
        <script type="text/javascript">
            function isInternetExplorerJavaEnabled() {
                try {
                    // Detect Java 5 platform
                    var obj = new ActiveXObject('JavaWebStart.isInstalled.1.5.0.0');
                    if (obj != null) {
                        return 1;
                    }
                } catch (exception) {
                    // do nothing
                }
                try {
                    // Detect Java 5 platform
                    var obj = new ActiveXObject('JavaWebStart.isInstalled.1.6.0.0');
                    if (obj != null) {
                        return 1;
                    }
                } catch (exception) {
                    // do nothing
                }
                return 0;
            }

            function isFirefoxJavaEnabled() {
                for (var i = 0; i < navigator.mimeTypes.length; i++) {
                    var s = navigator.mimeTypes[i].type;
                    // Detect Java 5 platform
                    if (s == 'application/x-java-applet;version=1.5'
                            || s == 'application/x-java-applet;version=1.6') {
                        return 1;
                    }
                }
                return 0;
            }

            if (!isInternetExplorerJavaEnabled() && !isFirefoxJavaEnabled()) {
                document.write('<p class="error">'
                        + 'There is no updated version of the Java JRE present on your system. '
                        + 'First install the Java JRE and restart your browser.'
                        + '</p>');
                document.write('<a class="error" href="http://java.com/getjava" target="_blank">'
                        + 'Click here to install the most recent Java JRE.'
                        + '</a>');
            }
        </script>
        <p>Klik on the link to start the Petclinic.</p>
        <div class="startApplication">
            <!-- Do not reformat the following line... -->
            <%--<a href="<%=request.getContextPath()%>/jnlp/launch.jnlp"><img src="image/startApplication.png" alt="->"/></a>--%>
            <a href="<%=request.getContextPath()%>/jnlp/petclinic-standalone.jnlp"><span>Start Petclinic standalone</span></a>
        </div>
        <div class="startApplication">
            <!-- Do not reformat the following line... -->
            <%--<a href="<%=request.getContextPath()%>/jnlp/launch.jnlp"><img src="image/startApplication.png" alt="->"/></a>--%>
            <a href="<%=request.getContextPath()%>/jnlp/petclinic-clientserver.jnlp"><span>Start Petclinic client-server</span></a>
        </div>
    </body>
</html>