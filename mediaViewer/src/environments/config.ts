/* 
this is your configuration file for your backend server, without changing the host, it is set to it self automatically,
because this Angular app is hosted by/within the Quarkus server
*/

export const CONFIG = {
  backend: {
    host: location.hostname,
    port: "8080"
  }
};
