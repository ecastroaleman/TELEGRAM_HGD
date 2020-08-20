package com.ericsson.hgd;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum ApplicationProperties {
    INSTANCE;

    private final Properties properties;

    ApplicationProperties() {
        properties = new Properties();
        try {
             properties.load(new FileInputStream("c:\\digicel2\\HGD_TELEGRAM_ES\\conf.properties"));
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getJiraUrl() {
        return properties.getProperty("JIRA_URL");
    }
    public String getJiraAdminUsername() {
        return properties.getProperty("JIRA_ADMIN_USERNAME");
    }
    public String getJiraAdminPassword() {
        return properties.getProperty("JIRA_ADMIN_PASSWORD");
    }
    public String apiToken() {return properties.getProperty("APITOKEN");}
    public String chatEvolutivos() {return properties.getProperty("CHATEVOLUTIVOS");}
    public String chatPersonal(){return properties.getProperty("CHATPERSONAL");}
    public String chatTemporal(){return properties.getProperty("CHATTEMPORAL");}
    public String urlTelegram(){return properties.getProperty("URL_TELEGRAM");}
    public String chatEnvio(){return properties.getProperty("CHATENVIO");}
    public String chatEnvioAsig(){return properties.getProperty("CHATENVIOASIGNACION");}
    public String chatEnvioFro(){return properties.getProperty("CHATENVIOFRONTALES");}
    public String chatEnvioCdR(){return properties.getProperty("CHATENVIOCDR");}
    public String chatEnvioEnt(){return properties.getProperty("CHATENVIOENTRADAS");}
    public String chatErrores(){return properties.getProperty("CHATERRORES");}
    public String getJQL(){return properties.getProperty("JQL");}
    public String getFilterID() {return properties.getProperty("filterID");}
}