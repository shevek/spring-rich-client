/**
 * Created on 24-feb-2005
 * Created by jh
 */
package org.springframework.richclient.widget.editor.provider;

/**
 * <p>Event dat doorgeeft welke veranderingen er gebeurd zijn op de achtergrond.
 * Mogelijks kan dit zijn:</p>
 * 
 *  <ul>
 *  <li>Aanmaken van nieuw object: {@link #newEntityEvent(Object)}</li>
 *  <li>Updaten van object: {@link #updateEntityEvent(Object, Object)}</li>
 *  <li>Verwijderen van object: {@link #deleteEntityEvent(Object)}</li>
 *  </ul>
 */
public class DataProviderEvent
{

    /**
     * Type of event: New, Update or Delete.
     */
    private final int eventType;

    /**
     * The new entity: result of update event or created object of new event.
     */
    private final Object newEntity;

    /**
     * The old entity: object that was deleted or previous value of update
     * event.
     */
    private final Object oldEntity;

    /**
     * New object created event. Should have a newEntity.
     */
    public static final int EVENT_TYPE_NEW = 1;

    /**
     * Object updated event. Should have both: a newEntity and an oldEntity.
     */
    public static final int EVENT_TYPE_UPDATE = 2;

    /**
     * Object deleted event. Should have an oldEntity.
     */
    public static final int EVENT_TYPE_DELETE = 3;

    /**
     * Constructor. Maak een nieuw event aan met de correcte nieuwe en oude
     * entiteit waarden.
     * 
     * @param eventType
     *            Type van event.
     * @param oldEntity
     *            Oude waarde van de entiteit.
     * @param newEntity
     *            Nieuwe waarde van de entiteit.
     */
    public DataProviderEvent(final int eventType, Object oldEntity, Object newEntity)
    {
        this.eventType = eventType;
        this.oldEntity = oldEntity;
        this.newEntity = newEntity;
    }

    /**
     * @return het nieuwe object. Kan <code>null</code> zijn.
     */
    public Object getNewEntity()
    {
        return this.newEntity;
    }

    /**
     * @return het oude Object. Kan <code>null</code> zijn.
     */
    public Object getOldEntity()
    {
        return oldEntity;
    }

    /**
     * @return het Type van event.
     */
    public int getEventType()
    {
        return this.eventType;
    }
    
    /**
     * Shortcut functie voor het aanmaken van een New Object event.
     * 
     * @param newEntity Het nieuwe object.
     * @return DataProviderEvent.
     */
    public static final DataProviderEvent newEntityEvent(Object newEntity)
    {
        return new DataProviderEvent(EVENT_TYPE_NEW, null, newEntity);
    }
    
    /**
     * Shortcut functie voor het aanmaken van een Update Object event.
     * 
     * @param oldEntity Het oude object.
     * @param newEntity Het nieuwe object.
     * @return DataProviderEvent.
     */
    public static final DataProviderEvent updateEntityEvent(Object oldEntity, Object newEntity)
    {
        return new DataProviderEvent(EVENT_TYPE_UPDATE, oldEntity, newEntity);
    }
    
    /**
     * Shortcut functie voor het aanmaken van een Delete Object event.
     * 
     * @param oldEntity Het verwijderde object.
     * @return DataProviderEvent.
     */
    public static final DataProviderEvent deleteEntityEvent(Object oldEntity)
    {
        return new DataProviderEvent(EVENT_TYPE_DELETE, oldEntity, null);
    }
}