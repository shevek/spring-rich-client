package org.springframework.richclient.widget.editor.provider;

import java.util.List;

/**
 * <p>
 * The DataProvider regulates access to the back-end services. It can be used by eg a
 * {@link DefaultDataEditorWidget} as a central access point to the services that provide the data.
 * </p>
 */
public interface DataProvider extends DataProviderEventSource
{

    /**
     * <p>
     * Each DataProvider can specify a policy that dictates when to refresh the data list. This policy must be
     * taken into account by any class using the DataProvider in order to keep the data in a consistent state.
     * </p>
     * 
     * <ul>
     * <li><em>NEVER</em> No automatic refresh, user should trigger a refresh when needed.</li>
     * <li><em>ON_EMPTY</em> Fetch the data once. This usually means when your client-side data list is
     * empty.</li>
     * <li><em>ON_USER_SWITCH</em> Data needs to be refreshed when a user switch is detected. The data may
     * contain user specific entries.</li>
     * <li><em>ALLWAYS</em> Data needs to be refreshed whenever the user views the list (on switching to
     * that screen).</li>
     * </ul>
     */
    public static enum RefreshPolicy {
        NEVER, ON_EMPTY, ON_USER_SWITCH, ALLWAYS
    }

    /**
     * Use {@link #getDetailObject(Object, boolean)} instead.
     */
    @Deprecated
    public boolean hasSpecificDetail();

    /**
     * Use {@link #getDetailObject(Object, boolean)} instead.
     */
    @Deprecated
    public Object getDetailObject(Object selectedObject);

    /**
     * Fetch the detailed object from the back-end. To optimize your code, a flag is passed (forceLoad) to
     * specify if a back-end load is absolutely necessary or not. If forceLoad is <code>false</code> you may
     * return the selectedObject directly if it is already detailed. On the other hand if forceLoad is
     * <code>true</code> you MUST fetch the detailed object from the back-end. If your object doesn't have a
     * specific detail the same logic must be applied to allow the fetching of a fresh individual object.
     * 
     * @param selectedObject
     *            the object that must be used to fetch the detailed one.
     * @param forceLoad
     *            if <code>true</code> always go to back-end and load the object; if <code>false</code> a
     *            shortcut can be used to return an already detailed selected object.
     */
    public Object getDetailObject(Object selectedObject, boolean forceLoad);

    /**
     * Haalt op de back-end het gekozen object in minimale vorm op.
     * 
     * Opmerking: Uit hoofde van performantie wordt aangeraden vooraf te testen of het selectedObject al niet
     * minimaal is. In dat geval kan de parameter gewoon geretourneerd worden.
     * 
     * @param selectedObject
     *            het beperkte object waarvan alle detail moet worden geladen.
     */
    public Object getSimpleObject(Object selectedObject);

    /**
     * Kunnen gereduceerde lijsten vanop de backend worden teruggegeven.
     * 
     * @return <code>false</code> geeft aan dat in de implementatie van {@link #getList(Object)}het
     *         'criteria' argument genegeerd wordt.
     */
    public boolean supportsFiltering();

    /**
     * Haalt een (mogelijks gefilterde) lijst op van de backend. <code>criteria == null</code> Vraagt een
     * ongefilterde lijst. Implementaties die geen filtering supporteren, kunnen het argument totaal negeren.
     * 
     * @param criteria
     *            waaraan de objecten in de lijst moeten voldoen.
     * 
     * @see #supportsFiltering()
     */
    public List getList(Object criteria);

    /**
     * Is 'update' ondersteund?
     * 
     * @return <code>false</code> geeft aan dat {@link #update}niets naar de backend doorstuurd en
     *         <code>null</code> retourneert.
     */
    public boolean supportsUpdate();

    /**
     * Bewaart de wijzigingen in de entiteit op de backend indien {@link #supportsUpdate()}.
     * 
     * @param updatedData
     *            entiteit die moet bewaard worden.
     * @return de bewaarde versie van de entiteit.
     */
    public Object update(Object updatedData);

    /**
     * Is 'create' en 'newInstance' ondersteund?
     * 
     * @return <code>false</code> geeft aan dat {@link #create(Object)}niets naar de backend doorstuurt en
     *         <code>null</code> retourneert.
     */
    public boolean supportsCreate();

    /**
     * Creeert de entiteit op de backend indien {@link #supportsCreate()}.
     * 
     * @param newData
     *            te creeren entiteit.
     * @return de gecreerde entiteit.
     */
    public Object create(Object newData);

    /**
     * Maak een nieuwe instantie aan die nog niet persistent is, eventueel default informatie bevat en kan
     * worden gebruikt om een persistente entiteit aan te maken met create().
     * 
     * @param criteria
     *            eventuele data die kan worden opgenomen in de entiteit (meestal filter).
     * @return een object dat nog niet persistent is en kan worden gebruikt om na aanvulling te persisteren
     *         via {@link #create(Object)}. Indien <code>null</code> kan de form eventueel het huidige
     *         object gebruiken om een nieuwe instantie aan te maken.
     */
    public Object newInstance(Object criteria);

    /**
     * Is 'clone' gesupporteerd?
     * 
     * @return <code>false</code> geeft aan dat {@link #clone(Object)}geen dubbel aanmaakt.
     */
    public boolean supportsClone();

    /**
     * Cloont (lokaal) de entiteit indien {@link #supportsClone()}.
     * 
     * @param sampleData
     *            te dupliceren object.
     * @return het duplicaat.
     */
    public Object clone(Object sampleData);

    /**
     * Is 'delete' ondersteund?
     * 
     * @return <code>false</code> geeft aan dat de {@link #delete(Object)}de entiteit op de backend <b>niet
     *         </b> verwijdert.
     */
    public boolean supportsDelete();

    /**
     * Verwijdert de entiteit op de backend indien {@link #supportsDelete()}.
     * 
     * @param dataToRemove
     *            te verwijderen entiteit.
     */
    public void delete(Object dataToRemove);

    /**
     * Kan er extra criteria worden gezet?
     */
    public boolean supportsBaseCriteria();

    /**
     * Extra criteria die kan gezet worden buiten de normale filter
     */
    public void setBaseCriteria(Object criteria);

    /**
     * Bestaat het object data reeds in de tabellen?
     */
    public boolean exists(Object data);

    /**
     * Elke DataProvider kan specifiëren wanneer zijn lijst data zeker gerefreshed moet worden.
     * 
     * @return RefreshPolicy die geldt voor deze provider.
     */
    public RefreshPolicy getRefreshPolicy();

}
