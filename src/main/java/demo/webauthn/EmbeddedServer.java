// Copyright 2022 Yubico AB
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package demo.webauthn;

import demo.App;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Standalone Java application launcher that runs the demo server with the API but no static
 * resources (i.e., no web GUI)
 */
public class EmbeddedServer {

  public static void main(String[] args) throws Exception {
    final int port = Config.port;

    App app = new App();

    ResourceConfig config = new ResourceConfig();
    config.registerClasses(app.getClasses());
    config.registerInstances(app.getSingletons());

    SslContextFactory ssl = new SslContextFactory("keystore.jks");
    ssl.setKeyStorePassword("p@ssw0rd");

    Server server = new Server();
    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme("https");
    httpConfig.setSecurePort(port);
    HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
    httpsConfig.addCustomizer(new SecureRequestCustomizer());

    ServerConnector connector =
        new ServerConnector(
            server,
            new SslConnectionFactory(ssl, HttpVersion.HTTP_1_1.asString()),
            new HttpConnectionFactory(httpsConfig));

    connector.setPort(port);
    connector.setHost("127.0.0.1");

    ServletHolder servlet = new ServletHolder(new ServletContainer(config));
    ServletContextHandler context = new ServletContextHandler(server, "/");
    context.addServlet(DefaultServlet.class, "/");
    context.setResourceBase("frontend/build/dist");
    context.addServlet(servlet, "/api/*");

    server.setConnectors(new Connector[] {connector});
    server.start();
  }
}
