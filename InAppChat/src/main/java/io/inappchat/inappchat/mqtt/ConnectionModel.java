package io.inappchat.inappchat.mqtt;

import java.util.Objects;

public class ConnectionModel {

  private String clientId = "";
  private String serverHostName = "predvsrv.pantepic.com";
  //private int serverPort = 1883;
  private boolean cleanSession = true;
  private String username = "";
  private String password = "";

  private boolean tlsConnection = false;
  private String tlsServerKey = "";
  private String tlsClientKey = "";
  private int timeout = 80;
  private int keepAlive = 200;
  private String lwtTopic = "";
  private String lwtMessage = "";
  private int lwtQos = 2;
  private boolean lwtRetain = true;
  private String clientHandle = serverHostName + '-' + clientId;

  public ConnectionModel() {
  }

  /** Initialise the ConnectionModel with an existing connection * */
  public ConnectionModel(Connection connection) {
    clientHandle = connection.handle();
    clientId = connection.getId();
    serverHostName = connection.getHostName();
    //serverPort = connection.getPort();
    cleanSession = connection.getConnectionOptions().isCleanSession();

    if (connection.getConnectionOptions().getUserName() == null) {
      username = "";
    } else {
      username = connection.getConnectionOptions().getUserName();
    }
    if (connection.getConnectionOptions().getPassword() != null) {
      password = new String(connection.getConnectionOptions().getPassword());
    } else {
      password = "";
    }
    tlsServerKey = "--- TODO ---";
    tlsClientKey = "--- TODO ---";
    timeout = connection.getConnectionOptions().getConnectionTimeout();
    keepAlive = connection.getConnectionOptions().getKeepAliveInterval();

    if (connection.getConnectionOptions().getWillDestination() == null) {
      lwtTopic = "";
    } else {
      lwtTopic = connection.getConnectionOptions().getWillDestination();
    }
    if (connection.getConnectionOptions().getWillMessage() != null) {
      lwtMessage = new String(connection.getConnectionOptions().getWillMessage().getPayload());
      lwtQos = connection.getConnectionOptions().getWillMessage().getQos();
      lwtRetain = connection.getConnectionOptions().getWillMessage().isRetained();
    } else {
      lwtMessage = "";
      lwtQos = 0;
      lwtRetain = false;
    }
  }

  public String getClientHandle() {
    return clientHandle;
  }

  public void setClientHandle(String clientHandle) {
    this.clientHandle = clientHandle;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getServerHostName() {
    return serverHostName;
  }

  public void setServerHostName(String serverHostName) {
    this.serverHostName = serverHostName;
  }


  /*public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }*/

  public boolean isCleanSession() {
    return cleanSession;
  }

  public void setCleanSession(boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getTlsServerKey() {
    return tlsServerKey;
  }

  public void setTlsServerKey(String tlsServerKey) {
    this.tlsServerKey = tlsServerKey;
  }

  public String getTlsClientKey() {
    return tlsClientKey;
  }

  public void setTlsClientKey(String tlsClientKey) {
    this.tlsClientKey = tlsClientKey;
  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public int getKeepAlive() {
    return keepAlive;
  }

  public void setKeepAlive(int keepAlive) {
    this.keepAlive = keepAlive;
  }

  public String getLwtTopic() {
    return lwtTopic;
  }

  public void setLwtTopic(String lwtTopic) {
    this.lwtTopic = lwtTopic;
  }

  public String getLwtMessage() {
    return lwtMessage;
  }

  public void setLwtMessage(String lwtMessage) {
    this.lwtMessage = lwtMessage;
  }

  public int getLwtQos() {
    return lwtQos;
  }

  public void setLwtQos(int lwtQos) {
    this.lwtQos = lwtQos;
  }

  public boolean isLwtRetain() {
    return lwtRetain;
  }

  public void setLwtRetain(boolean lwtRetain) {
    this.lwtRetain = lwtRetain;
  }

  public boolean isTlsConnection() {
    return tlsConnection;
  }

  public void setTlsConnection(boolean tlsConnection) {
    this.tlsConnection = tlsConnection;
  }

  @Override
  public String toString() {
    return "ConnectionModel{"
        + "clientHandle='"
        + clientHandle
        + '\''
        + ", clientId='"
        + clientId
        + '\''
        + ", serverHostName='"
        + serverHostName
        + '\''
        + ", cleanSession="
        + cleanSession
        + ", username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + ", tlsConnection="
        + tlsConnection
        + ", tlsServerKey='"
        + tlsServerKey
        + '\''
        + ", tlsClientKey='"
        + tlsClientKey
        + '\''
        + ", timeout="
        + timeout
        + ", keepAlive="
        + keepAlive
        + ", lwtTopic='"
        + lwtTopic
        + '\''
        + ", lwtMessage='"
        + lwtMessage
        + '\''
        + ", lwtQos="
        + lwtQos
        + ", lwtRetain="
        + lwtRetain
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ConnectionModel that = (ConnectionModel) o;

    //if (serverPort != that.serverPort) return false;
    if (cleanSession != that.cleanSession) return false;
    if (tlsConnection != that.tlsConnection) return false;
    if (timeout != that.timeout) return false;
    if (keepAlive != that.keepAlive) return false;
    if (lwtQos != that.lwtQos) return false;
    if (lwtRetain != that.lwtRetain) return false;
    if (!Objects.equals(clientHandle, that.clientHandle)) {
      return false;
    }
    if (!Objects.equals(clientId, that.clientId)) return false;
    if (!Objects.equals(serverHostName, that.serverHostName)) {
      return false;
    }
    if (!Objects.equals(username, that.username)) return false;
    if (!Objects.equals(password, that.password)) return false;
    if (!Objects.equals(tlsServerKey, that.tlsServerKey)) {
      return false;
    }
    if (!Objects.equals(tlsClientKey, that.tlsClientKey)) {
      return false;
    }
    if (!Objects.equals(lwtTopic, that.lwtTopic)) return false;
    return Objects.equals(lwtMessage, that.lwtMessage);
  }

  @Override
  public int hashCode() {
    int result = clientHandle != null ? clientHandle.hashCode() : 0;
    result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
    result = 31 * result + (serverHostName != null ? serverHostName.hashCode() : 0);
    //result = 31 * result + serverPort;
    result = 31 * result + (cleanSession ? 1 : 0);
    result = 31 * result + (username != null ? username.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    result = 31 * result + (tlsConnection ? 1 : 0);
    result = 31 * result + (tlsServerKey != null ? tlsServerKey.hashCode() : 0);
    result = 31 * result + (tlsClientKey != null ? tlsClientKey.hashCode() : 0);
    result = 31 * result + timeout;
    result = 31 * result + keepAlive;
    result = 31 * result + (lwtTopic != null ? lwtTopic.hashCode() : 0);
    result = 31 * result + (lwtMessage != null ? lwtMessage.hashCode() : 0);
    result = 31 * result + lwtQos;
    result = 31 * result + (lwtRetain ? 1 : 0);
    return result;
  }
}