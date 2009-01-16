package org.springframework.richclient.application.session;

import java.util.Map;
import java.util.List;


public class ApplicationSessionInitializer
{

    /**
     * Extra gebruikersattributen die moeten worden toegevoegd aan de
     * ExecutionContext na succesvol inloggen.
     */
    private Map<String, Object> userAttributes;

    /**
     * Extra sessieattributen die moeten worden toegevoegd aan de
     * ExecutionContext na succesvol inloggen.
     */
    private Map<String, Object> sessionAttributes;

    /**
     * Lijst van commandId's die moeten worden uitgevoerd vooraleer het
     * applicatieWindow te voorschijn komt.
     */
    private List<String> preStartupCommandIds;

    /**
     * Lijst van commandId's die moeten worden uitgevoerd wanneer het
     * applicatieWindow reeds getoond wordt.
     */
    private List<String> postStartupCommandIds;

    /**
     * Gebruikers attributen die in de ExecutionContext worden gezet na
     * succesvol inloggen.
     *
     * @param attributes
     *            Map met als strings als key an objecten als value.
     */
    public void setUserAttributes(Map<String, Object> attributes)
    {
        this.userAttributes = attributes;
    }

    /**
     * Gebruikers attributen die in de ExecutionContext worden gezet na
     * succesvol inloggen.
     *
     * @return een map met string/object pairs.
     */
    public Map<String, Object> getUserAttributes()
    {
        return userAttributes;
    }

    /**
     * Session attributen die in de ExecutionContext worden gezet bij het
     * opstarten van de applicatie.
     *
     * @param attributes
     *            Map met als strings als key an objecten als value.
     */
    public void setSessionAttributes(Map<String, Object> attributes)
    {
        this.sessionAttributes = attributes;
    }

    /**
     * Session attributen die in de ExecutionContext worden gezet bij het
     * opstarten van de applicatie.
     *
     * @return een map met string/object pairs.
     */
    public Map<String, Object> getSessionAttributes()
    {
        return sessionAttributes;
    }

    /**
     * Geef de lijst van commandId's die moeten worden uitgevoerd vooraleer het
     * applicatieWindow te voorschijn komt.
     *
     * @param commandIds
     *            een lijst van commandId's.
     */
    public void setPreStartupCommandIds(List<String> commandIds)
    {
        this.preStartupCommandIds = commandIds;
    }

    /**
     * @return de lijst van commandId's die moeten worden getoond vooraleer het
     *         applicatieWindow wordt getoond.
     */
    public List<String> getPreStartupCommandIds()
    {
        return preStartupCommandIds;
    }

    /**
     * Geef de lijst van commandId's die moeten worden uitgevoerd wanneer het
     * applicatieWindow reeds wordt getoond.
     *
     * @param commandIds
     *            een lijst van commandId's.
     */
    public void setPostStartupCommandIds(List<String> commandIds)
    {
        this.postStartupCommandIds = commandIds;
    }

    /**
     * @return de lijst van commandId's die moeten worden uitgevoerd nadat het
     *         applicatieScherm wordt getoond.
     */
    public List<String> getPostStartupCommandIds()
    {
        return postStartupCommandIds;
    }

    /**
     * Hook die wordt opgeroepen vooraleer de sessie attributen worden
     * opgevraagd. Hier heb je de mogelijkheid om in code sessie attributen op
     * te vragen en klaar te zetten via {@link #setSessionAttributes(Map)}.
     */
    public void initializeSession()
    {
    }

    /**
     * Hook die wordt opgeroepen vooraleer de user attributen worden opgevraagd.
     * Hier heb je de mogelijkheid om in code user attributen op te vragen en
     * klaar te zetten via {@link #setUserAttributes(Map)}.
     */
    public void initializeUser()
    {
    }
}
